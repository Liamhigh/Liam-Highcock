package org.verumomnis.forensic.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.verumomnis.forensic.R
import org.verumomnis.forensic.core.ForensicEngine
import org.verumomnis.forensic.core.ForensicReport
import org.verumomnis.forensic.databinding.ActivityReportViewerBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Report Viewer Activity
 * 
 * Displays generated forensic reports with:
 * - Report narrative
 * - Evidence summary
 * - Integrity verification
 * - Share/export options
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
class ReportViewerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REPORT_ID = "reportId"
        const val EXTRA_CASE_ID = "caseId"
    }

    private lateinit var binding: ActivityReportViewerBinding
    private lateinit var forensicEngine: ForensicEngine
    
    private var currentReport: ForensicReport? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        forensicEngine = ForensicEngine(this)

        val caseId = intent.getStringExtra(EXTRA_CASE_ID)
        
        if (caseId != null) {
            loadReport(caseId)
        } else {
            Toast.makeText(this, "No report specified", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            shareReport()
        }

        binding.btnSave.setOnClickListener {
            saveReport()
        }

        binding.btnVerify.setOnClickListener {
            verifyIntegrity()
        }
    }

    private fun loadReport(caseId: String) {
        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            try {
                val report = withContext(Dispatchers.IO) {
                    forensicEngine.generateReport(caseId)
                }

                binding.progressBar.visibility = android.view.View.GONE

                if (report != null) {
                    currentReport = report
                    displayReport(report)
                } else {
                    Toast.makeText(
                        this@ReportViewerActivity,
                        "Failed to generate report",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(
                    this@ReportViewerActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun displayReport(report: ForensicReport) {
        // Header
        binding.tvReportId.text = report.id
        binding.tvCaseName.text = report.caseName
        binding.tvGeneratedAt.text = "Generated: ${dateFormat.format(Date(report.generatedAt))}"

        // Integrity info
        binding.tvIntegrityHash.text = "Integrity: ${report.integrityHash.take(32)}..."
        binding.tvApkHash.text = "APK: ${report.apkHash.take(32)}..."

        // Evidence summary
        val evidenceSummary = report.evidenceSummary.joinToString("\n") { 
            "• ${it.type.name}: ${it.description}"
        }
        binding.tvEvidenceSummary.text = if (evidenceSummary.isNotEmpty()) {
            evidenceSummary
        } else {
            "No evidence items"
        }

        // Full narrative
        binding.tvNarrative.text = report.narrative

        // QR code data
        binding.tvQrData.text = report.qrCodeData
    }

    private fun shareReport() {
        val report = currentReport ?: return

        lifecycleScope.launch {
            try {
                val pdfFile = withContext(Dispatchers.IO) {
                    savePdfToFile(report)
                }

                val uri = FileProvider.getUriForFile(
                    this@ReportViewerActivity,
                    "${packageName}.fileprovider",
                    pdfFile
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Forensic Report: ${report.caseName}")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                startActivity(Intent.createChooser(shareIntent, "Share Forensic Report"))
            } catch (e: Exception) {
                Toast.makeText(
                    this@ReportViewerActivity,
                    "Share failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveReport() {
        val report = currentReport ?: return

        lifecycleScope.launch {
            try {
                val pdfFile = withContext(Dispatchers.IO) {
                    savePdfToFile(report)
                }

                Toast.makeText(
                    this@ReportViewerActivity,
                    "Report saved: ${pdfFile.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@ReportViewerActivity,
                    "Save failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun savePdfToFile(report: ForensicReport): File {
        val reportsDir = File(filesDir, "reports")
        if (!reportsDir.exists()) {
            reportsDir.mkdirs()
        }

        val filename = "FORENSIC_REPORT_${report.id}.pdf"
        val pdfFile = File(reportsDir, filename)
        
        pdfFile.writeBytes(report.pdfBytes)
        return pdfFile
    }

    private fun verifyIntegrity() {
        val report = currentReport ?: return
        val caseId = intent.getStringExtra(EXTRA_CASE_ID) ?: return

        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            try {
                val isValid = withContext(Dispatchers.IO) {
                    forensicEngine.verifyCase(caseId)
                }

                binding.progressBar.visibility = android.view.View.GONE

                val message = if (isValid) {
                    "✓ INTEGRITY VERIFIED\n\nCase integrity hash matches. Evidence chain is intact."
                } else {
                    "⚠ INTEGRITY CHECK FAILED\n\nCase may have been tampered with or is not yet sealed."
                }

                androidx.appcompat.app.AlertDialog.Builder(this@ReportViewerActivity)
                    .setTitle("Integrity Verification")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show()
            } catch (e: Exception) {
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(
                    this@ReportViewerActivity,
                    "Verification error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
