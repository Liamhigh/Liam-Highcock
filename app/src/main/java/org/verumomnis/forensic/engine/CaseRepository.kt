package org.verumomnis.forensic.engine

import android.content.Context
import com.google.gson.Gson
import org.verumomnis.forensic.model.Case
import java.io.File

/**
 * Case Repository
 * Handles local storage of cases as JSON files
 */
class CaseRepository(private val context: Context) {
    
    private val gson = Gson()
    
    /**
     * Get cases directory
     */
    private fun caseDir(): File {
        val dir = File(context.filesDir, "cases")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
    
    /**
     * Save case to JSON file
     */
    fun saveCase(case: Case) {
        try {
            val file = File(caseDir(), "${case.caseId}.json")
            file.writeText(gson.toJson(case))
        } catch (e: Exception) {
            android.util.Log.e("CaseRepository", "Failed to save case", e)
        }
    }
    
    /**
     * Load case from JSON file
     */
    fun loadCase(id: String): Case? {
        return try {
            val file = File(caseDir(), "$id.json")
            if (!file.exists()) return null
            gson.fromJson(file.readText(), Case::class.java)
        } catch (e: Exception) {
            android.util.Log.e("CaseRepository", "Failed to load case", e)
            null
        }
    }
    
    /**
     * Get all cases
     */
    fun getAllCases(): List<Case> {
        return try {
            val files = caseDir().listFiles { file -> file.extension == "json" }
            files?.mapNotNull { file ->
                try {
                    gson.fromJson(file.readText(), Case::class.java)
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
        } catch (e: Exception) {
            android.util.Log.e("CaseRepository", "Failed to load cases", e)
            emptyList()
        }
    }
    
    /**
     * Delete case
     */
    fun deleteCase(id: String): Boolean {
        return try {
            val file = File(caseDir(), "$id.json")
            file.delete()
        } catch (e: Exception) {
            android.util.Log.e("CaseRepository", "Failed to delete case", e)
            false
        }
    }
}
