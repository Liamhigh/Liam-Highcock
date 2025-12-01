package org.verumomnis.forensic.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.verumomnis.forensic.R
import org.verumomnis.forensic.core.EvidenceType
import org.verumomnis.forensic.core.ForensicEngine
import org.verumomnis.forensic.databinding.ActivityScannerBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Scanner Activity - Document/Photo Capture
 * 
 * Uses CameraX for evidence capture with GPS tagging
 * 
 * @author Liam Highcock
 */
class ScannerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CASE_ID = "extra_case_id"
        private const val TAG = "ScannerActivity"
    }

    private lateinit var binding: ActivityScannerBinding
    private lateinit var forensicEngine: ForensicEngine
    private var imageCapture: ImageCapture? = null
    private var caseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        caseId = intent.getStringExtra(EXTRA_CASE_ID)
        if (caseId == null) {
            Toast.makeText(this, "Invalid case", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        forensicEngine = ForensicEngine(this)

        setupToolbar()
        checkCameraPermission()
        setupCaptureButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Capture Evidence"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.viewFinder.surfaceProvider
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
                Log.e(TAG, "Use case binding failed", e)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun setupCaptureButton() {
        binding.btnCapture.setOnClickListener {
            captureImage()
        }
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        // Create output file
        val photoFile = File(
            filesDir,
            "evidence_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        binding.btnCapture.isEnabled = false
        binding.textStatus.text = "Capturing..."

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    saveEvidenceWithLocation(photoFile)
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    binding.btnCapture.isEnabled = true
                    binding.textStatus.text = "Capture failed"
                    Toast.makeText(this@ScannerActivity, "Capture failed", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun saveEvidenceWithLocation(photoFile: File) {
        lifecycleScope.launch {
            binding.textStatus.text = "Saving with GPS location..."

            val evidence = forensicEngine.addEvidence(
                caseId = caseId!!,
                type = EvidenceType.PHOTO,
                description = "Photo evidence captured at ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(System.currentTimeMillis())}",
                content = photoFile.readBytes(),
                filePath = photoFile.absolutePath
            )

            if (evidence != null) {
                val locationInfo = if (evidence.latitude != null && evidence.longitude != null) {
                    "GPS: %.4f, %.4f".format(evidence.latitude, evidence.longitude)
                } else {
                    "No GPS available"
                }
                
                Toast.makeText(
                    this@ScannerActivity,
                    "Evidence captured\n$locationInfo",
                    Toast.LENGTH_LONG
                ).show()
                
                finish()
            } else {
                binding.btnCapture.isEnabled = true
                binding.textStatus.text = "Ready to capture"
                Toast.makeText(this@ScannerActivity, "Failed to save evidence", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
