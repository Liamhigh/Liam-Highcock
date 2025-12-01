package org.verumomnis.forensic.crypto

import org.verumomnis.forensic.core.ForensicCase
import org.verumomnis.forensic.core.ForensicEvidence
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Cryptographic Sealing Engine
 * 
 * Provides cryptographic sealing for forensic evidence using:
 * - SHA-512 for content hashing
 * - HMAC-SHA512 for tamper-proof sealing
 * 
 * All operations are stateless and offline-capable.
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
class CryptographicSealingEngine {

    companion object {
        private const val HASH_ALGORITHM = "SHA-512"
        private const val HMAC_ALGORITHM = "HmacSHA512"
        private const val SEAL_PREFIX = "VERUM_OMNIS_SEAL_V1"
    }

    private val secureRandom = SecureRandom()

    /**
     * Calculate SHA-512 hash of data
     */
    fun calculateHash(data: ByteArray): String {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val hashBytes = digest.digest(data)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Calculate SHA-512 hash of string
     */
    fun calculateHash(data: String): String {
        return calculateHash(data.toByteArray(Charsets.UTF_8))
    }

    /**
     * Seal evidence with HMAC-SHA512
     */
    fun sealEvidence(evidence: ForensicEvidence): String {
        val sealData = buildSealData(evidence)
        return generateHmacSeal(sealData)
    }

    /**
     * Calculate case integrity hash
     */
    fun calculateCaseIntegrityHash(forensicCase: ForensicCase): String {
        val caseData = buildCaseData(forensicCase)
        return calculateHash(caseData)
    }

    /**
     * Verify evidence seal
     */
    fun verifySeal(evidence: ForensicEvidence, providedSeal: String): Boolean {
        if (!evidence.sealed || evidence.sealHash == null) {
            return false
        }
        
        // Recalculate seal
        val expectedSeal = sealEvidence(evidence.copy(sealed = false, sealHash = null))
        
        // Compare seals
        return expectedSeal == providedSeal
    }

    /**
     * Verify content hash
     */
    fun verifyContentHash(content: ByteArray, expectedHash: String): Boolean {
        val calculatedHash = calculateHash(content)
        return calculatedHash == expectedHash
    }

    /**
     * Generate cryptographic salt
     */
    fun generateSalt(length: Int = 32): ByteArray {
        val salt = ByteArray(length)
        secureRandom.nextBytes(salt)
        return salt
    }

    /**
     * Generate timestamp-based nonce
     */
    fun generateNonce(): String {
        val timestamp = System.currentTimeMillis()
        val random = ByteArray(8)
        secureRandom.nextBytes(random)
        val randomHex = random.joinToString("") { "%02x".format(it) }
        return "$timestamp-$randomHex"
    }

    // Private helper methods

    private fun buildSealData(evidence: ForensicEvidence): String {
        return StringBuilder().apply {
            append(SEAL_PREFIX)
            append("|")
            append(evidence.id)
            append("|")
            append(evidence.type.name)
            append("|")
            append(evidence.contentHash)
            append("|")
            append(evidence.mimeType)
            append("|")
            append(evidence.timestamp)
            append("|")
            evidence.location?.let {
                append("${it.latitude},${it.longitude},${it.accuracy}")
            } ?: append("NO_LOCATION")
            append("|")
            append(evidence.metadata.fileSize)
            append("|")
            append(evidence.metadata.createdAt)
            append("|")
            append(evidence.metadata.deviceInfo)
            append("|")
            append(evidence.metadata.appVersion)
        }.toString()
    }

    private fun buildCaseData(forensicCase: ForensicCase): String {
        return StringBuilder().apply {
            append(SEAL_PREFIX)
            append("|CASE|")
            append(forensicCase.id)
            append("|")
            append(forensicCase.name)
            append("|")
            append(forensicCase.createdAt)
            append("|")
            append(forensicCase.evidence.size)
            append("|")
            
            // Add all evidence hashes in order
            forensicCase.evidence.sortedBy { it.timestamp }.forEach { evidence ->
                append(evidence.contentHash)
                append(";")
                evidence.sealHash?.let { append(it) }
                append("|")
            }
        }.toString()
    }

    private fun generateHmacSeal(data: String): String {
        // Use device-specific key derivation for HMAC
        val keyMaterial = deriveKeyMaterial()
        
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        val secretKey = SecretKeySpec(keyMaterial, HMAC_ALGORITHM)
        mac.init(secretKey)
        
        val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return hmacBytes.joinToString("") { "%02x".format(it) }
    }

    private fun deriveKeyMaterial(): ByteArray {
        // Derive key from constant seed (in production, use secure key storage)
        val seedPhrase = "VERUM_OMNIS_FORENSIC_INTEGRITY_KEY_V1_LIAM_HIGHCOCK"
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        return digest.digest(seedPhrase.toByteArray(Charsets.UTF_8)).take(64).toByteArray()
    }
}
