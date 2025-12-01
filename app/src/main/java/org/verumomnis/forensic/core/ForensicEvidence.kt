package org.verumomnis.forensic.core

import java.io.Serializable

/**
 * Forensic Evidence Data Model
 * Represents a single piece of evidence with cryptographic sealing
 */
data class ForensicEvidence(
    val id: String,
    val type: EvidenceType,
    val content: ByteArray,
    val contentHash: String,
    val mimeType: String,
    val timestamp: Long,
    val location: ForensicLocation?,
    val metadata: EvidenceMetadata,
    val sealed: Boolean = false,
    val sealHash: String? = null
) : Serializable {
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ForensicEvidence
        return id == other.id && contentHash == other.contentHash
    }

    override fun hashCode(): Int {
        return 31 * id.hashCode() + contentHash.hashCode()
    }
}

/**
 * Evidence Type Enumeration
 */
enum class EvidenceType {
    DOCUMENT,   // Scanned documents
    PHOTO,      // Captured photos
    TEXT,       // Text notes and observations
    AUDIO,      // Audio recordings (future)
    VIDEO       // Video recordings (future)
}

/**
 * Forensic Location Data
 */
data class ForensicLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val altitude: Double?,
    val timestamp: Long,
    val provider: String
) : Serializable

/**
 * Evidence Metadata
 */
data class EvidenceMetadata(
    val filename: String?,
    val fileSize: Long,
    val createdAt: Long,
    val modifiedAt: Long?,
    val author: String?,
    val deviceInfo: String,
    val appVersion: String
) : Serializable

/**
 * Forensic Case - Container for multiple evidence items
 */
data class ForensicCase(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: Long,
    val modifiedAt: Long,
    val evidence: MutableList<ForensicEvidence> = mutableListOf(),
    val status: CaseStatus = CaseStatus.OPEN,
    val integrityHash: String? = null
) : Serializable

/**
 * Case Status
 */
enum class CaseStatus {
    OPEN,       // Active case, evidence can be added
    SEALED,     // Case sealed, no more evidence can be added
    REPORTED,   // Forensic report generated
    ARCHIVED    // Case archived
}

/**
 * Forensic Report Data
 */
data class ForensicReport(
    val id: String,
    val caseId: String,
    val caseName: String,
    val generatedAt: Long,
    val narrative: String,
    val evidenceSummary: List<EvidenceSummary>,
    val integrityHash: String,
    val apkHash: String,
    val qrCodeData: String,
    val pdfBytes: ByteArray
) : Serializable {
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ForensicReport
        return id == other.id && integrityHash == other.integrityHash
    }

    override fun hashCode(): Int {
        return 31 * id.hashCode() + integrityHash.hashCode()
    }
}

/**
 * Evidence Summary for Reports
 */
data class EvidenceSummary(
    val evidenceId: String,
    val type: EvidenceType,
    val hash: String,
    val timestamp: Long,
    val description: String
) : Serializable

/**
 * Analysis Result from Verum Omnis Logic
 */
data class AnalysisResult(
    val keywords: List<KeywordMatch>,
    val legalSubjects: List<LegalSubject>,
    val redFlags: List<RedFlag>,
    val behavioralPatterns: List<BehavioralPattern>,
    val timeline: List<TimelineEvent>,
    val integrityScore: Float,
    val confidence: Float
) : Serializable

/**
 * Keyword Match
 */
data class KeywordMatch(
    val keyword: String,
    val context: String,
    val position: Int,
    val severity: Severity
) : Serializable

/**
 * Legal Subject Detection
 */
data class LegalSubject(
    val name: String,
    val keywords: List<String>,
    val severity: Severity,
    val matchedText: String
) : Serializable

/**
 * Red Flag Detection
 */
data class RedFlag(
    val type: RedFlagType,
    val description: String,
    val evidence: String,
    val severity: Severity,
    val weight: Int
) : Serializable

/**
 * Red Flag Types
 */
enum class RedFlagType {
    CONTRADICTION,
    OMISSION,
    MANIPULATION,
    CONCEALMENT,
    EVASION
}

/**
 * Behavioral Pattern
 */
data class BehavioralPattern(
    val type: BehavioralPatternType,
    val score: Float,
    val examples: List<String>,
    val frequency: Int
) : Serializable

/**
 * Behavioral Pattern Types
 */
enum class BehavioralPatternType {
    EVASION,
    GASLIGHTING,
    CONCEALMENT,
    DEFLECTION,
    CONTRADICTION
}

/**
 * Timeline Event
 */
data class TimelineEvent(
    val timestamp: Long,
    val description: String,
    val source: String,
    val confidence: Float
) : Serializable

/**
 * Severity Levels
 */
enum class Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
