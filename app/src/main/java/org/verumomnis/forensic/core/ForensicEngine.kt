package org.verumomnis.forensic.core

import android.content.Context
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.verumomnis.forensic.crypto.CryptographicSealingEngine
import org.verumomnis.forensic.database.ForensicRepository
import org.verumomnis.forensic.pdf.ForensicPdfGenerator
import org.verumomnis.forensic.report.ForensicNarrativeGenerator
import java.io.File
import java.security.MessageDigest
import java.util.UUID

/**
 * Verum Omnis Forensic Engine
 * 
 * Core engine for forensic evidence collection, sealing, and reporting.
 * Operates in offline-first mode with local persistence.
 * 
 * Features:
 * - Cryptographic Evidence Sealing (SHA-512 + HMAC-SHA512)
 * - GPS Location Capture
 * - AI-Readable PDF Reports
 * - Tamper Detection
 * - Chain of Custody Verification
 * - Local Database Persistence
 * 
 * @author Liam Highcock
 * @version 1.1.0
 */
class ForensicEngine(private val context: Context) {

    private val cryptoEngine = CryptographicSealingEngine()
    private val pdfGenerator = ForensicPdfGenerator(context)
    private val narrativeGenerator = ForensicNarrativeGenerator()
    private val repository = ForensicRepository(context)
    private val scope = CoroutineScope(Dispatchers.IO)
    
    // In-memory cache for active cases
    private val activeCases = mutableMapOf<String, ForensicCase>()
    
    init {
        // Load persisted cases on initialization
        scope.launch {
            try {
                loadPersistedCases()
            } catch (e: Exception) {
                // Log error but don't crash - empty case list is acceptable fallback
                android.util.Log.e("ForensicEngine", "Failed to load persisted cases", e)
            }
        }
    }
    
    private suspend fun loadPersistedCases() {
        val cases = repository.getAllCases()
        cases.forEach { case ->
            activeCases[case.id] = case
        }
    }
    
    /**
     * Get all cases as a Flow for reactive UI updates
     */
    fun getAllCasesFlow(): Flow<List<ForensicCase>> {
        return repository.getAllCasesFlow()
    }
    
    /**
     * Create a new forensic case
     */
    fun createCase(name: String, description: String = ""): ForensicCase {
        val caseId = generateCaseId()
        val now = System.currentTimeMillis()
        
        val newCase = ForensicCase(
            id = caseId,
            name = name,
            description = description,
            createdAt = now,
            modifiedAt = now,
            evidence = mutableListOf(),
            status = CaseStatus.OPEN
        )
        
        activeCases[caseId] = newCase
        
        // Persist to database
        scope.launch {
            repository.saveCase(newCase)
        }
        
        return newCase
    }
    
    /**
     * Add evidence to an existing case
     */
    fun addEvidence(
        caseId: String,
        type: EvidenceType,
        content: ByteArray,
        mimeType: String,
        filename: String? = null,
        location: ForensicLocation? = null
    ): ForensicEvidence? {
        val forensicCase = activeCases[caseId] ?: return null
        
        if (forensicCase.status != CaseStatus.OPEN) {
            return null // Cannot add evidence to sealed/archived cases
        }
        
        val evidenceId = generateEvidenceId()
        val now = System.currentTimeMillis()
        val contentHash = cryptoEngine.calculateHash(content)
        
        val evidence = ForensicEvidence(
            id = evidenceId,
            type = type,
            content = content,
            contentHash = contentHash,
            mimeType = mimeType,
            timestamp = now,
            location = location,
            metadata = EvidenceMetadata(
                filename = filename,
                fileSize = content.size.toLong(),
                createdAt = now,
                modifiedAt = null,
                author = null,
                deviceInfo = getDeviceInfo(),
                appVersion = VerumOmnisApplication.VERSION
            ),
            sealed = false
        )
        
        forensicCase.evidence.add(evidence)
        
        // Persist evidence and update case
        scope.launch {
            repository.saveEvidence(caseId, evidence)
            repository.updateCase(forensicCase.copy(modifiedAt = now))
        }
        
        return evidence
    }
    
    /**
     * Seal a piece of evidence with cryptographic signature
     */
    fun sealEvidence(caseId: String, evidenceId: String): ForensicEvidence? {
        val forensicCase = activeCases[caseId] ?: return null
        val evidence = forensicCase.evidence.find { it.id == evidenceId } ?: return null
        
        if (evidence.sealed) {
            return evidence // Already sealed
        }
        
        val sealHash = cryptoEngine.sealEvidence(evidence)
        
        // Create sealed evidence (immutable)
        val sealedEvidence = evidence.copy(
            sealed = true,
            sealHash = sealHash
        )
        
        // Replace in list
        val index = forensicCase.evidence.indexOfFirst { it.id == evidenceId }
        if (index >= 0) {
            forensicCase.evidence[index] = sealedEvidence
        }
        
        // Persist update
        scope.launch {
            repository.updateEvidence(caseId, sealedEvidence)
        }
        
        return sealedEvidence
    }
    
    /**
     * Seal entire case
     */
    fun sealCase(caseId: String): ForensicCase? {
        val forensicCase = activeCases[caseId] ?: return null
        
        if (forensicCase.status != CaseStatus.OPEN) {
            return forensicCase
        }
        
        // Seal all unsealed evidence
        forensicCase.evidence.forEachIndexed { index, evidence ->
            if (!evidence.sealed) {
                val sealHash = cryptoEngine.sealEvidence(evidence)
                forensicCase.evidence[index] = evidence.copy(
                    sealed = true,
                    sealHash = sealHash
                )
            }
        }
        
        // Calculate case integrity hash
        val integrityHash = cryptoEngine.calculateCaseIntegrityHash(forensicCase)
        
        // Update case status
        val sealedCase = forensicCase.copy(
            status = CaseStatus.SEALED,
            modifiedAt = System.currentTimeMillis(),
            integrityHash = integrityHash
        )
        
        activeCases[caseId] = sealedCase
        
        // Persist update
        scope.launch {
            repository.updateCase(sealedCase)
            sealedCase.evidence.forEach { ev ->
                repository.updateEvidence(caseId, ev)
            }
        }
        
        return sealedCase
    }
    
    /**
     * Generate forensic PDF report
     */
    fun generateReport(caseId: String): ForensicReport? {
        val forensicCase = activeCases[caseId] ?: return null
        
        // Seal case if not already sealed
        if (forensicCase.status == CaseStatus.OPEN) {
            sealCase(caseId)
        }
        
        val sealedCase = activeCases[caseId] ?: return null
        
        // Generate narrative
        val narrative = narrativeGenerator.generateNarrative(sealedCase)
        
        // Generate evidence summaries
        val evidenceSummary = sealedCase.evidence.map { evidence ->
            EvidenceSummary(
                evidenceId = evidence.id,
                type = evidence.type,
                hash = evidence.contentHash,
                timestamp = evidence.timestamp,
                description = "${evidence.type.name}: ${evidence.metadata.filename ?: "Unnamed"}"
            )
        }
        
        // Get APK hash for chain of trust
        val apkHash = getApkHash()
        
        // Generate report ID and hash
        val reportId = generateReportId()
        val reportTimestamp = System.currentTimeMillis()
        
        // Generate QR code data
        val qrCodeData = buildQrCodeData(
            reportId = reportId,
            caseId = caseId,
            integrityHash = sealedCase.integrityHash ?: "",
            apkHash = apkHash,
            timestamp = reportTimestamp
        )
        
        // Generate PDF
        val pdfBytes = pdfGenerator.generateReport(
            forensicCase = sealedCase,
            narrative = narrative,
            evidenceSummary = evidenceSummary,
            qrCodeData = qrCodeData,
            apkHash = apkHash
        )
        
        // Calculate final integrity hash
        val integrityHash = cryptoEngine.calculateHash(pdfBytes)
        
        val report = ForensicReport(
            id = reportId,
            caseId = caseId,
            caseName = sealedCase.name,
            generatedAt = reportTimestamp,
            narrative = narrative,
            evidenceSummary = evidenceSummary,
            integrityHash = integrityHash,
            apkHash = apkHash,
            qrCodeData = qrCodeData,
            pdfBytes = pdfBytes
        )
        
        // Update case status
        val reportedCase = sealedCase.copy(
            status = CaseStatus.REPORTED,
            modifiedAt = reportTimestamp
        )
        activeCases[caseId] = reportedCase
        
        // Persist update
        scope.launch {
            repository.updateCase(reportedCase)
        }
        
        return report
    }
    
    /**
     * Export case to file
     */
    suspend fun exportCase(caseId: String): File? {
        val exportDir = File(context.getExternalFilesDir(null), "exports").apply { mkdirs() }
        return repository.exportCase(caseId, exportDir)
    }
    
    /**
     * Verify evidence integrity
     */
    fun verifyEvidence(evidence: ForensicEvidence): Boolean {
        if (!evidence.sealed || evidence.sealHash == null) {
            return false
        }
        
        val computedHash = cryptoEngine.calculateHash(evidence.content)
        return computedHash == evidence.contentHash
    }
    
    /**
     * Verify case integrity
     */
    fun verifyCase(caseId: String): Boolean {
        val forensicCase = activeCases[caseId] ?: return false
        
        if (forensicCase.integrityHash == null) {
            return false
        }
        
        val computedHash = cryptoEngine.calculateCaseIntegrityHash(forensicCase)
        return computedHash == forensicCase.integrityHash
    }
    
    /**
     * Get case by ID
     */
    fun getCase(caseId: String): ForensicCase? = activeCases[caseId]
    
    /**
     * Get all active cases
     */
    fun getAllCases(): List<ForensicCase> = activeCases.values.toList()
    
    /**
     * Delete case (only if not sealed)
     */
    fun deleteCase(caseId: String): Boolean {
        val forensicCase = activeCases[caseId] ?: return false
        
        if (forensicCase.status != CaseStatus.OPEN) {
            return false // Cannot delete sealed cases
        }
        
        activeCases.remove(caseId)
        
        // Delete from database
        scope.launch {
            repository.deleteCase(caseId)
        }
        
        return true
    }
    
    // Private helper methods
    
    private fun generateCaseId(): String = "CASE-${UUID.randomUUID().toString().take(8).uppercase()}"
    
    private fun generateEvidenceId(): String = "EV-${UUID.randomUUID().toString().take(8).uppercase()}"
    
    private fun generateReportId(): String = "RPT-${UUID.randomUUID().toString().take(8).uppercase()}"
    
    private fun getDeviceInfo(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})"
    }
    
    private fun getApkHash(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
            )
            
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }
            
            if (signatures != null && signatures.isNotEmpty()) {
                val signature = signatures[0]
                val md = MessageDigest.getInstance("SHA-512")
                md.update(signature.toByteArray())
                md.digest().joinToString("") { "%02x".format(it) }
            } else {
                "UNSIGNED_DEBUG_BUILD"
            }
        } catch (e: Exception) {
            "APK_HASH_ERROR"
        }
    }
    
    private fun buildQrCodeData(
        reportId: String,
        caseId: String,
        integrityHash: String,
        apkHash: String,
        timestamp: Long
    ): String {
        return """
            VERUM_OMNIS_FORENSIC_REPORT
            REPORT_ID:$reportId
            CASE_ID:$caseId
            INTEGRITY_HASH:${integrityHash.take(32)}
            APK_HASH:${apkHash.take(32)}
            TIMESTAMP:$timestamp
            VERSION:${VerumOmnisApplication.VERSION}
        """.trimIndent()
    }
}
