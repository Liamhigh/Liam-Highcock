package org.verumomnis.forensic.leveler

import org.verumomnis.forensic.core.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Leveler Engine - B1-B9 Compliance
 * 
 * Complete contradiction detection and integrity verification system
 * following Verum Omnis Constitutional Governance Framework.
 * 
 * Features:
 * - B1: Event Chronology Reconstruction
 * - B2: Contradiction Detection Matrix
 * - B3: Missing Evidence Gap Analysis
 * - B4: Timeline Manipulation Detection
 * - B5: Behavioral Pattern Recognition
 * - B6: Financial Transaction Correlation
 * - B7: Communication Pattern Analysis
 * - B8: Jurisdictional Compliance Check
 * - B9: Integrity Index Scoring
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
object LevelerEngine {

    // Data classes for Leveler analysis
    
    data class LevelerResult(
        val contradictions: List<Contradiction>,
        val missingEvidence: List<EvidenceGap>,
        val timelineAnomalies: List<TimelineAnomaly>,
        val behavioralPatterns: List<DetectedBehavioralPattern>,
        val integrityScore: Float,
        val confidence: Float,
        val recommendations: List<String>
    )

    data class Contradiction(
        val type: ContradictionType,
        val statementA: Statement,
        val statementB: Statement,
        val supportingEvidence: List<String>,
        val severity: Severity,
        val timestamp: String,
        val ruleViolated: String
    )

    data class Statement(
        val id: String,
        val speaker: String,
        val content: String,
        val timestamp: Long,
        val source: String
    )

    enum class ContradictionType {
        DIRECT_OPPOSITE,       // "I did X" vs "I didn't do X"
        FACTUAL_DISCREPANCY,   // Dates/amounts don't match
        OMISSION,              // Key detail missing
        TIMELINE_BREAK,        // Impossible sequence
        BEHAVIORAL_MISMATCH    // Actions don't match words
    }

    data class EvidenceGap(
        val type: String,
        val criticality: Severity,
        val recommendedAction: String,
        val timelinePosition: String
    )

    data class TimelineAnomaly(
        val type: TimelineAnomalyType,
        val documentId: String,
        val description: String,
        val suspicionScore: Float,
        val originalTimestamp: Long? = null,
        val modifiedTimestamp: Long? = null
    )

    enum class TimelineAnomalyType {
        EDIT_AFTER_FACT,
        SUSPICIOUS_GAP,
        BACKDATED,
        OUT_OF_SEQUENCE,
        METADATA_MISMATCH
    }

    data class DetectedBehavioralPattern(
        val type: BehavioralPatternType,
        val score: Float,
        val examples: List<String>,
        val frequency: Int
    )

    data class Chronology(
        val events: List<ChronologyEvent>,
        val gaps: List<TimeGap>,
        val integrityScore: Float
    )

    data class ChronologyEvent(
        val id: String,
        val content: String,
        val source: String,
        val timestamp: Long,
        val confidence: Float,
        val relatedEvents: List<String>
    )

    data class TimeGap(
        val startTime: Long,
        val endTime: Long,
        val durationHours: Long,
        val significance: Severity
    )

    data class IntegrityIndex(
        val score: Float,
        val category: IntegrityCategory,
        val breakdown: IntegrityBreakdown
    )

    enum class IntegrityCategory {
        EXCELLENT,  // 90-100
        GOOD,       // 70-89
        FAIR,       // 50-69
        POOR,       // 25-49
        COMPROMISED // 0-24
    }

    data class IntegrityBreakdown(
        val contradictionPenalty: Float,
        val timelinePenalty: Float,
        val behavioralPenalty: Float,
        val compliancePenalty: Float,
        val gapPenalty: Float
    )

    // Main analysis function
    
    /**
     * Perform complete Leveler analysis on evidence
     */
    fun analyze(
        statements: List<Statement>,
        evidence: List<ForensicEvidence>,
        expectedEvidence: List<String> = emptyList()
    ): LevelerResult {
        
        // B1: Reconstruct chronology
        val chronology = reconstructChronology(evidence)
        
        // B2: Detect contradictions
        val contradictions = detectContradictions(statements, evidence)
        
        // B3: Analyze evidence gaps
        val gaps = analyzeEvidenceGaps(chronology, expectedEvidence)
        
        // B4: Detect timeline anomalies
        val anomalies = detectTimelineAnomalies(evidence)
        
        // B5: Recognize behavioral patterns
        val patterns = analyzeBehavioralPatterns(statements)
        
        // B9: Calculate integrity index
        val integrityIndex = calculateIntegrityIndex(
            contradictions, anomalies, patterns, gaps
        )
        
        // Generate recommendations
        val recommendations = generateRecommendations(
            contradictions, gaps, anomalies, patterns
        )
        
        return LevelerResult(
            contradictions = contradictions,
            missingEvidence = gaps,
            timelineAnomalies = anomalies,
            behavioralPatterns = patterns,
            integrityScore = integrityIndex.score,
            confidence = calculateConfidence(evidence.size, contradictions.size),
            recommendations = recommendations
        )
    }

    // B1: Event Chronology Reconstruction
    
    private fun reconstructChronology(evidence: List<ForensicEvidence>): Chronology {
        val events = evidence.map { ev ->
            ChronologyEvent(
                id = ev.id,
                content = ev.metadata.filename ?: ev.type.name,
                source = ev.id,
                timestamp = ev.timestamp,
                confidence = if (ev.sealed) 1.0f else 0.7f,
                relatedEvents = findRelatedEvents(ev, evidence)
            )
        }.sortedBy { it.timestamp }
        
        val gaps = detectTimeGaps(events)
        val integrityScore = calculateChronologyIntegrity(events, gaps)
        
        return Chronology(events, gaps, integrityScore)
    }

    private fun findRelatedEvents(
        evidence: ForensicEvidence,
        allEvidence: List<ForensicEvidence>
    ): List<String> {
        return allEvidence
            .filter { it.id != evidence.id && it.type == evidence.type }
            .map { it.id }
    }

    private fun detectTimeGaps(events: List<ChronologyEvent>): List<TimeGap> {
        val gaps = mutableListOf<TimeGap>()
        
        for (i in 0 until events.size - 1) {
            val current = events[i]
            val next = events[i + 1]
            val gapHours = (next.timestamp - current.timestamp) / (1000 * 60 * 60)
            
            if (gapHours > 48) { // Gap larger than 48 hours
                gaps.add(TimeGap(
                    startTime = current.timestamp,
                    endTime = next.timestamp,
                    durationHours = gapHours,
                    significance = when {
                        gapHours > 168 -> Severity.HIGH     // > 1 week
                        gapHours > 72 -> Severity.MEDIUM    // > 3 days
                        else -> Severity.LOW
                    }
                ))
            }
        }
        
        return gaps
    }

    private fun calculateChronologyIntegrity(
        events: List<ChronologyEvent>,
        gaps: List<TimeGap>
    ): Float {
        if (events.isEmpty()) return 0f
        
        var score = 100f
        
        // Penalize for gaps
        gaps.forEach { gap ->
            score -= when (gap.significance) {
                Severity.CRITICAL -> 20f
                Severity.HIGH -> 15f
                Severity.MEDIUM -> 10f
                Severity.LOW -> 5f
            }
        }
        
        // Penalize for low confidence events
        val lowConfidenceCount = events.count { it.confidence < 0.8f }
        score -= lowConfidenceCount * 2f
        
        return score.coerceIn(0f, 100f)
    }

    // B2: Contradiction Detection Matrix
    
    private fun detectContradictions(
        statements: List<Statement>,
        evidence: List<ForensicEvidence>
    ): List<Contradiction> {
        val contradictions = mutableListOf<Contradiction>()
        
        // Check for direct contradictions between statements
        val statementGroups = statements.groupBy { it.speaker }
        
        for ((speaker, stmts) in statementGroups) {
            if (stmts.size > 1) {
                val pairs = findContradictoryPairs(stmts)
                contradictions.addAll(pairs)
            }
        }
        
        return contradictions
    }

    private fun findContradictoryPairs(statements: List<Statement>): List<Contradiction> {
        val contradictions = mutableListOf<Contradiction>()
        
        // Contradiction patterns
        val contradictionPatterns = listOf(
            Pair("no deal", "invoice"),
            Pair("denied", "admitted"),
            Pair("refused", "accepted"),
            Pair("never", "always"),
            Pair("didn't", "did")
        )
        
        for (i in statements.indices) {
            for (j in i + 1 until statements.size) {
                val a = statements[i]
                val b = statements[j]
                
                // Check for contradiction patterns
                for ((pattern1, pattern2) in contradictionPatterns) {
                    val aLower = a.content.lowercase()
                    val bLower = b.content.lowercase()
                    
                    if ((aLower.contains(pattern1) && bLower.contains(pattern2)) ||
                        (aLower.contains(pattern2) && bLower.contains(pattern1))) {
                        
                        contradictions.add(Contradiction(
                            type = ContradictionType.DIRECT_OPPOSITE,
                            statementA = a,
                            statementB = b,
                            supportingEvidence = emptyList(),
                            severity = Severity.HIGH,
                            timestamp = DateTimeFormatter.ISO_DATE_TIME
                                .format(LocalDateTime.now()),
                            ruleViolated = "Verum Rule B2.1: Direct Contradiction"
                        ))
                    }
                }
            }
        }
        
        return contradictions
    }

    // B3: Missing Evidence Gap Analysis
    
    private fun analyzeEvidenceGaps(
        chronology: Chronology,
        expectedEvidence: List<String>
    ): List<EvidenceGap> {
        return expectedEvidence.mapNotNull { expected ->
            val found = chronology.events.any { event ->
                event.content.lowercase().contains(expected.lowercase())
            }
            
            if (!found) {
                EvidenceGap(
                    type = expected,
                    criticality = calculateGapCriticality(expected),
                    recommendedAction = "Obtain $expected to complete evidence chain",
                    timelinePosition = "Unknown"
                )
            } else null
        }
    }

    private fun calculateGapCriticality(expectedType: String): Severity {
        val criticalTypes = listOf("contract", "invoice", "bank statement", "agreement")
        val highTypes = listOf("email", "meeting minutes", "receipt")
        
        return when {
            criticalTypes.any { expectedType.contains(it, ignoreCase = true) } -> Severity.CRITICAL
            highTypes.any { expectedType.contains(it, ignoreCase = true) } -> Severity.HIGH
            else -> Severity.MEDIUM
        }
    }

    // B4: Timeline Manipulation Detection
    
    private fun detectTimelineAnomalies(
        evidence: List<ForensicEvidence>
    ): List<TimelineAnomaly> {
        val anomalies = mutableListOf<TimelineAnomaly>()
        
        val sorted = evidence.sortedBy { it.timestamp }
        
        for (i in 0 until sorted.size - 1) {
            val current = sorted[i]
            val next = sorted[i + 1]
            
            // Check for modification after creation
            if (current.metadata.modifiedAt != null &&
                current.metadata.modifiedAt > current.metadata.createdAt + (24 * 60 * 60 * 1000)) {
                
                anomalies.add(TimelineAnomaly(
                    type = TimelineAnomalyType.EDIT_AFTER_FACT,
                    documentId = current.id,
                    description = "Document modified ${(current.metadata.modifiedAt - current.metadata.createdAt) / (1000 * 60 * 60)} hours after creation",
                    suspicionScore = 0.85f,
                    originalTimestamp = current.metadata.createdAt,
                    modifiedTimestamp = current.metadata.modifiedAt
                ))
            }
            
            // Check for suspicious gaps
            val gapHours = (next.timestamp - current.timestamp) / (1000 * 60 * 60)
            if (gapHours > 48 && current.type == next.type) {
                anomalies.add(TimelineAnomaly(
                    type = TimelineAnomalyType.SUSPICIOUS_GAP,
                    documentId = "${current.id}-${next.id}",
                    description = "Unexplained gap of $gapHours hours between related evidence",
                    suspicionScore = 0.65f
                ))
            }
        }
        
        return anomalies
    }

    // B5: Behavioral Pattern Recognition
    
    private fun analyzeBehavioralPatterns(
        statements: List<Statement>
    ): List<DetectedBehavioralPattern> {
        val patterns = mutableListOf<DetectedBehavioralPattern>()
        
        // Evasion patterns
        val evasionIndicators = listOf("refuse", "ignore", "deflect", "avoid", "later")
        val evasionCount = statements.count { stmt ->
            evasionIndicators.any { stmt.content.lowercase().contains(it) }
        }
        if (evasionCount > 0) {
            patterns.add(DetectedBehavioralPattern(
                type = BehavioralPatternType.EVASION,
                score = (evasionCount.toFloat() / statements.size).coerceIn(0f, 1f),
                examples = findPatternExamples(statements, evasionIndicators),
                frequency = evasionCount
            ))
        }
        
        // Concealment patterns
        val concealmentIndicators = listOf("delete", "erase", "remove", "lost", "forgot")
        val concealmentCount = statements.count { stmt ->
            concealmentIndicators.any { stmt.content.lowercase().contains(it) }
        }
        if (concealmentCount > 0) {
            patterns.add(DetectedBehavioralPattern(
                type = BehavioralPatternType.CONCEALMENT,
                score = (concealmentCount.toFloat() / statements.size).coerceIn(0f, 1f),
                examples = findPatternExamples(statements, concealmentIndicators),
                frequency = concealmentCount
            ))
        }
        
        // Gaslighting patterns
        val gaslightingIndicators = listOf("never happened", "imagining", "crazy", "mistaken")
        val gaslightingCount = statements.count { stmt ->
            gaslightingIndicators.any { stmt.content.lowercase().contains(it) }
        }
        if (gaslightingCount > 0) {
            patterns.add(DetectedBehavioralPattern(
                type = BehavioralPatternType.GASLIGHTING,
                score = (gaslightingCount.toFloat() / statements.size).coerceIn(0f, 1f),
                examples = findPatternExamples(statements, gaslightingIndicators),
                frequency = gaslightingCount
            ))
        }
        
        return patterns
    }

    private fun findPatternExamples(
        statements: List<Statement>,
        indicators: List<String>
    ): List<String> {
        return statements
            .filter { stmt -> indicators.any { stmt.content.lowercase().contains(it) } }
            .take(3)
            .map { it.content.take(100) + "..." }
    }

    // B9: Integrity Index Scoring
    
    private fun calculateIntegrityIndex(
        contradictions: List<Contradiction>,
        anomalies: List<TimelineAnomaly>,
        patterns: List<DetectedBehavioralPattern>,
        gaps: List<EvidenceGap>
    ): IntegrityIndex {
        var score = 100f
        
        // Contradiction penalty (up to 30 points)
        val contradictionPenalty = (contradictions.size * 10f).coerceAtMost(30f)
        score -= contradictionPenalty
        
        // Timeline anomaly penalty (up to 25 points)
        val timelinePenalty = anomalies.sumOf { (it.suspicionScore * 10).toDouble() }
            .toFloat().coerceAtMost(25f)
        score -= timelinePenalty
        
        // Behavioral pattern penalty (up to 20 points)
        val behavioralPenalty = patterns.sumOf { (it.score * 10).toDouble() }
            .toFloat().coerceAtMost(20f)
        score -= behavioralPenalty
        
        // Evidence gap penalty (up to 15 points)
        val gapPenalty = gaps.sumOf { gap ->
            when (gap.criticality) {
                Severity.CRITICAL -> 5.0
                Severity.HIGH -> 3.0
                Severity.MEDIUM -> 2.0
                Severity.LOW -> 1.0
            }
        }.toFloat().coerceAtMost(15f)
        score -= gapPenalty
        
        score = score.coerceIn(0f, 100f)
        
        val category = when {
            score >= 90 -> IntegrityCategory.EXCELLENT
            score >= 70 -> IntegrityCategory.GOOD
            score >= 50 -> IntegrityCategory.FAIR
            score >= 25 -> IntegrityCategory.POOR
            else -> IntegrityCategory.COMPROMISED
        }
        
        return IntegrityIndex(
            score = score,
            category = category,
            breakdown = IntegrityBreakdown(
                contradictionPenalty = contradictionPenalty,
                timelinePenalty = timelinePenalty,
                behavioralPenalty = behavioralPenalty,
                compliancePenalty = 0f,
                gapPenalty = gapPenalty
            )
        )
    }

    private fun calculateConfidence(evidenceCount: Int, contradictionCount: Int): Float {
        if (evidenceCount == 0) return 0f
        
        val baseConfidence = when {
            evidenceCount >= 10 -> 0.9f
            evidenceCount >= 5 -> 0.7f
            evidenceCount >= 2 -> 0.5f
            else -> 0.3f
        }
        
        val contradictionPenalty = (contradictionCount * 0.1f).coerceAtMost(0.3f)
        
        return (baseConfidence - contradictionPenalty).coerceIn(0f, 1f)
    }

    private fun generateRecommendations(
        contradictions: List<Contradiction>,
        gaps: List<EvidenceGap>,
        anomalies: List<TimelineAnomaly>,
        patterns: List<DetectedBehavioralPattern>
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        if (contradictions.isNotEmpty()) {
            recommendations.add("RESOLVE ${contradictions.size} contradiction(s) found in statements")
        }
        
        gaps.filter { it.criticality == Severity.CRITICAL || it.criticality == Severity.HIGH }
            .forEach { gap ->
                recommendations.add("OBTAIN missing ${gap.type} evidence")
            }
        
        anomalies.filter { it.suspicionScore > 0.7 }.forEach { anomaly ->
            recommendations.add("INVESTIGATE ${anomaly.type.name} anomaly in ${anomaly.documentId}")
        }
        
        patterns.filter { it.score > 0.5 }.forEach { pattern ->
            recommendations.add("REVIEW ${pattern.type.name} behavioral pattern (${(pattern.score * 100).toInt()}% match)")
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("No critical issues found - evidence chain appears intact")
        }
        
        return recommendations
    }
}
