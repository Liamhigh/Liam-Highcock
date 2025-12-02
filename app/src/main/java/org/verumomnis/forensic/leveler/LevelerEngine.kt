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
 * - B6: Financial Transaction Correlation (Enhanced)
 * - B7: Communication Pattern Analysis (Enhanced)
 * - B8: Jurisdictional Compliance Check (UAE, UK, EU, US)
 * - B9: Integrity Index Scoring
 * 
 * @author Liam Highcock
 * @version 2.0.0
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

    // B6: Financial Transaction Correlation data classes
    data class FinancialTransaction(
        val id: String,
        val amount: Double,
        val currency: String,
        val date: Long,
        val description: String,
        val type: TransactionType,
        val parties: List<String>
    )

    enum class TransactionType {
        INVOICE,
        PAYMENT,
        RECEIPT,
        BANK_STATEMENT,
        CONTRACT_VALUE
    }

    data class FinancialDiscrepancy(
        val type: FinancialDiscrepancyType,
        val description: String,
        val expectedAmount: Double?,
        val actualAmount: Double?,
        val severity: Severity,
        val relatedDocuments: List<String>
    )

    enum class FinancialDiscrepancyType {
        AMOUNT_MISMATCH,
        MISSING_PAYMENT,
        DUPLICATE_INVOICE,
        UNAUTHORIZED_TRANSACTION,
        DATE_DISCREPANCY
    }

    // B7: Communication Pattern Analysis data classes
    data class CommunicationPattern(
        val type: CommunicationType,
        val frequency: Int,
        val averageResponseTime: Long,
        val suspiciousIndicators: List<String>,
        val score: Float
    )

    enum class CommunicationType {
        DELAYED_RESPONSE,
        DELETED_MESSAGES,
        UNUSUAL_TIMING,
        CHANGED_TONE,
        AVOIDANCE_PATTERN
    }

    // B8: Jurisdictional Compliance data classes
    data class JurisdictionalCompliance(
        val jurisdiction: Jurisdiction,
        val isCompliant: Boolean,
        val violations: List<ComplianceViolation>,
        val requirements: List<String>,
        val score: Float
    )

    enum class Jurisdiction {
        UAE,
        UK,
        EU,
        US,
        INTERNATIONAL
    }

    data class ComplianceViolation(
        val rule: String,
        val jurisdiction: Jurisdiction,
        val description: String,
        val severity: Severity,
        val recommendation: String
    )

    // Enhanced LevelerResult with B6-B8 data
    data class EnhancedLevelerResult(
        val contradictions: List<Contradiction>,
        val missingEvidence: List<EvidenceGap>,
        val timelineAnomalies: List<TimelineAnomaly>,
        val behavioralPatterns: List<DetectedBehavioralPattern>,
        val financialDiscrepancies: List<FinancialDiscrepancy>,
        val communicationPatterns: List<CommunicationPattern>,
        val jurisdictionalCompliance: List<JurisdictionalCompliance>,
        val integrityScore: Float,
        val confidence: Float,
        val recommendations: List<String>
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

    // ============================================================
    // B6: Financial Transaction Correlation (Enhanced)
    // ============================================================

    /**
     * Analyze financial documents for discrepancies and correlate transactions.
     * Parses invoices, receipts, bank statements, and contracts to detect:
     * - Amount mismatches between documents
     * - Missing payments for invoices
     * - Duplicate invoices
     * - Unauthorized transactions
     * - Date discrepancies
     */
    fun analyzeFinancialTransactions(
        statements: List<Statement>,
        evidence: List<ForensicEvidence>
    ): List<FinancialDiscrepancy> {
        val discrepancies = mutableListOf<FinancialDiscrepancy>()
        
        // Parse financial data from statements and evidence
        val transactions = extractFinancialData(statements, evidence)
        
        // Check for amount mismatches
        discrepancies.addAll(detectAmountMismatches(transactions))
        
        // Check for missing payments
        discrepancies.addAll(detectMissingPayments(transactions))
        
        // Check for duplicate invoices
        discrepancies.addAll(detectDuplicateInvoices(transactions))
        
        // Check for date discrepancies
        discrepancies.addAll(detectDateDiscrepancies(transactions))
        
        return discrepancies
    }

    private fun extractFinancialData(
        statements: List<Statement>,
        evidence: List<ForensicEvidence>
    ): List<FinancialTransaction> {
        val transactions = mutableListOf<FinancialTransaction>()
        
        // Financial keywords and patterns
        val amountPattern = Regex("""[\$£€]?\s*(\d{1,3}(?:,\d{3})*(?:\.\d{2})?)""")
        val invoiceKeywords = listOf("invoice", "bill", "statement", "amount due")
        val paymentKeywords = listOf("payment", "paid", "received", "transferred", "deposited")
        val receiptKeywords = listOf("receipt", "confirmation", "transaction id")
        
        statements.forEachIndexed { index, stmt ->
            val content = stmt.content.lowercase()
            val amounts = amountPattern.findAll(stmt.content).map { 
                it.groupValues[1].replace(",", "").toDoubleOrNull() ?: 0.0 
            }.toList()
            
            if (amounts.isNotEmpty()) {
                val type = when {
                    invoiceKeywords.any { content.contains(it) } -> TransactionType.INVOICE
                    paymentKeywords.any { content.contains(it) } -> TransactionType.PAYMENT
                    receiptKeywords.any { content.contains(it) } -> TransactionType.RECEIPT
                    else -> TransactionType.CONTRACT_VALUE
                }
                
                amounts.forEach { amount ->
                    transactions.add(FinancialTransaction(
                        id = "TXN-${index}-${amount.toLong()}",
                        amount = amount,
                        currency = detectCurrency(stmt.content),
                        date = stmt.timestamp,
                        description = stmt.content.take(100),
                        type = type,
                        parties = listOf(stmt.speaker)
                    ))
                }
            }
        }
        
        return transactions
    }

    private fun detectCurrency(content: String): String {
        return when {
            content.contains("$") -> "USD"
            content.contains("£") -> "GBP"
            content.contains("€") -> "EUR"
            content.contains("AED") || content.contains("dirham") -> "AED"
            else -> "USD"
        }
    }

    private fun detectAmountMismatches(transactions: List<FinancialTransaction>): List<FinancialDiscrepancy> {
        val discrepancies = mutableListOf<FinancialDiscrepancy>()
        
        // Group by similar amounts and look for discrepancies
        val invoices = transactions.filter { it.type == TransactionType.INVOICE }
        val payments = transactions.filter { it.type == TransactionType.PAYMENT }
        
        invoices.forEach { invoice ->
            val matchingPayments = payments.filter { payment ->
                kotlin.math.abs(payment.amount - invoice.amount) < 0.01
            }
            
            if (matchingPayments.isEmpty() && payments.isNotEmpty()) {
                // Check for partial payments
                val totalPayments = payments.sumOf { it.amount }
                if (kotlin.math.abs(totalPayments - invoice.amount) > invoice.amount * 0.1) {
                    discrepancies.add(FinancialDiscrepancy(
                        type = FinancialDiscrepancyType.AMOUNT_MISMATCH,
                        description = "Invoice amount ${invoice.amount} does not match total payments $totalPayments",
                        expectedAmount = invoice.amount,
                        actualAmount = totalPayments,
                        severity = Severity.HIGH,
                        relatedDocuments = listOf(invoice.id)
                    ))
                }
            }
        }
        
        return discrepancies
    }

    private fun detectMissingPayments(transactions: List<FinancialTransaction>): List<FinancialDiscrepancy> {
        val discrepancies = mutableListOf<FinancialDiscrepancy>()
        
        val invoices = transactions.filter { it.type == TransactionType.INVOICE }
        val payments = transactions.filter { it.type == TransactionType.PAYMENT || it.type == TransactionType.RECEIPT }
        
        invoices.forEach { invoice ->
            val hasMatchingPayment = payments.any { payment ->
                kotlin.math.abs(payment.amount - invoice.amount) < invoice.amount * 0.05 &&
                payment.date >= invoice.date
            }
            
            if (!hasMatchingPayment && payments.isNotEmpty()) {
                discrepancies.add(FinancialDiscrepancy(
                    type = FinancialDiscrepancyType.MISSING_PAYMENT,
                    description = "No payment found for invoice of ${invoice.amount} ${invoice.currency}",
                    expectedAmount = invoice.amount,
                    actualAmount = null,
                    severity = Severity.CRITICAL,
                    relatedDocuments = listOf(invoice.id)
                ))
            }
        }
        
        return discrepancies
    }

    private fun detectDuplicateInvoices(transactions: List<FinancialTransaction>): List<FinancialDiscrepancy> {
        val discrepancies = mutableListOf<FinancialDiscrepancy>()
        
        val invoices = transactions.filter { it.type == TransactionType.INVOICE }
        
        // Group by amount to find potential duplicates
        val groupedByAmount = invoices.groupBy { it.amount }
        
        groupedByAmount.filter { it.value.size > 1 }.forEach { (amount, invoiceGroup) ->
            // Check if invoices are within 30 days of each other (potential duplicates)
            val sortedByDate = invoiceGroup.sortedBy { it.date }
            for (i in 0 until sortedByDate.size - 1) {
                val daysBetween = (sortedByDate[i + 1].date - sortedByDate[i].date) / (1000 * 60 * 60 * 24)
                if (daysBetween < 30) {
                    discrepancies.add(FinancialDiscrepancy(
                        type = FinancialDiscrepancyType.DUPLICATE_INVOICE,
                        description = "Potential duplicate invoices of $amount found within $daysBetween days",
                        expectedAmount = amount,
                        actualAmount = amount * 2,
                        severity = Severity.MEDIUM,
                        relatedDocuments = listOf(sortedByDate[i].id, sortedByDate[i + 1].id)
                    ))
                }
            }
        }
        
        return discrepancies
    }

    private fun detectDateDiscrepancies(transactions: List<FinancialTransaction>): List<FinancialDiscrepancy> {
        val discrepancies = mutableListOf<FinancialDiscrepancy>()
        
        val invoices = transactions.filter { it.type == TransactionType.INVOICE }
        val payments = transactions.filter { it.type == TransactionType.PAYMENT }
        
        // Check for payments before invoice date
        payments.forEach { payment ->
            val relatedInvoice = invoices.find { invoice ->
                kotlin.math.abs(invoice.amount - payment.amount) < invoice.amount * 0.05
            }
            
            if (relatedInvoice != null && payment.date < relatedInvoice.date) {
                discrepancies.add(FinancialDiscrepancy(
                    type = FinancialDiscrepancyType.DATE_DISCREPANCY,
                    description = "Payment dated before corresponding invoice - possible backdating",
                    expectedAmount = relatedInvoice.amount,
                    actualAmount = payment.amount,
                    severity = Severity.HIGH,
                    relatedDocuments = listOf(payment.id, relatedInvoice.id)
                ))
            }
        }
        
        return discrepancies
    }

    // ============================================================
    // B7: Communication Pattern Analysis (Enhanced)
    // ============================================================

    /**
     * Analyze communication patterns for suspicious behavior including:
     * - Delayed responses indicating avoidance
     * - Evidence of deleted messages
     * - Unusual timing patterns
     * - Tone changes indicating deception
     * - Response avoidance patterns
     */
    fun analyzeCommunicationPatterns(
        statements: List<Statement>
    ): List<CommunicationPattern> {
        val patterns = mutableListOf<CommunicationPattern>()
        
        // Group by speaker to analyze individual patterns
        val bySpeaker = statements.groupBy { it.speaker }
        
        bySpeaker.forEach { (speaker, speakerStatements) ->
            // Analyze response delays
            val delayPattern = analyzeResponseDelays(speakerStatements)
            if (delayPattern != null) patterns.add(delayPattern)
            
            // Analyze for deleted message indicators
            val deletedPattern = analyzeDeletedMessageIndicators(speakerStatements)
            if (deletedPattern != null) patterns.add(deletedPattern)
            
            // Analyze timing patterns
            val timingPattern = analyzeTimingPatterns(speakerStatements)
            if (timingPattern != null) patterns.add(timingPattern)
            
            // Analyze tone changes
            val tonePattern = analyzeToneChanges(speakerStatements)
            if (tonePattern != null) patterns.add(tonePattern)
        }
        
        return patterns
    }

    private fun analyzeResponseDelays(statements: List<Statement>): CommunicationPattern? {
        if (statements.size < 2) return null
        
        val sorted = statements.sortedBy { it.timestamp }
        val delays = mutableListOf<Long>()
        
        for (i in 0 until sorted.size - 1) {
            val delay = sorted[i + 1].timestamp - sorted[i].timestamp
            delays.add(delay)
        }
        
        val avgDelay = delays.average()
        val longDelays = delays.count { it > avgDelay * 2 }
        
        if (longDelays > delays.size / 3) {
            return CommunicationPattern(
                type = CommunicationType.DELAYED_RESPONSE,
                frequency = longDelays,
                averageResponseTime = avgDelay.toLong(),
                suspiciousIndicators = listOf(
                    "Frequent long delays between communications",
                    "Average delay: ${avgDelay / (1000 * 60)} minutes",
                    "$longDelays responses exceeded normal delay threshold"
                ),
                score = (longDelays.toFloat() / delays.size).coerceIn(0f, 1f)
            )
        }
        
        return null
    }

    private fun analyzeDeletedMessageIndicators(statements: List<Statement>): CommunicationPattern? {
        val deletedIndicators = listOf(
            "message deleted", "this message was deleted", "removed message",
            "no longer available", "content unavailable", "message not found",
            "gap in conversation", "missing message", "deleted by"
        )
        
        val indicators = mutableListOf<String>()
        var frequency = 0
        
        statements.forEach { stmt ->
            val content = stmt.content.lowercase()
            deletedIndicators.forEach { indicator ->
                if (content.contains(indicator)) {
                    frequency++
                    indicators.add("Found: \"$indicator\" in message from ${stmt.speaker}")
                }
            }
        }
        
        if (frequency > 0) {
            return CommunicationPattern(
                type = CommunicationType.DELETED_MESSAGES,
                frequency = frequency,
                averageResponseTime = 0,
                suspiciousIndicators = indicators.take(5),
                score = (frequency.toFloat() / statements.size).coerceIn(0f, 1f)
            )
        }
        
        return null
    }

    private fun analyzeTimingPatterns(statements: List<Statement>): CommunicationPattern? {
        if (statements.size < 3) return null
        
        // Check for unusual timing (e.g., all messages at odd hours)
        val hours = statements.map { 
            java.util.Calendar.getInstance().apply { 
                timeInMillis = it.timestamp 
            }.get(java.util.Calendar.HOUR_OF_DAY)
        }
        
        val oddHourMessages = hours.count { it < 6 || it > 22 }
        val weekendMessages = statements.count { stmt ->
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = stmt.timestamp }
            val dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
            dayOfWeek == java.util.Calendar.SATURDAY || dayOfWeek == java.util.Calendar.SUNDAY
        }
        
        val suspiciousIndicators = mutableListOf<String>()
        if (oddHourMessages > statements.size / 3) {
            suspiciousIndicators.add("$oddHourMessages messages sent during unusual hours (before 6AM or after 10PM)")
        }
        if (weekendMessages > statements.size * 0.7) {
            suspiciousIndicators.add("${(weekendMessages * 100 / statements.size)}% of messages sent on weekends")
        }
        
        if (suspiciousIndicators.isNotEmpty()) {
            return CommunicationPattern(
                type = CommunicationType.UNUSUAL_TIMING,
                frequency = oddHourMessages + weekendMessages,
                averageResponseTime = 0,
                suspiciousIndicators = suspiciousIndicators,
                score = ((oddHourMessages + weekendMessages).toFloat() / (statements.size * 2)).coerceIn(0f, 1f)
            )
        }
        
        return null
    }

    private fun analyzeToneChanges(statements: List<Statement>): CommunicationPattern? {
        if (statements.size < 2) return null
        
        // Simple tone indicators
        val formalIndicators = listOf("dear", "regards", "sincerely", "respectfully", "please find")
        val informalIndicators = listOf("hey", "hi", "thanks", "ok", "sure", "lol", "btw")
        val hostileIndicators = listOf("demand", "legal action", "lawyer", "court", "sue", "threat", "warning")
        
        var formalCount = 0
        var informalCount = 0
        var hostileCount = 0
        
        statements.forEach { stmt ->
            val content = stmt.content.lowercase()
            if (formalIndicators.any { content.contains(it) }) formalCount++
            if (informalIndicators.any { content.contains(it) }) informalCount++
            if (hostileIndicators.any { content.contains(it) }) hostileCount++
        }
        
        // Detect sudden tone shifts
        val suspiciousIndicators = mutableListOf<String>()
        if (formalCount > 0 && informalCount > 0 && hostileCount > 0) {
            suspiciousIndicators.add("Mixed communication tones detected: formal ($formalCount), informal ($informalCount), hostile ($hostileCount)")
        }
        if (hostileCount > statements.size / 4) {
            suspiciousIndicators.add("High frequency of hostile/threatening language detected")
        }
        
        if (suspiciousIndicators.isNotEmpty()) {
            return CommunicationPattern(
                type = CommunicationType.CHANGED_TONE,
                frequency = hostileCount,
                averageResponseTime = 0,
                suspiciousIndicators = suspiciousIndicators,
                score = (hostileCount.toFloat() / statements.size).coerceIn(0f, 1f)
            )
        }
        
        return null
    }

    // ============================================================
    // B8: Jurisdictional Compliance Check (UAE, UK, EU, US)
    // ============================================================

    /**
     * Check evidence and statements for jurisdictional compliance across:
     * - UAE: Federal Law compliance, DIFC regulations, evidence admissibility
     * - UK: Civil Evidence Act, GDPR (retained), disclosure requirements
     * - EU: GDPR, eIDAS, electronic evidence regulations
     * - US: Federal Rules of Evidence, state variations, authentication requirements
     */
    fun checkJurisdictionalCompliance(
        evidence: List<ForensicEvidence>,
        statements: List<Statement>
    ): List<JurisdictionalCompliance> {
        return listOf(
            checkUAECompliance(evidence, statements),
            checkUKCompliance(evidence, statements),
            checkEUCompliance(evidence, statements),
            checkUSCompliance(evidence, statements)
        )
    }

    private fun checkUAECompliance(
        evidence: List<ForensicEvidence>,
        statements: List<Statement>
    ): JurisdictionalCompliance {
        val violations = mutableListOf<ComplianceViolation>()
        val requirements = mutableListOf<String>()
        
        requirements.add("UAE Federal Law No. 10 of 2019 - Electronic Transactions")
        requirements.add("UAE Evidence Law - Federal Law No. 10 of 1992")
        requirements.add("DIFC Courts Rules for electronic evidence")
        
        // Check for Arabic translation requirement
        val hasArabicContent = statements.any { stmt ->
            stmt.content.any { char -> char.code in 0x0600..0x06FF }
        }
        if (!hasArabicContent && statements.isNotEmpty()) {
            violations.add(ComplianceViolation(
                rule = "Arabic Translation Requirement",
                jurisdiction = Jurisdiction.UAE,
                description = "Official court submissions in UAE may require Arabic translation",
                severity = Severity.MEDIUM,
                recommendation = "Prepare certified Arabic translations of all evidence"
            ))
        }
        
        // Check timestamp integrity for UAE
        val unsealedEvidence = evidence.count { !it.sealed }
        if (unsealedEvidence > 0) {
            violations.add(ComplianceViolation(
                rule = "Evidence Authentication",
                jurisdiction = Jurisdiction.UAE,
                description = "$unsealedEvidence evidence items are not cryptographically sealed",
                severity = Severity.HIGH,
                recommendation = "Seal all evidence with cryptographic signatures for UAE court admissibility"
            ))
        }
        
        val score = (100f - violations.sumOf { 
            when (it.severity) {
                Severity.CRITICAL -> 30
                Severity.HIGH -> 20
                Severity.MEDIUM -> 10
                Severity.LOW -> 5
            }
        }).coerceIn(0f, 100f)
        
        return JurisdictionalCompliance(
            jurisdiction = Jurisdiction.UAE,
            isCompliant = violations.none { it.severity == Severity.CRITICAL || it.severity == Severity.HIGH },
            violations = violations,
            requirements = requirements,
            score = score
        )
    }

    private fun checkUKCompliance(
        evidence: List<ForensicEvidence>,
        statements: List<Statement>
    ): JurisdictionalCompliance {
        val violations = mutableListOf<ComplianceViolation>()
        val requirements = mutableListOf<String>()
        
        requirements.add("Civil Evidence Act 1995")
        requirements.add("Criminal Evidence Act 1984 (PACE)")
        requirements.add("UK GDPR / Data Protection Act 2018")
        requirements.add("Practice Direction 31B - Disclosure of Electronic Documents")
        
        // Check for proper authentication
        val unsealedEvidence = evidence.count { !it.sealed }
        if (unsealedEvidence > 0) {
            violations.add(ComplianceViolation(
                rule = "PD 31B Authentication",
                jurisdiction = Jurisdiction.UK,
                description = "$unsealedEvidence evidence items lack digital authentication",
                severity = Severity.HIGH,
                recommendation = "Authenticate all electronic evidence per Practice Direction 31B"
            ))
        }
        
        // Check for chain of custody
        val missingLocation = evidence.count { it.location == null }
        if (missingLocation > evidence.size / 2) {
            violations.add(ComplianceViolation(
                rule = "Chain of Custody",
                jurisdiction = Jurisdiction.UK,
                description = "Insufficient geolocation data for chain of custody verification",
                severity = Severity.MEDIUM,
                recommendation = "Ensure all evidence capture includes location metadata"
            ))
        }
        
        val score = (100f - violations.sumOf { 
            when (it.severity) {
                Severity.CRITICAL -> 30
                Severity.HIGH -> 20
                Severity.MEDIUM -> 10
                Severity.LOW -> 5
            }
        }).coerceIn(0f, 100f)
        
        return JurisdictionalCompliance(
            jurisdiction = Jurisdiction.UK,
            isCompliant = violations.none { it.severity == Severity.CRITICAL || it.severity == Severity.HIGH },
            violations = violations,
            requirements = requirements,
            score = score
        )
    }

    private fun checkEUCompliance(
        evidence: List<ForensicEvidence>,
        statements: List<Statement>
    ): JurisdictionalCompliance {
        val violations = mutableListOf<ComplianceViolation>()
        val requirements = mutableListOf<String>()
        
        requirements.add("EU General Data Protection Regulation (GDPR)")
        requirements.add("eIDAS Regulation (EU) No 910/2014")
        requirements.add("Regulation (EU) 2023/1543 on e-evidence")
        requirements.add("Brussels I Regulation (Recast)")
        
        // Check for GDPR compliance indicators
        val personalDataIndicators = listOf("name:", "email:", "phone:", "address:", "date of birth", "national id")
        val containsPersonalData = statements.any { stmt ->
            personalDataIndicators.any { stmt.content.lowercase().contains(it) }
        }
        
        if (containsPersonalData) {
            violations.add(ComplianceViolation(
                rule = "GDPR Article 5 - Data Minimization",
                jurisdiction = Jurisdiction.EU,
                description = "Evidence may contain personal data requiring GDPR compliance",
                severity = Severity.MEDIUM,
                recommendation = "Ensure lawful basis for processing and data subject rights compliance"
            ))
        }
        
        // Check for qualified electronic signatures (eIDAS)
        val allSealed = evidence.all { it.sealed && it.sealHash != null }
        if (!allSealed) {
            violations.add(ComplianceViolation(
                rule = "eIDAS Qualified Electronic Signature",
                jurisdiction = Jurisdiction.EU,
                description = "Not all evidence has qualified electronic signatures",
                severity = Severity.HIGH,
                recommendation = "Apply qualified electronic signatures per eIDAS for cross-border recognition"
            ))
        }
        
        val score = (100f - violations.sumOf { 
            when (it.severity) {
                Severity.CRITICAL -> 30
                Severity.HIGH -> 20
                Severity.MEDIUM -> 10
                Severity.LOW -> 5
            }
        }).coerceIn(0f, 100f)
        
        return JurisdictionalCompliance(
            jurisdiction = Jurisdiction.EU,
            isCompliant = violations.none { it.severity == Severity.CRITICAL || it.severity == Severity.HIGH },
            violations = violations,
            requirements = requirements,
            score = score
        )
    }

    private fun checkUSCompliance(
        evidence: List<ForensicEvidence>,
        statements: List<Statement>
    ): JurisdictionalCompliance {
        val violations = mutableListOf<ComplianceViolation>()
        val requirements = mutableListOf<String>()
        
        requirements.add("Federal Rules of Evidence (FRE) 901, 902")
        requirements.add("Federal Rules of Civil Procedure 26, 34, 37(e)")
        requirements.add("Stored Communications Act (18 U.S.C. § 2701)")
        requirements.add("State-specific authentication requirements")
        
        // Check FRE 901(a) - Authentication requirement
        val unsealedEvidence = evidence.count { !it.sealed }
        if (unsealedEvidence > 0) {
            violations.add(ComplianceViolation(
                rule = "FRE 901(a) - Authentication",
                jurisdiction = Jurisdiction.US,
                description = "$unsealedEvidence items lack authentication per Federal Rules of Evidence",
                severity = Severity.HIGH,
                recommendation = "Authenticate all evidence with hash verification and chain of custody"
            ))
        }
        
        // Check for metadata preservation (FRCP 37(e))
        val missingMetadata = evidence.count { 
            it.metadata.filename == null || it.metadata.author == null 
        }
        if (missingMetadata > evidence.size / 3) {
            violations.add(ComplianceViolation(
                rule = "FRCP 37(e) - ESI Preservation",
                jurisdiction = Jurisdiction.US,
                description = "Insufficient metadata may impact e-discovery obligations",
                severity = Severity.MEDIUM,
                recommendation = "Preserve all original metadata for electronically stored information"
            ))
        }
        
        // Check for timestamp verification
        val hasTimestamps = evidence.all { it.timestamp > 0 }
        if (!hasTimestamps) {
            violations.add(ComplianceViolation(
                rule = "FRE 901(b)(4) - Distinctive Characteristics",
                jurisdiction = Jurisdiction.US,
                description = "Missing or invalid timestamps on evidence",
                severity = Severity.HIGH,
                recommendation = "Ensure all evidence has verifiable timestamps"
            ))
        }
        
        val score = (100f - violations.sumOf { 
            when (it.severity) {
                Severity.CRITICAL -> 30
                Severity.HIGH -> 20
                Severity.MEDIUM -> 10
                Severity.LOW -> 5
            }
        }).coerceIn(0f, 100f)
        
        return JurisdictionalCompliance(
            jurisdiction = Jurisdiction.US,
            isCompliant = violations.none { it.severity == Severity.CRITICAL || it.severity == Severity.HIGH },
            violations = violations,
            requirements = requirements,
            score = score
        )
    }

    /**
     * Perform enhanced analysis including all B1-B9 modules
     */
    fun analyzeEnhanced(
        statements: List<Statement>,
        evidence: List<ForensicEvidence>,
        expectedEvidence: List<String> = emptyList()
    ): EnhancedLevelerResult {
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
        
        // B6: Financial transaction correlation (Enhanced)
        val financialDiscrepancies = analyzeFinancialTransactions(statements, evidence)
        
        // B7: Communication pattern analysis (Enhanced)
        val communicationPatterns = analyzeCommunicationPatterns(statements)
        
        // B8: Jurisdictional compliance check
        val jurisdictionalCompliance = checkJurisdictionalCompliance(evidence, statements)
        
        // B9: Calculate integrity index
        val integrityIndex = calculateIntegrityIndex(
            contradictions, anomalies, patterns, gaps
        )
        
        // Generate enhanced recommendations
        val recommendations = generateEnhancedRecommendations(
            contradictions, gaps, anomalies, patterns,
            financialDiscrepancies, communicationPatterns, jurisdictionalCompliance
        )
        
        return EnhancedLevelerResult(
            contradictions = contradictions,
            missingEvidence = gaps,
            timelineAnomalies = anomalies,
            behavioralPatterns = patterns,
            financialDiscrepancies = financialDiscrepancies,
            communicationPatterns = communicationPatterns,
            jurisdictionalCompliance = jurisdictionalCompliance,
            integrityScore = integrityIndex.score,
            confidence = calculateConfidence(evidence.size, contradictions.size),
            recommendations = recommendations
        )
    }

    private fun generateEnhancedRecommendations(
        contradictions: List<Contradiction>,
        gaps: List<EvidenceGap>,
        anomalies: List<TimelineAnomaly>,
        patterns: List<DetectedBehavioralPattern>,
        financialDiscrepancies: List<FinancialDiscrepancy>,
        communicationPatterns: List<CommunicationPattern>,
        jurisdictionalCompliance: List<JurisdictionalCompliance>
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        // Basic recommendations
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
        
        // B6: Financial recommendations
        financialDiscrepancies.filter { it.severity == Severity.CRITICAL || it.severity == Severity.HIGH }
            .forEach { discrepancy ->
                recommendations.add("VERIFY ${discrepancy.type.name}: ${discrepancy.description}")
            }
        
        // B7: Communication recommendations
        communicationPatterns.filter { it.score > 0.5 }.forEach { pattern ->
            recommendations.add("ANALYZE ${pattern.type.name} communication pattern")
        }
        
        // B8: Jurisdictional recommendations
        jurisdictionalCompliance.forEach { compliance ->
            compliance.violations.filter { it.severity == Severity.HIGH || it.severity == Severity.CRITICAL }
                .forEach { violation ->
                    recommendations.add("${compliance.jurisdiction.name} COMPLIANCE: ${violation.recommendation}")
                }
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("No critical issues found - evidence chain appears intact and compliant")
        }
        
        return recommendations
    }
}
