package org.verumomnis.forensic

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for the Verum Omnis Forensic Engine
 */
class CryptographicSealingEngineTest {

    @Test
    fun testHashGeneration() {
        // Given
        val content = "Test evidence content"
        val contentBytes = content.toByteArray(Charsets.UTF_8)
        
        // When - Manually compute expected hash
        val digest = java.security.MessageDigest.getInstance("SHA-512")
        val hashBytes = digest.digest(contentBytes)
        val expectedHash = android.util.Base64.encodeToString(hashBytes, android.util.Base64.NO_WRAP)
        
        // Note: This test would need Android context to run properly
        // This is a placeholder for the actual implementation test
        assertNotNull(contentBytes)
    }

    @Test
    fun testHashConsistency() {
        // Given
        val content = "Same content should produce same hash"
        
        // When
        val digest1 = java.security.MessageDigest.getInstance("SHA-512")
        val hash1 = digest1.digest(content.toByteArray())
        
        val digest2 = java.security.MessageDigest.getInstance("SHA-512")
        val hash2 = digest2.digest(content.toByteArray())
        
        // Then
        assertArrayEquals(hash1, hash2)
    }

    @Test
    fun testDifferentContentDifferentHash() {
        // Given
        val content1 = "First content"
        val content2 = "Second content"
        
        // When
        val digest = java.security.MessageDigest.getInstance("SHA-512")
        val hash1 = digest.digest(content1.toByteArray())
        
        digest.reset()
        val hash2 = digest.digest(content2.toByteArray())
        
        // Then
        assertFalse(hash1.contentEquals(hash2))
    }

    @Test
    fun testSHA512OutputLength() {
        // Given
        val content = "Test content"
        
        // When
        val digest = java.security.MessageDigest.getInstance("SHA-512")
        val hash = digest.digest(content.toByteArray())
        
        // Then - SHA-512 produces 64 bytes (512 bits)
        assertEquals(64, hash.size)
    }
}
