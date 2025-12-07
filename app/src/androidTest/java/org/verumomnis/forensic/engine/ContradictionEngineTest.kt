package org.verumomnis.forensic.engine

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.verumomnis.forensic.model.Evidence
import org.junit.Assert.*

/**
 * Test for Contradiction Engine
 */
@RunWith(AndroidJUnit4::class)
class ContradictionEngineTest {
    
    private lateinit var contradictionEngine: ContradictionEngine
    
    @Before
    fun setup() {
        contradictionEngine = ContradictionEngine()
    }
    
    @Test
    fun testDetectContradiction_didVsDidNot() {
        val statements = listOf(
            "I did sign the contract",
            "I did not sign the contract"
        )
        
        val contradictions = contradictionEngine.detectContradictions(statements)
        
        assertEquals(1, contradictions.size)
        assertEquals("Direct contradiction detected", contradictions[0].reason)
    }
    
    @Test
    fun testDetectContradiction_neverVsDid() {
        val statements = listOf(
            "I never received the email",
            "I did receive the email"
        )
        
        val contradictions = contradictionEngine.detectContradictions(statements)
        
        assertEquals(1, contradictions.size)
    }
    
    @Test
    fun testDetectContradiction_alwaysVsNever() {
        val statements = listOf(
            "I always attend meetings",
            "I never attend meetings"
        )
        
        val contradictions = contradictionEngine.detectContradictions(statements)
        
        assertEquals(1, contradictions.size)
    }
    
    @Test
    fun testNoContradiction() {
        val statements = listOf(
            "I attended the meeting",
            "The meeting was on Monday"
        )
        
        val contradictions = contradictionEngine.detectContradictions(statements)
        
        assertEquals(0, contradictions.size)
    }
    
    @Test
    fun testEvidenceParser() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val parser = EvidenceParser(context)
        
        val evidence = Evidence.createText(
            "I did sign. I never received it. I was there.",
            "Test evidence"
        )
        
        val statements = parser.parseEvidence(evidence)
        
        assertEquals(3, statements.size)
        assertTrue(statements.contains("I did sign"))
        assertTrue(statements.contains("I never received it"))
        assertTrue(statements.contains("I was there"))
    }
}
