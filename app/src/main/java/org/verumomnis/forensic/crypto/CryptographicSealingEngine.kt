package org.verumomnis.forensic.crypto

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

/**
 * Cryptographic Sealing Engine
 * 
 * Implements SHA-512 hashing with HMAC-SHA512 sealing for tamper detection.
 * All operations are performed locally with no external key exchange.
 * 
 * Standards:
 * - Hash Algorithm: SHA-512
 * - Seal Algorithm: HMAC-SHA512
 * - Encoding: Base64
 */
class CryptographicSealingEngine {

    companion object {
        private const val HASH_ALGORITHM = "SHA-512"
        private const val HMAC_ALGORITHM = "HmacSHA512"
        private const val KEY_SIZE = 64 // 512 bits
    }

    private val secureRandom = SecureRandom()

    /**
     * Generates a SHA-512 hash of the content
     */
    fun generateHash(content: ByteArray): String {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val hashBytes = digest.digest(content)
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }

    /**
     * Generates a SHA-512 hash of a string
     */
    fun generateHash(content: String): String {
        return generateHash(content.toByteArray(Charsets.UTF_8))
    }

    /**
     * Generates an HMAC-SHA512 seal for the content
     * The seal includes the case ID for case-specific integrity verification
     */
    fun generateSeal(content: ByteArray, caseId: String): String {
        val key = deriveKey(caseId)
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        val keySpec = SecretKeySpec(key, HMAC_ALGORITHM)
        mac.init(keySpec)
        
        val sealBytes = mac.doFinal(content)
        return Base64.encodeToString(sealBytes, Base64.NO_WRAP)
    }

    /**
     * Verifies an HMAC-SHA512 seal
     */
    fun verifySeal(content: ByteArray, caseId: String, seal: String): Boolean {
        val expectedSeal = generateSeal(content, caseId)
        return constantTimeEquals(expectedSeal, seal)
    }

    /**
     * Derives a key from the case ID using SHA-512
     * This provides case-specific sealing without external key storage
     */
    private fun deriveKey(caseId: String): ByteArray {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        return digest.digest(caseId.toByteArray(Charsets.UTF_8))
    }

    /**
     * Generates a secure random nonce
     */
    fun generateNonce(): ByteArray {
        val nonce = ByteArray(32)
        secureRandom.nextBytes(nonce)
        return nonce
    }

    /**
     * Creates a timestamp-bound seal that includes current time
     */
    fun generateTimestampedSeal(content: ByteArray, caseId: String, timestamp: Long): String {
        val timestampBytes = timestamp.toString().toByteArray(Charsets.UTF_8)
        val combined = content + timestampBytes
        return generateSeal(combined, caseId)
    }

    /**
     * Constant-time comparison to prevent timing attacks
     */
    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) {
            return false
        }
        
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }

    /**
     * Generates a chain seal that links to previous evidence
     */
    fun generateChainSeal(
        content: ByteArray,
        caseId: String,
        previousSeal: String?
    ): String {
        val chainData = if (previousSeal != null) {
            content + previousSeal.toByteArray(Charsets.UTF_8)
        } else {
            content
        }
        return generateSeal(chainData, caseId)
    }

    /**
     * Creates a complete evidence seal record
     */
    fun createSealRecord(
        content: ByteArray,
        caseId: String,
        evidenceId: String
    ): SealRecord {
        val hash = generateHash(content)
        val seal = generateSeal(content, caseId)
        val timestamp = System.currentTimeMillis()
        
        return SealRecord(
            evidenceId = evidenceId,
            hash = hash,
            seal = seal,
            algorithm = HASH_ALGORITHM,
            timestamp = timestamp
        )
    }
}

/**
 * Record of a cryptographic seal
 */
data class SealRecord(
    val evidenceId: String,
    val hash: String,
    val seal: String,
    val algorithm: String,
    val timestamp: Long
)
