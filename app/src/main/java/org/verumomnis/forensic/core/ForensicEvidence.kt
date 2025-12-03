package org.verumomnis.forensic.core

import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a piece of forensic evidence with cryptographic sealing
 */
data class ForensicEvidence(
    val id: String = UUID.randomUUID().toString(),
    val type: EvidenceType,
    val content: ByteArray,
    val contentDescription: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val location: GeoLocation? = null,
    val hash: String = "",
    val seal: String = "",
    val metadata: EvidenceMetadata = EvidenceMetadata()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ForensicEvidence
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

/**
 * Types of evidence that can be collected
 */
enum class EvidenceType {
    DOCUMENT,   // Scanned documents
    PHOTO,      // Captured photos
    TEXT,       // Text notes and observations
    AUDIO,      // Audio recordings (future)
    VIDEO       // Video recordings (future)
}

/**
 * Geographic location data for evidence
 */
data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val altitude: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Additional metadata for evidence
 */
data class EvidenceMetadata(
    val source: String = "",
    val capturedBy: String = "",
    val deviceInfo: String = "",
    val notes: String = ""
)

/**
 * Represents a forensic case containing multiple pieces of evidence
 */
data class ForensicCase(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val evidence: MutableList<ForensicEvidence> = mutableListOf(),
    val status: CaseStatus = CaseStatus.OPEN,
    val jurisdiction: String = "UAE"
)

/**
 * Case status enumeration
 */
enum class CaseStatus {
    OPEN,
    IN_PROGRESS,
    PENDING_REVIEW,
    CLOSED,
    ARCHIVED
}

/**
 * Result of forensic analysis
 */
data class ForensicResult(
    val caseId: String,
    val analysisTimestamp: LocalDateTime = LocalDateTime.now(),
    val evidenceCount: Int,
    val integrityScore: Int,
    val findings: List<Finding>,
    val recommendations: List<String>,
    val pdfReportPath: String? = null
)

/**
 * A finding from forensic analysis
 */
data class Finding(
    val id: String = UUID.randomUUID().toString(),
    val type: FindingType,
    val severity: Severity,
    val description: String,
    val evidenceIds: List<String>,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

/**
 * Types of findings
 */
enum class FindingType {
    CONTRADICTION,
    TIMELINE_ANOMALY,
    BEHAVIORAL_PATTERN,
    COMPLIANCE_VIOLATION,
    DATA_INTEGRITY_ISSUE,
    EVIDENCE_GAP
}

/**
 * Severity levels
 */
enum class Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
