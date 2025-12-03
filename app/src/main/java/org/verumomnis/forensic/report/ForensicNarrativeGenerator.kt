package org.verumomnis.forensic.report

import org.verumomnis.forensic.core.Finding
import org.verumomnis.forensic.core.FindingType
import org.verumomnis.forensic.core.ForensicCase
import org.verumomnis.forensic.core.Severity
import java.time.format.DateTimeFormatter

/**
 * Forensic Narrative Generator
 * 
 * Generates structured forensic narratives following legal admissibility standards.
 * Outputs are AI-readable and suitable for court presentation.
 */
class ForensicNarrativeGenerator {

    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' HH:mm:ss")

    /**
     * Generates a complete forensic narrative for a case
     */
    fun generateNarrative(case: ForensicCase, findings: List<Finding>): String {
        val builder = StringBuilder()
        
        // Opening statement
        builder.append(generateOpeningStatement(case))
        
        // Evidence summary
        builder.append("\n\n")
        builder.append(generateEvidenceSummary(case))
        
        // Timeline reconstruction
        builder.append("\n\n")
        builder.append(generateTimeline(case))
        
        // Findings analysis
        if (findings.isNotEmpty()) {
            builder.append("\n\n")
            builder.append(generateFindingsAnalysis(findings))
        }
        
        // Integrity assessment
        builder.append("\n\n")
        builder.append(generateIntegrityAssessment(case, findings))
        
        // Closing statement
        builder.append("\n\n")
        builder.append(generateClosingStatement(case, findings))
        
        return builder.toString()
    }

    private fun generateOpeningStatement(case: ForensicCase): String {
        return """
            FORENSIC EXAMINATION REPORT
            
            This report documents the forensic examination conducted for case "${case.name}" 
            (Case ID: ${case.id}), initiated on ${case.createdAt.format(dateFormatter)}. 
            The examination was performed in accordance with the Verum Omnis Constitutional 
            Governance Layer, ensuring adherence to principles of Truth, Fairness, and 
            Human Rights throughout the forensic process.
            
            Jurisdiction: ${case.jurisdiction}
            Current Status: ${case.status.name}
        """.trimIndent()
    }

    private fun generateEvidenceSummary(case: ForensicCase): String {
        val evidenceByType = case.evidence.groupBy { it.type }
        
        val typeBreakdown = evidenceByType.entries.joinToString(", ") { (type, items) ->
            "${items.size} ${type.name.lowercase()}(s)"
        }
        
        return """
            EVIDENCE SUMMARY
            
            A total of ${case.evidence.size} piece(s) of evidence were collected and sealed 
            using SHA-512 cryptographic hashing with HMAC-SHA512 sealing for tamper detection.
            
            Evidence breakdown: $typeBreakdown
            
            All evidence has been processed through the Verum Omnis Forensic Engine and 
            assigned unique cryptographic seals for chain of custody verification.
        """.trimIndent()
    }

    private fun generateTimeline(case: ForensicCase): String {
        val sortedEvidence = case.evidence.sortedBy { it.timestamp }
        
        if (sortedEvidence.isEmpty()) {
            return """
                TIMELINE RECONSTRUCTION
                
                No evidence has been collected for this case yet.
            """.trimIndent()
        }
        
        val firstEvidence = sortedEvidence.first()
        val lastEvidence = sortedEvidence.last()
        
        val timelineEntries = sortedEvidence.mapIndexed { index, evidence ->
            val locationInfo = evidence.location?.let { 
                " at coordinates (${String.format("%.6f", it.latitude)}, ${String.format("%.6f", it.longitude)})"
            } ?: ""
            
            "${index + 1}. ${evidence.timestamp.format(dateFormatter)}: ${evidence.contentDescription}$locationInfo"
        }.joinToString("\n")
        
        return """
            TIMELINE RECONSTRUCTION
            
            Evidence collection period: ${firstEvidence.timestamp.format(dateFormatter)} 
            to ${lastEvidence.timestamp.format(dateFormatter)}
            
            Chronological sequence of evidence:
            
            $timelineEntries
        """.trimIndent()
    }

    private fun generateFindingsAnalysis(findings: List<Finding>): String {
        val findingsByType = findings.groupBy { it.type }
        val findingsBySeverity = findings.groupBy { it.severity }
        
        val severitySummary = listOf(
            Severity.CRITICAL to findingsBySeverity[Severity.CRITICAL]?.size ?: 0,
            Severity.HIGH to findingsBySeverity[Severity.HIGH]?.size ?: 0,
            Severity.MEDIUM to findingsBySeverity[Severity.MEDIUM]?.size ?: 0,
            Severity.LOW to findingsBySeverity[Severity.LOW]?.size ?: 0
        ).filter { it.second > 0 }
            .joinToString(", ") { "${it.second} ${it.first.name}" }
        
        val detailedFindings = findings.mapIndexed { index, finding ->
            """
                Finding ${index + 1}:
                Type: ${finding.type.name.replace("_", " ")}
                Severity: ${finding.severity.name}
                Description: ${finding.description}
                Affected Evidence: ${finding.evidenceIds.size} item(s)
            """.trimIndent()
        }.joinToString("\n\n")
        
        return """
            FINDINGS ANALYSIS
            
            The forensic examination identified ${findings.size} finding(s) requiring attention.
            
            Severity distribution: $severitySummary
            
            Detailed findings:
            
            $detailedFindings
        """.trimIndent()
    }

    private fun generateIntegrityAssessment(case: ForensicCase, findings: List<Finding>): String {
        val integrityIssues = findings.filter { it.type == FindingType.DATA_INTEGRITY_ISSUE }
        val hasIntegrityIssues = integrityIssues.isNotEmpty()
        
        val assessment = if (hasIntegrityIssues) {
            """
                INTEGRITY STATUS: COMPROMISED
                
                ${integrityIssues.size} integrity issue(s) were detected during examination. 
                Evidence chain may have been tampered with. Additional investigation is recommended 
                before relying on affected evidence items.
            """.trimIndent()
        } else {
            """
                INTEGRITY STATUS: VERIFIED
                
                All evidence items have passed cryptographic verification. The SHA-512 hashes 
                and HMAC-SHA512 seals are intact, confirming that no tampering has occurred 
                since evidence collection.
            """.trimIndent()
        }
        
        return """
            INTEGRITY ASSESSMENT
            
            $assessment
            
            Verification standard: SHA-512 with HMAC-SHA512 sealing
            Tamper detection: Enabled
            Chain of custody: ${if (hasIntegrityIssues) "Potentially compromised" else "Intact"}
        """.trimIndent()
    }

    private fun generateClosingStatement(case: ForensicCase, findings: List<Finding>): String {
        val criticalFindings = findings.count { it.severity == Severity.CRITICAL }
        val highFindings = findings.count { it.severity == Severity.HIGH }
        
        val recommendation = when {
            criticalFindings > 0 -> {
                "Immediate attention required. Critical findings indicate significant issues " +
                "that must be addressed before proceeding with case presentation."
            }
            highFindings > 0 -> {
                "Review recommended. High-severity findings should be addressed to strengthen " +
                "the evidentiary basis of this case."
            }
            findings.isNotEmpty() -> {
                "Minor issues identified. The case may proceed with awareness of the noted findings."
            }
            else -> {
                "No significant issues identified. The evidence chain is intact and suitable " +
                "for legal proceedings."
            }
        }
        
        return """
            CONCLUSION AND RECOMMENDATIONS
            
            This forensic examination of case "${case.name}" has been completed in accordance 
            with the Verum Omnis Constitutional Governance Layer standards.
            
            Assessment: $recommendation
            
            This report was generated by the Verum Omnis Forensic Engine operating under 
            the following constitutional principles:
            - Truth: Factual accuracy and verifiable evidence
            - Fairness: Protection of vulnerable parties
            - Human Rights: Dignity, equality, and agency
            - Integrity: No manipulation or bias
            
            All findings and recommendations are provided for human review and decision-making. 
            The Verum Omnis system assists but never overrides human authority.
            
            ---
            Report generated on behalf of: Verum Global Foundation
            Creator: Liam Highcock
        """.trimIndent()
    }
}
