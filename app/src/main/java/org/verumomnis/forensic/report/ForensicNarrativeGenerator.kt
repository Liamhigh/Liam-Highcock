package org.verumomnis.forensic.report

import org.verumomnis.forensic.core.*
import org.verumomnis.forensic.leveler.LevelerEngine
import java.text.SimpleDateFormat
import java.util.*

/**
 * Forensic Narrative Generator
 * 
 * Generates AI-readable forensic narratives following legal admissibility standards.
 * Produces structured reports with chain of custody documentation.
 * 
 * @author Liam Highcock
 * @version 1.1.0
 */
class ForensicNarrativeGenerator {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US)

    /**
     * Generate full forensic narrative for a case
     */
    fun generateNarrative(forensicCase: ForensicCase): String {
        return buildString {
            appendLine(generateHeader(forensicCase))
            appendLine()
            appendLine(generateExecutiveSummary(forensicCase))
            appendLine()
            appendLine(generateEvidenceInventory(forensicCase))
            appendLine()
            appendLine(generateChainOfCustody(forensicCase))
            appendLine()
            appendLine(generateIntegrityStatement(forensicCase))
            appendLine()
            appendLine(generateVerificationInstructions(forensicCase))
            appendLine()
            appendLine(generateFooter())
        }
    }

    /**
     * Generate enhanced narrative with Leveler analysis
     */
    fun generateNarrativeWithLeveler(
        forensicCase: ForensicCase,
        levelerResult: LevelerEngine.EnhancedLevelerResult
    ): String {
        return buildString {
            appendLine(generateHeader(forensicCase))
            appendLine()
            appendLine(generateExecutiveSummary(forensicCase))
            appendLine()
            
            // Add Leveler Analysis Section
            appendLine(generateLevelerAnalysis(levelerResult))
            appendLine()
            
            appendLine(generateEvidenceInventory(forensicCase))
            appendLine()
            appendLine(generateChainOfCustody(forensicCase))
            appendLine()
            appendLine(generateIntegrityStatement(forensicCase))
            appendLine()
            
            // Add Leveler Recommendations
            appendLine(generateLevelerRecommendations(levelerResult))
            appendLine()
            
            appendLine(generateVerificationInstructions(forensicCase))
            appendLine()
            appendLine(generateFooter())
        }
    }

    /**
     * Generate Leveler analysis section (B1-B9)
     */
    private fun generateLevelerAnalysis(result: LevelerEngine.EnhancedLevelerResult): String {
        return buildString {
            appendLine("B1-B9 LEVELER ANALYSIS")
            appendLine("══════════════════════")
            appendLine()
            
            // B9: Integrity Score (shown first as overall assessment)
            appendLine("┌─────────────────────────────────────────────────────────────────┐")
            appendLine("│ B9: INTEGRITY INDEX SCORE")
            appendLine("├─────────────────────────────────────────────────────────────────┤")
            appendLine("│ SCORE: ${String.format("%.2f", result.integrityScore)}/100")
            val category = ForensicConstants.IntegrityScore.getCategory(result.integrityScore)
            val statusIcon = when (category) {
                "EXCELLENT" -> "✓"
                "GOOD" -> ""
                "FAIR" -> ""
                else -> "⚠"
            }
            appendLine("│ CATEGORY: $category $statusIcon")
            appendLine("│ CONFIDENCE: ${String.format("%.1f", result.confidence * 100)}%")
            appendLine("└─────────────────────────────────────────────────────────────────┘")
            appendLine()
            
            // B2: Contradictions
            if (result.contradictions.isNotEmpty()) {
                appendLine("┌─────────────────────────────────────────────────────────────────┐")
                appendLine("│ B2: CONTRADICTION DETECTION")
                appendLine("├─────────────────────────────────────────────────────────────────┤")
                appendLine("│ TOTAL CONTRADICTIONS FOUND: ${result.contradictions.size}")
                appendLine("│")
                result.contradictions.take(5).forEach { contradiction ->
                    appendLine("│ • TYPE: ${contradiction.type.name}")
                    appendLine("│   SEVERITY: ${contradiction.severity.name}")
                    appendLine("│   RULE: ${contradiction.ruleViolated}")
                    appendLine("│")
                }
                if (result.contradictions.size > 5) {
                    appendLine("│ ... and ${result.contradictions.size - 5} more")
                    appendLine("│")
                }
                appendLine("└─────────────────────────────────────────────────────────────────┘")
                appendLine()
            }
            
            // B3: Evidence Gaps
            if (result.missingEvidence.isNotEmpty()) {
                appendLine("┌─────────────────────────────────────────────────────────────────┐")
                appendLine("│ B3: MISSING EVIDENCE GAP ANALYSIS")
                appendLine("├─────────────────────────────────────────────────────────────────┤")
                appendLine("│ EVIDENCE GAPS DETECTED: ${result.missingEvidence.size}")
                appendLine("│")
                result.missingEvidence.forEach { gap ->
                    appendLine("│ • ${gap.type.uppercase()}")
                    appendLine("│   Criticality: ${gap.criticality.name}")
                    appendLine("│   Action: ${gap.recommendedAction}")
                    appendLine("│")
                }
                appendLine("└─────────────────────────────────────────────────────────────────┘")
                appendLine()
            }
            
            // B4: Timeline Anomalies
            if (result.timelineAnomalies.isNotEmpty()) {
                appendLine("┌─────────────────────────────────────────────────────────────────┐")
                appendLine("│ B4: TIMELINE MANIPULATION DETECTION")
                appendLine("├─────────────────────────────────────────────────────────────────┤")
                appendLine("│ ANOMALIES DETECTED: ${result.timelineAnomalies.size}")
                appendLine("│")
                result.timelineAnomalies.forEach { anomaly ->
                    appendLine("│ • TYPE: ${anomaly.type.name}")
                    appendLine("│   Document: ${anomaly.documentId}")
                    appendLine("│   Suspicion: ${String.format("%.0f", anomaly.suspicionScore * 100)}%")
                    appendLine("│")
                }
                appendLine("└─────────────────────────────────────────────────────────────────┘")
                appendLine()
            }
            
            // B5: Behavioral Patterns
            if (result.behavioralPatterns.isNotEmpty()) {
                appendLine("┌─────────────────────────────────────────────────────────────────┐")
                appendLine("│ B5: BEHAVIORAL PATTERN RECOGNITION")
                appendLine("├─────────────────────────────────────────────────────────────────┤")
                appendLine("│ PATTERNS DETECTED: ${result.behavioralPatterns.size}")
                appendLine("│")
                result.behavioralPatterns.forEach { pattern ->
                    appendLine("│ • ${pattern.type.name}")
                    appendLine("│   Match: ${String.format("%.0f", pattern.score * 100)}%")
                    appendLine("│   Frequency: ${pattern.frequency} occurrences")
                    appendLine("│")
                }
                appendLine("└─────────────────────────────────────────────────────────────────┘")
                appendLine()
            }
            
            // B6: Financial Discrepancies
            if (result.financialDiscrepancies.isNotEmpty()) {
                appendLine("┌─────────────────────────────────────────────────────────────────┐")
                appendLine("│ B6: FINANCIAL TRANSACTION CORRELATION")
                appendLine("├─────────────────────────────────────────────────────────────────┤")
                appendLine("│ DISCREPANCIES FOUND: ${result.financialDiscrepancies.size}")
                appendLine("│")
                result.financialDiscrepancies.forEach { discrepancy ->
                    appendLine("│ • ${discrepancy.type.name}")
                    appendLine("│   ${discrepancy.description}")
                    appendLine("│   Severity: ${discrepancy.severity.name}")
                    appendLine("│")
                }
                appendLine("└─────────────────────────────────────────────────────────────────┘")
                appendLine()
            }
            
            // B7: Communication Patterns
            if (result.communicationPatterns.isNotEmpty()) {
                appendLine("┌─────────────────────────────────────────────────────────────────┐")
                appendLine("│ B7: COMMUNICATION PATTERN ANALYSIS")
                appendLine("├─────────────────────────────────────────────────────────────────┤")
                appendLine("│ PATTERNS IDENTIFIED: ${result.communicationPatterns.size}")
                appendLine("│")
                result.communicationPatterns.forEach { pattern ->
                    appendLine("│ • ${pattern.type.name}")
                    appendLine("│   Score: ${String.format("%.0f", pattern.score * 100)}%")
                    appendLine("│   Frequency: ${pattern.frequency}")
                    appendLine("│")
                }
                appendLine("└─────────────────────────────────────────────────────────────────┘")
                appendLine()
            }
            
            // B8: Jurisdictional Compliance
            appendLine("┌─────────────────────────────────────────────────────────────────┐")
            appendLine("│ B8: JURISDICTIONAL COMPLIANCE CHECK")
            appendLine("├─────────────────────────────────────────────────────────────────┤")
            result.jurisdictionalCompliance.forEach { compliance ->
                val status = if (compliance.isCompliant) "✓ COMPLIANT" else "⚠ NON-COMPLIANT"
                appendLine("│ ${compliance.jurisdiction.name}: $status")
                appendLine("│   Score: ${String.format("%.1f", compliance.score)}%")
                if (compliance.violations.isNotEmpty()) {
                    appendLine("│   Violations: ${compliance.violations.size}")
                    compliance.violations.take(2).forEach { violation ->
                        appendLine("│     - ${violation.rule}: ${violation.severity.name}")
                    }
                }
                appendLine("│")
            }
            appendLine("└─────────────────────────────────────────────────────────────────┘")
        }
    }

    /**
     * Generate Leveler recommendations
     */
    private fun generateLevelerRecommendations(result: LevelerEngine.EnhancedLevelerResult): String {
        return buildString {
            appendLine("FORENSIC RECOMMENDATIONS")
            appendLine("────────────────────────")
            appendLine()
            if (result.recommendations.isNotEmpty()) {
                appendLine("Based on the Leveler analysis, the following actions are recommended:")
                appendLine()
                result.recommendations.forEachIndexed { index, recommendation ->
                    appendLine("${index + 1}. $recommendation")
                }
            } else {
                appendLine("No critical issues detected. Evidence chain appears intact and compliant.")
            }
        }
    }

    /**
     * Generate report header
     */
    private fun generateHeader(forensicCase: ForensicCase): String {
        return buildString {
            appendLine("═══════════════════════════════════════════════════════════════════")
            appendLine("                    VERUM OMNIS FORENSIC REPORT")
            appendLine("═══════════════════════════════════════════════════════════════════")
            appendLine()
            appendLine("CASE REFERENCE: ${forensicCase.id}")
            appendLine("CASE NAME: ${forensicCase.name}")
            appendLine("REPORT GENERATED: ${dateFormat.format(Date())}")
            appendLine("STATUS: ${forensicCase.status.name}")
            appendLine()
            appendLine("CONSTITUTIONAL GOVERNANCE: ACTIVE")
            appendLine("CONSTITUTION VERSION: ${VerumOmnisApplication.CONSTITUTION_VERSION}")
            appendLine()
            appendLine("───────────────────────────────────────────────────────────────────")
        }
    }

    /**
     * Generate executive summary
     */
    private fun generateExecutiveSummary(forensicCase: ForensicCase): String {
        return buildString {
            appendLine("EXECUTIVE SUMMARY")
            appendLine("─────────────────")
            appendLine()
            appendLine("This forensic report documents evidence collected under the")
            appendLine("Verum Omnis Constitutional Governance Framework, ensuring:")
            appendLine()
            appendLine("  • Truth: All evidence is factually accurate and verifiable")
            appendLine("  • Fairness: Protection of vulnerable parties maintained")
            appendLine("  • Human Rights: Dignity, equality, and agency respected")
            appendLine("  • Non-Extraction: No sensitive data transmitted externally")
            appendLine("  • Integrity: No manipulation or bias in evidence handling")
            appendLine()
            
            if (forensicCase.description.isNotEmpty()) {
                appendLine("CASE DESCRIPTION:")
                appendLine(forensicCase.description)
                appendLine()
            }
            
            appendLine("EVIDENCE STATISTICS:")
            appendLine("  Total Evidence Items: ${forensicCase.evidence.size}")
            appendLine("  Sealed Items: ${forensicCase.evidence.count { it.sealed }}")
            
            val byType = forensicCase.evidence.groupBy { it.type }
            byType.forEach { (type, items) ->
                appendLine("  ${type.name}: ${items.size} item(s)")
            }
            
            appendLine()
            appendLine("COLLECTION PERIOD:")
            if (forensicCase.evidence.isNotEmpty()) {
                val earliest = forensicCase.evidence.minByOrNull { it.timestamp }
                val latest = forensicCase.evidence.maxByOrNull { it.timestamp }
                appendLine("  First Evidence: ${dateFormat.format(Date(earliest!!.timestamp))}")
                appendLine("  Last Evidence: ${dateFormat.format(Date(latest!!.timestamp))}")
            } else {
                appendLine("  No evidence collected")
            }
        }
    }

    /**
     * Generate evidence inventory
     */
    private fun generateEvidenceInventory(forensicCase: ForensicCase): String {
        return buildString {
            appendLine("EVIDENCE INVENTORY")
            appendLine("──────────────────")
            appendLine()
            
            if (forensicCase.evidence.isEmpty()) {
                appendLine("No evidence items in this case.")
                return@buildString
            }
            
            forensicCase.evidence.sortedBy { it.timestamp }.forEachIndexed { index, evidence ->
                appendLine("┌─────────────────────────────────────────────────────────────────┐")
                appendLine("│ EVIDENCE ITEM #${index + 1}")
                appendLine("├─────────────────────────────────────────────────────────────────┤")
                appendLine("│ ID: ${evidence.id}")
                appendLine("│ TYPE: ${evidence.type.name}")
                appendLine("│ FILENAME: ${evidence.metadata.filename ?: "N/A"}")
                appendLine("│ SIZE: ${formatFileSize(evidence.metadata.fileSize)}")
                appendLine("│ MIME TYPE: ${evidence.mimeType}")
                appendLine("│ COLLECTED: ${dateFormat.format(Date(evidence.timestamp))}")
                appendLine("│")
                appendLine("│ CONTENT HASH (SHA-512):")
                appendLine("│   ${evidence.contentHash.take(64)}")
                appendLine("│   ${evidence.contentHash.drop(64)}")
                appendLine("│")
                appendLine("│ SEAL STATUS: ${if (evidence.sealed) "SEALED ✓" else "UNSEALED"}")
                if (evidence.sealed && evidence.sealHash != null) {
                    appendLine("│ SEAL HASH:")
                    appendLine("│   ${evidence.sealHash.take(64)}")
                    appendLine("│   ${evidence.sealHash.drop(64)}")
                }
                appendLine("│")
                appendLine("│ GEOLOCATION:")
                evidence.location?.let { loc ->
                    appendLine("│   Latitude: ${loc.latitude}")
                    appendLine("│   Longitude: ${loc.longitude}")
                    appendLine("│   Accuracy: ±${loc.accuracy}m")
                } ?: appendLine("│   Not captured")
                appendLine("│")
                appendLine("│ DEVICE: ${evidence.metadata.deviceInfo}")
                appendLine("│ APP VERSION: ${evidence.metadata.appVersion}")
                appendLine("└─────────────────────────────────────────────────────────────────┘")
                appendLine()
            }
        }
    }

    /**
     * Generate chain of custody documentation
     */
    private fun generateChainOfCustody(forensicCase: ForensicCase): String {
        return buildString {
            appendLine("CHAIN OF CUSTODY")
            appendLine("────────────────")
            appendLine()
            appendLine("This section documents the handling and custody of evidence.")
            appendLine()
            appendLine("CUSTODY EVENTS:")
            appendLine()
            
            // Case creation
            appendLine("1. CASE CREATION")
            appendLine("   Time: ${dateFormat.format(Date(forensicCase.createdAt))}")
            appendLine("   Action: Forensic case created")
            appendLine("   Case ID: ${forensicCase.id}")
            appendLine()
            
            // Evidence collection events
            forensicCase.evidence.sortedBy { it.timestamp }.forEachIndexed { index, evidence ->
                appendLine("${index + 2}. EVIDENCE COLLECTION")
                appendLine("   Time: ${dateFormat.format(Date(evidence.timestamp))}")
                appendLine("   Action: ${evidence.type.name} evidence captured")
                appendLine("   Evidence ID: ${evidence.id}")
                appendLine("   Hash: ${evidence.contentHash.take(32)}...")
                appendLine()
            }
            
            // Case sealing
            if (forensicCase.status == CaseStatus.SEALED || 
                forensicCase.status == CaseStatus.REPORTED) {
                appendLine("${forensicCase.evidence.size + 2}. CASE SEALED")
                appendLine("   Time: ${dateFormat.format(Date(forensicCase.modifiedAt))}")
                appendLine("   Action: Case cryptographically sealed")
                appendLine("   Integrity Hash: ${forensicCase.integrityHash?.take(32) ?: "N/A"}...")
                appendLine()
            }
            
            appendLine("CUSTODY CERTIFICATION:")
            appendLine("I certify that the evidence described herein has been handled")
            appendLine("in accordance with the Verum Omnis Constitutional Governance")
            appendLine("Framework and forensic best practices.")
        }
    }

    /**
     * Generate integrity statement
     */
    private fun generateIntegrityStatement(forensicCase: ForensicCase): String {
        return buildString {
            appendLine("INTEGRITY STATEMENT")
            appendLine("───────────────────")
            appendLine()
            appendLine("CRYPTOGRAPHIC STANDARDS:")
            appendLine("  Hash Algorithm: ${VerumOmnisApplication.HASH_STANDARD}")
            appendLine("  Seal Algorithm: HMAC-SHA512")
            appendLine("  PDF Standard: ${VerumOmnisApplication.PDF_STANDARD}")
            appendLine()
            appendLine("CASE INTEGRITY:")
            forensicCase.integrityHash?.let {
                appendLine("  Case Integrity Hash:")
                appendLine("    ${it.take(64)}")
                appendLine("    ${it.drop(64)}")
            } ?: appendLine("  Case not yet sealed")
            appendLine()
            appendLine("TAMPER DETECTION:")
            appendLine("  If any evidence has been modified after sealing, the integrity")
            appendLine("  verification will fail. Compare the hashes above with the")
            appendLine("  independently calculated values to verify authenticity.")
        }
    }

    /**
     * Generate verification instructions
     */
    private fun generateVerificationInstructions(forensicCase: ForensicCase): String {
        return buildString {
            appendLine("VERIFICATION INSTRUCTIONS")
            appendLine("─────────────────────────")
            appendLine()
            appendLine("To independently verify this forensic report:")
            appendLine()
            appendLine("1. VERIFY APK INTEGRITY")
            appendLine("   - Calculate SHA-512 hash of the Verum Omnis APK")
            appendLine("   - Compare with the APK hash in the QR code")
            appendLine()
            appendLine("2. VERIFY EVIDENCE HASHES")
            appendLine("   - For each evidence item, recalculate the SHA-512 hash")
            appendLine("   - Compare with the content hash listed above")
            appendLine()
            appendLine("3. VERIFY CASE INTEGRITY")
            appendLine("   - Reconstruct the case data structure")
            appendLine("   - Calculate SHA-512 hash of combined evidence")
            appendLine("   - Compare with case integrity hash")
            appendLine()
            appendLine("4. SCAN QR CODE")
            appendLine("   - QR code contains report metadata and verification data")
            appendLine("   - Use any standard QR scanner to decode")
            appendLine()
            appendLine("WARNING:")
            appendLine("If any hash does not match, the evidence may have been")
            appendLine("tampered with and should be treated as compromised.")
        }
    }

    /**
     * Generate report footer
     */
    private fun generateFooter(): String {
        return buildString {
            appendLine("═══════════════════════════════════════════════════════════════════")
            appendLine("                         END OF REPORT")
            appendLine("═══════════════════════════════════════════════════════════════════")
            appendLine()
            appendLine("Generated by: Verum Omnis Forensic Engine v${VerumOmnisApplication.VERSION}")
            appendLine("Creator: Liam Highcock")
            appendLine("© ${Calendar.getInstance().get(Calendar.YEAR)} Verum Global Foundation")
            appendLine()
            appendLine("This report was generated offline with no external data transmission.")
            appendLine("AI FORENSICS FOR TRUTH")
        }
    }

    /**
     * Format file size for display
     */
    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }
}
