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
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.verumomnis.forensic.R
import org.verumomnis.forensic.core.ForensicCase
import org.verumomnis.forensic.core.ForensicEngine
import org.verumomnis.forensic.databinding.ActivityMainBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout

/**
 * Main Activity - SAPS Forensic Evidence Engine
 * 
 * Provides easy-to-use interface for law enforcement officers to:
 * - Create new forensic cases
 * - View existing cases
 * - Access case details and evidence
 * 
 * @author Liam Highcock
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var forensicEngine: ForensicEngine
    private lateinit var caseAdapter: CaseAdapter

    private val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        forensicEngine = ForensicEngine(this)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        checkPermissions()
        loadCases()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.subtitle = "Verum Omnis Governance"
    }

    private fun setupRecyclerView() {
        caseAdapter = CaseAdapter { case ->
            openCaseDetail(case)
        }
        binding.recyclerViewCases.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = caseAdapter
        }
    }

    private fun setupFab() {
        binding.fabNewCase.setOnClickListener {
            showNewCaseDialog()
        }
    }

    private fun checkPermissions() {
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun loadCases() {
        val cases = forensicEngine.getAllCases()
        caseAdapter.submitList(cases)
        
        binding.textNoCases.visibility = if (cases.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerViewCases.visibility = if (cases.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showNewCaseDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
        }

        val caseNumberInput = EditText(this).apply {
            hint = "Case Number (e.g., SAPS-2024-001)"
        }
        
        val descriptionInput = EditText(this).apply {
            hint = "Case Description"
            minLines = 2
        }

        layout.addView(caseNumberInput)
        layout.addView(descriptionInput)

        AlertDialog.Builder(this)
            .setTitle("Create New Case")
            .setView(layout)
            .setPositiveButton("Create") { _, _ ->
                val caseNumber = caseNumberInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()
                
                if (caseNumber.isNotEmpty() && description.isNotEmpty()) {
                    createNewCase(caseNumber, description)
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createNewCase(caseNumber: String, description: String) {
        lifecycleScope.launch {
            val case = forensicEngine.createCase(caseNumber, description)
            loadCases()
            Toast.makeText(this@MainActivity, "Case created: ${case.caseNumber}", Toast.LENGTH_SHORT).show()
            openCaseDetail(case)
        }
    }

    private fun openCaseDetail(case: ForensicCase) {
        val intent = Intent(this, CaseDetailActivity::class.java).apply {
            putExtra(CaseDetailActivity.EXTRA_CASE_ID, case.id)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadCases()
    }
}

/**
 * RecyclerView Adapter for displaying cases
 */
class CaseAdapter(
    private val onCaseClick: (ForensicCase) -> Unit
) : RecyclerView.Adapter<CaseAdapter.CaseViewHolder>() {

    private var cases: List<ForensicCase> = emptyList()

    fun submitList(newCases: List<ForensicCase>) {
        cases = newCases
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_case, parent, false)
        return CaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CaseViewHolder, position: Int) {
        holder.bind(cases[position])
    }

    override fun getItemCount(): Int = cases.size

    inner class CaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(case: ForensicCase) {
            itemView.findViewById<android.widget.TextView>(R.id.textCaseNumber).text = case.caseNumber
            itemView.findViewById<android.widget.TextView>(R.id.textCaseDescription).text = case.description
            itemView.findViewById<android.widget.TextView>(R.id.textCaseStatus).text = case.status.name
            itemView.findViewById<android.widget.TextView>(R.id.textEvidenceCount).text = 
                "${case.evidence.size} evidence item(s)"
            
            itemView.setOnClickListener { onCaseClick(case) }
        }
    }
}
