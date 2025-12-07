package org.verumomnis.forensic.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.verumomnis.forensic.R
import org.verumomnis.forensic.engine.CaseRepository
import org.verumomnis.forensic.engine.EngineOrchestrator
import org.verumomnis.forensic.model.Case
import org.verumomnis.forensic.model.Evidence
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Contradiction Engine Activity
 * Handles case creation, evidence import, and contradiction analysis
 */
class ContradictionEngineActivity : AppCompatActivity() {
    
    private lateinit var repository: CaseRepository
    private lateinit var orchestrator: EngineOrchestrator
    private lateinit var caseListAdapter: SimpleCaseAdapter
    private val cases = mutableListOf<Case>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contradiction_engine)
        
        repository = CaseRepository(this)
        orchestrator = EngineOrchestrator(this)
        
        setupUI()
        loadCases()
    }
    
    private fun setupUI() {
        // Create case button
        findViewById<View>(R.id.btnCreateCaseSimple)?.setOnClickListener {
            showCreateCaseDialog()
        }
        
        // Setup RecyclerView
        caseListAdapter = SimpleCaseAdapter(
            onCaseClick = { case -> openCaseDetails(case) },
            onRunEngine = { case -> runEngineOnCase(case) }
        )
        
        findViewById<RecyclerView>(R.id.rvCasesSimple)?.apply {
            layoutManager = LinearLayoutManager(this@ContradictionEngineActivity)
            adapter = caseListAdapter
        }
    }
    
    private fun loadCases() {
        lifecycleScope.launch {
            val loadedCases = withContext(Dispatchers.IO) {
                repository.getAllCases()
            }
            
            cases.clear()
            cases.addAll(loadedCases)
            caseListAdapter.updateCases(cases)
            
            updateEmptyState()
        }
    }
    
    private fun updateEmptyState() {
        val emptyView = findViewById<View>(R.id.tvEmptyState)
        val recyclerView = findViewById<RecyclerView>(R.id.rvCasesSimple)
        
        if (cases.isEmpty()) {
            emptyView?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        } else {
            emptyView?.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE
        }
    }
    
    private fun showCreateCaseDialog() {
        val input = android.widget.EditText(this)
        input.hint = "Case name"
        
        AlertDialog.Builder(this)
            .setTitle("Create New Case")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    createCase(name)
                } else {
                    Toast.makeText(this, "Case name required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun createCase(name: String) {
        val newCase = Case.create(name)
        repository.saveCase(newCase)
        cases.add(newCase)
        caseListAdapter.updateCases(cases)
        updateEmptyState()
        
        Toast.makeText(this, "Case created: ${newCase.caseId}", Toast.LENGTH_SHORT).show()
        
        // Open case details to add evidence
        openCaseDetails(newCase)
    }
    
    private fun openCaseDetails(case: Case) {
        val intent = Intent(this, CaseEvidenceActivity::class.java)
        intent.putExtra("caseId", case.caseId)
        startActivityForResult(intent, REQUEST_CASE_DETAILS)
    }
    
    private fun runEngineOnCase(case: Case) {
        if (case.evidence.isEmpty()) {
            Toast.makeText(this, "Add evidence first", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            val report = withContext(Dispatchers.IO) {
                orchestrator.run(case)
            }
            
            val intent = Intent(this@ContradictionEngineActivity, ReportViewerActivity::class.java)
            intent.putExtra("reportText", report)
            startActivity(intent)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CASE_DETAILS) {
            loadCases() // Refresh case list
        }
    }
    
    companion object {
        private const val REQUEST_CASE_DETAILS = 1001
    }
}

/**
 * Simple adapter for case list
 */
class SimpleCaseAdapter(
    private val onCaseClick: (Case) -> Unit,
    private val onRunEngine: (Case) -> Unit
) : RecyclerView.Adapter<SimpleCaseAdapter.ViewHolder>() {
    
    private var cases: List<Case> = emptyList()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    
    fun updateCases(newCases: List<Case>) {
        cases = newCases.sortedByDescending { it.createdAt }
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_simple_case, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cases[position])
    }
    
    override fun getItemCount() = cases.size
    
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvCaseName: android.widget.TextView = view.findViewById(R.id.tvSimpleCaseName)
        private val tvCaseId: android.widget.TextView = view.findViewById(R.id.tvSimpleCaseId)
        private val tvEvidenceCount: android.widget.TextView = view.findViewById(R.id.tvSimpleEvidenceCount)
        private val tvCreatedAt: android.widget.TextView = view.findViewById(R.id.tvSimpleCreatedAt)
        private val btnRunEngine: android.widget.Button = view.findViewById(R.id.btnSimpleRunEngine)
        
        fun bind(case: Case) {
            tvCaseName.text = case.caseName
            tvCaseId.text = "ID: ${case.caseId}"
            tvEvidenceCount.text = "${case.evidence.size} evidence item(s)"
            tvCreatedAt.text = dateFormat.format(Date(case.createdAt))
            
            itemView.setOnClickListener { onCaseClick(case) }
            btnRunEngine.setOnClickListener { onRunEngine(case) }
            btnRunEngine.isEnabled = case.evidence.isNotEmpty()
        }
    }
}
