package org.verumomnis.forensic.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.verumomnis.forensic.R
import org.verumomnis.forensic.core.*
import org.verumomnis.forensic.databinding.ActivityCaseDetailBinding
import org.verumomnis.forensic.leveler.LevelerEngine
import java.text.SimpleDateFormat
import java.util.*

/**
 * Case Detail Activity
 * 
 * Displays detailed case information:
 * - Case metadata
 * - Evidence list
 * - Add evidence options
 * - Leveler analysis results
 * - Generate report option
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
class CaseDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CASE_ID = "caseId"
    }

    private lateinit var binding: ActivityCaseDetailBinding
    private lateinit var forensicEngine: ForensicEngine
    private lateinit var evidenceAdapter: EvidenceAdapter
    
    private var caseId: String? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    // Scanner launcher
    private val scannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            refreshCaseDetails()
        }
    }

    // File intake launcher
    private val fileIntakeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            refreshCaseDetails()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        caseId = intent.getStringExtra(EXTRA_CASE_ID)

        if (caseId == null) {
            Toast.makeText(this, "No case specified", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        forensicEngine = ForensicEngine(this)
        setupUI()
        refreshCaseDetails()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAddDocument.setOnClickListener {
            openScanner(EvidenceType.DOCUMENT)
        }

        binding.btnAddPhoto.setOnClickListener {
            openScanner(EvidenceType.PHOTO)
        }

        binding.btnAddNote.setOnClickListener {
            showAddNoteDialog()
        }

        binding.btnImportFile.setOnClickListener {
            openFileIntake()
        }

        binding.btnGenerateReport.setOnClickListener {
            generateReport()
        }

        binding.btnRunAnalysis.setOnClickListener {
            runLevelerAnalysis()
        }

        binding.btnSealCase.setOnClickListener {
            sealCase()
        }

        // Setup evidence RecyclerView
        evidenceAdapter = EvidenceAdapter { evidence ->
            showEvidenceDetails(evidence)
        }
        binding.rvEvidence.layoutManager = LinearLayoutManager(this)
        binding.rvEvidence.adapter = evidenceAdapter
    }

    private fun openFileIntake() {
        val intent = Intent(this, FileIntakeActivity::class.java).apply {
            putExtra(FileIntakeActivity.EXTRA_CASE_ID, caseId)
        }
        fileIntakeLauncher.launch(intent)
    }

    private fun refreshCaseDetails() {
        val forensicCase = forensicEngine.getCase(caseId!!)

        if (forensicCase == null) {
            Toast.makeText(this, "Case not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Case header
        binding.tvCaseId.text = forensicCase.id
        binding.tvCaseName.text = forensicCase.name
        binding.tvStatus.text = "Status: ${forensicCase.status.name}"
        binding.tvCreated.text = "Created: ${dateFormat.format(Date(forensicCase.createdAt))}"
        binding.tvModified.text = "Modified: ${dateFormat.format(Date(forensicCase.modifiedAt))}"

        // Description
        binding.tvDescription.text = forensicCase.description.ifEmpty { "No description" }

        // Evidence count
        binding.tvEvidenceCount.text = "${forensicCase.evidence.size} Evidence Item(s)"

        // Evidence list
        evidenceAdapter.updateEvidence(forensicCase.evidence)
        binding.emptyEvidence.visibility = 
            if (forensicCase.evidence.isEmpty()) View.VISIBLE else View.GONE
        binding.rvEvidence.visibility = 
            if (forensicCase.evidence.isEmpty()) View.GONE else View.VISIBLE

        // Integrity hash
        binding.tvIntegrityHash.text = forensicCase.integrityHash?.let {
            "Integrity: ${it.take(32)}..."
        } ?: "Not sealed"

        // Enable/disable buttons based on case status
        val isOpen = forensicCase.status == CaseStatus.OPEN
        binding.btnAddDocument.isEnabled = isOpen
        binding.btnAddPhoto.isEnabled = isOpen
        binding.btnAddNote.isEnabled = isOpen
        binding.btnImportFile.isEnabled = isOpen
        binding.btnSealCase.isEnabled = isOpen && forensicCase.evidence.isNotEmpty()
        binding.btnGenerateReport.isEnabled = forensicCase.evidence.isNotEmpty()
        binding.btnExportCase.isEnabled = forensicCase.evidence.isNotEmpty()
    }

    private fun openScanner(type: EvidenceType) {
        val intent = Intent(this, ScannerActivity::class.java).apply {
            putExtra(ScannerActivity.EXTRA_CASE_ID, caseId)
            putExtra(ScannerActivity.EXTRA_EVIDENCE_TYPE, type)
        }
        scannerLauncher.launch(intent)
    }

    private fun openAudioRecorder() {
        val intent = Intent(this, AudioRecorderActivity::class.java).apply {
            putExtra(AudioRecorderActivity.EXTRA_CASE_ID, caseId)
        }
        audioRecorderLauncher.launch(intent)
    }

    private fun openVideoRecorder() {
        val intent = Intent(this, VideoRecorderActivity::class.java).apply {
            putExtra(VideoRecorderActivity.EXTRA_CASE_ID, caseId)
        }
        videoRecorderLauncher.launch(intent)
    }

    private fun exportCase() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val exportFile = forensicEngine.exportCase(caseId!!)
                
                binding.progressBar.visibility = View.GONE
                
                if (exportFile != null) {
                    Toast.makeText(
                        this@CaseDetailActivity,
                        "Case exported to: ${exportFile.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@CaseDetailActivity,
                        "Failed to export case",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@CaseDetailActivity,
                    "Export error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showAddNoteDialog() {
        val noteInput = android.widget.EditText(this).apply {
            hint = "Enter text note..."
            minLines = 3
            gravity = android.view.Gravity.TOP
        }

        AlertDialog.Builder(this)
            .setTitle("Add Text Note")
            .setView(noteInput)
            .setPositiveButton("Add") { _, _ ->
                val noteText = noteInput.text.toString().trim()
                if (noteText.isNotEmpty()) {
                    addTextNote(noteText)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addTextNote(text: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                
                val evidence = withContext(Dispatchers.IO) {
                    forensicEngine.addEvidence(
                        caseId = caseId!!,
                        type = EvidenceType.TEXT,
                        content = text.toByteArray(Charsets.UTF_8),
                        mimeType = "text/plain",
                        filename = "NOTE_$timestamp.txt"
                    )
                }

                if (evidence != null) {
                    withContext(Dispatchers.IO) {
                        forensicEngine.sealEvidence(caseId!!, evidence.id)
                    }
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@CaseDetailActivity, "Note added: ${evidence.id}", Toast.LENGTH_SHORT).show()
                    refreshCaseDetails()
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@CaseDetailActivity, "Failed to add note", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@CaseDetailActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sealCase() {
        AlertDialog.Builder(this)
            .setTitle("Seal Case")
            .setMessage(
                "Sealing the case will:\n\n" +
                "• Lock all evidence (no more additions)\n" +
                "• Generate integrity hash\n" +
                "• Enable report generation\n\n" +
                "This action cannot be undone."
            )
            .setPositiveButton("Seal Case") { _, _ ->
                binding.progressBar.visibility = View.VISIBLE
                
                lifecycleScope.launch {
                    try {
                        val sealedCase = withContext(Dispatchers.IO) {
                            forensicEngine.sealCase(caseId!!)
                        }
                        
                        binding.progressBar.visibility = View.GONE
                        
                        if (sealedCase != null) {
                            Toast.makeText(this@CaseDetailActivity, "Case sealed successfully", Toast.LENGTH_SHORT).show()
                            refreshCaseDetails()
                        } else {
                            Toast.makeText(this@CaseDetailActivity, "Failed to seal case", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@CaseDetailActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun generateReport() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val report = withContext(Dispatchers.IO) {
                    forensicEngine.generateReport(caseId!!)
                }

                binding.progressBar.visibility = View.GONE

                if (report != null) {
                    openReportViewer(report)
                } else {
                    Toast.makeText(
                        this@CaseDetailActivity,
                        "Failed to generate report",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@CaseDetailActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openReportViewer(report: ForensicReport) {
        val intent = Intent(this, ReportViewerActivity::class.java).apply {
            putExtra(ReportViewerActivity.EXTRA_REPORT_ID, report.id)
            putExtra(ReportViewerActivity.EXTRA_CASE_ID, report.caseId)
        }
        startActivity(intent)
    }

    private fun runLevelerAnalysis() {
        val forensicCase = forensicEngine.getCase(caseId!!) ?: return

        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.Default) {
                    // Create statements from evidence (for demo purposes)
                    val statements = forensicCase.evidence.mapIndexed { index, ev ->
                        LevelerEngine.Statement(
                            id = ev.id,
                            speaker = "Evidence",
                            content = ev.metadata.filename ?: "Evidence $index",
                            timestamp = ev.timestamp,
                            source = ev.id
                        )
                    }

                    LevelerEngine.analyze(
                        statements = statements,
                        evidence = forensicCase.evidence
                    )
                }

                binding.progressBar.visibility = View.GONE
                showLevelerResults(result)
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@CaseDetailActivity,
                    "Analysis error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showLevelerResults(result: LevelerEngine.LevelerResult) {
        val message = buildString {
            appendLine("LEVELER ANALYSIS RESULTS")
            appendLine("========================")
            appendLine()
            appendLine("Integrity Score: ${result.integrityScore.toInt()}/100")
            appendLine("Confidence: ${(result.confidence * 100).toInt()}%")
            appendLine()
            appendLine("Contradictions: ${result.contradictions.size}")
            appendLine("Timeline Anomalies: ${result.timelineAnomalies.size}")
            appendLine("Missing Evidence: ${result.missingEvidence.size}")
            appendLine("Behavioral Patterns: ${result.behavioralPatterns.size}")
            appendLine()
            appendLine("RECOMMENDATIONS:")
            result.recommendations.forEach {
                appendLine("• $it")
            }
        }

        AlertDialog.Builder(this)
            .setTitle("B1-B9 Analysis Complete")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showEvidenceDetails(evidence: ForensicEvidence) {
        val details = buildString {
            appendLine("ID: ${evidence.id}")
            appendLine("Type: ${evidence.type.name}")
            appendLine("Filename: ${evidence.metadata.filename ?: "N/A"}")
            appendLine("Size: ${formatFileSize(evidence.metadata.fileSize)}")
            appendLine("MIME: ${evidence.mimeType}")
            appendLine()
            appendLine("Captured: ${dateFormat.format(Date(evidence.timestamp))}")
            appendLine("Device: ${evidence.metadata.deviceInfo}")
            appendLine()
            appendLine("Sealed: ${if (evidence.sealed) "Yes ✓" else "No"}")
            appendLine()
            evidence.location?.let { loc ->
                appendLine("Location: ${loc.latitude}, ${loc.longitude}")
                appendLine("Accuracy: ±${loc.accuracy}m")
            } ?: appendLine("Location: Not captured")
            appendLine()
            appendLine("Content Hash:")
            appendLine(evidence.contentHash.take(64))
            appendLine(evidence.contentHash.drop(64))
            if (evidence.sealHash != null) {
                appendLine()
                appendLine("Seal Hash:")
                appendLine(evidence.sealHash.take(64))
                appendLine(evidence.sealHash.drop(64))
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Evidence Details")
            .setMessage(details)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }

    override fun onResume() {
        super.onResume()
        refreshCaseDetails()
    }
}

/**
 * Adapter for evidence list
 */
class EvidenceAdapter(
    private val onEvidenceClick: (ForensicEvidence) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<EvidenceAdapter.EvidenceViewHolder>() {

    private var evidence: List<ForensicEvidence> = emptyList()
    private val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.US)

    fun updateEvidence(newEvidence: List<ForensicEvidence>) {
        evidence = newEvidence.sortedByDescending { it.timestamp }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): EvidenceViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evidence, parent, false)
        return EvidenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: EvidenceViewHolder, position: Int) {
        holder.bind(evidence[position])
    }

    override fun getItemCount() = evidence.size

    inner class EvidenceViewHolder(view: View) : 
        androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        
        private val tvEvidenceId: android.widget.TextView = view.findViewById(R.id.tvEvidenceId)
        private val tvType: android.widget.TextView = view.findViewById(R.id.tvType)
        private val tvFilename: android.widget.TextView = view.findViewById(R.id.tvFilename)
        private val tvTimestamp: android.widget.TextView = view.findViewById(R.id.tvTimestamp)
        private val tvSealed: android.widget.TextView = view.findViewById(R.id.tvSealed)

        fun bind(evidence: ForensicEvidence) {
            tvEvidenceId.text = evidence.id
            tvType.text = evidence.type.name
            tvFilename.text = evidence.metadata.filename ?: "Unnamed"
            tvTimestamp.text = dateFormat.format(Date(evidence.timestamp))
            tvSealed.text = if (evidence.sealed) "SEALED ✓" else "UNSEALED"
            tvSealed.setTextColor(
                if (evidence.sealed) 
                    android.graphics.Color.parseColor("#4CAF50")
                else 
                    android.graphics.Color.parseColor("#FF9800")
            )

            itemView.setOnClickListener { onEvidenceClick(evidence) }
        }
    }
}
