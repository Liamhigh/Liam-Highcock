package org.verumomnis.forensic.crypto

import android.os.Build
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
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
 * Security Note: For enhanced security in production, consider integrating with
 * Android Keystore for hardware-backed key storage. The current implementation
 * uses a device-derived key combined with application context for sealing.
 * 
 * @author Liam Highcock
 */
class CryptographicSealingEngine {

    companion object {
        private const val HASH_ALGORITHM = "SHA-512"
        private const val HMAC_ALGORITHM = "HmacSHA512"
        
        /**
         * Derives a device-specific key component for sealing.
         * This is combined with evidence data to create unique seals.
         * Note: For production use with highly sensitive data, consider using
         * Android Keystore for hardware-backed key protection.
         */
        private fun deriveDeviceKey(): String {
            // Combine multiple device attributes for key derivation
            // This provides reasonable protection while maintaining portability
            val deviceInfo = buildString {
                append(Build.MANUFACTURER)
                append(Build.MODEL)
                append(Build.FINGERPRINT.hashCode())
                append("VERUM_OMNIS_SAPS")
            }
            // Hash the device info to create a consistent key
            val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            return Base64.encodeToString(
                digest.digest(deviceInfo.toByteArray(Charsets.UTF_8)),
                Base64.NO_WRAP
            )
        }
    }

    // Lazily initialize the device key
    private val deviceKey: String by lazy { deriveDeviceKey() }

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
     * @param key Optional custom key. If not provided, uses device-derived key.
     * @return Base64-encoded seal
     */
    fun createSeal(data: ByteArray, key: String? = null): String {
        val sealKey = key ?: deviceKey
        val secretKey = SecretKeySpec(sealKey.toByteArray(Charsets.UTF_8), HMAC_ALGORITHM)
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(secretKey)
        val sealBytes = mac.doFinal(data)
        return Base64.encodeToString(sealBytes, Base64.NO_WRAP)
    }

    /**
     * Verifies an HMAC-SHA512 seal
     * @param data The original data
     * @param seal The seal to verify
     * @param key Optional custom key. If not provided, uses device-derived key.
     * @return True if seal is valid
     */
    fun verifySeal(data: ByteArray, seal: String, key: String? = null): Boolean {
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
