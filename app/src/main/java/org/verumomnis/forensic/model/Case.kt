package org.verumomnis.forensic.model

import com.google.gson.Gson
import java.util.UUID

/**
 * Forensic Case Model for Contradiction Engine
 * Simple, serializable case with evidence tracking
 */
data class Case(
    val caseId: String,
    val caseName: String,
    val createdAt: Long,
    val evidence: MutableList<Evidence> = mutableListOf()
) {
    companion object {
        fun create(name: String): Case {
            return Case(
                caseId = "CASE-${UUID.randomUUID().toString().take(8).uppercase()}",
                caseName = name,
                createdAt = System.currentTimeMillis()
            )
        }
        
        fun fromJson(json: String): Case {
            return Gson().fromJson(json, Case::class.java)
        }
    }
    
    fun toJson(): String {
        return Gson().toJson(this)
    }
}
