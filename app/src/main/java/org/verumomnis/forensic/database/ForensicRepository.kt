package org.verumomnis.forensic.database

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.verumomnis.forensic.core.*
import java.io.File
import java.util.UUID

/**
 * Repository for forensic case and evidence persistence
 * 
 * Handles:
 * - Database operations for case/evidence metadata
 * - File storage for evidence content
 * - Conversion between entities and domain models
 */
class ForensicRepository(private val context: Context) {
    
    private val database = ForensicDatabase.getInstance(context)
    private val caseDao = database.caseDao()
    private val evidenceDao = database.evidenceDao()
    private val gson = com.google.gson.Gson()
    
    private val evidenceDir: File
        get() = File(context.filesDir, "evidence").apply { mkdirs() }
    
    // ==================== Case Operations ====================
    
    fun getAllCasesFlow(): Flow<List<ForensicCase>> {
        return caseDao.getAllCases().map { entities ->
            entities.map { entity ->
                val evidence = evidenceDao.getEvidenceForCaseOnce(entity.id)
                entityToCase(entity, evidence)
            }
        }
    }
    
    suspend fun getAllCases(): List<ForensicCase> {
        return caseDao.getAllCasesOnce().map { entity ->
            val evidence = evidenceDao.getEvidenceForCaseOnce(entity.id)
            entityToCase(entity, evidence)
        }
    }
    
    suspend fun getCaseById(caseId: String): ForensicCase? {
        val caseEntity = caseDao.getCaseById(caseId) ?: return null
        val evidence = evidenceDao.getEvidenceForCaseOnce(caseId)
        return entityToCase(caseEntity, evidence)
    }
    
    suspend fun saveCase(case: ForensicCase) {
        caseDao.insertCase(caseToEntity(case))
    }
    
    suspend fun updateCase(case: ForensicCase) {
        caseDao.updateCase(caseToEntity(case))
    }
    
    suspend fun deleteCase(caseId: String) {
        // Delete evidence files first
        val evidence = evidenceDao.getEvidenceForCaseOnce(caseId)
        evidence.forEach { ev ->
            File(ev.contentFilePath).delete()
        }
        // Database cascade will handle entity deletion
        caseDao.deleteCaseById(caseId)
    }
    
    // ==================== Evidence Operations ====================
    
    suspend fun saveEvidence(caseId: String, evidence: ForensicEvidence): String {
        // Save content to file
        val contentFile = File(evidenceDir, "${evidence.id}.bin")
        contentFile.writeBytes(evidence.content)
        
        // Save metadata to database
        val entity = evidenceToEntity(caseId, evidence, contentFile.absolutePath)
        evidenceDao.insertEvidence(entity)
        
        return evidence.id
    }
    
    suspend fun updateEvidence(caseId: String, evidence: ForensicEvidence) {
        val existingEntity = evidenceDao.getEvidenceById(evidence.id)
        val contentPath = existingEntity?.contentFilePath ?: run {
            val contentFile = File(evidenceDir, "${evidence.id}.bin")
            contentFile.writeBytes(evidence.content)
            contentFile.absolutePath
        }
        
        evidenceDao.updateEvidence(evidenceToEntity(caseId, evidence, contentPath))
    }
    
    suspend fun getEvidenceContent(evidenceId: String): ByteArray? {
        val entity = evidenceDao.getEvidenceById(evidenceId) ?: return null
        val file = File(entity.contentFilePath)
        return if (file.exists()) file.readBytes() else null
    }
    
    suspend fun deleteEvidence(evidenceId: String) {
        val entity = evidenceDao.getEvidenceById(evidenceId) ?: return
        File(entity.contentFilePath).delete()
        evidenceDao.deleteEvidenceById(evidenceId)
    }
    
    // ==================== Export Operations ====================
    
    suspend fun exportCase(caseId: String, exportDir: File): File? {
        val case = getCaseById(caseId) ?: return null
        
        val exportFile = File(exportDir, "case_${case.id}_${System.currentTimeMillis()}.json")
        
        // Create export data (without binary content, just metadata)
        val exportData = mapOf(
            "case" to case.copy(evidence = mutableListOf()),
            "evidenceCount" to case.evidence.size,
            "evidenceHashes" to case.evidence.map { it.contentHash },
            "exportedAt" to System.currentTimeMillis()
        )
        
        exportFile.writeText(gson.toJson(exportData))
        return exportFile
    }
    
    // ==================== Conversion Helpers ====================
    
    private fun caseToEntity(case: ForensicCase): CaseEntity {
        return CaseEntity(
            id = case.id,
            name = case.name,
            description = case.description,
            createdAt = case.createdAt,
            modifiedAt = case.modifiedAt,
            status = case.status,
            integrityHash = case.integrityHash
        )
    }
    
    private suspend fun entityToCase(entity: CaseEntity, evidenceEntities: List<EvidenceEntity>): ForensicCase {
        val evidenceList = evidenceEntities.map { ev ->
            entityToEvidence(ev)
        }.toMutableList()
        
        return ForensicCase(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            evidence = evidenceList,
            status = entity.status,
            integrityHash = entity.integrityHash
        )
    }
    
    private fun evidenceToEntity(caseId: String, evidence: ForensicEvidence, contentPath: String): EvidenceEntity {
        return EvidenceEntity(
            id = evidence.id,
            caseId = caseId,
            type = evidence.type,
            contentHash = evidence.contentHash,
            mimeType = evidence.mimeType,
            timestamp = evidence.timestamp,
            sealed = evidence.sealed,
            sealHash = evidence.sealHash,
            latitude = evidence.location?.latitude,
            longitude = evidence.location?.longitude,
            accuracy = evidence.location?.accuracy,
            altitude = evidence.location?.altitude,
            locationTimestamp = evidence.location?.timestamp,
            locationProvider = evidence.location?.provider,
            filename = evidence.metadata.filename,
            fileSize = evidence.metadata.fileSize,
            createdAt = evidence.metadata.createdAt,
            modifiedAt = evidence.metadata.modifiedAt,
            author = evidence.metadata.author,
            deviceInfo = evidence.metadata.deviceInfo,
            appVersion = evidence.metadata.appVersion,
            contentFilePath = contentPath
        )
    }
    
    private suspend fun entityToEvidence(entity: EvidenceEntity): ForensicEvidence {
        val content = File(entity.contentFilePath).let { 
            if (it.exists()) it.readBytes() else ByteArray(0)
        }
        
        val location = if (entity.latitude != null && entity.longitude != null) {
            ForensicLocation(
                latitude = entity.latitude,
                longitude = entity.longitude,
                accuracy = entity.accuracy ?: 0f,
                altitude = entity.altitude,
                timestamp = entity.locationTimestamp ?: 0L,
                provider = entity.locationProvider ?: "unknown"
            )
        } else null
        
        return ForensicEvidence(
            id = entity.id,
            type = entity.type,
            content = content,
            contentHash = entity.contentHash,
            mimeType = entity.mimeType,
            timestamp = entity.timestamp,
            location = location,
            metadata = EvidenceMetadata(
                filename = entity.filename,
                fileSize = entity.fileSize,
                createdAt = entity.createdAt,
                modifiedAt = entity.modifiedAt,
                author = entity.author,
                deviceInfo = entity.deviceInfo,
                appVersion = entity.appVersion
            ),
            sealed = entity.sealed,
            sealHash = entity.sealHash
        )
    }
}
