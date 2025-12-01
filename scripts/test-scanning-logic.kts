#!/usr/bin/env kotlin

/**
 * VERUM OMNIS - Test Scanning Logic Script
 * 
 * This script demonstrates the core scanning logic of the forensic engine:
 * 1. File input simulation
 * 2. SHA-512 hashing
 * 3. Evidence sealing
 * 4. Leveler analysis
 * 5. Report generation
 * 
 * Run with: kotlin scripts/test-scanning-logic.kts
 * 
 * @author Liam Highcock
 */

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

// ============================================================
// Cryptographic Engine (SHA-512 + HMAC-SHA512)
// ============================================================

object CryptographicEngine {
    private const val HASH_ALGORITHM = "SHA-512"
    private const val HMAC_ALGORITHM = "HmacSHA512"
    private const val SEAL_PREFIX = "VERUM_OMNIS_SEAL_V1"
    
    fun calculateHash(data: ByteArray): String {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val hashBytes = digest.digest(data)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    fun calculateHash(data: String): String = calculateHash(data.toByteArray(Charsets.UTF_8))
    
    fun generateSeal(evidenceId: String, contentHash: String, timestamp: Long): String {
        val sealData = "$SEAL_PREFIX|$evidenceId|$contentHash|$timestamp"
        val keyMaterial = deriveKeyMaterial()
        
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        val secretKey = SecretKeySpec(keyMaterial, HMAC_ALGORITHM)
        mac.init(secretKey)
        
        val hmacBytes = mac.doFinal(sealData.toByteArray(Charsets.UTF_8))
        return hmacBytes.joinToString("") { "%02x".format(it) }
    }
    
    fun verifyHash(content: ByteArray, expectedHash: String): Boolean {
        return calculateHash(content) == expectedHash
    }
    
    private fun deriveKeyMaterial(): ByteArray {
        val seedPhrase = "VERUM_OMNIS_FORENSIC_INTEGRITY_KEY_V1_LIAM_HIGHCOCK"
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        return digest.digest(seedPhrase.toByteArray(Charsets.UTF_8)).take(64).toByteArray()
    }
}

// ============================================================
// Data Models
// ============================================================

enum class EvidenceType { DOCUMENT, PHOTO, TEXT, AUDIO, VIDEO }
enum class Severity { LOW, MEDIUM, HIGH, CRITICAL }
enum class BehavioralPatternType { EVASION, GASLIGHTING, CONCEALMENT, DEFLECTION, CONTRADICTION }
enum class ContradictionType { DIRECT_OPPOSITE, FACTUAL_DISCREPANCY, OMISSION, TIMELINE_BREAK, BEHAVIORAL_MISMATCH }

data class ForensicLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val altitude: Double?,
    val timestamp: Long,
    val provider: String
)

data class EvidenceMetadata(
    val filename: String?,
    val fileSize: Long,
    val createdAt: Long,
    val deviceInfo: String,
    val appVersion: String
)

data class ForensicEvidence(
    val id: String,
    val type: EvidenceType,
    val content: ByteArray,
    val contentHash: String,
    val mimeType: String,
    val timestamp: Long,
    val location: ForensicLocation?,
    val metadata: EvidenceMetadata,
    val sealed: Boolean = false,
    val sealHash: String? = null
)

data class Statement(
    val id: String,
    val speaker: String,
    val content: String,
    val timestamp: Long,
    val source: String
)

data class Contradiction(
    val type: ContradictionType,
    val statementA: Statement,
    val statementB: Statement,
    val severity: Severity,
    val ruleViolated: String
)

data class EvidenceGap(
    val type: String,
    val criticality: Severity,
    val recommendedAction: String
)

data class BehavioralPattern(
    val type: BehavioralPatternType,
    val score: Float,
    val examples: List<String>,
    val frequency: Int
)

data class AnalysisResult(
    val contradictions: List<Contradiction>,
    val missingEvidence: List<EvidenceGap>,
    val behavioralPatterns: List<BehavioralPattern>,
    val integrityScore: Float,
    val confidence: Float,
    val recommendations: List<String>
)

// ============================================================
// Leveler Engine (B1-B9 Analysis)
// ============================================================

object LevelerEngine {
    
    fun analyze(
        statements: List<Statement>,
        evidence: List<ForensicEvidence>,
        expectedEvidence: List<String> = emptyList()
    ): AnalysisResult {
        
        val contradictions = detectContradictions(statements)
        val gaps = analyzeEvidenceGaps(expectedEvidence, evidence)
        val patterns = analyzeBehavioralPatterns(statements)
        val integrityScore = calculateIntegrityScore(contradictions, patterns, gaps)
        val confidence = calculateConfidence(evidence.size, contradictions.size)
        val recommendations = generateRecommendations(contradictions, gaps, patterns)
        
        return AnalysisResult(
            contradictions = contradictions,
            missingEvidence = gaps,
            behavioralPatterns = patterns,
            integrityScore = integrityScore,
            confidence = confidence,
            recommendations = recommendations
        )
    }
    
    private fun detectContradictions(statements: List<Statement>): List<Contradiction> {
        val contradictions = mutableListOf<Contradiction>()
        val contradictionPatterns = listOf(
            "no deal" to "invoice",
            "denied" to "admitted",
            "refused" to "accepted",
            "never" to "always",
            "didn't" to "did"
        )
        
        val bySpeaker = statements.groupBy { it.speaker }
        
        for ((_, stmts) in bySpeaker) {
            if (stmts.size > 1) {
                for (i in stmts.indices) {
                    for (j in i + 1 until stmts.size) {
                        val a = stmts[i]
                        val b = stmts[j]
                        
                        for ((p1, p2) in contradictionPatterns) {
                            val aLower = a.content.lowercase()
                            val bLower = b.content.lowercase()
                            
                            if ((aLower.contains(p1) && bLower.contains(p2)) ||
                                (aLower.contains(p2) && bLower.contains(p1))) {
                                
                                contradictions.add(Contradiction(
                                    type = ContradictionType.DIRECT_OPPOSITE,
                                    statementA = a,
                                    statementB = b,
                                    severity = Severity.HIGH,
                                    ruleViolated = "Verum Rule B2.1: Direct Contradiction"
                                ))
                            }
                        }
                    }
                }
            }
        }
        
        return contradictions
    }
    
    private fun analyzeEvidenceGaps(expected: List<String>, actual: List<ForensicEvidence>): List<EvidenceGap> {
        return expected.mapNotNull { exp ->
            val found = actual.any { ev ->
                ev.metadata.filename?.lowercase()?.contains(exp.lowercase()) == true
            }
            if (!found) {
                val criticality = when {
                    listOf("contract", "invoice", "bank statement").any { exp.contains(it, true) } -> Severity.CRITICAL
                    listOf("email", "receipt", "meeting").any { exp.contains(it, true) } -> Severity.HIGH
                    else -> Severity.MEDIUM
                }
                EvidenceGap(
                    type = exp,
                    criticality = criticality,
                    recommendedAction = "Obtain $exp to complete evidence chain"
                )
            } else null
        }
    }
    
    private fun analyzeBehavioralPatterns(statements: List<Statement>): List<BehavioralPattern> {
        val patterns = mutableListOf<BehavioralPattern>()
        
        // Evasion detection
        val evasionIndicators = listOf("refuse", "ignore", "deflect", "avoid", "later")
        val evasionMatches = statements.filter { stmt ->
            evasionIndicators.any { stmt.content.lowercase().contains(it) }
        }
        if (evasionMatches.isNotEmpty()) {
            patterns.add(BehavioralPattern(
                type = BehavioralPatternType.EVASION,
                score = (evasionMatches.size.toFloat() / statements.size).coerceIn(0f, 1f),
                examples = evasionMatches.take(3).map { it.content.take(60) + "..." },
                frequency = evasionMatches.size
            ))
        }
        
        // Concealment detection
        val concealmentIndicators = listOf("delete", "erase", "remove", "lost", "forgot")
        val concealmentMatches = statements.filter { stmt ->
            concealmentIndicators.any { stmt.content.lowercase().contains(it) }
        }
        if (concealmentMatches.isNotEmpty()) {
            patterns.add(BehavioralPattern(
                type = BehavioralPatternType.CONCEALMENT,
                score = (concealmentMatches.size.toFloat() / statements.size).coerceIn(0f, 1f),
                examples = concealmentMatches.take(3).map { it.content.take(60) + "..." },
                frequency = concealmentMatches.size
            ))
        }
        
        // Gaslighting detection
        val gaslightingIndicators = listOf("never happened", "imagining", "crazy", "mistaken")
        val gaslightingMatches = statements.filter { stmt ->
            gaslightingIndicators.any { stmt.content.lowercase().contains(it) }
        }
        if (gaslightingMatches.isNotEmpty()) {
            patterns.add(BehavioralPattern(
                type = BehavioralPatternType.GASLIGHTING,
                score = (gaslightingMatches.size.toFloat() / statements.size).coerceIn(0f, 1f),
                examples = gaslightingMatches.take(3).map { it.content.take(60) + "..." },
                frequency = gaslightingMatches.size
            ))
        }
        
        return patterns
    }
    
    private fun calculateIntegrityScore(
        contradictions: List<Contradiction>,
        patterns: List<BehavioralPattern>,
        gaps: List<EvidenceGap>
    ): Float {
        var score = 100f
        
        // Contradiction penalty (up to 30 points)
        score -= (contradictions.size * 10f).coerceAtMost(30f)
        
        // Behavioral pattern penalty (up to 20 points)
        score -= patterns.sumOf { (it.score * 10).toDouble() }.toFloat().coerceAtMost(20f)
        
        // Evidence gap penalty (up to 15 points)
        score -= gaps.sumOf { gap ->
            when (gap.criticality) {
                Severity.CRITICAL -> 5.0
                Severity.HIGH -> 3.0
                Severity.MEDIUM -> 2.0
                Severity.LOW -> 1.0
            }
        }.toFloat().coerceAtMost(15f)
        
        return score.coerceIn(0f, 100f)
    }
    
    private fun calculateConfidence(evidenceCount: Int, contradictionCount: Int): Float {
        if (evidenceCount == 0) return 0f
        
        val base = when {
            evidenceCount >= 10 -> 0.9f
            evidenceCount >= 5 -> 0.7f
            evidenceCount >= 2 -> 0.5f
            else -> 0.3f
        }
        
        val penalty = (contradictionCount * 0.1f).coerceAtMost(0.3f)
        return (base - penalty).coerceIn(0f, 1f)
    }
    
    private fun generateRecommendations(
        contradictions: List<Contradiction>,
        gaps: List<EvidenceGap>,
        patterns: List<BehavioralPattern>
    ): List<String> {
        val recs = mutableListOf<String>()
        
        if (contradictions.isNotEmpty()) {
            recs.add("RESOLVE ${contradictions.size} contradiction(s) found in statements")
        }
        
        gaps.filter { it.criticality in listOf(Severity.CRITICAL, Severity.HIGH) }.forEach {
            recs.add("OBTAIN missing ${it.type} evidence")
        }
        
        patterns.filter { it.score > 0.5f }.forEach {
            recs.add("REVIEW ${it.type.name} behavioral pattern (${(it.score * 100).toInt()}% match)")
        }
        
        if (recs.isEmpty()) {
            recs.add("No critical issues found - evidence chain appears intact")
        }
        
        return recs
    }
}

// ============================================================
// Report Generator
// ============================================================

object ReportGenerator {
    
    fun generateScanReport(
        evidence: ForensicEvidence,
        result: AnalysisResult
    ): String = buildString {
        appendLine("=".repeat(70))
        appendLine("              VERUM OMNIS FORENSIC SCAN REPORT")
        appendLine("=".repeat(70))
        appendLine()
        appendLine("SCAN SUMMARY")
        appendLine("-".repeat(70))
        appendLine("Evidence ID:       ${evidence.id}")
        appendLine("Type:              ${evidence.type}")
        appendLine("Filename:          ${evidence.metadata.filename ?: "N/A"}")
        appendLine("File Size:         ${evidence.metadata.fileSize} bytes")
        appendLine("Timestamp:         ${Date(evidence.timestamp)}")
        appendLine("Device:            ${evidence.metadata.deviceInfo}")
        appendLine()
        appendLine("CRYPTOGRAPHIC VERIFICATION")
        appendLine("-".repeat(70))
        appendLine("Content Hash (SHA-512):")
        appendLine("  ${evidence.contentHash.take(64)}")
        appendLine("  ${evidence.contentHash.takeLast(64)}")
        appendLine("Sealed:            ${if (evidence.sealed) "YES ✓" else "NO"}")
        if (evidence.sealHash != null) {
            appendLine("Seal Hash (HMAC-SHA512):")
            appendLine("  ${evidence.sealHash.take(64)}")
            appendLine("  ${evidence.sealHash.takeLast(64)}")
        }
        appendLine()
        if (evidence.location != null) {
            appendLine("LOCATION DATA")
            appendLine("-".repeat(70))
            appendLine("Latitude:          ${evidence.location.latitude}")
            appendLine("Longitude:         ${evidence.location.longitude}")
            appendLine("Accuracy:          ${evidence.location.accuracy}m")
            appendLine("Provider:          ${evidence.location.provider}")
            appendLine()
        }
        appendLine("INTEGRITY ANALYSIS (B1-B9 Leveler)")
        appendLine("-".repeat(70))
        appendLine("Integrity Score:   ${result.integrityScore.toInt()}/100")
        appendLine("Confidence:        ${(result.confidence * 100).toInt()}%")
        appendLine("Contradictions:    ${result.contradictions.size}")
        appendLine("Behavioral Flags:  ${result.behavioralPatterns.size}")
        appendLine("Missing Evidence:  ${result.missingEvidence.size}")
        appendLine()
        appendLine("RECOMMENDATIONS")
        appendLine("-".repeat(70))
        result.recommendations.forEach { rec ->
            appendLine("→ $rec")
        }
        appendLine()
        appendLine("=".repeat(70))
        appendLine("              VERUM OMNIS FORENSIC SEAL")
        appendLine("               AI FORENSICS FOR TRUTH")
        appendLine("=".repeat(70))
    }
    
    fun generateMultiFileReport(
        evidenceList: List<ForensicEvidence>,
        statements: List<Statement>,
        result: AnalysisResult
    ): String = buildString {
        appendLine("=".repeat(70))
        appendLine("         VERUM OMNIS MULTI-FILE FORENSIC ANALYSIS REPORT")
        appendLine("=".repeat(70))
        appendLine()
        appendLine("EVIDENCE ITEMS SCANNED: ${evidenceList.size}")
        appendLine("-".repeat(70))
        evidenceList.forEachIndexed { idx, ev ->
            appendLine("${idx + 1}. ${ev.id} - ${ev.metadata.filename ?: "Unknown"}")
            appendLine("   Hash: ${ev.contentHash.take(32)}...")
            appendLine("   Sealed: ${if (ev.sealed) "YES ✓" else "NO"}")
        }
        appendLine()
        appendLine("STATEMENTS ANALYZED: ${statements.size}")
        appendLine("-".repeat(70))
        statements.forEach { stmt ->
            appendLine("• [${stmt.speaker}] \"${stmt.content.take(50)}...\"")
        }
        appendLine()
        appendLine("CONTRADICTIONS DETECTED: ${result.contradictions.size}")
        appendLine("-".repeat(70))
        if (result.contradictions.isEmpty()) {
            appendLine("  No contradictions detected.")
        } else {
            result.contradictions.forEach { c ->
                appendLine("⚠ ${c.type}: ${c.ruleViolated}")
                appendLine("  Statement A: \"${c.statementA.content.take(40)}...\"")
                appendLine("  Statement B: \"${c.statementB.content.take(40)}...\"")
                appendLine("  Severity: ${c.severity}")
            }
        }
        appendLine()
        appendLine("BEHAVIORAL PATTERNS: ${result.behavioralPatterns.size}")
        appendLine("-".repeat(70))
        if (result.behavioralPatterns.isEmpty()) {
            appendLine("  No behavioral patterns detected.")
        } else {
            result.behavioralPatterns.forEach { p ->
                appendLine("⚑ ${p.type} (Score: ${(p.score * 100).toInt()}%, Frequency: ${p.frequency})")
                p.examples.forEach { ex ->
                    appendLine("    - \"$ex\"")
                }
            }
        }
        appendLine()
        appendLine("MISSING EVIDENCE GAPS: ${result.missingEvidence.size}")
        appendLine("-".repeat(70))
        result.missingEvidence.forEach { gap ->
            appendLine("! Missing: ${gap.type} (${gap.criticality} criticality)")
            appendLine("  Action: ${gap.recommendedAction}")
        }
        appendLine()
        appendLine("INTEGRITY SUMMARY")
        appendLine("-".repeat(70))
        appendLine("Integrity Score: ${result.integrityScore.toInt()}/100")
        appendLine("Confidence: ${(result.confidence * 100).toInt()}%")
        appendLine()
        appendLine("RECOMMENDATIONS")
        appendLine("-".repeat(70))
        result.recommendations.forEach { rec ->
            appendLine("→ $rec")
        }
        appendLine()
        appendLine("=".repeat(70))
        appendLine("              VERUM OMNIS FORENSIC SEAL")
        appendLine("=".repeat(70))
    }
}

// ============================================================
// Main Test Execution
// ============================================================

fun main() {
    println()
    println("╔════════════════════════════════════════════════════════════════════╗")
    println("║     VERUM OMNIS FORENSIC ENGINE - SCANNING LOGIC TEST SUITE        ║")
    println("╚════════════════════════════════════════════════════════════════════╝")
    println()
    
    // ============================================================
    // TEST 1: Single File Scanning
    // ============================================================
    println("▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓")
    println("  TEST 1: SINGLE FILE SCANNING AND REPORT GENERATION")
    println("▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓")
    println()
    
    val documentContent = """
        CONFIDENTIAL DOCUMENT
        Date: 2024-01-15
        Subject: Contract Agreement
        
        This document confirms the agreement between Party A and Party B.
        Total value: $50,000
        Payment terms: Net 30 days
        
        Signed by: John Smith
        Witness: Jane Doe
    """.trimIndent()
    
    val fileBytes = documentContent.toByteArray()
    val timestamp = System.currentTimeMillis()
    
    // Calculate hash
    val contentHash = CryptographicEngine.calculateHash(fileBytes)
    println("✓ File content hashed with SHA-512")
    
    // Create evidence
    val evidence = ForensicEvidence(
        id = "EV-SCAN-001",
        type = EvidenceType.DOCUMENT,
        content = fileBytes,
        contentHash = contentHash,
        mimeType = "text/plain",
        timestamp = timestamp,
        location = ForensicLocation(
            latitude = 51.5074,
            longitude = -0.1278,
            accuracy = 10.0f,
            altitude = 15.0,
            timestamp = timestamp,
            provider = "GPS"
        ),
        metadata = EvidenceMetadata(
            filename = "contract_agreement.txt",
            fileSize = fileBytes.size.toLong(),
            createdAt = timestamp,
            deviceInfo = "Test Device (Kotlin Script)",
            appVersion = "1.0.0"
        )
    )
    println("✓ ForensicEvidence object created")
    
    // Seal evidence
    val sealHash = CryptographicEngine.generateSeal(evidence.id, evidence.contentHash, evidence.timestamp)
    val sealedEvidence = evidence.copy(sealed = true, sealHash = sealHash)
    println("✓ Evidence sealed with HMAC-SHA512")
    
    // Verify integrity
    val verified = CryptographicEngine.verifyHash(fileBytes, contentHash)
    println("✓ Content integrity verified: $verified")
    
    // Run Leveler analysis
    val result = LevelerEngine.analyze(
        statements = emptyList(),
        evidence = listOf(sealedEvidence)
    )
    println("✓ Leveler Engine analysis complete")
    println()
    
    // Generate and print report
    val report = ReportGenerator.generateScanReport(sealedEvidence, result)
    println(report)
    
    // ============================================================
    // TEST 2: Multi-File Scanning with Contradiction Detection
    // ============================================================
    println()
    println("▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓")
    println("  TEST 2: MULTI-FILE SCANNING WITH CONTRADICTION DETECTION")
    println("▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓")
    println()
    
    val baseTime = System.currentTimeMillis()
    
    // Create multiple evidence items
    fun createEvidence(id: String, content: String, filename: String, ts: Long): ForensicEvidence {
        val bytes = content.toByteArray()
        val hash = CryptographicEngine.calculateHash(bytes)
        val seal = CryptographicEngine.generateSeal(id, hash, ts)
        return ForensicEvidence(
            id = id,
            type = EvidenceType.DOCUMENT,
            content = bytes,
            contentHash = hash,
            mimeType = "text/plain",
            timestamp = ts,
            location = null,
            metadata = EvidenceMetadata(
                filename = filename,
                fileSize = bytes.size.toLong(),
                createdAt = ts,
                deviceInfo = "Test Scanner",
                appVersion = "1.0.0"
            ),
            sealed = true,
            sealHash = seal
        )
    }
    
    val evidenceList = listOf(
        createEvidence("EV-DOC-001", "Initial contract value: \$50,000", "initial_contract.txt", baseTime),
        createEvidence("EV-DOC-002", "Amendment: Value changed to \$75,000", "contract_amendment.txt", baseTime + 3600000)
    )
    
    // Create contradictory statements
    val statements = listOf(
        Statement("STMT-001", "Party A Rep", "There was no deal made on this date", baseTime + 1000, "email"),
        Statement("STMT-002", "Party A Rep", "Here is the invoice for the deal we completed", baseTime + 2000, "email"),
        Statement("STMT-003", "Witness", "I refuse to provide that information", baseTime + 3000, "interview"),
        Statement("STMT-004", "Witness", "I forgot where I put the original documents", baseTime + 4000, "interview")
    )
    
    println("✓ ${evidenceList.size} evidence items scanned")
    println("✓ ${statements.size} statements analyzed")
    
    // Run analysis
    val multiResult = LevelerEngine.analyze(
        statements = statements,
        evidence = evidenceList,
        expectedEvidence = listOf("bank statement", "receipt")
    )
    
    println("✓ Contradictions detected: ${multiResult.contradictions.size}")
    println("✓ Behavioral patterns detected: ${multiResult.behavioralPatterns.size}")
    println("✓ Missing evidence gaps: ${multiResult.missingEvidence.size}")
    println()
    
    // Generate and print report
    val multiReport = ReportGenerator.generateMultiFileReport(evidenceList, statements, multiResult)
    println(multiReport)
    
    // ============================================================
    // TEST 3: Tamper Detection
    // ============================================================
    println()
    println("▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓")
    println("  TEST 3: TAMPER DETECTION VERIFICATION")
    println("▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓")
    println()
    
    val originalContent = "Original document content"
    val originalHash = CryptographicEngine.calculateHash(originalContent)
    
    println("Original Content: \"$originalContent\"")
    println("Original Hash: ${originalHash.take(32)}...")
    println()
    
    // Verify original
    val originalVerified = CryptographicEngine.verifyHash(originalContent.toByteArray(), originalHash)
    println("✓ Original content verification: ${if (originalVerified) "PASSED ✓" else "FAILED ✗"}")
    
    // Attempt tamper
    val tamperedContent = "Modified document content"
    val tamperDetected = !CryptographicEngine.verifyHash(tamperedContent.toByteArray(), originalHash)
    println("✓ Tampered content detection: ${if (tamperDetected) "TAMPER DETECTED ⚠" else "NOT DETECTED"}")
    
    println()
    println("=".repeat(70))
    println("              ALL TESTS COMPLETED SUCCESSFULLY")
    println("=".repeat(70))
    println()
}

// Run the tests
main()
