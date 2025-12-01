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
