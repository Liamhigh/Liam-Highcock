package org.verumomnis.forensic.core

import android.content.Context
import android.util.Log
import org.verumomnis.forensic.crypto.CryptographicSealingEngine
import org.verumomnis.forensic.location.ForensicLocationService
import org.verumomnis.forensic.pdf.ForensicPdfGenerator
import org.verumomnis.forensic.report.ForensicNarrativeGenerator
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Core Forensic Engine for SAPS
 * 
 * Implements Verum Omnis Constitutional Governance Layer
 * - Truth: Factual accuracy and verifiable evidence
 * - Fairness: Protection of vulnerable parties
 * - Human Rights: Dignity, equality, and agency
 * - Non-Extraction: No sensitive data transmission
 * - Human Authority: AI assists, never overrides
 * - Integrity: No manipulation or bias
 * - Independence: No external influence on outputs
 * 
 * @author Liam Highcock
 */
class ForensicEngine(private val context: Context) {

    companion object {
        private const val TAG = "ForensicEngine"
    }

    private val cryptoEngine = CryptographicSealingEngine()
    private val locationService = ForensicLocationService(context)
    private val pdfGenerator = ForensicPdfGenerator(context)
    private val narrativeGenerator = ForensicNarrativeGenerator()
    
    // In-memory case storage (stateless between sessions)
    private val activeCases = ConcurrentHashMap<String, ForensicCase>()

    /**
     * Creates a new forensic case
     */
    fun createCase(caseNumber: String, description: String): ForensicCase {
        val case = ForensicCase(
            caseNumber = caseNumber,
            description = description
        )
        activeCases[case.id] = case
        Log.i(TAG, "Created case: ${case.caseNumber}")
        return case
    }

    /**
     * Retrieves an active case by ID
     */
    fun getCase(caseId: String): ForensicCase? {
        return activeCases[caseId]
    }

    /**
     * Gets all active cases
     */
    fun getAllCases(): List<ForensicCase> {
        return activeCases.values.toList()
    }

    /**
     * Adds evidence to a case with GPS location and cryptographic sealing
     */
    suspend fun addEvidence(
        caseId: String,
        type: EvidenceType,
        description: String,
        content: ByteArray? = null,
        filePath: String? = null,
        notes: String? = null
    ): ForensicEvidence? {
        val case = activeCases[caseId] ?: return null

        // Get current GPS location
        val location = locationService.getCurrentLocation()

        // Calculate content hash if content is provided
        val contentHash = content?.let { cryptoEngine.calculateHash(it) }
        
        // Calculate seal hash (includes metadata)
        val sealData = buildString {
            append(caseId)
            append(type.name)
            append(description)
            append(System.currentTimeMillis())
            contentHash?.let { append(it) }
            location?.let { 
                append(it.latitude)
                append(it.longitude)
            }
        }
        val sealHash = cryptoEngine.calculateHash(sealData.toByteArray())

        val evidence = ForensicEvidence(
            type = type,
            description = description,
            latitude = location?.latitude,
            longitude = location?.longitude,
            filePath = filePath,
            contentHash = contentHash,
            sealHash = sealHash,
            metadata = EvidenceMetadata(
                deviceInfo = android.os.Build.MODEL,
                notes = notes
            )
        )

        case.evidence.add(evidence)
        Log.i(TAG, "Added evidence to case ${case.caseNumber}: ${evidence.id}")
        
        return evidence
    }

    /**
     * Generates a sealed forensic PDF report for a case
     */
    suspend fun generateReport(caseId: String): ForensicResult? {
        val case = activeCases[caseId] ?: return null

        // Generate narrative
        val narrative = narrativeGenerator.generateNarrative(case)

        // Create PDF
        val reportDir = File(context.filesDir, "reports")
        if (!reportDir.exists()) {
            reportDir.mkdirs()
        }
        
        val reportFile = File(reportDir, "SAPS_Report_${case.caseNumber}_${System.currentTimeMillis()}.pdf")
        
        pdfGenerator.generateReport(
            case = case,
            narrative = narrative,
            outputFile = reportFile
        )

        // Calculate final hash
        val reportHash = cryptoEngine.calculateHash(reportFile.readBytes())

        // Update case status
        case.copy(status = CaseStatus.UNDER_REVIEW).also {
            activeCases[caseId] = it
        }

        Log.i(TAG, "Generated report for case ${case.caseNumber}: ${reportFile.absolutePath}")

        return ForensicResult(
            caseId = caseId,
            evidenceCount = case.evidence.size,
            reportPath = reportFile.absolutePath,
            analysisHash = reportHash
        )
    }

    /**
     * Verifies the integrity of a sealed report
     */
    fun verifyReport(reportPath: String, expectedHash: String): Boolean {
        val file = File(reportPath)
        if (!file.exists()) return false
        
        val currentHash = cryptoEngine.calculateHash(file.readBytes())
        val verified = currentHash == expectedHash
        
        Log.i(TAG, "Report verification: ${if (verified) "PASSED" else "FAILED"}")
        return verified
    }

    /**
     * Closes a case
     */
    fun closeCase(caseId: String): Boolean {
        val case = activeCases[caseId] ?: return false
        activeCases[caseId] = case.copy(status = CaseStatus.CLOSED)
        Log.i(TAG, "Closed case: ${case.caseNumber}")
        return true
    }

    /**
     * Archives a case
     */
    fun archiveCase(caseId: String): Boolean {
        val case = activeCases[caseId] ?: return false
        activeCases[caseId] = case.copy(status = CaseStatus.ARCHIVED)
        Log.i(TAG, "Archived case: ${case.caseNumber}")
        return true
    }
}
