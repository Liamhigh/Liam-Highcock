package org.verumomnis.forensic.core

import android.content.Context
import org.json.JSONObject
import org.verumomnis.forensic.leveler.LevelerEngine
import java.io.BufferedReader

/**
 * Rule Engine
 * 
 * Loads and applies Verum Omnis constitutional rules from JSON configuration files.
 * Implements the rule-based forensic analysis framework.
 *
 * @author Liam Highcock
 * @version 1.0.0
 */
class RuleEngine(private val context: Context) {
    
    /**
     * Load Verum Omnis rules from assets
     */
    fun loadVerumRules(): VerumRules {
        return try {
            val jsonString = context.assets.open("rules/verum_rules.json")
                .bufferedReader()
                .use(BufferedReader::readText)
            
            parseVerumRules(jsonString)
        } catch (e: Exception) {
            android.util.Log.e("RuleEngine", "Failed to load verum_rules.json", e)
            getDefaultVerumRules()
        }
    }
    
    /**
     * Load Leveler rules from assets
     */
    fun loadLevelerRules(): LevelerRules {
        return try {
            val jsonString = context.assets.open("rules/leveler_rules.json")
                .bufferedReader()
                .use(BufferedReader::readText)
            
            parseLevelerRules(jsonString)
        } catch (e: Exception) {
            android.util.Log.e("RuleEngine", "Failed to load leveler_rules.json", e)
            getDefaultLevelerRules()
        }
    }
    
    /**
     * Get expected evidence types from rules
     */
    fun getExpectedEvidenceTypes(rules: VerumRules): List<String> {
        return rules.expectedEvidence.map { it.type }
    }
    
    /**
     * Parse Verum rules JSON
     */
    private fun parseVerumRules(jsonString: String): VerumRules {
        val json = JSONObject(jsonString)
        
        // Parse legal subjects
        val legalSubjects = mutableListOf<LegalSubject>()
        val legalSubjectsArray = json.getJSONArray("legal_subjects")
        for (i in 0 until legalSubjectsArray.length()) {
            val subject = legalSubjectsArray.getJSONObject(i)
            val keywordsArray = subject.getJSONArray("keywords")
            val keywords = mutableListOf<String>()
            for (j in 0 until keywordsArray.length()) {
                keywords.add(keywordsArray.getString(j))
            }
            
            legalSubjects.add(LegalSubject(
                name = subject.getString("name"),
                keywords = keywords,
                severity = Severity.valueOf(subject.getString("severity"))
            ))
        }
        
        // Parse dishonesty matrix
        val dishonestyMatrix = json.getJSONObject("dishonesty_matrix")
        val contradictions = dishonestyMatrix.getJSONObject("contradictions")
        val contradictionPatterns = mutableListOf<String>()
        val contradictionPatternsArray = contradictions.getJSONArray("patterns")
        for (i in 0 until contradictionPatternsArray.length()) {
            contradictionPatterns.add(contradictionPatternsArray.getString(i))
        }
        
        val omissions = dishonestyMatrix.getJSONObject("omissions")
        val omissionPatterns = mutableListOf<String>()
        val omissionPatternsArray = omissions.getJSONArray("patterns")
        for (i in 0 until omissionPatternsArray.length()) {
            omissionPatterns.add(omissionPatternsArray.getString(i))
        }
        
        // Parse behavioral patterns
        val behavioralPatterns = json.getJSONObject("behavioral_patterns")
        val behavioralRules = mutableListOf<BehavioralRule>()
        
        for (key in behavioralPatterns.keys()) {
            val pattern = behavioralPatterns.getJSONObject(key)
            val indicatorsArray = pattern.getJSONArray("indicators")
            val indicators = mutableListOf<String>()
            for (i in 0 until indicatorsArray.length()) {
                indicators.add(indicatorsArray.getString(i))
            }
            
            behavioralRules.add(BehavioralRule(
                type = key,
                indicators = indicators,
                weight = pattern.getInt("weight")
            ))
        }
        
        // Parse extraction protocol
        val extractionProtocol = json.getJSONObject("extraction_protocol")
        val step1Keywords = mutableListOf<String>()
        val step1Array = extractionProtocol.getJSONArray("step1_keywords")
        for (i in 0 until step1Array.length()) {
            step1Keywords.add(step1Array.getString(i))
        }
        
        val step2Tags = mutableListOf<String>()
        val step2Array = extractionProtocol.getJSONArray("step2_tags")
        for (i in 0 until step2Array.length()) {
            step2Tags.add(step2Array.getString(i))
        }
        
        return VerumRules(
            version = json.getString("version"),
            legalSubjects = legalSubjects,
            contradictionPatterns = contradictionPatterns,
            omissionPatterns = omissionPatterns,
            behavioralRules = behavioralRules,
            extractionKeywords = step1Keywords,
            extractionTags = step2Tags,
            expectedEvidence = getDefaultExpectedEvidence()
        )
    }
    
    /**
     * Parse Leveler rules JSON
     */
    private fun parseLevelerRules(jsonString: String): LevelerRules {
        val json = JSONObject(jsonString)
        
        return LevelerRules(
            version = json.getString("version"),
            b1Enabled = json.getJSONObject("b1_chronology").getBoolean("enabled"),
            b2Enabled = json.getJSONObject("b2_contradictions").getBoolean("enabled"),
            b3Enabled = json.getJSONObject("b3_evidence_gaps").getBoolean("enabled"),
            b4Enabled = json.getJSONObject("b4_timeline_manipulation").getBoolean("enabled"),
            b5Enabled = json.getJSONObject("b5_behavioral_patterns").getBoolean("enabled"),
            b6Enabled = json.getJSONObject("b6_financial_correlation").getBoolean("enabled"),
            b7Enabled = json.getJSONObject("b7_communication_patterns").getBoolean("enabled"),
            b8Enabled = json.getJSONObject("b8_jurisdictional_compliance").getBoolean("enabled"),
            b9Enabled = json.getJSONObject("b9_integrity_scoring").getBoolean("enabled"),
            gapThresholdHours = json.getJSONObject("b1_chronology").getLong("gap_threshold_hours")
        )
    }
    
    /**
     * Get default Verum rules fallback
     */
    private fun getDefaultVerumRules(): VerumRules {
        return VerumRules(
            version = "5.1.1",
            legalSubjects = listOf(
                LegalSubject("Shareholder Oppression", listOf("denied meeting", "withheld financial"), Severity.HIGH),
                LegalSubject("Cybercrime", listOf("unauthorized access", "hacking"), Severity.CRITICAL),
                LegalSubject("Fraud", listOf("forged", "false representation"), Severity.CRITICAL)
            ),
            contradictionPatterns = listOf("no deal.*invoice", "denied.*admitted", "refused.*accepted"),
            omissionPatterns = listOf("selective.*edit", "missing.*context", "cropped.*screenshot"),
            behavioralRules = listOf(
                BehavioralRule("evasion", listOf("refuse", "ignore", "deflect"), 2),
                BehavioralRule("gaslighting", listOf("never happened", "imagining"), 3),
                BehavioralRule("concealment", listOf("delete", "erase", "lost"), 3)
            ),
            extractionKeywords = listOf("admin", "deny", "forged", "access", "delete"),
            extractionTags = listOf("#Cybercrime", "#Fraud", "#Oppression"),
            expectedEvidence = getDefaultExpectedEvidence()
        )
    }
    
    /**
     * Get default Leveler rules fallback
     */
    private fun getDefaultLevelerRules(): LevelerRules {
        return LevelerRules(
            version = "1.0",
            b1Enabled = true,
            b2Enabled = true,
            b3Enabled = true,
            b4Enabled = true,
            b5Enabled = true,
            b6Enabled = true,
            b7Enabled = true,
            b8Enabled = true,
            b9Enabled = true,
            gapThresholdHours = 48
        )
    }
    
    /**
     * Get default expected evidence types
     */
    private fun getDefaultExpectedEvidence(): List<ExpectedEvidence> {
        return listOf(
            ExpectedEvidence("contract", Severity.CRITICAL),
            ExpectedEvidence("invoice", Severity.HIGH),
            ExpectedEvidence("bank_statement", Severity.HIGH),
            ExpectedEvidence("email", Severity.MEDIUM),
            ExpectedEvidence("meeting_minutes", Severity.MEDIUM)
        )
    }
}

/**
 * Verum Omnis Rules data class
 */
data class VerumRules(
    val version: String,
    val legalSubjects: List<LegalSubject>,
    val contradictionPatterns: List<String>,
    val omissionPatterns: List<String>,
    val behavioralRules: List<BehavioralRule>,
    val extractionKeywords: List<String>,
    val extractionTags: List<String>,
    val expectedEvidence: List<ExpectedEvidence>
)

/**
 * Legal subject definition
 */
data class LegalSubject(
    val name: String,
    val keywords: List<String>,
    val severity: Severity
)

/**
 * Behavioral analysis rule
 */
data class BehavioralRule(
    val type: String,
    val indicators: List<String>,
    val weight: Int
)

/**
 * Expected evidence item
 */
data class ExpectedEvidence(
    val type: String,
    val criticality: Severity
)

/**
 * Leveler configuration rules
 */
data class LevelerRules(
    val version: String,
    val b1Enabled: Boolean,
    val b2Enabled: Boolean,
    val b3Enabled: Boolean,
    val b4Enabled: Boolean,
    val b5Enabled: Boolean,
    val b6Enabled: Boolean,
    val b7Enabled: Boolean,
    val b8Enabled: Boolean,
    val b9Enabled: Boolean,
    val gapThresholdHours: Long
)
