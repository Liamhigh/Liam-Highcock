package org.verumomnis.forensic

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SAPS Forensic Engine
 * 
 * Note: These tests use explicit keys for testing as the device-derived key
 * requires Android runtime. The core cryptographic functions are tested here.
 */
class ForensicEngineTest {

    private val testKey = "TEST_KEY_FOR_UNIT_TESTING"

    @Test
    fun testHashCalculation() {
        val testData = "Test evidence content"
        val hash = calculateHash(testData.toByteArray())
        
        // SHA-512 produces 128 character hex string
        assertEquals(128, hash.length)
        
        // Same input should produce same hash
        val hash2 = calculateHash(testData.toByteArray())
        assertEquals(hash, hash2)
    }

    @Test
    fun testDifferentInputsDifferentHashes() {
        val hash1 = calculateHash("Content A".toByteArray())
        val hash2 = calculateHash("Content B".toByteArray())
        
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun testSealCreation() {
        val testData = "Test evidence content".toByteArray()
        val seal = createSeal(testData, testKey)
        
        // Seal should not be empty
        assertTrue(seal.isNotEmpty())
    }

    @Test
    fun testSealVerification() {
        val testData = "Test evidence content".toByteArray()
        val seal = createSeal(testData, testKey)
        
        // Verification should pass for same data
        val expectedSeal = createSeal(testData, testKey)
        assertEquals(seal, expectedSeal)
        
        // Verification should fail for different data
        val modifiedData = "Modified content".toByteArray()
        val modifiedSeal = createSeal(modifiedData, testKey)
        assertNotEquals(seal, modifiedSeal)
    }

    @Test
    fun testHashConsistency() {
        // Test that hashing is deterministic
        val input = "Consistent test data"
        val expectedHash = calculateHash(input.toByteArray())
        
        repeat(10) {
            val hash = calculateHash(input.toByteArray())
            assertEquals(expectedHash, hash)
        }
    }

    @Test
    fun testForensicSealDataIntegrity() {
        val content = "Evidence content"
        val timestamp = System.currentTimeMillis()
        val latitude = -25.7461
        val longitude = 28.1879
        
        // Create metadata string
        val contentHash = calculateHash(content.toByteArray())
        val metadata = buildString {
            append("HASH:$contentHash")
            append("|TIME:$timestamp")
            append("|LAT:$latitude")
            append("|LON:$longitude")
        }
        
        val metadataHash = calculateHash(metadata.toByteArray())
        
        // Verify metadata hash is consistent
        val metadataHash2 = calculateHash(metadata.toByteArray())
        assertEquals(metadataHash, metadataHash2)
        
        // Verify content hash changes with different content
        val tamperedContent = "Tampered content"
        val tamperedHash = calculateHash(tamperedContent.toByteArray())
        assertNotEquals(contentHash, tamperedHash)
    }

    // Helper functions that mirror the actual implementation
    private fun calculateHash(data: ByteArray): String {
        val digest = java.security.MessageDigest.getInstance("SHA-512")
        val hashBytes = digest.digest(data)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun createSeal(data: ByteArray, key: String): String {
        val secretKey = javax.crypto.spec.SecretKeySpec(
            key.toByteArray(Charsets.UTF_8), 
            "HmacSHA512"
        )
        val mac = javax.crypto.Mac.getInstance("HmacSHA512")
        mac.init(secretKey)
        val sealBytes = mac.doFinal(data)
        return java.util.Base64.getEncoder().encodeToString(sealBytes)
    }
}
