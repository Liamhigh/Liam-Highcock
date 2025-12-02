package org.verumomnis.forensic.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ForensicCase
 */
@Dao
interface CaseDao {
    @Query("SELECT * FROM forensic_cases ORDER BY modifiedAt DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM forensic_cases ORDER BY modifiedAt DESC")
    suspend fun getAllCasesOnce(): List<CaseEntity>

    @Query("SELECT * FROM forensic_cases WHERE id = :caseId")
    suspend fun getCaseById(caseId: String): CaseEntity?

    @Query("SELECT * FROM forensic_cases WHERE id = :caseId")
    fun getCaseByIdFlow(caseId: String): Flow<CaseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(case: CaseEntity)

    @Update
    suspend fun updateCase(case: CaseEntity)

    @Delete
    suspend fun deleteCase(case: CaseEntity)

    @Query("DELETE FROM forensic_cases WHERE id = :caseId")
    suspend fun deleteCaseById(caseId: String)
}

/**
 * Data Access Object for ForensicEvidence
 */
@Dao
interface EvidenceDao {
    @Query("SELECT * FROM forensic_evidence WHERE caseId = :caseId ORDER BY timestamp ASC")
    fun getEvidenceForCase(caseId: String): Flow<List<EvidenceEntity>>

    @Query("SELECT * FROM forensic_evidence WHERE caseId = :caseId ORDER BY timestamp ASC")
    suspend fun getEvidenceForCaseOnce(caseId: String): List<EvidenceEntity>

    @Query("SELECT * FROM forensic_evidence WHERE id = :evidenceId")
    suspend fun getEvidenceById(evidenceId: String): EvidenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidence: EvidenceEntity)

    @Update
    suspend fun updateEvidence(evidence: EvidenceEntity)

    @Delete
    suspend fun deleteEvidence(evidence: EvidenceEntity)

    @Query("DELETE FROM forensic_evidence WHERE id = :evidenceId")
    suspend fun deleteEvidenceById(evidenceId: String)

    @Query("DELETE FROM forensic_evidence WHERE caseId = :caseId")
    suspend fun deleteAllEvidenceForCase(caseId: String)
}
