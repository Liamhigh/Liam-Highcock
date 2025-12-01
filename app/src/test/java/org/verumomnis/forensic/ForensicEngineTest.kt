package org.verumomnis.forensic

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.verumomnis.forensic.crypto.CryptographicSealingEngine
import org.verumomnis.forensic.core.*
import org.verumomnis.forensic.leveler.LevelerEngine

/**
 * Unit tests for Verum Omnis Forensic Engine
 */
class ForensicEngineTest {

    private lateinit var cryptoEngine: CryptographicSealingEngine

    @Before
    fun setup() {
        cryptoEngine = CryptographicSealingEngine()
    }

    @Test
    fun `test SHA-512 hash generation`() {
        val testData = "Hello, Verum Omnis!"
        val hash = cryptoEngine.calculateHash(testData)
        
        // SHA-512 produces 128 character hex string
        assertEquals(128, hash.length)
        
        // Same input should produce same hash
        val hash2 = cryptoEngine.calculateHash(testData)
        assertEquals(hash, hash2)
        
        // Different input should produce different hash
        val hash3 = cryptoEngine.calculateHash("Different data")
        assertNotEquals(hash, hash3)
    }

    @Test
    fun `test hash consistency`() {
        val data = "Test evidence content"
        val hash1 = cryptoEngine.calculateHash(data)
        val hash2 = cryptoEngine.calculateHash(data)
        
        assertEquals(hash1, hash2)
    }

    @Test
    fun `test hash uniqueness`() {
        val data1 = "Evidence A"
        val data2 = "Evidence B"
        
        val hash1 = cryptoEngine.calculateHash(data1)
        val hash2 = cryptoEngine.calculateHash(data2)
        
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `test nonce generation`() {
        val nonce1 = cryptoEngine.generateNonce()
        val nonce2 = cryptoEngine.generateNonce()
        
        assertNotEquals(nonce1, nonce2)
        assertTrue(nonce1.contains("-"))
    }

    @Test
    fun `test salt generation`() {
        val salt = cryptoEngine.generateSalt(32)
        
        assertEquals(32, salt.size)
    }

    @Test
    fun `test content hash verification`() {
        val content = "Original content".toByteArray()
        val hash = cryptoEngine.calculateHash(content)
        
        assertTrue(cryptoEngine.verifyContentHash(content, hash))
        assertFalse(cryptoEngine.verifyContentHash("Modified".toByteArray(), hash))
    }
}

/**
 * Unit tests for Leveler Engine
 */
class LevelerEngineTest {

    @Test
    fun `test empty analysis`() {
        val result = LevelerEngine.analyze(
            statements = emptyList(),
            evidence = emptyList()
        )
        
        assertEquals(0, result.contradictions.size)
        assertEquals(0, result.timelineAnomalies.size)
    }

    @Test
    fun `test integrity score calculation`() {
        val result = LevelerEngine.analyze(
            statements = emptyList(),
            evidence = emptyList()
        )
        
        // With no issues, score should be high
        assertTrue(result.integrityScore >= 0)
        assertTrue(result.integrityScore <= 100)
    }

    @Test
    fun `test contradiction detection with patterns`() {
        val statements = listOf(
            LevelerEngine.Statement(
                id = "1",
                speaker = "Person A",
                content = "There was no deal made",
                timestamp = System.currentTimeMillis(),
                source = "email"
            ),
            LevelerEngine.Statement(
                id = "2",
                speaker = "Person A",
                content = "Here is the invoice for the deal",
                timestamp = System.currentTimeMillis() + 1000,
                source = "email"
            )
        )
        
        val result = LevelerEngine.analyze(
            statements = statements,
            evidence = emptyList()
        )
        
        // Should detect contradiction between "no deal" and "invoice"
        assertTrue(result.contradictions.isNotEmpty())
    }

    @Test
    fun `test confidence calculation`() {
        val evidence = listOf(
            createTestEvidence("1"),
            createTestEvidence("2"),
            createTestEvidence("3"),
            createTestEvidence("4"),
            createTestEvidence("5")
        )
        
        val result = LevelerEngine.analyze(
            statements = emptyList(),
            evidence = evidence
        )
        
        assertTrue(result.confidence >= 0.5f)
    }

    private fun createTestEvidence(id: String): ForensicEvidence {
        return ForensicEvidence(
            id = id,
            type = EvidenceType.DOCUMENT,
            content = "Test content".toByteArray(),
            contentHash = "test_hash_$id",
            mimeType = "text/plain",
            timestamp = System.currentTimeMillis(),
            location = null,
            metadata = EvidenceMetadata(
                filename = "test.txt",
                fileSize = 100,
                createdAt = System.currentTimeMillis(),
                modifiedAt = null,
                author = null,
                deviceInfo = "Test Device",
                appVersion = "1.0.0"
            ),
            sealed = true,
            sealHash = "seal_hash_$id"
        )
    }
}

/**
 * Test demonstrating full scanning flow with detailed report output
 * This test shows what happens when files are processed through the forensic engine
 */
class FileScanningReportTest {

    private lateinit var cryptoEngine: CryptographicSealingEngine

    @Before
    fun setup() {
        cryptoEngine = CryptographicSealingEngine()
    }

    /**
     * Demonstrates the complete file scanning flow:
     * 1. File input (simulated document content)
     * 2. Cryptographic hashing (SHA-512)
     * 3. Evidence sealing (HMAC-SHA512)
     * 4. Analysis through Leveler Engine
     * 5. Report generation
     */
    @Test
    fun `test complete file scanning flow with report generation`() {
        // ============================================================
        // STEP 1: Simulate file input (document content)
        // ============================================================
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
        val filename = "contract_agreement.txt"
        val mimeType = "text/plain"
        val timestamp = System.currentTimeMillis()

        // ============================================================
        // STEP 2: Calculate SHA-512 hash of file content
        // ============================================================
        val contentHash = cryptoEngine.calculateHash(fileBytes)

        // Verify hash properties
        assertEquals("SHA-512 hash should be 128 hex characters", 128, contentHash.length)
        assertTrue("Hash should only contain hex characters", contentHash.matches(Regex("[0-9a-f]+")))

        // ============================================================
        // STEP 3: Create ForensicEvidence object
        // ============================================================
        val evidence = ForensicEvidence(
            id = "EV-SCAN-001",
            type = EvidenceType.DOCUMENT,
            content = fileBytes,
            contentHash = contentHash,
            mimeType = mimeType,
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
                filename = filename,
                fileSize = fileBytes.size.toLong(),
                createdAt = timestamp,
                modifiedAt = null,
                author = "Test Scanner",
                deviceInfo = "Test Device (JUnit)",
                appVersion = "1.0.0"
            ),
            sealed = false,
            sealHash = null
        )

        // ============================================================
        // STEP 4: Seal the evidence with HMAC-SHA512
        // ============================================================
        val sealHash = cryptoEngine.sealEvidence(evidence)
        val sealedEvidence = evidence.copy(sealed = true, sealHash = sealHash)

        // Verify sealing
        assertTrue("Seal hash should be 128 hex characters", sealHash.length == 128)
        assertTrue("Evidence should be marked as sealed", sealedEvidence.sealed)
        assertNotNull("Seal hash should not be null", sealedEvidence.sealHash)

        // ============================================================
        // STEP 5: Verify content integrity
        // ============================================================
        val integrityVerified = cryptoEngine.verifyContentHash(fileBytes, contentHash)
        assertTrue("Content integrity should be verified", integrityVerified)

        // Tamper detection test
        val tamperedContent = "MODIFIED CONTENT".toByteArray()
        val tamperDetected = !cryptoEngine.verifyContentHash(tamperedContent, contentHash)
        assertTrue("Tampered content should be detected", tamperDetected)

        // ============================================================
        // STEP 6: Run Leveler Engine analysis
        // ============================================================
        val levelerResult = LevelerEngine.analyze(
            statements = emptyList(),
            evidence = listOf(sealedEvidence),
            expectedEvidence = emptyList()
        )

        // Verify analysis results
        assertTrue("Integrity score should be between 0 and 100", 
            levelerResult.integrityScore in 0f..100f)
        assertTrue("Confidence should be between 0 and 1", 
            levelerResult.confidence in 0f..1f)
        assertNotNull("Recommendations should not be null", levelerResult.recommendations)

        // ============================================================
        // STEP 7: Generate Report Output
        // ============================================================
        val report = buildScanReport(
            evidence = sealedEvidence,
            levelerResult = levelerResult
        )

        // Print the report for visibility
        println(report)

        // Verify report contains key information
        assertTrue("Report should contain evidence ID", report.contains("EV-SCAN-001"))
        assertTrue("Report should contain content hash", report.contains(contentHash.take(32)))
        assertTrue("Report should contain integrity score", 
            report.contains("Integrity Score"))
        assertTrue("Report should contain VERUM OMNIS header", 
            report.contains("VERUM OMNIS"))
    }

    /**
     * Test scanning multiple files and analyzing for contradictions
     */
    @Test
    fun `test scanning multiple files with contradiction detection`() {
        // Create multiple evidence items
        val evidenceList = mutableListOf<ForensicEvidence>()
        val baseTimestamp = System.currentTimeMillis()

        // File 1: Initial contract
        val file1Content = "Contract Value: $50,000 - Agreed on 2024-01-15"
        val file1 = createScannedEvidence(
            id = "EV-DOC-001",
            content = file1Content,
            filename = "initial_contract.txt",
            timestamp = baseTimestamp
        )
        evidenceList.add(file1)

        // File 2: Amendment (48+ hours later - will trigger timeline analysis)
        val file2Content = "Amendment: Contract value changed to $75,000"
        val file2 = createScannedEvidence(
            id = "EV-DOC-002",
            content = file2Content,
            filename = "contract_amendment.txt",
            timestamp = baseTimestamp + (72 * 60 * 60 * 1000) // 72 hours later
        )
        evidenceList.add(file2)

        // Create contradictory statements
        val statements = listOf(
            LevelerEngine.Statement(
                id = "STMT-001",
                speaker = "Party A Representative",
                content = "There was no deal made on this date",
                timestamp = baseTimestamp + 1000,
                source = "email"
            ),
            LevelerEngine.Statement(
                id = "STMT-002",
                speaker = "Party A Representative",
                content = "Here is the invoice for the deal we made",
                timestamp = baseTimestamp + 2000,
                source = "email"
            )
        )

        // Run analysis
        val result = LevelerEngine.analyze(
            statements = statements,
            evidence = evidenceList,
            expectedEvidence = listOf("bank statement", "receipt")
        )

        // Verify contradiction detection
        assertTrue("Should detect contradictions", result.contradictions.isNotEmpty())
        assertTrue("Should have missing evidence gaps", result.missingEvidence.isNotEmpty())
        assertTrue("Integrity score should be reduced due to issues", 
            result.integrityScore < 100f)

        // Generate detailed report
        val report = buildMultiFileReport(evidenceList, statements, result)
        println(report)

        // Verify report contents
        assertTrue("Report should list contradictions", 
            report.contains("CONTRADICTIONS DETECTED"))
        assertTrue("Report should list missing evidence", 
            report.contains("MISSING EVIDENCE"))
        assertTrue("Report should contain recommendations", 
            report.contains("RECOMMENDATIONS"))
    }

    /**
     * Test behavioral pattern detection in scanned content
     */
    @Test
    fun `test behavioral pattern detection in scanned statements`() {
        val statements = listOf(
            // Evasion patterns
            LevelerEngine.Statement(
                id = "S1",
                speaker = "Subject",
                content = "I refuse to answer that question",
                timestamp = System.currentTimeMillis(),
                source = "interview"
            ),
            LevelerEngine.Statement(
                id = "S2",
                speaker = "Subject",
                content = "I will address that later",
                timestamp = System.currentTimeMillis() + 1000,
                source = "interview"
            ),
            // Concealment patterns
            LevelerEngine.Statement(
                id = "S3",
                speaker = "Subject",
                content = "I deleted those files by accident",
                timestamp = System.currentTimeMillis() + 2000,
                source = "interview"
            ),
            LevelerEngine.Statement(
                id = "S4",
                speaker = "Subject",
                content = "I forgot where I put the documents",
                timestamp = System.currentTimeMillis() + 3000,
                source = "interview"
            )
        )

        val result = LevelerEngine.analyze(
            statements = statements,
            evidence = emptyList()
        )

        // Should detect behavioral patterns
        assertTrue("Should detect behavioral patterns", 
            result.behavioralPatterns.isNotEmpty())

        // Check for specific patterns
        val patternTypes = result.behavioralPatterns.map { it.type }
        assertTrue("Should detect evasion pattern", 
            patternTypes.contains(BehavioralPatternType.EVASION))
        assertTrue("Should detect concealment pattern", 
            patternTypes.contains(BehavioralPatternType.CONCEALMENT))

        // Generate behavioral report
        val report = buildBehavioralReport(statements, result)
        println(report)

        assertTrue("Report should contain behavioral analysis", 
            report.contains("BEHAVIORAL PATTERNS"))
    }

    // Helper methods for creating test data and reports

    private fun createScannedEvidence(
        id: String,
        content: String,
        filename: String,
        timestamp: Long
    ): ForensicEvidence {
        val bytes = content.toByteArray()
        val hash = cryptoEngine.calculateHash(bytes)
        
        val evidence = ForensicEvidence(
            id = id,
            type = EvidenceType.DOCUMENT,
            content = bytes,
            contentHash = hash,
            mimeType = "text/plain",
            timestamp = timestamp,
            location = null,
            metadata = EvidenceMetadata(
                filename = filename,
                fileSize = bytes.size.toLong(),
                createdAt = timestamp,
                modifiedAt = null,
                author = null,
                deviceInfo = "Test Scanner",
                appVersion = "1.0.0"
            ),
            sealed = false,
            sealHash = null
        )

        // Seal the evidence
        val sealHash = cryptoEngine.sealEvidence(evidence)
        return evidence.copy(sealed = true, sealHash = sealHash)
    }

    private fun buildScanReport(
        evidence: ForensicEvidence,
        levelerResult: LevelerEngine.LevelerResult
    ): String {
        return buildString {
            appendLine("=" .repeat(60))
            appendLine("       VERUM OMNIS FORENSIC SCAN REPORT")
            appendLine("=" .repeat(60))
            appendLine()
            appendLine("SCAN SUMMARY")
            appendLine("-".repeat(60))
            appendLine("Evidence ID:       ${evidence.id}")
            appendLine("Type:              ${evidence.type}")
            appendLine("Filename:          ${evidence.metadata.filename}")
            appendLine("File Size:         ${evidence.metadata.fileSize} bytes")
            appendLine("Timestamp:         ${java.util.Date(evidence.timestamp)}")
            appendLine("Device:            ${evidence.metadata.deviceInfo}")
            appendLine()
            appendLine("CRYPTOGRAPHIC VERIFICATION")
            appendLine("-".repeat(60))
            appendLine("Content Hash (SHA-512):")
            appendLine("  ${evidence.contentHash.take(64)}")
            appendLine("  ${evidence.contentHash.takeLast(64)}")
            appendLine("Sealed:            ${if (evidence.sealed) "YES ✓" else "NO"}")
            if (evidence.sealHash != null) {
                appendLine("Seal Hash (HMAC-SHA512):")
                appendLine("  ${evidence.sealHash!!.take(64)}")
                appendLine("  ${evidence.sealHash!!.takeLast(64)}")
            }
            appendLine()
            if (evidence.location != null) {
                appendLine("LOCATION DATA")
                appendLine("-".repeat(60))
                appendLine("Latitude:          ${evidence.location!!.latitude}")
                appendLine("Longitude:         ${evidence.location!!.longitude}")
                appendLine("Accuracy:          ${evidence.location!!.accuracy}m")
                appendLine("Provider:          ${evidence.location!!.provider}")
                appendLine()
            }
            appendLine("INTEGRITY ANALYSIS (B1-B9)")
            appendLine("-".repeat(60))
            appendLine("Integrity Score:   ${levelerResult.integrityScore}/100")
            appendLine("Confidence:        ${(levelerResult.confidence * 100).toInt()}%")
            appendLine("Contradictions:    ${levelerResult.contradictions.size}")
            appendLine("Timeline Anomalies: ${levelerResult.timelineAnomalies.size}")
            appendLine("Behavioral Flags:  ${levelerResult.behavioralPatterns.size}")
            appendLine("Missing Evidence:  ${levelerResult.missingEvidence.size}")
            appendLine()
            appendLine("RECOMMENDATIONS")
            appendLine("-".repeat(60))
            levelerResult.recommendations.forEach { rec ->
                appendLine("• $rec")
            }
            appendLine()
            appendLine("=" .repeat(60))
            appendLine("          VERUM OMNIS FORENSIC SEAL")
            appendLine("           AI FORENSICS FOR TRUTH")
            appendLine("=" .repeat(60))
        }
    }

    private fun buildMultiFileReport(
        evidenceList: List<ForensicEvidence>,
        statements: List<LevelerEngine.Statement>,
        result: LevelerEngine.LevelerResult
    ): String {
        return buildString {
            appendLine("=" .repeat(70))
            appendLine("         VERUM OMNIS MULTI-FILE FORENSIC ANALYSIS REPORT")
            appendLine("=" .repeat(70))
            appendLine()
            appendLine("EVIDENCE ITEMS SCANNED: ${evidenceList.size}")
            appendLine("-".repeat(70))
            evidenceList.forEachIndexed { index, ev ->
                appendLine("${index + 1}. ${ev.id} - ${ev.metadata.filename}")
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
            appendLine("MISSING EVIDENCE GAPS: ${result.missingEvidence.size}")
            appendLine("-".repeat(70))
            result.missingEvidence.forEach { gap ->
                appendLine("! Missing: ${gap.type} (${gap.criticality} criticality)")
                appendLine("  Action: ${gap.recommendedAction}")
            }
            appendLine()
            appendLine("TIMELINE ANOMALIES: ${result.timelineAnomalies.size}")
            appendLine("-".repeat(70))
            if (result.timelineAnomalies.isEmpty()) {
                appendLine("  No timeline anomalies detected.")
            } else {
                result.timelineAnomalies.forEach { anomaly ->
                    appendLine("⏱ ${anomaly.type}: ${anomaly.description}")
                    appendLine("  Suspicion Score: ${(anomaly.suspicionScore * 100).toInt()}%")
                }
            }
            appendLine()
            appendLine("INTEGRITY SUMMARY")
            appendLine("-".repeat(70))
            appendLine("Integrity Score: ${result.integrityScore}/100")
            appendLine("Confidence: ${(result.confidence * 100).toInt()}%")
            appendLine()
            appendLine("RECOMMENDATIONS")
            appendLine("-".repeat(70))
            result.recommendations.forEach { rec ->
                appendLine("→ $rec")
            }
            appendLine()
            appendLine("=" .repeat(70))
            appendLine("              VERUM OMNIS FORENSIC SEAL")
            appendLine("=" .repeat(70))
        }
    }

    private fun buildBehavioralReport(
        statements: List<LevelerEngine.Statement>,
        result: LevelerEngine.LevelerResult
    ): String {
        return buildString {
            appendLine("=" .repeat(60))
            appendLine("     VERUM OMNIS BEHAVIORAL ANALYSIS REPORT")
            appendLine("=" .repeat(60))
            appendLine()
            appendLine("STATEMENTS ANALYZED: ${statements.size}")
            appendLine("-".repeat(60))
            statements.forEach { stmt ->
                appendLine("• [${stmt.source}] ${stmt.speaker}: \"${stmt.content}\"")
            }
            appendLine()
            appendLine("BEHAVIORAL PATTERNS DETECTED: ${result.behavioralPatterns.size}")
            appendLine("-".repeat(60))
            result.behavioralPatterns.forEach { pattern ->
                appendLine("Pattern: ${pattern.type}")
                appendLine("  Score: ${(pattern.score * 100).toInt()}%")
                appendLine("  Frequency: ${pattern.frequency} occurrences")
                appendLine("  Examples:")
                pattern.examples.forEach { ex ->
                    appendLine("    - \"$ex\"")
                }
            }
            appendLine()
            appendLine("INTEGRITY IMPACT")
            appendLine("-".repeat(60))
            appendLine("Integrity Score: ${result.integrityScore}/100")
            appendLine()
            appendLine("=" .repeat(60))
        }
    }
}

/**
 * Unit tests for data models
 */
class DataModelTest {

    @Test
    fun `test ForensicCase creation`() {
        val case = ForensicCase(
            id = "CASE-12345678",
            name = "Test Case",
            description = "Test description",
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis(),
            evidence = mutableListOf(),
            status = CaseStatus.OPEN
        )
        
        assertEquals("CASE-12345678", case.id)
        assertEquals("Test Case", case.name)
        assertEquals(CaseStatus.OPEN, case.status)
        assertTrue(case.evidence.isEmpty())
    }

    @Test
    fun `test EvidenceType enum`() {
        assertEquals(5, EvidenceType.values().size)
        assertTrue(EvidenceType.values().contains(EvidenceType.DOCUMENT))
        assertTrue(EvidenceType.values().contains(EvidenceType.PHOTO))
        assertTrue(EvidenceType.values().contains(EvidenceType.TEXT))
    }

    @Test
    fun `test Severity enum ordering`() {
        assertTrue(Severity.LOW.ordinal < Severity.MEDIUM.ordinal)
        assertTrue(Severity.MEDIUM.ordinal < Severity.HIGH.ordinal)
        assertTrue(Severity.HIGH.ordinal < Severity.CRITICAL.ordinal)
    }

    @Test
    fun `test ForensicEvidence equality`() {
        val evidence1 = ForensicEvidence(
            id = "EV-1",
            type = EvidenceType.DOCUMENT,
            content = "content".toByteArray(),
            contentHash = "hash123",
            mimeType = "text/plain",
            timestamp = 1000L,
            location = null,
            metadata = EvidenceMetadata(
                filename = "test.txt",
                fileSize = 100,
                createdAt = 1000L,
                modifiedAt = null,
                author = null,
                deviceInfo = "Device",
                appVersion = "1.0.0"
            )
        )
        
        val evidence2 = evidence1.copy()
        
        assertEquals(evidence1, evidence2)
        assertEquals(evidence1.hashCode(), evidence2.hashCode())
    }
}
