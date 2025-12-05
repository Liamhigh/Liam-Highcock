package org.verumomnis.forensic.core

/**
 * Forensic Analysis Constants
 * 
 * Shared constants for integrity scoring and analysis thresholds
 * used across the Verum Omnis Forensic Engine.
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
object ForensicConstants {
    
    /**
     * Integrity Score Thresholds
     */
    object IntegrityScore {
        const val EXCELLENT_MIN = 90f
        const val GOOD_MIN = 70f
        const val FAIR_MIN = 50f
        const val POOR_MIN = 25f
        const val COMPROMISED_MIN = 0f
        
        const val MAXIMUM = 100f
        const val MINIMUM = 0f
        
        /**
         * Get integrity category for a score
         */
        fun getCategory(score: Float): String {
            return when {
                score >= EXCELLENT_MIN -> "EXCELLENT"
                score >= GOOD_MIN -> "GOOD"
                score >= FAIR_MIN -> "FAIR"
                score >= POOR_MIN -> "POOR"
                else -> "COMPROMISED"
            }
        }
        
        /**
         * Check if score is in acceptable range
         */
        fun isAcceptable(score: Float): Boolean {
            return score >= FAIR_MIN
        }
    }
    
    /**
     * Severity Weight Constants
     */
    object SeverityWeight {
        const val CRITICAL = 15
        const val HIGH = 10
        const val MEDIUM = 5
        const val LOW = 2
    }
    
    /**
     * Timeline Analysis Constants
     */
    object Timeline {
        const val GAP_THRESHOLD_HOURS = 48L
        const val MODIFICATION_THRESHOLD_HOURS = 24L
        const val SUSPICIOUS_GAP_HOURS = 72L
    }
    
    /**
     * Financial Analysis Constants
     */
    object Financial {
        const val AMOUNT_TOLERANCE_PERCENTAGE = 5.0
        const val DATE_TOLERANCE_DAYS = 7
    }
    
    /**
     * Communication Analysis Constants
     */
    object Communication {
        const val RESPONSE_TIME_ANOMALY_HOURS = 48L
        const val DELETION_RATE_THRESHOLD = 0.1f
        const val UNUSUAL_HOUR_START = 22
        const val UNUSUAL_HOUR_END = 6
    }
}
