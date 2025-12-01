package org.verumomnis.forensic.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.verumomnis.forensic.R
import org.verumomnis.forensic.core.*
import org.verumomnis.forensic.databinding.ActivityScannerBinding
import org.verumomnis.forensic.location.ForensicLocationService
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Scanner Activity - Evidence Capture
 * 
 * Camera interface for capturing document and photo evidence.
 * Automatically captures GPS location when evidence is captured.
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
class ScannerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CASE_ID = "caseId"
        const val EXTRA_EVIDENCE_TYPE = "evidenceType"
        const val RESULT_EVIDENCE_ID = "evidenceId"
    }

    private lateinit var binding: ActivityScannerBinding
    private lateinit var forensicEngine: ForensicEngine
    private lateinit var locationService: ForensicLocationService
    private lateinit var cameraExecutor: ExecutorService
    
    private var imageCapture: ImageCapture? = null
    private var caseId: String? = null
    private var evidenceType: EvidenceType = EvidenceType.DOCUMENT
    private var currentLocation: ForensicLocation? = null

    // Camera permission
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        caseId = intent.getStringExtra(EXTRA_CASE_ID)
        evidenceType = intent.getSerializableExtra(EXTRA_EVIDENCE_TYPE) as? EvidenceType 
            ?: EvidenceType.DOCUMENT

        if (caseId == null) {
            Toast.makeText(this, "No case selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializeServices()
        setupUI()
        checkCameraPermission()
        captureLocation()
    }

    private fun initializeServices() {
        forensicEngine = ForensicEngine(this)
        locationService = ForensicLocationService(this)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setupUI() {
        binding.tvCaseId.text = "Case: $caseId"
        binding.tvEvidenceType.text = "Capturing: ${evidenceType.name}"

        binding.btnCapture.setOnClickListener {
            captureImage()
        }

        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.btnSwitchType.setOnClickListener {
            toggleEvidenceType()
        }
    }

    private fun toggleEvidenceType() {
        evidenceType = when (evidenceType) {
            EvidenceType.DOCUMENT -> EvidenceType.PHOTO
            EvidenceType.PHOTO -> EvidenceType.DOCUMENT
            else -> EvidenceType.DOCUMENT
        }
        binding.tvEvidenceType.text = "Capturing: ${evidenceType.name}"
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureLocation() {
        if (!locationService.hasLocationPermission()) return

        lifecycleScope.launch {
            currentLocation = locationService.getCurrentLocation()
            currentLocation?.let { loc ->
                binding.tvLocation.text = locationService.formatLocation(loc)
            } ?: run {
                binding.tvLocation.text = "Location unavailable"
            }
        }
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        binding.btnCapture.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE

        // Create timestamped file
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val filename = "EVIDENCE_${timestamp}.jpg"
        
        val tempFile = File(cacheDir, filename)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    processAndSaveEvidence(tempFile, filename)
                }

                override fun onError(exception: ImageCaptureException) {
                    binding.btnCapture.isEnabled = true
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(
                        this@ScannerActivity,
                        "Capture failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun processAndSaveEvidence(imageFile: File, filename: String) {
        lifecycleScope.launch {
            try {
                val imageBytes = withContext(Dispatchers.IO) {
                    imageFile.readBytes()
                }

                val evidence = forensicEngine.addEvidence(
                    caseId = caseId!!,
                    type = evidenceType,
                    content = imageBytes,
                    mimeType = "image/jpeg",
                    filename = filename,
                    location = currentLocation
                )

                // Clean up temp file
                imageFile.delete()

                if (evidence != null) {
                    // Seal immediately
                    forensicEngine.sealEvidence(caseId!!, evidence.id)
                    
                    Toast.makeText(
                        this@ScannerActivity,
                        "Evidence captured and sealed: ${evidence.id}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Return result
                    val resultIntent = Intent().apply {
                        putExtra(RESULT_EVIDENCE_ID, evidence.id)
                        putExtra(EXTRA_CASE_ID, caseId)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(
                        this@ScannerActivity,
                        "Failed to add evidence",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ScannerActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.btnCapture.isEnabled = true
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
