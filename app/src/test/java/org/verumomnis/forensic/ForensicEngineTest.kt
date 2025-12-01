package org.verumomnis.forensic

import org.junit.Test
import org.junit.Assert.*
import org.verumomnis.forensic.crypto.CryptographicSealingEngine

/**
 * Unit tests for SAPS Forensic Engine
 */
class ForensicEngineTest {

    private val cryptoEngine = CryptographicSealingEngine()

    @Test
    fun testHashCalculation() {
        val testData = "Test evidence content"
        val hash = cryptoEngine.calculateHash(testData)
        
        // SHA-512 produces 128 character hex string
        assertEquals(128, hash.length)
        
        // Same input should produce same hash
        val hash2 = cryptoEngine.calculateHash(testData)
        assertEquals(hash, hash2)
    }

    @Test
    fun testDifferentInputsDifferentHashes() {
        val hash1 = cryptoEngine.calculateHash("Content A")
        val hash2 = cryptoEngine.calculateHash("Content B")
        
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun testSealCreation() {
        val testData = "Test evidence content".toByteArray()
        val seal = cryptoEngine.createSeal(testData)
        
        // Seal should not be empty
        assertTrue(seal.isNotEmpty())
    }

    @Test
    fun testSealVerification() {
        val testData = "Test evidence content".toByteArray()
        val seal = cryptoEngine.createSeal(testData)
        
        // Verification should pass for same data
        assertTrue(cryptoEngine.verifySeal(testData, seal))
        
        // Verification should fail for different data
        val modifiedData = "Modified content".toByteArray()
        assertFalse(cryptoEngine.verifySeal(modifiedData, seal))
    }

    @Test
    fun testForensicSealCreation() {
        val content = "Evidence content".toByteArray()
        val timestamp = System.currentTimeMillis()
        val latitude = -25.7461
        val longitude = 28.1879
        
        val forensicSeal = cryptoEngine.createForensicSeal(
            content = content,
            timestamp = timestamp,
            latitude = latitude,
            longitude = longitude
        )
        
        // Verify seal properties
        assertNotNull(forensicSeal.contentHash)
        assertNotNull(forensicSeal.metadataHash)
        assertNotNull(forensicSeal.seal)
        assertEquals(timestamp, forensicSeal.timestamp)
        assertEquals(latitude, forensicSeal.latitude)
        assertEquals(longitude, forensicSeal.longitude)
    }

    @Test
    fun testForensicSealVerification() {
        val content = "Evidence content".toByteArray()
        val forensicSeal = cryptoEngine.createForensicSeal(
            content = content,
            timestamp = System.currentTimeMillis(),
            latitude = -25.7461,
            longitude = 28.1879
        )
        
        // Verification should pass
        assertTrue(cryptoEngine.verifyForensicSeal(content, forensicSeal))
        
        // Verification should fail for tampered content
        val tamperedContent = "Tampered content".toByteArray()
        assertFalse(cryptoEngine.verifyForensicSeal(tamperedContent, forensicSeal))
    }

    @Test
    fun testHashConsistency() {
        // Test that hashing is deterministic
        val input = "Consistent test data"
        val expectedHash = cryptoEngine.calculateHash(input)
        
        repeat(10) {
            val hash = cryptoEngine.calculateHash(input)
            assertEquals(expectedHash, hash)
        }
    }
}
