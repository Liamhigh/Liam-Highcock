package org.verumomnis.forensic.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import android.widget.TextView
import android.widget.EditText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import org.verumomnis.forensic.R
import org.verumomnis.forensic.core.ForensicCase
import org.verumomnis.forensic.core.VerumOmnisApplication
import java.time.format.DateTimeFormatter

/**
 * Main Activity for the Verum Omnis Forensic Engine
 * 
 * Features:
 * - Case list display
 * - Create new cases
 * - Navigate to scanner and report viewer
 * - Location permission handling
 */
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabNewCase: FloatingActionButton
    private lateinit var emptyView: TextView
    
    private val forensicEngine by lazy { 
        VerumOmnisApplication.getInstance().forensicEngine 
    }
    
    private val caseAdapter = CaseAdapter { case ->
        navigateToCase(case)
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocation = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocation || coarseLocation) {
            Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this, 
                "Location permission denied. Evidence will not include geolocation.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupViews()
        requestLocationPermission()
        refreshCaseList()
    }

    override fun onResume() {
        super.onResume()
        refreshCaseList()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerViewCases)
        fabNewCase = findViewById(R.id.fabNewCase)
        emptyView = findViewById(R.id.textEmptyState)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = caseAdapter
        
        fabNewCase.setOnClickListener {
            showNewCaseDialog()
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage(
                        "The Verum Omnis Forensic Engine uses location data to geotag " +
                        "evidence at collection time. This helps establish the chain of " +
                        "custody for legal proceedings."
                    )
                    .setPositiveButton("Grant") { _, _ ->
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                    .setNegativeButton("Deny") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
            else -> {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun showNewCaseDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_case, null)
        val editCaseName = dialogView.findViewById<EditText>(R.id.editCaseName)
        val editCaseDescription = dialogView.findViewById<EditText>(R.id.editCaseDescription)
        
        AlertDialog.Builder(this)
            .setTitle("Create New Case")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = editCaseName.text.toString().trim()
                val description = editCaseDescription.text.toString().trim()
                
                if (name.isNotBlank()) {
                    createNewCase(name, description)
                } else {
                    Toast.makeText(this, "Please enter a case name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createNewCase(name: String, description: String) {
        val newCase = forensicEngine.createCase(name, description)
        Toast.makeText(this, "Case created: ${newCase.name}", Toast.LENGTH_SHORT).show()
        refreshCaseList()
        navigateToCase(newCase)
    }

    private fun navigateToCase(case: ForensicCase) {
        val intent = Intent(this, ScannerActivity::class.java).apply {
            putExtra(EXTRA_CASE_ID, case.id)
        }
        startActivity(intent)
    }

    private fun refreshCaseList() {
        val cases = forensicEngine.getAllCases()
        caseAdapter.submitList(cases)
        
        if (cases.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    companion object {
        const val EXTRA_CASE_ID = "extra_case_id"
    }

    /**
     * RecyclerView Adapter for cases
     */
    inner class CaseAdapter(
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
            private val textCaseName: TextView = itemView.findViewById(R.id.textCaseName)
            private val textCaseDate: TextView = itemView.findViewById(R.id.textCaseDate)
            private val textCaseStatus: TextView = itemView.findViewById(R.id.textCaseStatus)
            private val textEvidenceCount: TextView = itemView.findViewById(R.id.textEvidenceCount)

            fun bind(case: ForensicCase) {
                textCaseName.text = case.name
                textCaseDate.text = case.createdAt.format(dateFormatter)
                textCaseStatus.text = case.status.name
                textEvidenceCount.text = "${case.evidence.size} evidence items"
                
                itemView.setOnClickListener {
                    onCaseClick(case)
                }
            }
        }
    }
}
