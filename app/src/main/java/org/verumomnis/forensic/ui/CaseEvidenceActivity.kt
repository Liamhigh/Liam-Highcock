package org.verumomnis.forensic.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.verumomnis.forensic.R
import org.verumomnis.forensic.engine.CaseRepository
import org.verumomnis.forensic.model.Case
import org.verumomnis.forensic.model.Evidence
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Case Evidence Activity
 * Allows adding text and file evidence to a case
 */
class CaseEvidenceActivity : AppCompatActivity() {
    
    private lateinit var repository: CaseRepository
    private lateinit var evidenceAdapter: SimpleEvidenceAdapter
    private var currentCase: Case? = null
    private var caseId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_evidence)
        
        repository = CaseRepository(this)
        caseId = intent.getStringExtra("caseId")
        
        if (caseId == null) {
            Toast.makeText(this, "No case specified", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupUI()
        loadCase()
    }
    
    private fun setupUI() {
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }
        
        findViewById<View>(R.id.btnAddTextEvidence)?.setOnClickListener {
            showAddTextEvidenceDialog()
        }
        
        evidenceAdapter = SimpleEvidenceAdapter { evidence ->
            showEvidenceDetails(evidence)
        }
        
        findViewById<RecyclerView>(R.id.rvEvidenceSimple)?.apply {
            layoutManager = LinearLayoutManager(this@CaseEvidenceActivity)
            adapter = evidenceAdapter
        }
    }
    
    private fun loadCase() {
        currentCase = repository.loadCase(caseId!!)
        
        if (currentCase == null) {
            Toast.makeText(this, "Case not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        updateUI()
    }
    
    private fun updateUI() {
        val case = currentCase ?: return
        
        findViewById<android.widget.TextView>(R.id.tvCaseNameHeader)?.text = case.caseName
        findViewById<android.widget.TextView>(R.id.tvCaseIdHeader)?.text = "ID: ${case.caseId}"
        findViewById<android.widget.TextView>(R.id.tvEvidenceCountHeader)?.text = 
            "${case.evidence.size} evidence item(s)"
        
        evidenceAdapter.updateEvidence(case.evidence)
        
        val emptyView = findViewById<View>(R.id.tvNoEvidence)
        val recyclerView = findViewById<RecyclerView>(R.id.rvEvidenceSimple)
        
        if (case.evidence.isEmpty()) {
            emptyView?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        } else {
            emptyView?.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE
        }
    }
    
    private fun showAddTextEvidenceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_text_evidence, null)
        val summaryInput = dialogView.findViewById<android.widget.EditText>(R.id.etEvidenceSummary)
        val contentInput = dialogView.findViewById<android.widget.EditText>(R.id.etEvidenceContent)
        
        AlertDialog.Builder(this)
            .setTitle("Add Text Evidence")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val summary = summaryInput.text.toString().trim()
                val content = contentInput.text.toString().trim()
                
                if (summary.isNotEmpty() && content.isNotEmpty()) {
                    addTextEvidence(summary, content)
                } else {
                    Toast.makeText(this, "Both fields required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun addTextEvidence(summary: String, content: String) {
        val case = currentCase ?: return
        
        val evidence = Evidence.createText(content, summary)
        case.evidence.add(evidence)
        
        repository.saveCase(case)
        updateUI()
        
        Toast.makeText(this, "Evidence added", Toast.LENGTH_SHORT).show()
    }
    
    private fun showEvidenceDetails(evidence: Evidence) {
        val details = """
            ID: ${evidence.evidenceId}
            Type: ${evidence.type}
            Summary: ${evidence.summary}
            
            Content:
            ${evidence.content}
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("Evidence Details")
            .setMessage(details)
            .setPositiveButton("OK", null)
            .show()
    }
}

/**
 * Simple adapter for evidence list
 */
class SimpleEvidenceAdapter(
    private val onEvidenceClick: (Evidence) -> Unit
) : RecyclerView.Adapter<SimpleEvidenceAdapter.ViewHolder>() {
    
    private var evidence: List<Evidence> = emptyList()
    private val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.US)
    
    fun updateEvidence(newEvidence: List<Evidence>) {
        evidence = newEvidence.sortedByDescending { it.timestamp }
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_simple_evidence, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(evidence[position])
    }
    
    override fun getItemCount() = evidence.size
    
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvEvidenceId: android.widget.TextView = view.findViewById(R.id.tvEvidenceIdSimple)
        private val tvSummary: android.widget.TextView = view.findViewById(R.id.tvEvidenceSummary)
        private val tvType: android.widget.TextView = view.findViewById(R.id.tvEvidenceType)
        private val tvTimestamp: android.widget.TextView = view.findViewById(R.id.tvEvidenceTimestamp)
        
        fun bind(evidence: Evidence) {
            tvEvidenceId.text = evidence.evidenceId
            tvSummary.text = evidence.summary
            tvType.text = evidence.type.uppercase()
            tvTimestamp.text = dateFormat.format(Date(evidence.timestamp))
            
            itemView.setOnClickListener { onEvidenceClick(evidence) }
        }
    }
}
