package org.verumomnis.forensic.engine

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

/**
 * Law Engine
 * Reads rule JSON files, applies legal logic, and produces findings
 */
class LawEngine(private val context: Context) {
    
    /**
     * Evaluate statements against legal rules
     */
    fun evaluate(statements: List<String>): List<String> {
        val rules = loadJsonArray("rules/verum_rules.json")
        val findings = mutableListOf<String>()
        
        for (i in 0 until rules.length()) {
            val rule = rules.optJSONObject(i) ?: continue
            val trigger = rule.optString("trigger", "")
            val consequence = rule.optString("consequence", "")
            
            if (trigger.isNotEmpty() && consequence.isNotEmpty()) {
                if (statements.any { it.contains(trigger, ignoreCase = true) }) {
                    findings.add(consequence)
                }
            }
        }
        
        // Also check for legal subjects in verum_rules.json
        val legalSubjects = loadLegalSubjects()
        for (subject in legalSubjects) {
            val name = subject.optString("name", "")
            val keywords = subject.optJSONArray("keywords")
            
            if (keywords != null) {
                for (k in 0 until keywords.length()) {
                    val keyword = keywords.optString(k, "")
                    if (keyword.isNotEmpty() && statements.any { it.contains(keyword, ignoreCase = true) }) {
                        findings.add("Legal subject detected: $name")
                        break
                    }
                }
            }
        }
        
        return findings
    }
    
    /**
     * Load legal subjects from verum_rules.json
     */
    private fun loadLegalSubjects(): List<JSONObject> {
        return try {
            val input: InputStream = context.assets.open("rules/verum_rules.json")
            val text = input.bufferedReader().use { it.readText() }
            val root = JSONObject(text)
            val subjects = root.optJSONArray("legal_subjects")
            val result = mutableListOf<JSONObject>()
            
            if (subjects != null) {
                for (i in 0 until subjects.length()) {
                    subjects.optJSONObject(i)?.let { result.add(it) }
                }
            }
            
            result
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Load JSON array from assets
     */
    private fun loadJsonArray(path: String): JSONArray {
        return try {
            val input: InputStream = context.assets.open(path)
            val text = input.bufferedReader().use { it.readText() }
            
            // Try to parse as array first
            try {
                JSONArray(text)
            } catch (e: Exception) {
                // If not an array, might be an object with arrays inside
                JSONArray()
            }
        } catch (e: Exception) {
            JSONArray()
        }
    }
}
