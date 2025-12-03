package org.verumomnis.forensic.core

import android.app.Application

/**
 * Verum Omnis Forensic Application
 * 
 * Operating under the Verum Omnis Constitution Mode:
 * - Truth: Factual accuracy and verifiable evidence
 * - Fairness: Protection of vulnerable parties
 * - Human Rights: Dignity, equality, and agency
 * - Non-Extraction: No sensitive data transmission
 * - Human Authority: AI assists, never overrides
 * - Integrity: No manipulation or bias
 * - Independence: No external influence on outputs
 * 
 * Security Features:
 * - Offline First: True
 * - Stateless: True
 * - No Cloud Logging: True
 * - No Telemetry: True
 * - Airgap Ready: True
 */
class VerumOmnisApplication : Application() {

    lateinit var forensicEngine: ForensicEngine
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        initializeForensicEngine()
    }

    private fun initializeForensicEngine() {
        forensicEngine = ForensicEngine(applicationContext)
    }

    companion object {
        private lateinit var instance: VerumOmnisApplication

        fun getInstance(): VerumOmnisApplication = instance

        // Constitution Mode Constants
        const val HASH_STANDARD = "SHA-512"
        const val PDF_STANDARD = "PDF 1.7"
        const val WATERMARK = "VERUM OMNIS"
        const val QR_CODE_ENABLED = true
        const val TAMPER_DETECTION_MANDATORY = true
        const val ADMISSIBILITY_STANDARD = "Legal-grade"
    }
}
