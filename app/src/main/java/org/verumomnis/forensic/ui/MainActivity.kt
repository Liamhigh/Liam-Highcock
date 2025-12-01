package org.verumomnis.forensic.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.verumomnis.forensic.R
import org.verumomnis.forensic.core.*
import org.verumomnis.forensic.databinding.ActivityMainBinding
import org.verumomnis.forensic.location.ForensicLocationService
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main Activity - Forensic Case Management
 * 
 * Primary interface for:
 * - Creating forensic cases
 * - Adding evidence
 * - Viewing case status
 * - Generating reports
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var forensicEngine: ForensicEngine
    private lateinit var locationService: ForensicLocationService
    private lateinit var caseAdapter: CaseAdapter

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            onPermissionsGranted()
        } else {
            showPermissionRationale()
        }
    }

    // Scanner result launcher
    private val scannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { handleScannerResult(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeEngine()
        setupUI()
        checkPermissions()
    }

    private fun initializeEngine() {
        forensicEngine = ForensicEngine(this)
        locationService = ForensicLocationService(this)
    }

    private fun setupUI() {
        // App branding
        binding.tvAppName.text = "VERUM OMNIS"
        binding.tvTagline.text = "AI FORENSICS FOR TRUTH"
        binding.tvVersion.text = "v${VerumOmnisApplication.VERSION}"
        binding.tvConstitution.text = "Constitutional Governance: ACTIVE"

        // Create case button
        binding.btnCreateCase.setOnClickListener {
            showCreateCaseDialog()
        }

        // Setup RecyclerView for cases
        caseAdapter = CaseAdapter(
            onCaseClick = { caseId -> openCaseDetail(caseId) },
            onGenerateReport = { caseId -> generateReport(caseId) }
        )
        binding.rvCases.layoutManager = LinearLayoutManager(this)
        binding.rvCases.adapter = caseAdapter

        // Refresh cases
        refreshCaseList()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val permissionsNeeded = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNeeded.isNotEmpty()) {
            permissionLauncher.launch(permissionsNeeded.toTypedArray())
        }
    }

    private fun onPermissionsGranted() {
        Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage(
                "Verum Omnis Forensic Engine requires:\n\n" +
                "• Camera: To capture document evidence\n" +
                "• Location: To geotag evidence\n\n" +
                "All data stays on your device."
            )
            .setPositiveButton("Grant") { _, _ -> checkPermissions() }
            .setNegativeButton("Continue Without") { _, _ -> }
            .show()
    }

    private fun showCreateCaseDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_case, null)
        val nameInput = dialogView.findViewById<android.widget.EditText>(R.id.etCaseName)
        val descInput = dialogView.findViewById<android.widget.EditText>(R.id.etCaseDescription)

        AlertDialog.Builder(this)
            .setTitle("Create Forensic Case")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = nameInput.text.toString().trim()
                val description = descInput.text.toString().trim()
                
                if (name.isNotEmpty()) {
                    createCase(name, description)
                } else {
                    Toast.makeText(this, "Case name required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createCase(name: String, description: String) {
        val newCase = forensicEngine.createCase(name, description)
        Toast.makeText(this, "Case created: ${newCase.id}", Toast.LENGTH_SHORT).show()
        refreshCaseList()
    }

    private fun refreshCaseList() {
        val cases = forensicEngine.getAllCases()
        caseAdapter.updateCases(cases)
        
        binding.tvCaseCount.text = "${cases.size} Active Case(s)"
        binding.emptyState.visibility = if (cases.isEmpty()) View.VISIBLE else View.GONE
        binding.rvCases.visibility = if (cases.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun openCaseDetail(caseId: String) {
        val intent = Intent(this, CaseDetailActivity::class.java).apply {
            putExtra(CaseDetailActivity.EXTRA_CASE_ID, caseId)
        }
        startActivity(intent)
    }

    private fun generateReport(caseId: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val report = withContext(Dispatchers.IO) {
                    forensicEngine.generateReport(caseId)
                }
                
                binding.progressBar.visibility = View.GONE
                
                if (report != null) {
                    openReportViewer(report)
                    Toast.makeText(this@MainActivity, "Report generated!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to generate report", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        
        refreshCaseList()
    }

    private fun openReportViewer(report: ForensicReport) {
        val intent = Intent(this, ReportViewerActivity::class.java).apply {
            putExtra(ReportViewerActivity.EXTRA_REPORT_ID, report.id)
            putExtra(ReportViewerActivity.EXTRA_CASE_ID, report.caseId)
        }
        startActivity(intent)
    }

    private fun handleScannerResult(data: Intent) {
        // Handle scanner result
        val caseId = data.getStringExtra("caseId") ?: return
        refreshCaseList()
    }

    override fun onResume() {
        super.onResume()
        refreshCaseList()
    }
}

/**
 * Adapter for displaying forensic cases
 */
class CaseAdapter(
    private val onCaseClick: (String) -> Unit,
    private val onGenerateReport: (String) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<CaseAdapter.CaseViewHolder>() {

    private var cases: List<ForensicCase> = emptyList()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

    fun updateCases(newCases: List<ForensicCase>) {
        cases = newCases.sortedByDescending { it.modifiedAt }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CaseViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_case, parent, false)
        return CaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CaseViewHolder, position: Int) {
        holder.bind(cases[position])
    }

    override fun getItemCount() = cases.size

    inner class CaseViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        private val tvCaseId: android.widget.TextView = view.findViewById(R.id.tvCaseId)
        private val tvCaseName: android.widget.TextView = view.findViewById(R.id.tvCaseName)
        private val tvStatus: android.widget.TextView = view.findViewById(R.id.tvStatus)
        private val tvEvidenceCount: android.widget.TextView = view.findViewById(R.id.tvEvidenceCount)
        private val tvDate: android.widget.TextView = view.findViewById(R.id.tvDate)
        private val btnReport: android.widget.Button = view.findViewById(R.id.btnGenerateReport)

        fun bind(forensicCase: ForensicCase) {
            tvCaseId.text = forensicCase.id
            tvCaseName.text = forensicCase.name
            tvStatus.text = forensicCase.status.name
            tvEvidenceCount.text = "${forensicCase.evidence.size} evidence item(s)"
            tvDate.text = "Modified: ${dateFormat.format(Date(forensicCase.modifiedAt))}"

            itemView.setOnClickListener { onCaseClick(forensicCase.id) }
            btnReport.setOnClickListener { onGenerateReport(forensicCase.id) }

            // Disable report button for cases with no evidence
            btnReport.isEnabled = forensicCase.evidence.isNotEmpty()
        }
    }
}
