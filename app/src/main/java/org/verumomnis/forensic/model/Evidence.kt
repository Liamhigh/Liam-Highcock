package org.verumomnis.forensic.model

import java.util.UUID

/**
 * Evidence Model for Contradiction Engine
 * Represents a single piece of evidence with text content
 */
data class Evidence(
    val evidenceId: String,
    val type: String, // "text" or "file"
    val content: String, // Text content or file content
    val filePath: String = "", // Path to file if type is "file"
    val summary: String, // Brief summary for reports
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun createText(content: String, summary: String): Evidence {
            return Evidence(
                evidenceId = "EV-${UUID.randomUUID().toString().take(8).uppercase()}",
                type = "text",
                content = content,
                summary = summary
            )
        }
        
        fun createFile(filePath: String, summary: String): Evidence {
            return Evidence(
                evidenceId = "EV-${UUID.randomUUID().toString().take(8).uppercase()}",
                type = "file",
                content = "",
                filePath = filePath,
                summary = summary
            )
        }
    }
}
