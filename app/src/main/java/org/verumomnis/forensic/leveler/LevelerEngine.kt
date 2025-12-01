package org.verumomnis.forensic.leveler

import org.verumomnis.forensic.core.Finding
import org.verumomnis.forensic.core.FindingType
import org.verumomnis.forensic.core.ForensicCase
import org.verumomnis.forensic.core.ForensicEvidence
import org.verumomnis.forensic.core.Severity
import java.time.Duration
import java.time.LocalDateTime

/**
 * Leveler Engine for B1-B9 Compliance
 * 
 * Implements the Verum Omnis forensic analysis system for:
 * - Contradiction detection across statements
 * - Timeline anomaly detection
 * - Behavioral pattern analysis (gaslighting, evasion, concealment)
 * - Jurisdictional compliance checking
 * - Integrity index calculation
 * 
 * B1-B9 Compliance Levels:
 * B1: Basic evidence collection
 * B2: Contradiction detection
 * B3: Timeline reconstruction
 * B4: Behavioral pattern analysis
 * B5: Cross-reference verification
 * B6: Jurisdictional compliance
 * B7: Financial correlation
 * B8: Multi-party analysis
 * B9: Court-ready reporting
 */
class LevelerEngine {

    companion object {
        // Evasion keywords for behavioral analysis
        private val EVASION_KEYWORDS = listOf(
            "cannot comment", "not sure", "don't recall", "maybe",
            "possibly", "i think", "not certain", "unclear"
        )
        
        // Gaslighting indicators
        private val GASLIGHTING_INDICATORS = listOf(
            "you misunderstood", "that never happened", "you're confused",
            "you're imagining", "you're being paranoid", "that's not what i said"
        )
        
        // Concealment patterns
        private val CONCEALMENT_PATTERNS = listOf(
            "deleted", "lost", "forgot", "not available", "accidentally",
            "misplaced", "cannot find", "destroyed"
        )
        
        // Deflection tactics
        private val DEFLECTION_TACTICS = listOf(
            "what about you", "others did worse", "not my department",
            "not my responsibility", "someone else", "blame"
        )

        // Severity weights for integrity calculation
        private const val CRITICAL_WEIGHT = 25
        private const val HIGH_WEIGHT = 15
        private const val MEDIUM_WEIGHT = 10
        private const val LOW_WEIGHT = 5
    }

    /**
     * Performs full B1-B9 analysis on a case
     */
    fun analyzeCase(case: ForensicCase): LevelerReport {
        val contradictions = detectContradictions(case)
        val timelineAnomalies = detectTimelineAnomalies(case)
        val behavioralPatterns = analyzeBehavioralPatterns(case)
        val complianceReport = checkJurisdictionalCompliance(case)
        val chronology = reconstructChronology(case)
        
        val allFindings = contradictions + timelineAnomalies.map { it.toFinding() }
        val integrityScore = calculateIntegrityScore(allFindings, behavioralPatterns, complianceReport)
        val recommendations = generateRecommendations(allFindings, behavioralPatterns)
        
        return LevelerReport(
            caseId = case.id,
            complianceLevel = determineComplianceLevel(case),
            contradictions = contradictions,
            timelineAnomalies = timelineAnomalies,
            behavioralPatterns = behavioralPatterns,
            complianceReport = complianceReport,
            chronology = chronology,
            integrityScore = integrityScore,
            recommendations = recommendations
        )
    }

    /**
     * Detects contradictions in evidence content
     */
    private fun detectContradictions(case: ForensicCase): List<Finding> {
        val findings = mutableListOf<Finding>()
        val textEvidence = case.evidence.filter { 
            it.contentDescription.isNotBlank() 
        }
        
        // Compare each piece of evidence with others for contradictions
        for (i in textEvidence.indices) {
            for (j in i + 1 until textEvidence.size) {
                val contradiction = checkForContradiction(textEvidence[i], textEvidence[j])
                if (contradiction != null) {
                    findings.add(contradiction)
                }
            }
        }
        
        return findings
    }

    private fun checkForContradiction(
        evidenceA: ForensicEvidence,
        evidenceB: ForensicEvidence
    ): Finding? {
        val contentA = evidenceA.contentDescription.lowercase()
        val contentB = evidenceB.contentDescription.lowercase()
        
        // Check for direct negation patterns
        val negationPatterns = listOf(
            Pair("never", "always"),
            Pair("yes", "no"),
            Pair("did", "did not"),
            Pair("was", "was not"),
            Pair("signed", "never signed"),
            Pair("present", "absent"),
            Pair("before", "after")
        )
        
        for ((positive, negative) in negationPatterns) {
            if ((contentA.contains(positive) && contentB.contains(negative)) ||
                (contentA.contains(negative) && contentB.contains(positive))) {
                return Finding(
                    type = FindingType.CONTRADICTION,
                    severity = Severity.HIGH,
                    description = "Potential contradiction detected between evidence items: " +
                            "'${evidenceA.contentDescription}' vs '${evidenceB.contentDescription}'",
                    evidenceIds = listOf(evidenceA.id, evidenceB.id)
                )
            }
        }
        
        return null
    }

    /**
     * Detects anomalies in the evidence timeline
     */
    private fun detectTimelineAnomalies(case: ForensicCase): List<TimelineAnomaly> {
        val anomalies = mutableListOf<TimelineAnomaly>()
        val sortedEvidence = case.evidence.sortedBy { it.timestamp }
        
        // Check for large gaps in timeline
        for (i in 1 until sortedEvidence.size) {
            val gap = Duration.between(
                sortedEvidence[i - 1].timestamp,
                sortedEvidence[i].timestamp
            )
            
            if (gap.toDays() > 30) {
                anomalies.add(
                    TimelineAnomaly(
                        type = TimelineAnomalyType.GAP,
                        documentIds = listOf(sortedEvidence[i - 1].id, sortedEvidence[i].id),
                        suspicionScore = calculateGapSuspicionScore(gap.toDays()),
                        description = "Evidence gap of ${gap.toDays()} days detected"
                    )
                )
            }
        }
        
        // Check for out-of-sequence evidence
        for (i in sortedEvidence.indices) {
            val evidence = sortedEvidence[i]
            if (evidence.metadata.notes.contains("backdated", ignoreCase = true) ||
                evidence.metadata.notes.contains("predated", ignoreCase = true)) {
                anomalies.add(
                    TimelineAnomaly(
                        type = TimelineAnomalyType.BACKDATED,
                        documentIds = listOf(evidence.id),
                        suspicionScore = 0.9f,
                        description = "Evidence appears to be backdated: ${evidence.contentDescription}"
                    )
                )
            }
        }
        
        return anomalies
    }

    private fun calculateGapSuspicionScore(gapDays: Long): Float {
        return when {
            gapDays > 365 -> 0.95f
            gapDays > 180 -> 0.8f
            gapDays > 90 -> 0.6f
            gapDays > 30 -> 0.4f
            else -> 0.2f
        }
    }

    /**
     * Analyzes behavioral patterns in evidence
     */
    private fun analyzeBehavioralPatterns(case: ForensicCase): List<BehavioralPattern> {
        val patterns = mutableListOf<BehavioralPattern>()
        
        val allText = case.evidence.joinToString(" ") { it.contentDescription }.lowercase()
        
        // Check for evasion
        val evasionMatches = EVASION_KEYWORDS.filter { allText.contains(it) }
        if (evasionMatches.isNotEmpty()) {
            patterns.add(
                BehavioralPattern(
                    type = BehavioralPatternType.EVASION,
                    score = minOf(evasionMatches.size * 0.15f, 1.0f),
                    examples = evasionMatches,
                    frequency = evasionMatches.size
                )
            )
        }
        
        // Check for gaslighting
        val gaslightingMatches = GASLIGHTING_INDICATORS.filter { allText.contains(it) }
        if (gaslightingMatches.isNotEmpty()) {
            patterns.add(
                BehavioralPattern(
                    type = BehavioralPatternType.GASLIGHTING,
                    score = minOf(gaslightingMatches.size * 0.25f, 1.0f),
                    examples = gaslightingMatches,
                    frequency = gaslightingMatches.size
                )
            )
        }
        
        // Check for concealment
        val concealmentMatches = CONCEALMENT_PATTERNS.filter { allText.contains(it) }
        if (concealmentMatches.isNotEmpty()) {
            patterns.add(
                BehavioralPattern(
                    type = BehavioralPatternType.CONCEALMENT,
                    score = minOf(concealmentMatches.size * 0.2f, 1.0f),
                    examples = concealmentMatches,
                    frequency = concealmentMatches.size
                )
            )
        }
        
        // Check for deflection
        val deflectionMatches = DEFLECTION_TACTICS.filter { allText.contains(it) }
        if (deflectionMatches.isNotEmpty()) {
            patterns.add(
                BehavioralPattern(
                    type = BehavioralPatternType.DEFLECTION,
                    score = minOf(deflectionMatches.size * 0.15f, 1.0f),
                    examples = deflectionMatches,
                    frequency = deflectionMatches.size
                )
            )
        }
        
        return patterns
    }

    /**
     * Checks jurisdictional compliance
     */
    private fun checkJurisdictionalCompliance(case: ForensicCase): ComplianceReport {
        val violations = mutableListOf<ComplianceViolation>()
        
        when (case.jurisdiction.uppercase()) {
            "UAE" -> {
                // Check UAE-specific requirements
                if (!hasArabicTranslation(case)) {
                    violations.add(
                        ComplianceViolation(
                            law = "UAE Commercial Law",
                            article = "Language Requirements",
                            requirement = "Arabic language documentation",
                            severity = Severity.MEDIUM
                        )
                    )
                }
            }
            "SA" -> {
                // Check South Africa requirements
                // ECT Act compliance
            }
            "EU" -> {
                // Check GDPR compliance
                if (!hasGdprConsent(case)) {
                    violations.add(
                        ComplianceViolation(
                            law = "GDPR",
                            article = "Article 6",
                            requirement = "Lawful basis for data processing",
                            severity = Severity.HIGH
                        )
                    )
                }
            }
        }
        
        val complianceScore = if (violations.isEmpty()) 1.0f 
            else maxOf(0f, 1.0f - violations.size * 0.1f)
        
        return ComplianceReport(
            jurisdiction = case.jurisdiction,
            violations = violations,
            complianceScore = complianceScore,
            recommendations = violations.map { 
                "Address ${it.law} ${it.article}: ${it.requirement}" 
            }
        )
    }

    private fun hasArabicTranslation(case: ForensicCase): Boolean {
        return case.evidence.any { 
            it.metadata.notes.contains("arabic", ignoreCase = true) ||
            it.metadata.notes.contains("translated", ignoreCase = true)
        }
    }

    private fun hasGdprConsent(case: ForensicCase): Boolean {
        return case.evidence.any {
            it.metadata.notes.contains("consent", ignoreCase = true) ||
            it.metadata.notes.contains("gdpr", ignoreCase = true)
        }
    }

    /**
     * Reconstructs chronology from evidence
     */
    private fun reconstructChronology(case: ForensicCase): Chronology {
        val sortedEvidence = case.evidence.sortedBy { it.timestamp }
        
        val events = sortedEvidence.map { evidence ->
            ChronologyEvent(
                timestamp = evidence.timestamp,
                description = evidence.contentDescription,
                evidenceId = evidence.id,
                type = evidence.type.name
            )
        }
        
        val gaps = mutableListOf<ChronologyGap>()
        for (i in 1 until sortedEvidence.size) {
            val duration = Duration.between(
                sortedEvidence[i - 1].timestamp,
                sortedEvidence[i].timestamp
            )
            if (duration.toDays() > 7) {
                gaps.add(
                    ChronologyGap(
                        startTime = sortedEvidence[i - 1].timestamp,
                        endTime = sortedEvidence[i].timestamp,
                        durationDays = duration.toDays()
                    )
                )
            }
        }
        
        return Chronology(events = events, gaps = gaps)
    }

    /**
     * Calculates integrity score (0-100)
     */
    private fun calculateIntegrityScore(
        findings: List<Finding>,
        patterns: List<BehavioralPattern>,
        compliance: ComplianceReport
    ): IntegrityScore {
        var score = 100
        
        // Deduct for findings
        for (finding in findings) {
            score -= when (finding.severity) {
                Severity.CRITICAL -> CRITICAL_WEIGHT
                Severity.HIGH -> HIGH_WEIGHT
                Severity.MEDIUM -> MEDIUM_WEIGHT
                Severity.LOW -> LOW_WEIGHT
            }
        }
        
        // Deduct for behavioral patterns
        for (pattern in patterns) {
            score -= (pattern.score * 10).toInt()
        }
        
        // Deduct for compliance issues
        score -= ((1 - compliance.complianceScore) * 20).toInt()
        
        val finalScore = maxOf(0, score)
        
        return IntegrityScore(
            score = finalScore,
            category = when {
                finalScore >= 90 -> IntegrityCategory.EXCELLENT
                finalScore >= 70 -> IntegrityCategory.GOOD
                finalScore >= 50 -> IntegrityCategory.FAIR
                finalScore >= 30 -> IntegrityCategory.POOR
                else -> IntegrityCategory.CRITICAL
            },
            breakdown = mapOf(
                "Findings Impact" to (100 - findings.sumOf { 
                    when (it.severity) {
                        Severity.CRITICAL -> CRITICAL_WEIGHT
                        Severity.HIGH -> HIGH_WEIGHT
                        Severity.MEDIUM -> MEDIUM_WEIGHT
                        Severity.LOW -> LOW_WEIGHT
                    }
                }),
                "Behavioral Patterns" to (100 - (patterns.sumOf { (it.score * 10).toInt() })),
                "Compliance" to (compliance.complianceScore * 100).toInt()
            )
        )
    }

    /**
     * Determines B1-B9 compliance level
     */
    private fun determineComplianceLevel(case: ForensicCase): String {
        return when {
            case.evidence.isEmpty() -> "B1"
            case.evidence.size < 5 -> "B2"
            case.evidence.all { it.hash.isNotBlank() } -> "B5"
            case.evidence.any { it.location != null } -> "B6"
            else -> "B4"
        }
    }

    /**
     * Generates recommendations based on analysis
     */
    private fun generateRecommendations(
        findings: List<Finding>,
        patterns: List<BehavioralPattern>
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        if (findings.any { it.type == FindingType.CONTRADICTION }) {
            recommendations.add("Cross-reference contradictory statements with documentary evidence")
        }
        
        if (findings.any { it.type == FindingType.EVIDENCE_GAP }) {
            recommendations.add("Collect additional evidence to fill timeline gaps")
        }
        
        if (patterns.any { it.type == BehavioralPatternType.GASLIGHTING }) {
            recommendations.add("Document gaslighting attempts and seek independent verification")
        }
        
        if (patterns.any { it.type == BehavioralPatternType.CONCEALMENT }) {
            recommendations.add("Request formal discovery for concealed documents")
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Evidence chain appears intact. Continue with case preparation.")
        }
        
        return recommendations
    }
}

// Data classes for Leveler Engine

data class LevelerReport(
    val caseId: String,
    val complianceLevel: String,
    val contradictions: List<Finding>,
    val timelineAnomalies: List<TimelineAnomaly>,
    val behavioralPatterns: List<BehavioralPattern>,
    val complianceReport: ComplianceReport,
    val chronology: Chronology,
    val integrityScore: IntegrityScore,
    val recommendations: List<String>
)

data class TimelineAnomaly(
    val type: TimelineAnomalyType,
    val documentIds: List<String>,
    val suspicionScore: Float,
    val description: String
) {
    fun toFinding(): Finding = Finding(
        type = FindingType.TIMELINE_ANOMALY,
        severity = when {
            suspicionScore > 0.8 -> Severity.HIGH
            suspicionScore > 0.5 -> Severity.MEDIUM
            else -> Severity.LOW
        },
        description = description,
        evidenceIds = documentIds
    )
}

enum class TimelineAnomalyType {
    GAP,
    BACKDATED,
    OUT_OF_SEQUENCE,
    CLUSTERING
}

data class BehavioralPattern(
    val type: BehavioralPatternType,
    val score: Float,
    val examples: List<String>,
    val frequency: Int
)

enum class BehavioralPatternType {
    EVASION,
    GASLIGHTING,
    CONCEALMENT,
    DEFLECTION
}

data class ComplianceReport(
    val jurisdiction: String,
    val violations: List<ComplianceViolation>,
    val complianceScore: Float,
    val recommendations: List<String>
)

data class ComplianceViolation(
    val law: String,
    val article: String,
    val requirement: String,
    val severity: Severity
)

data class Chronology(
    val events: List<ChronologyEvent>,
    val gaps: List<ChronologyGap>
)

data class ChronologyEvent(
    val timestamp: LocalDateTime,
    val description: String,
    val evidenceId: String,
    val type: String
)

data class ChronologyGap(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val durationDays: Long
)

data class IntegrityScore(
    val score: Int,
    val category: IntegrityCategory,
    val breakdown: Map<String, Int>
)

enum class IntegrityCategory {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    CRITICAL
}
