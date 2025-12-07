package org.verumomnis.forensic.engine

import android.content.Context
import org.verumomnis.forensic.model.Case
import org.verumomnis.forensic.model.Contradiction

/**
 * Engine Orchestrator
 * Coordinates all engines to produce a complete forensic report
 */
class EngineOrchestrator(private val context: Context) {
    
    private val parser = EvidenceParser(context)
    private val contradictionEngine = ContradictionEngine()
    private val lawEngine = LawEngine(context)
    
    /**
     * Run full analysis on a case
     */
    fun run(case: Case): String {
        // Extract all statements from evidence
        val allStatements = mutableListOf<String>()
        case.evidence.forEach { evidence ->
            allStatements += parser.parseEvidence(evidence)
        }
        
        // Detect contradictions
        val contradictions = contradictionEngine.detectContradictions(allStatements)
        
        // Evaluate against legal rules
        val legalFindings = lawEngine.evaluate(allStatements)
        
        // Build comprehensive report
        return buildReport(case, contradictions, legalFindings)
    }
    
    /**
     * Build formatted report
     */
    private fun buildReport(
        case: Case,
        contradictions: List<Contradiction>,
        legalFindings: List<String>
    ): String {
        val sb = StringBuilder()
        
        sb.appendLine("═══════════════════════════════════════════════════════════")
        sb.appendLine("         VERUM OMNIS FORENSIC REPORT")
        sb.appendLine("═══════════════════════════════════════════════════════════")
        sb.appendLine()
        sb.appendLine("Case: ${case.caseName}")
        sb.appendLine("Case ID: ${case.caseId}")
        sb.appendLine("Generated: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())}")
        sb.appendLine()
        sb.appendLine("═══════════════════════════════════════════════════════════")
        sb.appendLine()
        
        // Evidence Summary
        sb.appendLine("EVIDENCE SUMMARY:")
        sb.appendLine("───────────────────────────────────────────────────────────")
        if (case.evidence.isEmpty()) {
            sb.appendLine("No evidence items found.")
        } else {
            case.evidence.forEachIndexed { index, evidence ->
                sb.appendLine("${index + 1}. ${evidence.summary}")
                sb.appendLine("   ID: ${evidence.evidenceId}")
                sb.appendLine("   Type: ${evidence.type}")
            }
        }
        sb.appendLine()
        
        // Contradictions
        sb.appendLine("CONTRADICTIONS FOUND:")
        sb.appendLine("───────────────────────────────────────────────────────────")
        if (contradictions.isEmpty()) {
            sb.appendLine("✓ No contradictions detected.")
        } else {
            contradictions.forEachIndexed { index, contradiction ->
                sb.appendLine("${index + 1}. CONTRADICTION DETECTED:")
                sb.appendLine("   Statement A: \"${contradiction.statementA}\"")
                sb.appendLine("   Statement B: \"${contradiction.statementB}\"")
                sb.appendLine("   Reason: ${contradiction.reason}")
                sb.appendLine()
            }
        }
        sb.appendLine()
        
        // Legal Evaluation
        sb.appendLine("LEGAL EVALUATION:")
        sb.appendLine("───────────────────────────────────────────────────────────")
        if (legalFindings.isEmpty()) {
            sb.appendLine("No specific legal issues detected.")
        } else {
            legalFindings.forEachIndexed { index, finding ->
                sb.appendLine("${index + 1}. $finding")
            }
        }
        sb.appendLine()
        
        sb.appendLine("═══════════════════════════════════════════════════════════")
        sb.appendLine("         END OF REPORT")
        sb.appendLine("═══════════════════════════════════════════════════════════")
        
        return sb.toString()
    }
}
