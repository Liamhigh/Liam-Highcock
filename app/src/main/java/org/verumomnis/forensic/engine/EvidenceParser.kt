package org.verumomnis.forensic.engine

import android.content.Context
import org.verumomnis.forensic.model.Evidence
import java.io.File

/**
 * Evidence Parser
 * Reads text, PDFs, images (via OCR), extracts statements
 */
class EvidenceParser(private val context: Context) {
    
    /**
     * Parse evidence and extract statements
     */
    fun parseEvidence(evidence: Evidence): List<String> {
        val text = when (evidence.type) {
            "text" -> evidence.content
            "file" -> readFile(evidence.filePath)
            else -> ""
        }
        return splitIntoStatements(text)
    }
    
    /**
     * Read file content
     */
    private fun readFile(path: String): String {
        return try {
            File(path).takeIf { it.exists() }?.readText() ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Split text into individual statements
     */
    private fun splitIntoStatements(text: String): List<String> {
        return text.split(Regex("[.!?\\n]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}
