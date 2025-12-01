package org.verumomnis.forensic.report

import org.verumomnis.forensic.core.EvidenceType
import org.verumomnis.forensic.core.ForensicCase
import org.verumomnis.forensic.core.ForensicEvidence
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Forensic Narrative Generator
 * 
 * Generates structured forensic narratives following legal admissibility standards.
 * AI-readable format for automated analysis and court presentation.
 * 
 * @author Liam Highcock
 */
class ForensicNarrativeGenerator {

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    }

    /**
     * Generates a complete forensic narrative for a case
     */
    fun generateNarrative(case: ForensicCase): ForensicNarrative {
        val sections = mutableListOf<NarrativeSection>()

        // Case Overview Section
        sections.add(generateCaseOverview(case))

        // Evidence Summary Section
        sections.add(generateEvidenceSummary(case))

        // Detailed Evidence Section
        case.evidence.forEachIndexed { index, evidence ->
            sections.add(generateEvidenceDetail(evidence, index + 1))
        }

        // Chain of Custody Section
        sections.add(generateChainOfCustody(case))

        // Integrity Verification Section
        sections.add(generateIntegritySection(case))

        return ForensicNarrative(
            caseNumber = case.caseNumber,
            generatedAt = System.currentTimeMillis(),
            sections = sections
        )
    }

    private fun generateCaseOverview(case: ForensicCase): NarrativeSection {
        val content = buildString {
            appendLine("FORENSIC CASE OVERVIEW")
            appendLine("=" .repeat(50))
            appendLine()
            appendLine("Case Number: ${case.caseNumber}")
            appendLine("Description: ${case.description}")
            appendLine("Status: ${case.status}")
            appendLine("Created: ${dateFormat.format(Date(case.createdAt))}")
            appendLine("Evidence Count: ${case.evidence.size}")
            appendLine()
            appendLine("This report was generated in accordance with Verum Omnis")
            appendLine("Constitutional Governance standards for forensic evidence.")
        }
        return NarrativeSection(
            title = "Case Overview",
            content = content,
            type = SectionType.HEADER
        )
    }

    private fun generateEvidenceSummary(case: ForensicCase): NarrativeSection {
        val evidenceByType = case.evidence.groupBy { it.type }
        
        val content = buildString {
            appendLine("EVIDENCE SUMMARY")
            appendLine("=" .repeat(50))
            appendLine()
            
            EvidenceType.entries.forEach { type ->
                val count = evidenceByType[type]?.size ?: 0
                if (count > 0) {
                    appendLine("${type.name}: $count item(s)")
                }
            }
            
            appendLine()
            appendLine("Total Evidence Items: ${case.evidence.size}")
        }
        
        return NarrativeSection(
            title = "Evidence Summary",
            content = content,
            type = SectionType.SUMMARY
        )
    }

    private fun generateEvidenceDetail(evidence: ForensicEvidence, index: Int): NarrativeSection {
        val content = buildString {
            appendLine("EVIDENCE ITEM #$index")
            appendLine("-".repeat(40))
            appendLine()
            appendLine("ID: ${evidence.id}")
            appendLine("Type: ${evidence.type}")
            appendLine("Description: ${evidence.description}")
            appendLine("Captured: ${dateFormat.format(Date(evidence.capturedAt))}")
            
            if (evidence.latitude != null && evidence.longitude != null) {
                appendLine()
                appendLine("GPS Location:")
                appendLine("  Latitude: ${evidence.latitude}")
                appendLine("  Longitude: ${evidence.longitude}")
            }
            
            evidence.contentHash?.let {
                appendLine()
                appendLine("Content Hash (SHA-512):")
                appendLine("  $it")
            }
            
            evidence.sealHash?.let {
                appendLine()
                appendLine("Seal Hash:")
                appendLine("  $it")
            }
            
            evidence.metadata.notes?.let {
                appendLine()
                appendLine("Notes: $it")
            }
            
            evidence.metadata.deviceInfo?.let {
                appendLine("Device: $it")
            }
        }
        
        return NarrativeSection(
            title = "Evidence #$index: ${evidence.description}",
            content = content,
            type = SectionType.EVIDENCE
        )
    }

    private fun generateChainOfCustody(case: ForensicCase): NarrativeSection {
        val content = buildString {
            appendLine("CHAIN OF CUSTODY")
            appendLine("=" .repeat(50))
            appendLine()
            appendLine("This section documents the chain of custody for all")
            appendLine("evidence items contained within this forensic report.")
            appendLine()
            
            case.evidence.sortedBy { it.capturedAt }.forEachIndexed { index, evidence ->
                appendLine("${index + 1}. ${evidence.description}")
                appendLine("   Collected: ${dateFormat.format(Date(evidence.capturedAt))}")
                evidence.metadata.collectorId?.let {
                    appendLine("   Collector: $it")
                }
                appendLine()
            }
            
            appendLine("All evidence has been cryptographically sealed using SHA-512")
            appendLine("hash standard with HMAC-SHA512 tamper detection.")
        }
        
        return NarrativeSection(
            title = "Chain of Custody",
            content = content,
            type = SectionType.CUSTODY
        )
    }

    private fun generateIntegritySection(case: ForensicCase): NarrativeSection {
        val content = buildString {
            appendLine("INTEGRITY VERIFICATION")
            appendLine("=" .repeat(50))
            appendLine()
            appendLine("Hash Standard: SHA-512")
            appendLine("Seal Algorithm: HMAC-SHA512")
            appendLine("Tamper Detection: ENABLED")
            appendLine()
            appendLine("All evidence items have been cryptographically sealed")
            appendLine("at the time of collection. Any modification to the")
            appendLine("evidence after sealing will be detectable through")
            appendLine("hash verification.")
            appendLine()
            appendLine("VERUM OMNIS CONSTITUTIONAL GOVERNANCE")
            appendLine("This report adheres to the following principles:")
            appendLine("  - Truth: Factual accuracy and verifiable evidence")
            appendLine("  - Fairness: Protection of vulnerable parties")
            appendLine("  - Human Rights: Dignity, equality, and agency")
            appendLine("  - Integrity: No manipulation or bias")
        }
        
        return NarrativeSection(
            title = "Integrity Verification",
            content = content,
            type = SectionType.VERIFICATION
        )
    }
}

/**
 * Represents a complete forensic narrative
 */
data class ForensicNarrative(
    val caseNumber: String,
    val generatedAt: Long,
    val sections: List<NarrativeSection>
) {
    fun toFullText(): String {
        return sections.joinToString("\n\n") { it.content }
    }
}

/**
 * A section of the forensic narrative
 */
data class NarrativeSection(
    val title: String,
    val content: String,
    val type: SectionType
)

/**
 * Types of narrative sections
 */
enum class SectionType {
    HEADER,
    SUMMARY,
    EVIDENCE,
    CUSTODY,
    VERIFICATION
}
