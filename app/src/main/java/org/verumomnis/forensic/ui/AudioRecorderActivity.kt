package org.verumomnis.forensic.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.verumomnis.forensic.core.EvidenceType
import org.verumomnis.forensic.core.ForensicEngine
import org.verumomnis.forensic.core.ForensicLocation
import org.verumomnis.forensic.databinding.ActivityAudioRecorderBinding
import org.verumomnis.forensic.location.ForensicLocationService
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Audio Recorder Activity
 * 
 * Captures audio evidence with:
 * - High quality recording
 * - GPS location tagging
 * - Cryptographic sealing
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
class AudioRecorderActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CASE_ID = "caseId"
        const val RESULT_EVIDENCE_ID = "evidenceId"
    }

    private lateinit var binding: ActivityAudioRecorderBinding
    private lateinit var forensicEngine: ForensicEngine
    private lateinit var locationService: ForensicLocationService
    
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var isRecording = false
    private var recordingStartTime: Long = 0
    private var currentLocation: ForensicLocation? = null
    private var caseId: String? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            captureLocation()
        } else {
            Toast.makeText(this, "Audio permission required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioRecorderBinding.inflate(layoutInflater)
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
                stopRecording()
                audioFile?.delete()
            }
            setResult(RESULT_CANCELED)
            finish()
        }

        updateRecordingUI()
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                captureLocation()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
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
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val filename = "AUDIO_$timestamp.m4a"
        audioFile = File(cacheDir, filename)

        try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }

            isRecording = true
            recordingStartTime = SystemClock.elapsedRealtime()
            updateRecordingUI()
            startTimer()

            binding.tvStatus.text = "Recording..."
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to start recording: ${e.message}", Toast.LENGTH_SHORT).show()
            audioFile?.delete()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            updateRecordingUI()

            binding.tvStatus.text = "Processing..."
            saveAudioEvidence()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to stop recording: ${e.message}", Toast.LENGTH_SHORT).show()
            audioFile?.delete()
        }
    }

    private fun saveAudioEvidence() {
        val file = audioFile ?: return

        lifecycleScope.launch {
            try {
                val audioBytes = withContext(Dispatchers.IO) {
                    file.readBytes()
                }

                val duration = SystemClock.elapsedRealtime() - recordingStartTime
                val filename = "AUDIO_${System.currentTimeMillis()}_${duration}ms.m4a"

                val evidence = forensicEngine.addEvidence(
                    caseId = caseId!!,
                    type = EvidenceType.AUDIO,
                    content = audioBytes,
                    mimeType = "audio/mp4",
                    filename = filename,
                    location = currentLocation
                )

                // Clean up temp file
                file.delete()

                if (evidence != null) {
                    // Seal immediately
                    forensicEngine.sealEvidence(caseId!!, evidence.id)

                    Toast.makeText(
                        this@AudioRecorderActivity,
                        "Audio evidence captured and sealed: ${evidence.id}",
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
                        this@AudioRecorderActivity,
                        "Failed to add audio evidence",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@AudioRecorderActivity,
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
            binding.btnRecord.text = "🎤 Record"
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
        mediaRecorder?.release()
        mediaRecorder = null
    }
}
