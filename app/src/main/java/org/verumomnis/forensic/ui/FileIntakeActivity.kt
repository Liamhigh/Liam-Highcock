package org.verumomnis.forensic.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
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
import org.verumomnis.forensic.databinding.ActivityFileIntakeBinding
import org.verumomnis.forensic.location.ForensicLocationService
import org.verumomnis.forensic.metadata.EvidenceMetadataExtractor
import java.io.InputStream

/**
 * File Intake Activity - Document/File Evidence Import
 * 
 * Allows users to import existing documents, images, and files as evidence.
 * Supports picking files from device storage.
 * Extracts metadata from files when possible.
 * 
 * Police Evidence Workflow: File Intake step
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
class FileIntakeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CASE_ID = "caseId"
        const val RESULT_EVIDENCE_ID = "evidenceId"
        
        private val SUPPORTED_MIME_TYPES = arrayOf(
            "image/*",
            "application/pdf",
            "text/*",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )
    }

    private lateinit var binding: ActivityFileIntakeBinding
    private lateinit var forensicEngine: ForensicEngine
    private lateinit var locationService: ForensicLocationService
    private lateinit var metadataExtractor: EvidenceMetadataExtractor
    
    private var caseId: String? = null
    private var currentLocation: ForensicLocation? = null

    // File picker launcher
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            processSelectedFile(uri)
        }
    }

    // Storage permission launcher (for older Android versions)
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            openFilePicker()
        } else {
            Toast.makeText(this, "Storage permission required to import files", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileIntakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        caseId = intent.getStringExtra(EXTRA_CASE_ID)

        if (caseId == null) {
            Toast.makeText(this, "No case selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializeServices()
        setupUI()
        captureLocation()
    }

    private fun initializeServices() {
        forensicEngine = ForensicEngine(this)
        locationService = ForensicLocationService(this)
        metadataExtractor = EvidenceMetadataExtractor(this)
    }

    private fun setupUI() {
        binding.tvCaseId.text = "Case: $caseId"
        binding.tvStatus.text = "Ready to import file"

        binding.btnBack.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.btnPickDocument.setOnClickListener {
            checkPermissionsAndOpenPicker()
        }

        binding.btnPickImage.setOnClickListener {
            openImagePicker()
        }

        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun captureLocation() {
        if (!locationService.hasLocationPermission()) return

        lifecycleScope.launch {
            currentLocation = locationService.getCurrentLocation()
            currentLocation?.let { loc ->
                binding.tvLocation.text = "Location: ${locationService.formatLocation(loc)}"
            } ?: run {
                binding.tvLocation.text = "Location: Not available"
            }
        }
    }

    private fun checkPermissionsAndOpenPicker() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Need storage permission for older Android versions
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                return
            }
        }
        openFilePicker()
    }

    private fun openFilePicker() {
        filePickerLauncher.launch(SUPPORTED_MIME_TYPES)
    }

    private fun openImagePicker() {
        filePickerLauncher.launch(arrayOf("image/*"))
    }

    private fun processSelectedFile(uri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnPickDocument.isEnabled = false
        binding.btnPickImage.isEnabled = false
        binding.tvStatus.text = "Processing file..."

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    // Get file metadata
                    val fileName = getFileName(uri)
                    val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
                    
                    // Read file content
                    val fileBytes = contentResolver.openInputStream(uri)?.use { 
                        it.readBytes() 
                    } ?: throw Exception("Failed to read file")

                    // Determine evidence type based on MIME type
                    val evidenceType = when {
                        mimeType.startsWith("image/") -> EvidenceType.PHOTO
                        mimeType == "application/pdf" -> EvidenceType.DOCUMENT
                        mimeType.startsWith("text/") -> EvidenceType.TEXT
                        mimeType.contains("word") -> EvidenceType.DOCUMENT
                        else -> EvidenceType.DOCUMENT
                    }

                    // Extract EXIF metadata for images
                    val imageMetadata = if (evidenceType == EvidenceType.PHOTO) {
                        metadataExtractor.extractImageMetadata(uri)
                    } else null

                    // Prefer current GPS location, fall back to EXIF location
                    val finalLocation = currentLocation ?: imageMetadata?.location

                    // Add evidence (includes SHA-512 hashing)
                    val evidence = forensicEngine.addEvidence(
                        caseId = caseId!!,
                        type = evidenceType,
                        content = fileBytes,
                        mimeType = mimeType,
                        filename = fileName,
                        location = finalLocation
                    )

                    if (evidence != null) {
                        // Seal immediately (HMAC-SHA512)
                        forensicEngine.sealEvidence(caseId!!, evidence.id)
                    }

                    evidence
                }

                binding.progressBar.visibility = View.GONE
                binding.btnPickDocument.isEnabled = true
                binding.btnPickImage.isEnabled = true

                if (result != null) {
                    binding.tvStatus.text = "Evidence added: ${result.id}"
                    
                    Toast.makeText(
                        this@FileIntakeActivity,
                        "File imported and sealed: ${result.id}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Return result
                    val resultIntent = Intent().apply {
                        putExtra(RESULT_EVIDENCE_ID, result.id)
                        putExtra(EXTRA_CASE_ID, caseId)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    binding.tvStatus.text = "Failed to add evidence"
                    Toast.makeText(
                        this@FileIntakeActivity,
                        "Failed to import file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnPickDocument.isEnabled = true
                binding.btnPickImage.isEnabled = true
                binding.tvStatus.text = "Error: ${e.message}"
                
                Toast.makeText(
                    this@FileIntakeActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var result = "IMPORTED_FILE"
        
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex)
                }
            }
        }
        
        return result
    }
}
