package org.verumomnis.forensic.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.verumomnis.forensic.core.EvidenceType
import org.verumomnis.forensic.core.ForensicEngine
import org.verumomnis.forensic.core.ForensicLocation
import org.verumomnis.forensic.databinding.ActivityVideoRecorderBinding
import org.verumomnis.forensic.location.ForensicLocationService
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Video Recorder Activity
 * 
 * Captures video evidence with:
 * - High quality recording using CameraX
 * - GPS location tagging
 * - Cryptographic sealing
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
class VideoRecorderActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CASE_ID = "caseId"
        const val RESULT_EVIDENCE_ID = "evidenceId"
    }

    private lateinit var binding: ActivityVideoRecorderBinding
    private lateinit var forensicEngine: ForensicEngine
    private lateinit var locationService: ForensicLocationService
    private lateinit var cameraExecutor: ExecutorService
    
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var videoFile: File? = null
    private var isRecording = false
    private var recordingStartTime: Long = 0
    private var currentLocation: ForensicLocation? = null
    private var caseId: String? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            startCamera()
            captureLocation()
        } else {
            Toast.makeText(this, "Camera and audio permissions required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoRecorderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        caseId = intent.getStringExtra(EXTRA_CASE_ID)
        if (caseId == null) {
            Toast.makeText(this, "No case selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializeServices()
        setupUI()
        checkPermissions()
    }

    private fun initializeServices() {
        forensicEngine = ForensicEngine(this)
        locationService = ForensicLocationService(this)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setupUI() {
        binding.tvCaseId.text = "Case: $caseId"
        binding.tvRecordingTime.text = "00:00"
        binding.tvStatus.text = "Ready to record"

        binding.btnRecord.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        binding.btnCancel.setOnClickListener {
            if (isRecording) {
                recording?.stop()
                videoFile?.delete()
            }
            setResult(RESULT_CANCELED)
            finish()
        }

        updateRecordingUI()
    }

    private fun checkPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        val allGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            startCamera()
            captureLocation()
        } else {
            permissionLauncher.launch(requiredPermissions)
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

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
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

    private fun startRecording() {
        val videoCapture = this.videoCapture ?: return

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val filename = "VIDEO_$timestamp.mp4"
        videoFile = File(cacheDir, filename)

        val outputOptions = FileOutputOptions.Builder(videoFile!!).build()

        recording = videoCapture.output
            .prepareRecording(this, outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        isRecording = true
                        recordingStartTime = SystemClock.elapsedRealtime()
                        updateRecordingUI()
                        startTimer()
                        binding.tvStatus.text = "Recording..."
                    }
                    is VideoRecordEvent.Finalize -> {
                        isRecording = false
                        updateRecordingUI()
                        
                        if (!recordEvent.hasError()) {
                            saveVideoEvidence()
                        } else {
                            Toast.makeText(
                                this,
                                "Recording error: ${recordEvent.error}",
                                Toast.LENGTH_SHORT
                            ).show()
                            videoFile?.delete()
                        }
                    }
                }
            }
    }

    private fun stopRecording() {
        recording?.stop()
        recording = null
        binding.tvStatus.text = "Processing..."
    }

    private fun saveVideoEvidence() {
        val file = videoFile ?: return

        lifecycleScope.launch {
            try {
                val videoBytes = withContext(Dispatchers.IO) {
                    file.readBytes()
                }

                val duration = SystemClock.elapsedRealtime() - recordingStartTime
                val filename = "VIDEO_${System.currentTimeMillis()}_${duration}ms.mp4"

                val evidence = forensicEngine.addEvidence(
                    caseId = caseId!!,
                    type = EvidenceType.VIDEO,
                    content = videoBytes,
                    mimeType = "video/mp4",
                    filename = filename,
                    location = currentLocation
                )

                // Clean up temp file
                file.delete()

                if (evidence != null) {
                    // Seal immediately
                    forensicEngine.sealEvidence(caseId!!, evidence.id)

                    Toast.makeText(
                        this@VideoRecorderActivity,
                        "Video evidence captured and sealed: ${evidence.id}",
                        Toast.LENGTH_LONG
                    ).show()

                    val resultIntent = Intent().apply {
                        putExtra(RESULT_EVIDENCE_ID, evidence.id)
                        putExtra(EXTRA_CASE_ID, caseId)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(
                        this@VideoRecorderActivity,
                        "Failed to add video evidence",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@VideoRecorderActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateRecordingUI() {
        if (isRecording) {
            binding.btnRecord.text = "⏹ Stop"
            binding.btnRecord.backgroundTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_red_dark))
        } else {
            binding.btnRecord.text = "🎥 Record"
            binding.btnRecord.backgroundTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_green_dark))
        }
    }

    private fun startTimer() {
        lifecycleScope.launch {
            while (isRecording) {
                val elapsed = SystemClock.elapsedRealtime() - recordingStartTime
                val seconds = (elapsed / 1000) % 60
                val minutes = (elapsed / 1000) / 60
                binding.tvRecordingTime.text = String.format("%02d:%02d", minutes, seconds)
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
