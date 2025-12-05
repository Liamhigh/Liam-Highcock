package org.verumomnis.forensic.core

import android.content.Context
import org.verumomnis.forensic.leveler.LevelerEngine
import org.verumomnis.forensic.report.ForensicNarrativeGenerator

/**
 * Document Processor
 * 
 * Stateless processing of forensic evidence with Verum Omnis logic integration.
 * Processes documents and applies B1-B9 Leveler analysis.
 *
 * @author Liam Highcock
 * @version 1.0.0
 */
class DocumentProcessor(private val context: Context) {
    
    private val ruleEngine = RuleEngine(context)
    private val narrativeGenerator = ForensicNarrativeGenerator()
    
    /**
     * Process forensic case with Leveler analysis
     */
    fun processCase(
        forensicCase: ForensicCase,
        statements: List<LevelerEngine.Statement> = emptyList()
    ): ProcessedResult {
        
        // Load Verum Omnis rules from assets
        val rules = ruleEngine.loadVerumRules()
        
        // Extract evidence types for gap analysis
        val expectedEvidence = ruleEngine.getExpectedEvidenceTypes(rules)
        
        // Run Leveler analysis
        val levelerResult = LevelerEngine.analyzeEnhanced(
            statements = statements,
            evidence = forensicCase.evidence,
            expectedEvidence = expectedEvidence
        )
        
        // Generate enhanced narrative with Leveler insights
        val narrative = narrativeGenerator.generateNarrativeWithLeveler(
            forensicCase = forensicCase,
            levelerResult = levelerResult
        )
        
        return ProcessedResult(
            forensicCase = forensicCase,
            levelerResult = levelerResult,
            narrative = narrative,
            rules = rules
        )
    }
    
    /**
     * Result of document processing
     */
    data class ProcessedResult(
        val forensicCase: ForensicCase,
        val levelerResult: LevelerEngine.EnhancedLevelerResult,
        val narrative: String,
        val rules: VerumRules
    )
}
