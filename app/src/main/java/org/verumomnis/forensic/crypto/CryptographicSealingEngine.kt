package org.verumomnis.forensic.crypto

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Cryptographic Sealing Engine for Forensic Evidence
 * 
 * Implements SHA-512 hashing with HMAC-SHA512 sealing for tamper detection
 * as per Verum Omnis Forensic Standards.
 * 
 * Hash Standard: SHA-512
 * Tamper Detection: Mandatory
 * Admissibility Standard: Legal-grade
 * 
 * @author Liam Highcock
 */
class CryptographicSealingEngine {

    companion object {
        private const val HASH_ALGORITHM = "SHA-512"
        private const val HMAC_ALGORITHM = "HmacSHA512"
        private const val FORENSIC_SALT = "VERUM_OMNIS_SAPS_2024"
    }

    /**
     * Calculates SHA-512 hash of the given data
     * @param data The byte array to hash
     * @return Hex-encoded hash string
     */
    fun calculateHash(data: ByteArray): String {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val hashBytes = digest.digest(data)
        return hashBytes.toHexString()
    }

    /**
     * Calculates SHA-512 hash of a string
     * @param text The string to hash
     * @return Hex-encoded hash string
     */
    fun calculateHash(text: String): String {
        return calculateHash(text.toByteArray(Charsets.UTF_8))
    }

    /**
     * Creates an HMAC-SHA512 seal for evidence integrity verification
     * @param data The data to seal
     * @param key The secret key for sealing
     * @return Base64-encoded seal
     */
    fun createSeal(data: ByteArray, key: String = FORENSIC_SALT): String {
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), HMAC_ALGORITHM)
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(secretKey)
        val sealBytes = mac.doFinal(data)
        return Base64.encodeToString(sealBytes, Base64.NO_WRAP)
    }

    /**
     * Verifies an HMAC-SHA512 seal
     * @param data The original data
     * @param seal The seal to verify
     * @param key The secret key used for sealing
     * @return True if seal is valid
     */
    fun verifySeal(data: ByteArray, seal: String, key: String = FORENSIC_SALT): Boolean {
        val expectedSeal = createSeal(data, key)
        return expectedSeal == seal
    }

    /**
     * Creates a complete forensic seal combining content hash and timestamp
     * @param content The evidence content
     * @param timestamp The capture timestamp
     * @param location Optional GPS coordinates
     * @return ForensicSeal object with all verification data
     */
    fun createForensicSeal(
        content: ByteArray,
        timestamp: Long,
        latitude: Double? = null,
        longitude: Double? = null
    ): ForensicSeal {
        val contentHash = calculateHash(content)
        
        // Create metadata string for sealing
        val metadata = buildString {
            append("HASH:$contentHash")
            append("|TIME:$timestamp")
            latitude?.let { append("|LAT:$it") }
            longitude?.let { append("|LON:$it") }
        }
        
        val metadataHash = calculateHash(metadata)
        val seal = createSeal((contentHash + metadataHash).toByteArray())
        
        return ForensicSeal(
            contentHash = contentHash,
            metadataHash = metadataHash,
            seal = seal,
            timestamp = timestamp,
            latitude = latitude,
            longitude = longitude
        )
    }

    /**
     * Verifies a complete forensic seal
     * @param content The evidence content to verify
     * @param forensicSeal The seal to verify against
     * @return True if the content matches the seal
     */
    fun verifyForensicSeal(content: ByteArray, forensicSeal: ForensicSeal): Boolean {
        val currentHash = calculateHash(content)
        if (currentHash != forensicSeal.contentHash) {
            return false
        }
        
        val metadata = buildString {
            append("HASH:${forensicSeal.contentHash}")
            append("|TIME:${forensicSeal.timestamp}")
            forensicSeal.latitude?.let { append("|LAT:$it") }
            forensicSeal.longitude?.let { append("|LON:$it") }
        }
        
        val metadataHash = calculateHash(metadata)
        return metadataHash == forensicSeal.metadataHash
    }

    /**
     * Extension function to convert ByteArray to hex string
     */
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}

/**
 * Data class representing a complete forensic seal
 */
data class ForensicSeal(
    val contentHash: String,
    val metadataHash: String,
    val seal: String,
    val timestamp: Long,
    val latitude: Double? = null,
    val longitude: Double? = null
)
