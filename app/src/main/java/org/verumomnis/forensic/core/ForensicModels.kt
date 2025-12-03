package org.verumomnis.forensic.core

import java.util.UUID

/**
 * Data classes for forensic evidence management
 */

/**
 * Represents a forensic case
 */
data class ForensicCase(
    val id: String = UUID.randomUUID().toString(),
    val caseNumber: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val status: CaseStatus = CaseStatus.OPEN,
    val evidence: MutableList<ForensicEvidence> = mutableListOf()
)

/**
 * Case status enumeration
 */
enum class CaseStatus {
    OPEN,
    IN_PROGRESS,
    UNDER_REVIEW,
    CLOSED,
    ARCHIVED
}

/**
 * Represents a piece of forensic evidence
 */
data class ForensicEvidence(
    val id: String = UUID.randomUUID().toString(),
    val type: EvidenceType,
    val description: String,
    val capturedAt: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val filePath: String? = null,
    val contentHash: String? = null,
    val sealHash: String? = null,
    val metadata: EvidenceMetadata = EvidenceMetadata()
)

/**
 * Evidence type enumeration
 */
enum class EvidenceType {
    DOCUMENT,
    PHOTO,
    TEXT_NOTE,
    AUDIO,      // Future implementation
    VIDEO       // Future implementation
}

/**
 * Additional metadata for evidence
 */
data class EvidenceMetadata(
    val collectorId: String? = null,
    val deviceInfo: String? = null,
    val notes: String? = null,
    val tags: List<String> = emptyList()
)

/**
 * Result of forensic processing
 */
data class ForensicResult(
    val caseId: String,
    val evidenceCount: Int,
    val reportPath: String,
    val analysisHash: String,
    val sealedAt: Long = System.currentTimeMillis(),
    val integrityVerified: Boolean = true
)

/**
 * Represents a sealed forensic report
 */
data class SealedReport(
    val id: String = UUID.randomUUID().toString(),
    val caseId: String,
    val pdfPath: String,
    val contentHash: String,
    val sealHash: String,
    val createdAt: Long = System.currentTimeMillis(),
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED
)

/**
 * Verification status for sealed reports
 */
enum class VerificationStatus {
    VERIFIED,
    TAMPERED,
    UNKNOWN
}
