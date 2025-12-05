package org.verumomnis.forensic.leveler

import org.junit.Test
import org.junit.Assert.*
import org.verumomnis.forensic.core.*

/**
 * Unit tests for LevelerEngine
 * 
 * Tests B1-B9 Leveler analysis functionality as described in "So valuable .PDF"
 */
class LevelerEngineTest {

    @Test
    fun `B2 should detect direct contradictions between statements`() {
        // Given: Two contradictory statements from the same speaker
        val statementA = LevelerEngine.Statement(
            id = "stmt1",
            speaker = "TestSpeaker",
            content = "I never signed that contract",
            timestamp = System.currentTimeMillis(),
            source = "chat_log_1"
        )
        
        val statementB = LevelerEngine.Statement(
            id = "stmt2",
            speaker = "TestSpeaker",
            content = "Yes, I signed the contract on January 15",
            timestamp = System.currentTimeMillis() + 1000,
            source = "email_1"
        )
        
        // When: Analyzing statements
        val result = LevelerEngine.analyze(
            statements = listOf(statementA, statementB),
            evidence = emptyList(),
            expectedEvidence = emptyList()
        )
        
        // Then: Should detect contradiction
        assertTrue("Should detect at least one contradiction", result.contradictions.isNotEmpty())
        assertEquals("Should detect exactly 1 contradiction", 1, result.contradictions.size)
        assertEquals(
            "Contradiction type should be DIRECT_OPPOSITE",
            LevelerEngine.ContradictionType.DIRECT_OPPOSITE,
            result.contradictions[0].type
        )
    }

    @Test
    fun `B3 should identify missing evidence gaps`() {
        // Given: Evidence list missing critical items
        val evidence = listOf<ForensicEvidence>()  // Empty evidence
        val expectedEvidence = listOf("contract", "invoice", "bank statement")
        
        // When: Analyzing for gaps
        val result = LevelerEngine.analyze(
            statements = emptyList(),
            evidence = evidence,
            expectedEvidence = expectedEvidence
        )
        
        // Then: Should identify all missing evidence
        assertEquals(
            "Should find 3 evidence gaps",
            3,
            result.missingEvidence.size
        )
        assertTrue(
            "Should include contract gap",
            result.missingEvidence.any { it.type.contains("contract", ignoreCase = true) }
        )
    }

    @Test
    fun `B5 should detect evasion behavioral patterns`() {
        // Given: Statements with evasion indicators
        val statements = listOf(
            LevelerEngine.Statement(
                id = "1",
                speaker = "Speaker",
                content = "I refuse to answer that question",
                timestamp = System.currentTimeMillis(),
                source = "doc1"
            ),
            LevelerEngine.Statement(
                id = "2",
                speaker = "Speaker",
                content = "I will respond later, maybe",
                timestamp = System.currentTimeMillis() + 1000,
                source = "doc2"
            )
        )
        
        // When: Analyzing behavioral patterns
        val result = LevelerEngine.analyze(
            statements = statements,
            evidence = emptyList()
        )
        
        // Then: Should detect evasion pattern
        assertTrue("Should detect behavioral patterns", result.behavioralPatterns.isNotEmpty())
        val evasionPattern = result.behavioralPatterns.find { 
            it.type == BehavioralPatternType.EVASION 
        }
        assertNotNull("Should detect EVASION pattern", evasionPattern)
        assertTrue("Evasion frequency should be > 0", evasionPattern!!.frequency > 0)
    }

    @Test
    fun `B9 should calculate integrity score correctly`() {
        // Given: Evidence with no issues
        val cleanEvidence = listOf(
            createTestEvidence("ev1", EvidenceType.DOCUMENT, sealed = true)
        )
        
        // When: Analyzing clean evidence
        val result = LevelerEngine.analyze(
            statements = emptyList(),
            evidence = cleanEvidence,
            expectedEvidence = emptyList()
        )
        
        // Then: Should have high integrity score
        assertTrue(
            "Integrity score should be high (>70) for clean evidence",
            result.integrityScore > 70f
        )
    }

    @Test
    fun `B9 integrity score should decrease with contradictions`() {
        // Given: Contradictory statements
        val statements = listOf(
            LevelerEngine.Statement("1", "A", "I denied the deal", 1000L, "doc1"),
            LevelerEngine.Statement("2", "A", "Yes there was a deal with invoice", 2000L, "doc2")
        )
        
        // When: Analyzing
        val result = LevelerEngine.analyze(
            statements = statements,
            evidence = emptyList()
        )
        
        // Then: Integrity score should be reduced
        assertTrue(
            "Integrity score should be reduced due to contradictions",
            result.integrityScore < 100f
        )
    }

    @Test
    fun `B6 should detect financial amount mismatches`() {
        // Given: Statements with financial discrepancies
        val statements = listOf(
            LevelerEngine.Statement(
                id = "1",
                speaker = "Person",
                content = "The invoice was for $1000",
                timestamp = 1000L,
                source = "statement1"
            ),
            LevelerEngine.Statement(
                id = "2",
                speaker = "Person",
                content = "I paid $500 for that invoice",
                timestamp = 2000L,
                source = "statement2"
            )
        )
        
        // When: Analyzing financial transactions
        val discrepancies = LevelerEngine.analyzeFinancialTransactions(statements, emptyList())
        
        // Then: Should detect amount mismatch
        assertTrue(
            "Should detect financial discrepancies",
            discrepancies.isNotEmpty()
        )
    }

    @Test
    fun `B7 should detect deleted message indicators`() {
        // Given: Statements mentioning deleted messages
        val statements = listOf(
            LevelerEngine.Statement(
                id = "1",
                speaker = "Person",
                content = "This message was deleted",
                timestamp = 1000L,
                source = "chat1"
            ),
            LevelerEngine.Statement(
                id = "2",
                speaker = "Person",
                content = "Normal message",
                timestamp = 2000L,
                source = "chat2"
            )
        )
        
        // When: Analyzing communication patterns
        val patterns = LevelerEngine.analyzeCommunicationPatterns(statements)
        
        // Then: Should detect deleted message pattern
        val deletedPattern = patterns.find { 
            it.type == LevelerEngine.CommunicationType.DELETED_MESSAGES 
        }
        assertNotNull("Should detect deleted message pattern", deletedPattern)
        assertTrue("Frequency should be > 0", deletedPattern!!.frequency > 0)
    }

    @Test
    fun `B8 should check jurisdictional compliance for UAE`() {
        // Given: Evidence without proper sealing
        val evidence = listOf(
            createTestEvidence("ev1", EvidenceType.DOCUMENT, sealed = false)
        )
        
        // When: Checking compliance
        val compliance = LevelerEngine.checkJurisdictionalCompliance(evidence, emptyList())
        
        // Then: Should find UAE compliance violations
        val uaeCompliance = compliance.find { it.jurisdiction == LevelerEngine.Jurisdiction.UAE }
        assertNotNull("Should have UAE compliance check", uaeCompliance)
        assertTrue(
            "Should find violations for unsealed evidence",
            uaeCompliance!!.violations.isNotEmpty()
        )
    }

    @Test
    fun `Enhanced analysis should include all B1-B9 results`() {
        // Given: Sample evidence and statements
        val evidence = listOf(
            createTestEvidence("ev1", EvidenceType.DOCUMENT, sealed = true)
        )
        val statements = listOf(
            LevelerEngine.Statement("1", "Person", "Test statement", 1000L, "doc1")
        )
        
        // When: Running enhanced analysis
        val result = LevelerEngine.analyzeEnhanced(
            statements = statements,
            evidence = evidence,
            expectedEvidence = listOf("contract")
        )
        
        // Then: Should have all analysis components
        assertNotNull("Should have contradictions list", result.contradictions)
        assertNotNull("Should have missing evidence list", result.missingEvidence)
        assertNotNull("Should have timeline anomalies", result.timelineAnomalies)
        assertNotNull("Should have behavioral patterns", result.behavioralPatterns)
        assertNotNull("Should have financial discrepancies", result.financialDiscrepancies)
        assertNotNull("Should have communication patterns", result.communicationPatterns)
        assertNotNull("Should have jurisdictional compliance", result.jurisdictionalCompliance)
        assertNotNull("Should have recommendations", result.recommendations)
        assertTrue("Should have integrity score", result.integrityScore >= 0f)
    }

    // Helper method to create test evidence
    private fun createTestEvidence(
        id: String,
        type: EvidenceType,
        sealed: Boolean = false
    ): ForensicEvidence {
        return ForensicEvidence(
            id = id,
            type = type,
            content = "Test content".toByteArray(),
            contentHash = "test-hash",
            mimeType = "text/plain",
            timestamp = System.currentTimeMillis(),
            location = null,
            metadata = EvidenceMetadata(
                filename = "test.txt",
                fileSize = 100L,
                createdAt = System.currentTimeMillis(),
                modifiedAt = null,
                author = null,
                deviceInfo = "Test Device",
                appVersion = "1.0.0"
            ),
            sealed = sealed,
            sealHash = if (sealed) "seal-hash" else null
        )
    }
}
