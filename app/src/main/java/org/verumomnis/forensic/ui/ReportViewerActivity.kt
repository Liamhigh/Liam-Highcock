package org.verumomnis.forensic.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import org.verumomnis.forensic.R
import java.io.File

/**
 * Report Viewer Activity
 * 
 * Features:
 * - Display forensic report summary
 * - Show integrity score
 * - Share report via system share intent
 * - Open PDF in external viewer
 */
class ReportViewerActivity : AppCompatActivity() {

    private lateinit var textReportTitle: TextView
    private lateinit var textIntegrityScore: TextView
    private lateinit var textReportPath: TextView
    private lateinit var btnOpenPdf: Button
    private lateinit var btnShareReport: Button
    
    private var reportPath: String? = null
    private var integrityScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_viewer)
        
        reportPath = intent.getStringExtra(ScannerActivity.EXTRA_REPORT_PATH)
        integrityScore = intent.getIntExtra(ScannerActivity.EXTRA_INTEGRITY_SCORE, 0)
        
        setupViews()
        displayReportInfo()
    }

    private fun setupViews() {
        textReportTitle = findViewById(R.id.textReportTitle)
        textIntegrityScore = findViewById(R.id.textIntegrityScore)
        textReportPath = findViewById(R.id.textReportPath)
        btnOpenPdf = findViewById(R.id.btnOpenPdf)
        btnShareReport = findViewById(R.id.btnShareReport)
        
        btnOpenPdf.setOnClickListener {
            openPdfReport()
        }
        
        btnShareReport.setOnClickListener {
            shareReport()
        }
    }

    private fun displayReportInfo() {
        textReportTitle.text = "Verum Omnis Forensic Report"
        
        val scoreCategory = when {
            integrityScore >= 90 -> "EXCELLENT"
            integrityScore >= 70 -> "GOOD"
            integrityScore >= 50 -> "FAIR"
            integrityScore >= 30 -> "POOR"
            else -> "CRITICAL"
        }
        
        textIntegrityScore.text = "Integrity Score: $integrityScore/100 ($scoreCategory)"
        
        reportPath?.let {
            textReportPath.text = "Report saved to:\n$it"
        } ?: run {
            textReportPath.text = "Report path not available"
        }
    }

    private fun openPdfReport() {
        reportPath?.let { path ->
            try {
                val file = File(path)
                if (file.exists()) {
                    val uri = try {
                        FileProvider.getUriForFile(
                            this,
                            "${packageName}.fileprovider",
                            file
                        )
                    } catch (e: SecurityException) {
                        Toast.makeText(
                            this,
                            "Cannot access report file: permission denied",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@let
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(
                            this,
                            "Report file is outside accessible paths",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@let
                    }
                    
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf")
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "No PDF viewer installed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Report file not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Error opening PDF: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun shareReport() {
        reportPath?.let { path ->
            try {
                val file = File(path)
                if (file.exists()) {
                    val uri = try {
                        FileProvider.getUriForFile(
                            this,
                            "${packageName}.fileprovider",
                            file
                        )
                    } catch (e: SecurityException) {
                        Toast.makeText(
                            this,
                            "Cannot share report file: permission denied",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@let
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(
                            this,
                            "Report file is outside accessible paths",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@let
                    }
                    
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra(Intent.EXTRA_SUBJECT, "Verum Omnis Forensic Report")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Forensic report generated by Verum Omnis Engine. " +
                            "Integrity Score: $integrityScore/100"
                        )
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    
                    startActivity(Intent.createChooser(shareIntent, "Share Report"))
                } else {
                    Toast.makeText(this, "Report file not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Error sharing report: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
