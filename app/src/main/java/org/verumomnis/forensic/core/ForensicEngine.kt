package org.verumomnis.forensic.core

import android.content.Context
import org.verumomnis.forensic.crypto.CryptographicSealingEngine
import org.verumomnis.forensic.location.ForensicLocationService
import org.verumomnis.forensic.pdf.ForensicPdfGenerator
import org.verumomnis.forensic.report.ForensicNarrativeGenerator
import java.time.LocalDateTime
import java.util.UUID

/**
 * Core Forensic Engine
 * 
 * Implements the Verum Omnis forensic collection and analysis system.
 * All operations are performed offline with no external data transmission.
 * 
 * Features:
 * - SHA-512 evidence hashing
 * - HMAC-SHA512 cryptographic sealing
 * - GPS location tagging
 * - PDF report generation with watermarks and QR codes
 * - Tamper detection
 * - Legal-grade admissibility standards
 */
class ForensicEngine(private val context: Context) {

    private val sealingEngine = CryptographicSealingEngine()
    private val locationService = ForensicLocationService(context)
    private val pdfGenerator = ForensicPdfGenerator(context)
    private val narrativeGenerator = ForensicNarrativeGenerator()

    private val cases = mutableMapOf<String, ForensicCase>()

    /**
     * Creates a new forensic case
     */
    fun createCase(name: String, description: String = "", jurisdiction: String = "UAE"): ForensicCase {
        val case = ForensicCase(
            name = name,
            description = description,
            jurisdiction = jurisdiction
        )
        cases[case.id] = case
        return case
    }

    /**
     * Gets a case by ID
     */
    fun getCase(caseId: String): ForensicCase? = cases[caseId]

    /**
     * Gets all cases
     */
    fun getAllCases(): List<ForensicCase> = cases.values.toList()

    /**
     * Adds evidence to a case with cryptographic sealing
     */
    suspend fun addEvidence(
        caseId: String,
        type: EvidenceType,
        content: ByteArray,
        contentDescription: String,
        metadata: EvidenceMetadata = EvidenceMetadata()
    ): ForensicEvidence? {
        val case = cases[caseId] ?: return null

        // Get current location if available
        val location = locationService.getCurrentLocation()

        // Generate hash and seal
        val hash = sealingEngine.generateHash(content)
        val seal = sealingEngine.generateSeal(content, caseId)

        val evidence = ForensicEvidence(
            type = type,
            content = content,
            contentDescription = contentDescription,
            location = location,
            hash = hash,
            seal = seal,
            metadata = metadata
        )

        case.evidence.add(evidence)
        return evidence
    }

    /**
     * Verifies the integrity of evidence
     */
    fun verifyEvidence(evidence: ForensicEvidence, caseId: String): Boolean {
        val expectedHash = sealingEngine.generateHash(evidence.content)
        if (expectedHash != evidence.hash) {
            return false
        }

        return sealingEngine.verifySeal(evidence.content, caseId, evidence.seal)
    }

    /**
     * Generates a forensic report for a case
     */
    suspend fun generateReport(caseId: String): ForensicResult? {
        val case = cases[caseId] ?: return null

        // Analyze all evidence
        val findings = analyzeEvidence(case)

        // Calculate integrity score
        val integrityScore = calculateIntegrityScore(case, findings)

        // Generate narrative
        val narrative = narrativeGenerator.generateNarrative(case, findings)

        // Generate PDF report
        val pdfPath = pdfGenerator.generateReport(case, findings, narrative)

        // Generate recommendations
        val recommendations = generateRecommendations(findings)

        return ForensicResult(
            caseId = caseId,
            evidenceCount = case.evidence.size,
            integrityScore = integrityScore,
            findings = findings,
            recommendations = recommendations,
            pdfReportPath = pdfPath
        )
    }

    /**
     * Analyzes evidence for contradictions, anomalies, and patterns
     */
    private fun analyzeEvidence(case: ForensicCase): List<Finding> {
        val findings = mutableListOf<Finding>()

        // Check evidence integrity
        for (evidence in case.evidence) {
            if (!verifyEvidence(evidence, case.id)) {
                findings.add(
                    Finding(
                        type = FindingType.DATA_INTEGRITY_ISSUE,
                        severity = Severity.CRITICAL,
                        description = "Evidence tampered: ${evidence.contentDescription}",
                        evidenceIds = listOf(evidence.id)
                    )
                )
            }
        }

        // Check for timeline anomalies
        val sortedEvidence = case.evidence.sortedBy { it.timestamp }
        for (i in 1 until sortedEvidence.size) {
            val gap = java.time.Duration.between(
                sortedEvidence[i - 1].timestamp,
                sortedEvidence[i].timestamp
            ).toDays()
            
            if (gap > 30) {
                findings.add(
                    Finding(
                        type = FindingType.EVIDENCE_GAP,
                        severity = Severity.MEDIUM,
                        description = "Evidence gap of $gap days detected",
                        evidenceIds = listOf(sortedEvidence[i - 1].id, sortedEvidence[i].id)
                    )
                )
            }
        }

        return findings
    }

    /**
     * Calculates the integrity score (0-100)
     */
    private fun calculateIntegrityScore(case: ForensicCase, findings: List<Finding>): Int {
        var score = 100

        for (finding in findings) {
            score -= when (finding.severity) {
                Severity.CRITICAL -> 25
                Severity.HIGH -> 15
                Severity.MEDIUM -> 10
                Severity.LOW -> 5
            }
        }

        return maxOf(0, score)
    }

    /**
     * Generates recommendations based on findings
     */
    private fun generateRecommendations(findings: List<Finding>): List<String> {
        val recommendations = mutableListOf<String>()

        if (findings.any { it.type == FindingType.DATA_INTEGRITY_ISSUE }) {
            recommendations.add("Investigate evidence tampering and secure evidence chain")
        }

        if (findings.any { it.type == FindingType.EVIDENCE_GAP }) {
            recommendations.add("Review timeline gaps and collect additional supporting evidence")
        }

        if (findings.any { it.type == FindingType.CONTRADICTION }) {
            recommendations.add("Cross-reference contradictory statements with documentary evidence")
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Evidence chain is intact. Proceed with case presentation.")
        }

        return recommendations
    }

    /**
     * Closes a case
     */
    fun closeCase(caseId: String): Boolean {
        val case = cases[caseId] ?: return false
        cases[caseId] = case.copy(status = CaseStatus.CLOSED)
        return true
    }

    /**
     * Deletes a case (for stateless operation)
     */
    fun deleteCase(caseId: String): Boolean {
        return cases.remove(caseId) != null
    }
}
