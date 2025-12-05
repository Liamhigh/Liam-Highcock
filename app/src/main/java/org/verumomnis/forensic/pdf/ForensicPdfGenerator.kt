package org.verumomnis.forensic.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment
import org.verumomnis.forensic.R
import org.verumomnis.forensic.core.*
import org.verumomnis.forensic.leveler.LevelerEngine
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Forensic PDF Generator
 * 
 * Generates sealed forensic PDF reports with:
 * - Verum Omnis watermark
 * - QR code with verification data
 * - Chain of custody documentation
 * - Evidence inventory
 * - Cryptographic hashes
 * - B1-B9 Leveler analysis results
 * 
 * @author Liam Highcock
 * @version 1.1.0
 */
class ForensicPdfGenerator(private val context: Context) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US)
    
    // Brand colors
    private val brandNavy = DeviceRgb(15, 30, 54)  // #0F1E36
    private val brandBlue = DeviceRgb(30, 136, 229) // #1E88E5
    private val alertRed = DeviceRgb(244, 67, 54)   // #F44336
    private val warningOrange = DeviceRgb(255, 152, 0) // #FF9800
    private val successGreen = DeviceRgb(76, 175, 80) // #4CAF50

    /**
     * Generate complete forensic PDF report
     */
    fun generateReport(
        forensicCase: ForensicCase,
        narrative: String,
        evidenceSummary: List<EvidenceSummary>,
        qrCodeData: String,
        apkHash: String
    ): ByteArray {
        val outputStream = ByteArrayOutputStream()
        
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument, PageSize.A4)
        
        document.setMargins(50f, 50f, 50f, 50f)
        
        try {
            // Add watermark to all pages
            addWatermark(pdfDocument)
            
            // Cover page
            addCoverPage(document, forensicCase, apkHash)
            document.add(AreaBreak())
            
            // Table of contents
            addTableOfContents(document, forensicCase)
            document.add(AreaBreak())
            
            // Executive summary
            addExecutiveSummary(document, forensicCase)
            document.add(AreaBreak())
            
            // Evidence inventory
            addEvidenceInventory(document, forensicCase, evidenceSummary)
            document.add(AreaBreak())
            
            // Full narrative
            addNarrativeSection(document, narrative)
            document.add(AreaBreak())
            
            // Verification page with QR code
            addVerificationPage(document, forensicCase, qrCodeData, apkHash)
            
            document.close()
        } catch (e: Exception) {
            document.close()
            throw e
        }
        
        return outputStream.toByteArray()
    }

    /**
     * Generate enhanced forensic PDF report with Leveler analysis
     */
    fun generateReportWithLeveler(
        forensicCase: ForensicCase,
        narrative: String,
        evidenceSummary: List<EvidenceSummary>,
        levelerResult: LevelerEngine.EnhancedLevelerResult,
        qrCodeData: String,
        apkHash: String
    ): ByteArray {
        val outputStream = ByteArrayOutputStream()
        
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument, PageSize.A4)
        
        document.setMargins(50f, 50f, 50f, 50f)
        
        try {
            // Add watermark to all pages
            addWatermark(pdfDocument)
            
            // Cover page with Leveler badge
            addCoverPageWithLeveler(document, forensicCase, levelerResult, apkHash)
            document.add(AreaBreak())
            
            // Table of contents
            addTableOfContentsWithLeveler(document, forensicCase)
            document.add(AreaBreak())
            
            // Executive summary
            addExecutiveSummary(document, forensicCase)
            document.add(AreaBreak())
            
            // === ADD LEVELER ANALYSIS SECTION ===
            addLevelerAnalysisSection(document, levelerResult)
            document.add(AreaBreak())
            
            // Evidence inventory
            addEvidenceInventory(document, forensicCase, evidenceSummary)
            document.add(AreaBreak())
            
            // Full narrative
            addNarrativeSection(document, narrative)
            document.add(AreaBreak())
            
            // Verification page with QR code
            addVerificationPage(document, forensicCase, qrCodeData, apkHash)
            
            document.close()
        } catch (e: Exception) {
            document.close()
            throw e
        }
        
        return outputStream.toByteArray()
    }

    /**
     * Add enhanced cover page with Leveler integrity badge
     */
    private fun addCoverPageWithLeveler(
        document: Document,
        forensicCase: ForensicCase,
        levelerResult: LevelerEngine.EnhancedLevelerResult,
        apkHash: String
    ) {
        // Logo
        val logoText = Paragraph("VERUM OMNIS")
            .setFontSize(36f)
            .setFontColor(brandNavy)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(100f)
        document.add(logoText)
        
        val tagline = Paragraph("AI FORENSICS FOR TRUTH")
            .setFontSize(14f)
            .setFontColor(brandBlue)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(30f)
        document.add(tagline)
        
        // Integrity Badge
        val integrityScore = levelerResult.integrityScore
        val badgeColor = when {
            integrityScore >= 90 -> successGreen
            integrityScore >= 70 -> brandBlue
            integrityScore >= 50 -> warningOrange
            else -> alertRed
        }
        
        val badgeText = Paragraph("INTEGRITY SCORE: ${String.format("%.1f", integrityScore)}/100")
            .setFontSize(20f)
            .setFontColor(badgeColor)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(50f)
        document.add(badgeText)
        
        // Standard cover content
        val title = Paragraph("FORENSIC REPORT")
            .setFontSize(28f)
            .setFontColor(brandNavy)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
        document.add(title)
        
        val caseInfo = Paragraph("Case: ${forensicCase.name}\nID: ${forensicCase.id}")
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(20f)
        document.add(caseInfo)
        
        val dateInfo = Paragraph("Generated: ${dateFormat.format(Date())}")
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(10f)
            .setFontColor(ColorConstants.GRAY)
        document.add(dateInfo)
    }

    /**
     * Add enhanced table of contents with Leveler section
     */
    private fun addTableOfContentsWithLeveler(document: Document, forensicCase: ForensicCase) {
        val heading = Paragraph("TABLE OF CONTENTS")
            .setFontSize(18f)
            .setFontColor(brandNavy)
            .setBold()
            .setMarginBottom(20f)
        document.add(heading)
        
        val toc = listOf(
            "1. Executive Summary",
            "2. B1-B9 Leveler Analysis",
            "3. Evidence Inventory",
            "4. Forensic Narrative",
            "5. Verification & QR Code"
        )
        
        toc.forEach { item ->
            val tocItem = Paragraph(item)
                .setFontSize(12f)
                .setMarginLeft(20f)
                .setMarginBottom(5f)
            document.add(tocItem)
        }
    }

    /**
     * Add comprehensive Leveler analysis section to PDF
     */
    private fun addLevelerAnalysisSection(
        document: Document,
        levelerResult: LevelerEngine.EnhancedLevelerResult
    ) {
        // Section heading
        val heading = Paragraph("B1-B9 LEVELER ANALYSIS")
            .setFontSize(20f)
            .setFontColor(brandNavy)
            .setBold()
            .setMarginBottom(15f)
        document.add(heading)
        
        // B9: Integrity Index (shown first as overall assessment)
        addLevelerIntegrityIndex(document, levelerResult)
        
        // B2: Contradictions
        if (levelerResult.contradictions.isNotEmpty()) {
            addLevelerContradictions(document, levelerResult.contradictions)
        }
        
        // B3: Evidence Gaps
        if (levelerResult.missingEvidence.isNotEmpty()) {
            addLevelerEvidenceGaps(document, levelerResult.missingEvidence)
        }
        
        // B4: Timeline Anomalies
        if (levelerResult.timelineAnomalies.isNotEmpty()) {
            addLevelerTimelineAnomalies(document, levelerResult.timelineAnomalies)
        }
        
        // B5: Behavioral Patterns
        if (levelerResult.behavioralPatterns.isNotEmpty()) {
            addLevelerBehavioralPatterns(document, levelerResult.behavioralPatterns)
        }
        
        // B6: Financial Discrepancies
        if (levelerResult.financialDiscrepancies.isNotEmpty()) {
            addLevelerFinancialDiscrepancies(document, levelerResult.financialDiscrepancies)
        }
        
        // B7: Communication Patterns
        if (levelerResult.communicationPatterns.isNotEmpty()) {
            addLevelerCommunicationPatterns(document, levelerResult.communicationPatterns)
        }
        
        // B8: Jurisdictional Compliance
        addLevelerJurisdictionalCompliance(document, levelerResult.jurisdictionalCompliance)
        
        // Recommendations
        if (levelerResult.recommendations.isNotEmpty()) {
            addLevelerRecommendations(document, levelerResult.recommendations)
        }
    }

    private fun addLevelerIntegrityIndex(document: Document, result: LevelerEngine.EnhancedLevelerResult) {
        val subheading = Paragraph("B9: INTEGRITY INDEX")
            .setFontSize(14f)
            .setFontColor(brandBlue)
            .setBold()
            .setMarginTop(10f)
        document.add(subheading)
        
        val score = String.format("%.2f", result.integrityScore)
        val category = when {
            result.integrityScore >= 90 -> "EXCELLENT"
            result.integrityScore >= 70 -> "GOOD"
            result.integrityScore >= 50 -> "FAIR"
            result.integrityScore >= 25 -> "POOR"
            else -> "COMPROMISED"
        }
        
        val scoreColor = when {
            result.integrityScore >= 70 -> successGreen
            result.integrityScore >= 50 -> warningOrange
            else -> alertRed
        }
        
        val scoreText = Paragraph("Score: $score/100 ($category)")
            .setFontSize(12f)
            .setFontColor(scoreColor)
            .setBold()
            .setMarginLeft(20f)
        document.add(scoreText)
        
        val confidenceText = Paragraph("Confidence: ${String.format("%.1f", result.confidence * 100)}%")
            .setFontSize(10f)
            .setMarginLeft(20f)
            .setMarginBottom(10f)
        document.add(confidenceText)
    }

    private fun addLevelerContradictions(document: Document, contradictions: List<LevelerEngine.Contradiction>) {
        val subheading = Paragraph("B2: CONTRADICTION DETECTION")
            .setFontSize(14f)
            .setFontColor(brandBlue)
            .setBold()
            .setMarginTop(10f)
        document.add(subheading)
        
        val summary = Paragraph("Total contradictions found: ${contradictions.size}")
            .setFontSize(11f)
            .setMarginLeft(20f)
        document.add(summary)
        
        contradictions.take(5).forEach { contradiction ->
            val item = Paragraph("• ${contradiction.type.name} (${contradiction.severity.name})")
                .setFontSize(10f)
                .setMarginLeft(30f)
                .setMarginBottom(3f)
            document.add(item)
        }
        
        if (contradictions.size > 5) {
            val more = Paragraph("... and ${contradictions.size - 5} more")
                .setFontSize(10f)
                .setFontColor(ColorConstants.GRAY)
                .setMarginLeft(30f)
                .setMarginBottom(10f)
            document.add(more)
        }
    }

    private fun addLevelerEvidenceGaps(document: Document, gaps: List<LevelerEngine.EvidenceGap>) {
        val subheading = Paragraph("B3: MISSING EVIDENCE GAP ANALYSIS")
            .setFontSize(14f)
            .setFontColor(brandBlue)
            .setBold()
            .setMarginTop(10f)
        document.add(subheading)
        
        gaps.forEach { gap ->
            val item = Paragraph("• ${gap.type.uppercase()} (${gap.criticality.name})")
                .setFontSize(10f)
                .setMarginLeft(30f)
                .setMarginBottom(3f)
            document.add(item)
        }
    }

    private fun addLevelerTimelineAnomalies(document: Document, anomalies: List<LevelerEngine.TimelineAnomaly>) {
        val subheading = Paragraph("B4: TIMELINE MANIPULATION DETECTION")
            .setFontSize(14f)
            .setFontColor(brandBlue)
            .setBold()
            .setMarginTop(10f)
        document.add(subheading)
        
        anomalies.forEach { anomaly ->
            val item = Paragraph("• ${anomaly.type.name} (Suspicion: ${String.format("%.0f", anomaly.suspicionScore * 100)}%)")
                .setFontSize(10f)
                .setMarginLeft(30f)
                .setMarginBottom(3f)
            document.add(item)
        }
    }

    private fun addLevelerBehavioralPatterns(document: Document, patterns: List<LevelerEngine.DetectedBehavioralPattern>) {
        val subheading = Paragraph("B5: BEHAVIORAL PATTERN RECOGNITION")
            .setFontSize(14f)
            .setFontColor(brandBlue)
            .setBold()
            .setMarginTop(10f)
        document.add(subheading)
        
        patterns.forEach { pattern ->
            val item = Paragraph("• ${pattern.type.name} (Match: ${String.format("%.0f", pattern.score * 100)}%)")
                .setFontSize(10f)
                .setMarginLeft(30f)
                .setMarginBottom(3f)
            document.add(item)
        }
    }

    private fun addLevelerFinancialDiscrepancies(document: Document, discrepancies: List<LevelerEngine.FinancialDiscrepancy>) {
        val subheading = Paragraph("B6: FINANCIAL TRANSACTION CORRELATION")
            .setFontSize(14f)
            .setFontColor(brandBlue)
            .setBold()
            .setMarginTop(10f)
        document.add(subheading)
        
        discrepancies.forEach { discrepancy ->
            val item = Paragraph("• ${discrepancy.type.name}: ${discrepancy.description}")
                .setFontSize(10f)
                .setMarginLeft(30f)
                .setMarginBottom(3f)
            document.add(item)
        }
    }

    private fun addLevelerCommunicationPatterns(document: Document, patterns: List<LevelerEngine.CommunicationPattern>) {
        val subheading = Paragraph("B7: COMMUNICATION PATTERN ANALYSIS")
            .setFontSize(14f)
            .setFontColor(brandBlue)
            .setBold()
            .setMarginTop(10f)
        document.add(subheading)
        
        patterns.forEach { pattern ->
            val item = Paragraph("• ${pattern.type.name} (Score: ${String.format("%.0f", pattern.score * 100)}%)")
                .setFontSize(10f)
                .setMarginLeft(30f)
                .setMarginBottom(3f)
            document.add(item)
        }
    }

    private fun addLevelerJurisdictionalCompliance(document: Document, compliance: List<LevelerEngine.JurisdictionalCompliance>) {
        val subheading = Paragraph("B8: JURISDICTIONAL COMPLIANCE CHECK")
            .setFontSize(14f)
            .setFontColor(brandBlue)
            .setBold()
            .setMarginTop(10f)
        document.add(subheading)
        
        compliance.forEach { comp ->
            val status = if (comp.isCompliant) "✓ COMPLIANT" else "⚠ NON-COMPLIANT"
            val item = Paragraph("• ${comp.jurisdiction.name}: $status (${String.format("%.1f", comp.score)}%)")
                .setFontSize(10f)
                .setMarginLeft(30f)
                .setMarginBottom(3f)
            document.add(item)
        }
    }

    private fun addLevelerRecommendations(document: Document, recommendations: List<String>) {
        val subheading = Paragraph("FORENSIC RECOMMENDATIONS")
            .setFontSize(14f)
            .setFontColor(brandBlue)
            .setBold()
            .setMarginTop(15f)
        document.add(subheading)
        
        recommendations.forEachIndexed { index, recommendation ->
            val item = Paragraph("${index + 1}. $recommendation")
                .setFontSize(10f)
                .setMarginLeft(20f)
                .setMarginBottom(5f)
            document.add(item)
        }
    }


    /**
     * Add watermark to PDF
     */
    private fun addWatermark(pdfDocument: PdfDocument) {
        val numberOfPages = pdfDocument.numberOfPages
        for (i in 1..numberOfPages) {
            val page = pdfDocument.getPage(i)
            val canvas = PdfCanvas(page)
            
            val pageSize = page.pageSize
            val x = pageSize.width / 2
            val y = pageSize.height / 2
            
            canvas.saveState()
            canvas.setFillColor(DeviceRgb(200, 200, 200))
            
            // Watermark text
            val font = PdfFontFactory.createFont()
            canvas.beginText()
                .setFontAndSize(font, 60f)
                .moveText(x.toDouble() - 200, y.toDouble())
                .showText(VerumOmnisApplication.WATERMARK_TEXT)
                .endText()
            
            canvas.restoreState()
        }
    }

    /**
     * Add cover page
     */
    private fun addCoverPage(document: Document, forensicCase: ForensicCase, apkHash: String) {
        // Logo placeholder (would use actual logo in production)
        val logoText = Paragraph("VERUM OMNIS")
            .setFontSize(36f)
            .setFontColor(brandNavy)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(100f)
        document.add(logoText)
        
        val tagline = Paragraph("AI FORENSICS FOR TRUTH")
            .setFontSize(14f)
            .setFontColor(brandBlue)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(50f)
        document.add(tagline)
        
        val title = Paragraph("FORENSIC INVESTIGATION REPORT")
            .setFontSize(28f)
            .setFontColor(brandNavy)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(30f)
        document.add(title)
        
        val caseInfo = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f)))
            .setWidth(UnitValue.createPercentValue(80f))
            .setHorizontalAlignment(HorizontalAlignment.CENTER)
            .setMarginTop(50f)
        
        addInfoRow(caseInfo, "Case Reference:", forensicCase.id)
        addInfoRow(caseInfo, "Case Name:", forensicCase.name)
        addInfoRow(caseInfo, "Status:", forensicCase.status.name)
        addInfoRow(caseInfo, "Evidence Items:", forensicCase.evidence.size.toString())
        addInfoRow(caseInfo, "Created:", dateFormat.format(Date(forensicCase.createdAt)))
        addInfoRow(caseInfo, "Report Generated:", dateFormat.format(Date()))
        
        document.add(caseInfo)
        
        // Constitutional Governance badge
        val governanceBadge = Paragraph("CONSTITUTIONAL GOVERNANCE: ACTIVE")
            .setFontSize(12f)
            .setFontColor(ColorConstants.WHITE)
            .setBackgroundColor(brandNavy)
            .setPadding(10f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(50f)
        document.add(governanceBadge)
        
        // APK Hash for chain of trust
        val apkInfo = Paragraph()
            .add(Text("APK Integrity Hash: ").setBold())
            .add(Text(apkHash.take(32) + "...").setFontSize(8f))
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(30f)
        document.add(apkInfo)
        
        // Footer
        val footer = Paragraph("© ${Calendar.getInstance().get(Calendar.YEAR)} Verum Global Foundation | Creator: Liam Highcock")
            .setFontSize(10f)
            .setFontColor(ColorConstants.GRAY)
            .setTextAlignment(TextAlignment.CENTER)
            .setFixedPosition(50f, 50f, 500f)
        document.add(footer)
    }

    /**
     * Add table of contents
     */
    private fun addTableOfContents(document: Document, forensicCase: ForensicCase) {
        val heading = Paragraph("TABLE OF CONTENTS")
            .setFontSize(20f)
            .setFontColor(brandNavy)
            .setBold()
            .setMarginBottom(20f)
        document.add(heading)
        
        val tocItems = listOf(
            "1. Executive Summary",
            "2. Evidence Inventory (${forensicCase.evidence.size} items)",
            "3. Forensic Narrative",
            "4. Chain of Custody",
            "5. Integrity Verification",
            "6. QR Code Verification"
        )
        
        tocItems.forEach { item ->
            val tocLine = Paragraph(item)
                .setFontSize(14f)
                .setMarginLeft(20f)
                .setMarginBottom(10f)
            document.add(tocLine)
        }
    }

    /**
     * Add executive summary
     */
    private fun addExecutiveSummary(document: Document, forensicCase: ForensicCase) {
        val heading = Paragraph("EXECUTIVE SUMMARY")
            .setFontSize(20f)
            .setFontColor(brandNavy)
            .setBold()
            .setMarginBottom(20f)
        document.add(heading)
        
        val summaryText = buildString {
            appendLine("This forensic report documents evidence collected under the Verum Omnis Constitutional Governance Framework.")
            appendLine()
            appendLine("CORE PRINCIPLES ENFORCED:")
            appendLine("• Truth: Factual accuracy and verifiable evidence")
            appendLine("• Fairness: Protection of vulnerable parties")
            appendLine("• Human Rights: Dignity, equality, and agency")
            appendLine("• Non-Extraction: No sensitive data transmission")
            appendLine("• Integrity: No manipulation or bias")
        }
        
        val summary = Paragraph(summaryText)
            .setFontSize(12f)
            .setMarginBottom(20f)
        document.add(summary)
        
        // Statistics table
        val statsTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
            .setWidth(UnitValue.createPercentValue(100f))
        
        statsTable.addHeaderCell(createHeaderCell("Statistic"))
        statsTable.addHeaderCell(createHeaderCell("Value"))
        
        addDataRow(statsTable, "Total Evidence Items", forensicCase.evidence.size.toString())
        addDataRow(statsTable, "Sealed Items", forensicCase.evidence.count { it.sealed }.toString())
        
        val byType = forensicCase.evidence.groupBy { it.type }
        byType.forEach { (type, items) ->
            addDataRow(statsTable, "${type.name} Evidence", items.size.toString())
        }
        
        if (forensicCase.evidence.isNotEmpty()) {
            val earliest = forensicCase.evidence.minByOrNull { it.timestamp }
            val latest = forensicCase.evidence.maxByOrNull { it.timestamp }
            addDataRow(statsTable, "First Evidence", dateFormat.format(Date(earliest!!.timestamp)))
            addDataRow(statsTable, "Last Evidence", dateFormat.format(Date(latest!!.timestamp)))
        }
        
        document.add(statsTable)
    }

    /**
     * Add evidence inventory
     */
    private fun addEvidenceInventory(
        document: Document,
        forensicCase: ForensicCase,
        evidenceSummary: List<EvidenceSummary>
    ) {
        val heading = Paragraph("EVIDENCE INVENTORY")
            .setFontSize(20f)
            .setFontColor(brandNavy)
            .setBold()
            .setMarginBottom(20f)
        document.add(heading)
        
        forensicCase.evidence.sortedBy { it.timestamp }.forEachIndexed { index, evidence ->
            val itemHeading = Paragraph("Evidence Item #${index + 1}: ${evidence.id}")
                .setFontSize(14f)
                .setBold()
                .setFontColor(brandBlue)
                .setMarginTop(15f)
            document.add(itemHeading)
            
            val evidenceTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f)))
                .setWidth(UnitValue.createPercentValue(100f))
                .setMarginBottom(10f)
            
            addDataRow(evidenceTable, "Type", evidence.type.name)
            addDataRow(evidenceTable, "Filename", evidence.metadata.filename ?: "N/A")
            addDataRow(evidenceTable, "Size", formatFileSize(evidence.metadata.fileSize))
            addDataRow(evidenceTable, "MIME Type", evidence.mimeType)
            addDataRow(evidenceTable, "Collected", dateFormat.format(Date(evidence.timestamp)))
            addDataRow(evidenceTable, "Seal Status", if (evidence.sealed) "SEALED ✓" else "UNSEALED")
            
            document.add(evidenceTable)
            
            // Hash information
            val hashInfo = Paragraph()
                .add(Text("Content Hash (SHA-512): ").setBold().setFontSize(10f))
                .add(Text(evidence.contentHash).setFontSize(8f))
                .setMarginBottom(5f)
            document.add(hashInfo)
            
            if (evidence.sealed && evidence.sealHash != null) {
                val sealInfo = Paragraph()
                    .add(Text("Seal Hash: ").setBold().setFontSize(10f))
                    .add(Text(evidence.sealHash).setFontSize(8f))
                    .setMarginBottom(10f)
                document.add(sealInfo)
            }
            
            // Location if available
            evidence.location?.let { loc ->
                val locInfo = Paragraph()
                    .add(Text("Geolocation: ").setBold().setFontSize(10f))
                    .add(Text("${loc.latitude}, ${loc.longitude} (±${loc.accuracy}m)").setFontSize(10f))
                    .setMarginBottom(10f)
                document.add(locInfo)
            }
        }
    }

    /**
     * Add narrative section
     */
    private fun addNarrativeSection(document: Document, narrative: String) {
        val heading = Paragraph("FORENSIC NARRATIVE")
            .setFontSize(20f)
            .setFontColor(brandNavy)
            .setBold()
            .setMarginBottom(20f)
        document.add(heading)
        
        val narrativeParagraph = Paragraph(narrative)
            .setFontSize(10f)
            .setMultipliedLeading(1.4f)
        document.add(narrativeParagraph)
    }

    /**
     * Add verification page with QR code
     */
    private fun addVerificationPage(
        document: Document,
        forensicCase: ForensicCase,
        qrCodeData: String,
        apkHash: String
    ) {
        val heading = Paragraph("VERIFICATION")
            .setFontSize(20f)
            .setFontColor(brandNavy)
            .setBold()
            .setMarginBottom(20f)
        document.add(heading)
        
        // Generate QR code
        try {
            val qrBitmap = generateQrCode(qrCodeData, 300)
            val qrBytes = bitmapToBytes(qrBitmap)
            val qrImage = Image(ImageDataFactory.create(qrBytes))
                .setWidth(150f)
                .setHeight(150f)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
            document.add(qrImage)
        } catch (e: Exception) {
            val qrError = Paragraph("QR Code generation failed: ${e.message}")
                .setFontColor(ColorConstants.RED)
            document.add(qrError)
        }
        
        val qrLabel = Paragraph("Scan to verify report integrity")
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(30f)
        document.add(qrLabel)
        
        // Verification instructions
        val instructions = Paragraph()
            .add(Text("VERIFICATION INSTRUCTIONS\n\n").setBold())
            .add("1. VERIFY APK INTEGRITY\n")
            .add("   Calculate SHA-512 hash of the Verum Omnis APK\n")
            .add("   Compare with: ${apkHash.take(32)}...\n\n")
            .add("2. VERIFY EVIDENCE HASHES\n")
            .add("   Recalculate SHA-512 for each evidence item\n")
            .add("   Compare with listed content hashes\n\n")
            .add("3. VERIFY CASE INTEGRITY\n")
            .add("   Case Integrity Hash: ${forensicCase.integrityHash?.take(32) ?: "N/A"}...\n\n")
            .add("⚠️ If any hash does not match, evidence may be compromised")
            .setFontSize(10f)
        document.add(instructions)
        
        // Tamper detection warning
        val warning = Paragraph("FORENSIC ENGINE INTEGRITY CHECK")
            .setFontSize(14f)
            .setBold()
            .setFontColor(ColorConstants.WHITE)
            .setBackgroundColor(brandNavy)
            .setPadding(10f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(30f)
        document.add(warning)
        
        val warningText = Paragraph(
            "This report was generated by the Verum Omnis Forensic Engine. " +
            "Any modification to the report or evidence after generation will " +
            "cause integrity verification to fail."
        )
            .setFontSize(10f)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(10f)
        document.add(warningText)
    }

    // Helper methods

    private fun addInfoRow(table: Table, label: String, value: String) {
        table.addCell(Cell().add(Paragraph(label).setBold()).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph(value)).setBorder(Border.NO_BORDER))
    }

    private fun createHeaderCell(text: String): Cell {
        return Cell()
            .add(Paragraph(text).setBold().setFontColor(ColorConstants.WHITE))
            .setBackgroundColor(brandNavy)
            .setPadding(8f)
    }

    private fun addDataRow(table: Table, label: String, value: String) {
        table.addCell(Cell().add(Paragraph(label).setBold()).setPadding(5f))
        table.addCell(Cell().add(Paragraph(value)).setPadding(5f))
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }

    private fun generateQrCode(data: String, size: Int): Bitmap {
        val hints = hashMapOf<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.MARGIN] = 1
        
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints)
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
