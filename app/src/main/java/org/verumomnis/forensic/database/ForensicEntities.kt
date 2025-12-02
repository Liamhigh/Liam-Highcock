package org.verumomnis.forensic.database

import androidx.room.*
import org.verumomnis.forensic.core.CaseStatus
import org.verumomnis.forensic.core.EvidenceType

/**
 * Database entity for ForensicCase
 */
@Entity(tableName = "forensic_cases")
data class CaseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val createdAt: Long,
    val modifiedAt: Long,
    val status: CaseStatus,
    val integrityHash: String?
)

/**
 * Database entity for ForensicEvidence
 * Evidence content is stored as files, not in database
 */
@Entity(
    tableName = "forensic_evidence",
    foreignKeys = [
        ForeignKey(
            entity = CaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["caseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("caseId")]
)
data class EvidenceEntity(
    @PrimaryKey
    val id: String,
    val caseId: String,
    val type: EvidenceType,
    val contentHash: String,
    val mimeType: String,
    val timestamp: Long,
    val sealed: Boolean,
    val sealHash: String?,
    // Location data
    val latitude: Double?,
    val longitude: Double?,
    val accuracy: Float?,
    val altitude: Double?,
    val locationTimestamp: Long?,
    val locationProvider: String?,
    // Metadata
    val filename: String?,
    val fileSize: Long,
    val createdAt: Long,
    val modifiedAt: Long?,
    val author: String?,
    val deviceInfo: String,
    val appVersion: String,
    // File path for content storage
    val contentFilePath: String
)

/**
 * Type converters for Room
 */
class Converters {
    @TypeConverter
    fun fromCaseStatus(status: CaseStatus): String = status.name

    @TypeConverter
    fun toCaseStatus(value: String): CaseStatus = CaseStatus.valueOf(value)

    @TypeConverter
    fun fromEvidenceType(type: EvidenceType): String = type.name

    @TypeConverter
    fun toEvidenceType(value: String): EvidenceType = EvidenceType.valueOf(value)
}
