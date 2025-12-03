package org.verumomnis.forensic.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.verumomnis.forensic.R
import org.verumomnis.forensic.core.EvidenceType
import org.verumomnis.forensic.core.ForensicCase
import org.verumomnis.forensic.core.ForensicEngine
import org.verumomnis.forensic.core.ForensicEvidence
import org.verumomnis.forensic.databinding.ActivityCaseDetailBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Case Detail Activity
 * 
 * Displays case details and allows adding evidence
 * Easy-to-use interface for SAPS officers
 * 
 * @author Liam Highcock
 */
class CaseDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CASE_ID = "extra_case_id"
    }

    private lateinit var binding: ActivityCaseDetailBinding
    private lateinit var forensicEngine: ForensicEngine
    private lateinit var evidenceAdapter: EvidenceAdapter
    private var currentCase: ForensicCase? = null
    private var caseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        caseId = intent.getStringExtra(EXTRA_CASE_ID)
        if (caseId == null) {
            Toast.makeText(this, "Invalid case", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        forensicEngine = ForensicEngine(this)
        
        setupToolbar()
        setupRecyclerView()
        setupButtons()
        loadCase()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        evidenceAdapter = EvidenceAdapter()
        binding.recyclerViewEvidence.apply {
            layoutManager = LinearLayoutManager(this@CaseDetailActivity)
            adapter = evidenceAdapter
        }
    }

    private fun setupButtons() {
        // Add Document Evidence
        binding.btnAddDocument.setOnClickListener {
            showAddEvidenceDialog(EvidenceType.DOCUMENT)
        }

        // Add Photo Evidence
        binding.btnAddPhoto.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java).apply {
                putExtra(ScannerActivity.EXTRA_CASE_ID, caseId)
            }
            startActivity(intent)
        }

        // Add Text Note
        binding.btnAddNote.setOnClickListener {
            showAddEvidenceDialog(EvidenceType.TEXT_NOTE)
        }

        // Generate Report
        binding.btnGenerateReport.setOnClickListener {
            generateReport()
        }
    }

    private fun loadCase() {
        currentCase = forensicEngine.getCase(caseId!!)
        currentCase?.let { case ->
            supportActionBar?.title = case.caseNumber
            binding.textCaseDescription.text = case.description
            binding.textCaseStatus.text = "Status: ${case.status}"
            binding.textEvidenceCount.text = "Evidence Items: ${case.evidence.size}"
            
            evidenceAdapter.submitList(case.evidence)
            
            binding.textNoEvidence.visibility = 
                if (case.evidence.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewEvidence.visibility = 
                if (case.evidence.isEmpty()) View.GONE else View.VISIBLE
                
            // Enable report generation only if there's evidence
            binding.btnGenerateReport.isEnabled = case.evidence.isNotEmpty()
        } ?: run {
            Toast.makeText(this, "Case not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showAddEvidenceDialog(type: EvidenceType) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
        }

        val descriptionInput = EditText(this).apply {
            hint = when (type) {
                EvidenceType.DOCUMENT -> "Document description"
                EvidenceType.TEXT_NOTE -> "Enter your notes"
                else -> "Evidence description"
            }
            if (type == EvidenceType.TEXT_NOTE) {
                minLines = 4
            }
        }

        layout.addView(descriptionInput)

        AlertDialog.Builder(this)
            .setTitle("Add ${type.name.replace("_", " ")}")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val description = descriptionInput.text.toString().trim()
                if (description.isNotEmpty()) {
                    addEvidence(type, description)
                } else {
                    Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addEvidence(type: EvidenceType, description: String) {
        lifecycleScope.launch {
            val evidence = forensicEngine.addEvidence(
                caseId = caseId!!,
                type = type,
                description = description
            )
            
            if (evidence != null) {
                loadCase()
                Toast.makeText(
                    this@CaseDetailActivity, 
                    "Evidence added with GPS location", 
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@CaseDetailActivity, 
                    "Failed to add evidence", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun generateReport() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnGenerateReport.isEnabled = false

        lifecycleScope.launch {
            try {
                val result = forensicEngine.generateReport(caseId!!)
                
                binding.progressBar.visibility = View.GONE
                binding.btnGenerateReport.isEnabled = true
                
                if (result != null) {
                    showReportGeneratedDialog(result.reportPath)
                } else {
                    Toast.makeText(
                        this@CaseDetailActivity, 
                        "Failed to generate report", 
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnGenerateReport.isEnabled = true
                Toast.makeText(
                    this@CaseDetailActivity, 
                    "Error: ${e.message}", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showReportGeneratedDialog(reportPath: String) {
        AlertDialog.Builder(this)
            .setTitle("Report Generated")
            .setMessage("Forensic report has been generated successfully.\n\nPath: $reportPath")
            .setPositiveButton("View Report") { _, _ ->
                openReport(reportPath)
            }
            .setNeutralButton("Share") { _, _ ->
                shareReport(reportPath)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun openReport(reportPath: String) {
        val intent = Intent(this, ReportViewerActivity::class.java).apply {
            putExtra(ReportViewerActivity.EXTRA_REPORT_PATH, reportPath)
        }
        startActivity(intent)
    }

    private fun shareReport(reportPath: String) {
        val file = File(reportPath)
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Report"))
    }

    override fun onResume() {
        super.onResume()
        loadCase()
    }
}

/**
 * RecyclerView Adapter for displaying evidence items
 */
class EvidenceAdapter : RecyclerView.Adapter<EvidenceAdapter.EvidenceViewHolder>() {

    private var evidence: List<ForensicEvidence> = emptyList()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

    fun submitList(newEvidence: List<ForensicEvidence>) {
        evidence = newEvidence
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvidenceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evidence, parent, false)
        return EvidenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: EvidenceViewHolder, position: Int) {
        holder.bind(evidence[position])
    }

    override fun getItemCount(): Int = evidence.size

    inner class EvidenceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ForensicEvidence) {
            itemView.findViewById<android.widget.TextView>(R.id.textEvidenceType).text = 
                item.type.name.replace("_", " ")
            itemView.findViewById<android.widget.TextView>(R.id.textEvidenceDescription).text = 
                item.description
            itemView.findViewById<android.widget.TextView>(R.id.textEvidenceTimestamp).text = 
                dateFormat.format(Date(item.capturedAt))
            
            val locationText = if (item.latitude != null && item.longitude != null) {
                "📍 %.4f, %.4f".format(item.latitude, item.longitude)
            } else {
                "📍 No GPS data"
            }
            itemView.findViewById<android.widget.TextView>(R.id.textEvidenceLocation).text = locationText
            
            val hashText = item.contentHash?.take(16) ?: "—"
            itemView.findViewById<android.widget.TextView>(R.id.textEvidenceHash).text = 
                "Hash: $hashText..."
        }
    }
}
