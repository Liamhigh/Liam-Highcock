package org.verumomnis.forensic.ui

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import org.verumomnis.forensic.databinding.ActivityReportViewerBinding
import java.io.File

/**
 * Report Viewer Activity
 * 
 * Displays PDF forensic reports and allows sharing
 * 
 * @author Liam Highcock
 */
class ReportViewerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REPORT_PATH = "extra_report_path"
    }

    private lateinit var binding: ActivityReportViewerBinding
    private var reportPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reportPath = intent.getStringExtra(EXTRA_REPORT_PATH)
        if (reportPath == null) {
            Toast.makeText(this, "Invalid report", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupButtons()
        displayReport()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Forensic Report"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupButtons() {
        binding.btnShare.setOnClickListener {
            shareReport()
        }

        binding.btnOpenExternal.setOnClickListener {
            openInExternalApp()
        }
    }

    private fun displayReport() {
        val file = File(reportPath!!)
        if (!file.exists()) {
            Toast.makeText(this, "Report file not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Display report info
        binding.textReportPath.text = "File: ${file.name}"
        binding.textReportSize.text = "Size: ${file.length() / 1024} KB"

        // Note: For full PDF viewing, you would integrate a PDF viewer library
        // For now, showing file information and allowing external viewing
        binding.textReportInfo.text = """
            SAPS Forensic Evidence Report
            
            This PDF report has been cryptographically sealed
            using SHA-512 hash standard.
            
            Tap "Open External" to view the full PDF document.
            Tap "Share" to send the report to other officers.
        """.trimIndent()
    }

    private fun shareReport() {
        val file = File(reportPath!!)
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "SAPS Forensic Report")
            putExtra(Intent.EXTRA_TEXT, "Please find the attached forensic evidence report.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Report"))
    }

    private fun openInExternalApp() {
        val file = File(reportPath!!)
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No PDF viewer app installed", Toast.LENGTH_SHORT).show()
        }
    }
}
