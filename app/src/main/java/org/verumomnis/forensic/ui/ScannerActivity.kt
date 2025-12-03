package org.verumomnis.forensic.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import org.verumomnis.forensic.R
import org.verumomnis.forensic.core.EvidenceMetadata
import org.verumomnis.forensic.core.EvidenceType
import org.verumomnis.forensic.core.ForensicEvidence
import org.verumomnis.forensic.core.VerumOmnisApplication
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter

/**
 * Scanner Activity for evidence collection
 * 
 * Features:
 * - Capture photos as evidence
 * - Add text notes
 * - View collected evidence
 * - Generate forensic reports
 */
class ScannerActivity : AppCompatActivity() {

    private lateinit var textCaseName: TextView
    private lateinit var recyclerViewEvidence: RecyclerView
    private lateinit var fabAddEvidence: FloatingActionButton
    private lateinit var btnGenerateReport: Button
    private lateinit var textEvidenceCount: TextView
    
    private val forensicEngine by lazy { 
        VerumOmnisApplication.getInstance().forensicEngine 
    }
    
    private var caseId: String? = null
    private val evidenceAdapter = EvidenceAdapter()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { capturePhoto(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        
        caseId = intent.getStringExtra(MainActivity.EXTRA_CASE_ID)
        
        if (caseId == null) {
            Toast.makeText(this, "No case selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupViews()
        refreshEvidence()
    }

    private fun setupViews() {
        textCaseName = findViewById(R.id.textCaseName)
        recyclerViewEvidence = findViewById(R.id.recyclerViewEvidence)
        fabAddEvidence = findViewById(R.id.fabAddEvidence)
        btnGenerateReport = findViewById(R.id.btnGenerateReport)
        textEvidenceCount = findViewById(R.id.textEvidenceCount)
        
        recyclerViewEvidence.layoutManager = LinearLayoutManager(this)
        recyclerViewEvidence.adapter = evidenceAdapter
        
        fabAddEvidence.setOnClickListener {
            showAddEvidenceDialog()
        }
        
        btnGenerateReport.setOnClickListener {
            generateReport()
        }
        
        forensicEngine.getCase(caseId!!)?.let { case ->
            textCaseName.text = case.name
        }
    }

    private fun showAddEvidenceDialog() {
        val options = arrayOf("Take Photo", "Add Text Note", "Scan Document")
        
        AlertDialog.Builder(this)
            .setTitle("Add Evidence")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermission()
                    1 -> showAddTextNoteDialog()
                    2 -> showScanDocumentDialog()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        cameraLauncher.launch(null)
    }

    private fun capturePhoto(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        val photoBytes = stream.toByteArray()
        
        showDescriptionDialog("Photo Evidence") { description ->
            addEvidence(EvidenceType.PHOTO, photoBytes, description)
        }
    }

    private fun showAddTextNoteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_text_note, null)
        val editNote = dialogView.findViewById<EditText>(R.id.editTextNote)
        
        AlertDialog.Builder(this)
            .setTitle("Add Text Note")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val note = editNote.text.toString().trim()
                if (note.isNotBlank()) {
                    addEvidence(
                        EvidenceType.TEXT,
                        note.toByteArray(Charsets.UTF_8),
                        note.take(100)
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showScanDocumentDialog() {
        // For now, use camera for document scanning
        showDescriptionDialog("Document Scan") { description ->
            requestCameraPermission()
        }
    }

    private fun showDescriptionDialog(defaultDescription: String, onConfirm: (String) -> Unit) {
        val editText = EditText(this).apply {
            hint = "Evidence description"
            setText(defaultDescription)
        }
        
        AlertDialog.Builder(this)
            .setTitle("Describe Evidence")
            .setView(editText)
            .setPositiveButton("Confirm") { _, _ ->
                onConfirm(editText.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addEvidence(type: EvidenceType, content: ByteArray, description: String) {
        lifecycleScope.launch {
            val evidence = forensicEngine.addEvidence(
                caseId = caseId!!,
                type = type,
                content = content,
                contentDescription = description,
                metadata = EvidenceMetadata(
                    source = "Android Scanner",
                    capturedBy = "Verum Omnis Forensic Engine",
                    deviceInfo = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
                )
            )
            
            if (evidence != null) {
                Toast.makeText(
                    this@ScannerActivity,
                    "Evidence added and sealed",
                    Toast.LENGTH_SHORT
                ).show()
                refreshEvidence()
            } else {
                Toast.makeText(
                    this@ScannerActivity,
                    "Failed to add evidence",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun refreshEvidence() {
        forensicEngine.getCase(caseId!!)?.let { case ->
            evidenceAdapter.submitList(case.evidence)
            textEvidenceCount.text = "${case.evidence.size} evidence items collected"
        }
    }

    private fun generateReport() {
        lifecycleScope.launch {
            Toast.makeText(
                this@ScannerActivity,
                "Generating forensic report...",
                Toast.LENGTH_SHORT
            ).show()
            
            val result = forensicEngine.generateReport(caseId!!)
            
            if (result != null) {
                val intent = Intent(this@ScannerActivity, ReportViewerActivity::class.java).apply {
                    putExtra(EXTRA_REPORT_PATH, result.pdfReportPath)
                    putExtra(EXTRA_INTEGRITY_SCORE, result.integrityScore)
                }
                startActivity(intent)
            } else {
                Toast.makeText(
                    this@ScannerActivity,
                    "Failed to generate report",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        const val EXTRA_REPORT_PATH = "extra_report_path"
        const val EXTRA_INTEGRITY_SCORE = "extra_integrity_score"
    }

    /**
     * RecyclerView Adapter for evidence
     */
    inner class EvidenceAdapter : RecyclerView.Adapter<EvidenceAdapter.EvidenceViewHolder>() {

        private var evidenceList: List<ForensicEvidence> = emptyList()

        fun submitList(newList: List<ForensicEvidence>) {
            evidenceList = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvidenceViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_evidence, parent, false)
            return EvidenceViewHolder(view)
        }

        override fun onBindViewHolder(holder: EvidenceViewHolder, position: Int) {
            holder.bind(evidenceList[position])
        }

        override fun getItemCount(): Int = evidenceList.size

        inner class EvidenceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textType: TextView = itemView.findViewById(R.id.textEvidenceType)
            private val textDescription: TextView = itemView.findViewById(R.id.textEvidenceDescription)
            private val textTimestamp: TextView = itemView.findViewById(R.id.textEvidenceTimestamp)
            private val textHash: TextView = itemView.findViewById(R.id.textEvidenceHash)

            fun bind(evidence: ForensicEvidence) {
                textType.text = evidence.type.name
                textDescription.text = evidence.contentDescription
                textTimestamp.text = evidence.timestamp.format(dateFormatter)
                textHash.text = "SHA-512: ${evidence.hash.take(16)}..."
            }
        }
    }
}
