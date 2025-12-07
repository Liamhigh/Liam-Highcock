package org.verumomnis.forensic.engine

import org.verumomnis.forensic.model.Contradiction

/**
 * Contradiction Engine
 * Detects contradictions between statements using pattern matching
 */
class ContradictionEngine {
    
    /**
     * Detect contradictions in a list of statements
     */
    fun detectContradictions(statements: List<String>): List<Contradiction> {
        val results = mutableListOf<Contradiction>()
        
        for (i in statements.indices) {
            for (j in i + 1 until statements.size) {
                val a = statements[i]
                val b = statements[j]
                
                if (isContradiction(a, b)) {
                    results.add(Contradiction(a, b, "Direct contradiction detected"))
                }
            }
        }
        
        return results
    }
    
    /**
     * Check if two statements contradict each other
     */
    private fun isContradiction(a: String, b: String): Boolean {
        val aLower = a.lowercase()
        val bLower = b.lowercase()
        
        // Pattern 1: "did" vs "did not"
        if (aLower.contains("did") && bLower.contains("did not")) return true
        if (bLower.contains("did") && aLower.contains("did not")) return true
        
        // Pattern 2: "never" vs "did"
        if (aLower.contains("never") && bLower.contains("did")) return true
        if (bLower.contains("never") && aLower.contains("did")) return true
        
        // Pattern 3: "always" vs "never"
        if (aLower.contains("always") && bLower.contains("never")) return true
        if (bLower.contains("always") && aLower.contains("never")) return true
        
        // Pattern 4: "was" vs "was not"
        if (aLower.contains("was") && bLower.contains("was not")) return true
        if (bLower.contains("was") && aLower.contains("was not")) return true
        
        return false
    }
}
