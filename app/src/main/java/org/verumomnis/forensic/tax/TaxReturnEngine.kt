package org.verumomnis.forensic.tax

import org.verumomnis.forensic.leveler.LevelerEngine.Jurisdiction
import java.util.Calendar

/**
 * Verum Omnis Tax Return Engine
 * 
 * Provides tax return preparation services at 50% cheaper than local accountants.
 * Supports individuals and companies across multiple jurisdictions.
 * 
 * Features:
 * - Multi-jurisdiction tax calculations (UAE, UK, EU, US)
 * - Individual and corporate tax returns
 * - Price comparison with local accountant rates
 * - Tax deadline tracking
 * - Deduction optimization
 * - Compliance checking
 * 
 * Pricing Model:
 * - 50% discount compared to average accountant rates in each geographical area
 * - Transparent fee structure
 * - No hidden charges
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
object TaxReturnEngine {

    // ============================================================
    // Data Classes
    // ============================================================

    data class TaxPayer(
        val id: String,
        val type: TaxPayerType,
        val name: String,
        val jurisdiction: Jurisdiction,
        val taxIdentificationNumber: String?,
        val fiscalYearEnd: Int = 12, // Month (1-12)
        val registrationDate: Long
    )

    enum class TaxPayerType {
        INDIVIDUAL,
        SOLE_PROPRIETOR,
        PARTNERSHIP,
        LIMITED_COMPANY,
        CORPORATION,
        NON_PROFIT
    }

    data class IncomeSource(
        val type: IncomeType,
        val amount: Double,
        val currency: String,
        val description: String,
        val withholdingTax: Double = 0.0,
        val documentIds: List<String> = emptyList()
    )

    enum class IncomeType {
        EMPLOYMENT,
        SELF_EMPLOYMENT,
        RENTAL,
        DIVIDENDS,
        INTEREST,
        CAPITAL_GAINS,
        BUSINESS_INCOME,
        PENSION,
        OTHER
    }

    data class Deduction(
        val type: DeductionType,
        val amount: Double,
        val description: String,
        val isVerified: Boolean = false,
        val documentIds: List<String> = emptyList()
    )

    enum class DeductionType {
        BUSINESS_EXPENSE,
        HOME_OFFICE,
        TRAVEL,
        PROFESSIONAL_FEES,
        INSURANCE,
        PENSION_CONTRIBUTION,
        CHARITABLE_DONATION,
        MORTGAGE_INTEREST,
        MEDICAL_EXPENSE,
        EDUCATION,
        DEPRECIATION,
        OTHER
    }

    data class TaxReturn(
        val id: String,
        val taxPayer: TaxPayer,
        val taxYear: Int,
        val incomeSources: List<IncomeSource>,
        val deductions: List<Deduction>,
        val totalIncome: Double,
        val totalDeductions: Double,
        val taxableIncome: Double,
        val taxLiability: Double,
        val taxPaid: Double,
        val taxDue: Double,
        val refundDue: Double,
        val effectiveTaxRate: Double,
        val filingDeadline: Long,
        val status: TaxReturnStatus,
        val createdAt: Long,
        val submittedAt: Long?
    )

    enum class TaxReturnStatus {
        DRAFT,
        IN_PROGRESS,
        READY_FOR_REVIEW,
        SUBMITTED,
        ACCEPTED,
        REJECTED,
        AMENDED
    }

    data class TaxCalculation(
        val grossIncome: Double,
        val allowableDeductions: Double,
        val taxableIncome: Double,
        val taxBrackets: List<TaxBracketApplication>,
        val totalTax: Double,
        val effectiveRate: Double,
        val marginalRate: Double
    )

    data class TaxBracketApplication(
        val bracketName: String,
        val lowerBound: Double,
        val upperBound: Double,
        val rate: Double,
        val incomeInBracket: Double,
        val taxInBracket: Double
    )

    data class AccountantPricing(
        val jurisdiction: Jurisdiction,
        val taxPayerType: TaxPayerType,
        val averageAccountantFee: Double,
        val verumOmnisFee: Double,
        val savings: Double,
        val savingsPercentage: Double,
        val currency: String,
        val lastUpdated: Long
    )

    data class TaxDeadline(
        val jurisdiction: Jurisdiction,
        val taxPayerType: TaxPayerType,
        val taxYear: Int,
        val filingDeadline: Long,
        val paymentDeadline: Long,
        val extensionAvailable: Boolean,
        val extendedDeadline: Long?,
        val penalties: List<PenaltyInfo>
    )

    data class PenaltyInfo(
        val type: String,
        val description: String,
        val amount: Double?,
        val percentage: Double?,
        val isPerDay: Boolean
    )

    data class TaxOptimization(
        val currentTax: Double,
        val optimizedTax: Double,
        val savings: Double,
        val recommendations: List<OptimizationRecommendation>
    )

    data class OptimizationRecommendation(
        val type: String,
        val description: String,
        val potentialSavings: Double,
        val riskLevel: RiskLevel,
        val implementationSteps: List<String>
    )

    enum class RiskLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    // ============================================================
    // Accountant Pricing Database (Average rates by jurisdiction)
    // ============================================================

    private val accountantRates = mapOf(
        // UAE Rates (in AED)
        Pair(Jurisdiction.UAE, TaxPayerType.INDIVIDUAL) to AccountantRate(1500.0, "AED"),
        Pair(Jurisdiction.UAE, TaxPayerType.SOLE_PROPRIETOR) to AccountantRate(3000.0, "AED"),
        Pair(Jurisdiction.UAE, TaxPayerType.LIMITED_COMPANY) to AccountantRate(8000.0, "AED"),
        Pair(Jurisdiction.UAE, TaxPayerType.CORPORATION) to AccountantRate(15000.0, "AED"),
        
        // UK Rates (in GBP)
        Pair(Jurisdiction.UK, TaxPayerType.INDIVIDUAL) to AccountantRate(250.0, "GBP"),
        Pair(Jurisdiction.UK, TaxPayerType.SOLE_PROPRIETOR) to AccountantRate(400.0, "GBP"),
        Pair(Jurisdiction.UK, TaxPayerType.LIMITED_COMPANY) to AccountantRate(1200.0, "GBP"),
        Pair(Jurisdiction.UK, TaxPayerType.CORPORATION) to AccountantRate(3500.0, "GBP"),
        
        // EU Rates (in EUR) - Average across EU
        Pair(Jurisdiction.EU, TaxPayerType.INDIVIDUAL) to AccountantRate(300.0, "EUR"),
        Pair(Jurisdiction.EU, TaxPayerType.SOLE_PROPRIETOR) to AccountantRate(500.0, "EUR"),
        Pair(Jurisdiction.EU, TaxPayerType.LIMITED_COMPANY) to AccountantRate(1500.0, "EUR"),
        Pair(Jurisdiction.EU, TaxPayerType.CORPORATION) to AccountantRate(4000.0, "EUR"),
        
        // US Rates (in USD)
        Pair(Jurisdiction.US, TaxPayerType.INDIVIDUAL) to AccountantRate(350.0, "USD"),
        Pair(Jurisdiction.US, TaxPayerType.SOLE_PROPRIETOR) to AccountantRate(600.0, "USD"),
        Pair(Jurisdiction.US, TaxPayerType.LIMITED_COMPANY) to AccountantRate(1800.0, "USD"),
        Pair(Jurisdiction.US, TaxPayerType.CORPORATION) to AccountantRate(5000.0, "USD")
    )

    private data class AccountantRate(val amount: Double, val currency: String)

    // ============================================================
    // Tax Brackets by Jurisdiction
    // ============================================================

    private val ukTaxBrackets = listOf(
        TaxBracket("Personal Allowance", 0.0, 12570.0, 0.0),
        TaxBracket("Basic Rate", 12571.0, 50270.0, 0.20),
        TaxBracket("Higher Rate", 50271.0, 125140.0, 0.40),
        TaxBracket("Additional Rate", 125141.0, Double.MAX_VALUE, 0.45)
    )

    private val usTaxBrackets2024 = listOf(
        TaxBracket("10%", 0.0, 11600.0, 0.10),
        TaxBracket("12%", 11601.0, 47150.0, 0.12),
        TaxBracket("22%", 47151.0, 100525.0, 0.22),
        TaxBracket("24%", 100526.0, 191950.0, 0.24),
        TaxBracket("32%", 191951.0, 243725.0, 0.32),
        TaxBracket("35%", 243726.0, 609350.0, 0.35),
        TaxBracket("37%", 609351.0, Double.MAX_VALUE, 0.37)
    )

    // UAE has no personal income tax (as of 2024)
    private val uaeTaxBrackets = listOf(
        TaxBracket("Tax Free", 0.0, Double.MAX_VALUE, 0.0)
    )

    // UAE Corporate Tax (introduced 2023)
    private val uaeCorporateTaxBrackets = listOf(
        TaxBracket("Small Business Relief", 0.0, 375000.0, 0.0),
        TaxBracket("Standard Rate", 375001.0, Double.MAX_VALUE, 0.09)
    )

    private data class TaxBracket(
        val name: String,
        val lowerBound: Double,
        val upperBound: Double,
        val rate: Double
    )

    // ============================================================
    // Core Functions
    // ============================================================

    /**
     * Get pricing comparison between Verum Omnis and local accountants
     * Verum Omnis offers 50% discount on average accountant rates
     */
    fun getPricing(
        jurisdiction: Jurisdiction,
        taxPayerType: TaxPayerType
    ): AccountantPricing {
        // Try to get exact rate, then fallback to individual rate for jurisdiction
        val rate = accountantRates[Pair(jurisdiction, taxPayerType)]
            ?: accountantRates[Pair(jurisdiction, TaxPayerType.INDIVIDUAL)]
            ?: getDefaultRateForJurisdiction(jurisdiction)
        
        val verumOmnisFee = rate.amount * 0.50 // 50% discount
        val savings = rate.amount - verumOmnisFee
        
        return AccountantPricing(
            jurisdiction = jurisdiction,
            taxPayerType = taxPayerType,
            averageAccountantFee = rate.amount,
            verumOmnisFee = verumOmnisFee,
            savings = savings,
            savingsPercentage = 50.0,
            currency = rate.currency,
            lastUpdated = System.currentTimeMillis()
        )
    }

    /**
     * Get default accountant rate for a jurisdiction when specific type not found
     */
    private fun getDefaultRateForJurisdiction(jurisdiction: Jurisdiction): AccountantRate {
        return when (jurisdiction) {
            Jurisdiction.UAE -> AccountantRate(1500.0, "AED")
            Jurisdiction.UK -> AccountantRate(250.0, "GBP")
            Jurisdiction.EU -> AccountantRate(300.0, "EUR")
            Jurisdiction.US -> AccountantRate(350.0, "USD")
            Jurisdiction.INTERNATIONAL -> AccountantRate(400.0, "USD")
        }
    }

    /**
     * Get all pricing for a jurisdiction
     */
    fun getAllPricingForJurisdiction(jurisdiction: Jurisdiction): List<AccountantPricing> {
        return TaxPayerType.values().map { type ->
            getPricing(jurisdiction, type)
        }
    }

    /**
     * Calculate tax based on jurisdiction and income
     */
    fun calculateTax(
        jurisdiction: Jurisdiction,
        taxPayerType: TaxPayerType,
        grossIncome: Double,
        deductions: Double = 0.0
    ): TaxCalculation {
        val taxableIncome = (grossIncome - deductions).coerceAtLeast(0.0)
        
        val brackets = when {
            jurisdiction == Jurisdiction.UAE && taxPayerType == TaxPayerType.INDIVIDUAL -> uaeTaxBrackets
            jurisdiction == Jurisdiction.UAE && (taxPayerType == TaxPayerType.CORPORATION || 
                taxPayerType == TaxPayerType.LIMITED_COMPANY) -> uaeCorporateTaxBrackets
            jurisdiction == Jurisdiction.UK -> ukTaxBrackets
            jurisdiction == Jurisdiction.US -> usTaxBrackets2024
            else -> ukTaxBrackets // Default to UK brackets for EU
        }
        
        val bracketApplications = mutableListOf<TaxBracketApplication>()
        var remainingIncome = taxableIncome
        var totalTax = 0.0
        var marginalRate = 0.0
        
        for (bracket in brackets) {
            if (remainingIncome <= 0) break
            
            val bracketSize = bracket.upperBound - bracket.lowerBound
            val incomeInBracket = minOf(remainingIncome, bracketSize)
            val taxInBracket = incomeInBracket * bracket.rate
            
            if (incomeInBracket > 0) {
                bracketApplications.add(TaxBracketApplication(
                    bracketName = bracket.name,
                    lowerBound = bracket.lowerBound,
                    upperBound = bracket.upperBound,
                    rate = bracket.rate,
                    incomeInBracket = incomeInBracket,
                    taxInBracket = taxInBracket
                ))
                marginalRate = bracket.rate
            }
            
            totalTax += taxInBracket
            remainingIncome -= incomeInBracket
        }
        
        val effectiveRate = if (taxableIncome > 0) totalTax / taxableIncome else 0.0
        
        return TaxCalculation(
            grossIncome = grossIncome,
            allowableDeductions = deductions,
            taxableIncome = taxableIncome,
            taxBrackets = bracketApplications,
            totalTax = totalTax,
            effectiveRate = effectiveRate,
            marginalRate = marginalRate
        )
    }

    /**
     * Prepare a tax return
     */
    fun prepareTaxReturn(
        taxPayer: TaxPayer,
        taxYear: Int,
        incomeSources: List<IncomeSource>,
        deductions: List<Deduction>
    ): TaxReturn {
        val totalIncome = incomeSources.sumOf { it.amount }
        val totalDeductions = deductions.sumOf { it.amount }
        val taxPaid = incomeSources.sumOf { it.withholdingTax }
        
        val taxCalc = calculateTax(
            jurisdiction = taxPayer.jurisdiction,
            taxPayerType = taxPayer.type,
            grossIncome = totalIncome,
            deductions = totalDeductions
        )
        
        val taxDue = (taxCalc.totalTax - taxPaid).coerceAtLeast(0.0)
        val refundDue = (taxPaid - taxCalc.totalTax).coerceAtLeast(0.0)
        
        val deadline = getFilingDeadline(taxPayer.jurisdiction, taxPayer.type, taxYear)
        
        return TaxReturn(
            id = "TAX-${taxPayer.id}-$taxYear-${System.currentTimeMillis()}",
            taxPayer = taxPayer,
            taxYear = taxYear,
            incomeSources = incomeSources,
            deductions = deductions,
            totalIncome = totalIncome,
            totalDeductions = totalDeductions,
            taxableIncome = taxCalc.taxableIncome,
            taxLiability = taxCalc.totalTax,
            taxPaid = taxPaid,
            taxDue = taxDue,
            refundDue = refundDue,
            effectiveTaxRate = taxCalc.effectiveRate,
            filingDeadline = deadline.filingDeadline,
            status = TaxReturnStatus.DRAFT,
            createdAt = System.currentTimeMillis(),
            submittedAt = null
        )
    }

    /**
     * Get filing deadline for a jurisdiction
     */
    fun getFilingDeadline(
        jurisdiction: Jurisdiction,
        taxPayerType: TaxPayerType,
        taxYear: Int
    ): TaxDeadline {
        val calendar = Calendar.getInstance()
        
        val (filingMonth, filingDay, paymentMonth, paymentDay) = when (jurisdiction) {
            Jurisdiction.UK -> {
                if (taxPayerType == TaxPayerType.LIMITED_COMPANY || 
                    taxPayerType == TaxPayerType.CORPORATION) {
                    // Companies House: 9 months after year end
                    Quadruple(12, 31, 12, 31) // Varies by company year end
                } else {
                    // Self Assessment: January 31st following tax year
                    Quadruple(1, 31, 1, 31)
                }
            }
            Jurisdiction.US -> {
                if (taxPayerType == TaxPayerType.CORPORATION) {
                    Quadruple(4, 15, 4, 15) // April 15th
                } else {
                    Quadruple(4, 15, 4, 15) // April 15th
                }
            }
            Jurisdiction.UAE -> {
                // UAE Corporate Tax: 9 months after financial year end
                Quadruple(9, 30, 9, 30)
            }
            Jurisdiction.EU -> {
                // Varies by country - using common deadline
                Quadruple(6, 30, 6, 30)
            }
            else -> Quadruple(4, 15, 4, 15)
        }
        
        calendar.set(taxYear + 1, filingMonth - 1, filingDay, 23, 59, 59)
        val filingDeadline = calendar.timeInMillis
        
        calendar.set(taxYear + 1, paymentMonth - 1, paymentDay, 23, 59, 59)
        val paymentDeadline = calendar.timeInMillis
        
        val penalties = when (jurisdiction) {
            Jurisdiction.UK -> listOf(
                PenaltyInfo("Initial Penalty", "£100 fixed penalty for late filing", 100.0, null, false),
                PenaltyInfo("Daily Penalty", "£10 per day after 3 months (max 90 days)", 10.0, null, true),
                PenaltyInfo("6 Month Penalty", "5% of tax due or £300 (whichever is greater)", null, 5.0, false),
                PenaltyInfo("12 Month Penalty", "Additional 5% of tax due or £300", null, 5.0, false)
            )
            Jurisdiction.US -> listOf(
                PenaltyInfo("Failure to File", "5% of unpaid taxes per month (max 25%)", null, 5.0, false),
                PenaltyInfo("Failure to Pay", "0.5% of unpaid taxes per month (max 25%)", null, 0.5, false),
                PenaltyInfo("Interest", "Federal short-term rate plus 3%", null, null, false)
            )
            Jurisdiction.UAE -> listOf(
                PenaltyInfo("Late Registration", "AED 10,000 for failure to register", 10000.0, null, false),
                PenaltyInfo("Late Filing", "AED 500-2,000 depending on delay", 500.0, null, false)
            )
            else -> emptyList()
        }
        
        return TaxDeadline(
            jurisdiction = jurisdiction,
            taxPayerType = taxPayerType,
            taxYear = taxYear,
            filingDeadline = filingDeadline,
            paymentDeadline = paymentDeadline,
            extensionAvailable = jurisdiction == Jurisdiction.US,
            extendedDeadline = if (jurisdiction == Jurisdiction.US) {
                calendar.set(taxYear + 1, 9, 15) // October 15th
                calendar.timeInMillis
            } else null,
            penalties = penalties
        )
    }

    /**
     * Get tax optimization recommendations
     */
    fun getOptimizationRecommendations(
        taxReturn: TaxReturn
    ): TaxOptimization {
        val recommendations = mutableListOf<OptimizationRecommendation>()
        var potentialSavings = 0.0
        
        // Check for pension contributions
        val hasPensionDeduction = taxReturn.deductions.any { it.type == DeductionType.PENSION_CONTRIBUTION }
        if (!hasPensionDeduction && taxReturn.totalIncome > 30000) {
            val pensionSavings = taxReturn.totalIncome * 0.05 * 0.20 // Assume 5% contribution at 20% rate
            potentialSavings += pensionSavings
            recommendations.add(OptimizationRecommendation(
                type = "Pension Contribution",
                description = "Consider contributing to a pension scheme to reduce taxable income",
                potentialSavings = pensionSavings,
                riskLevel = RiskLevel.LOW,
                implementationSteps = listOf(
                    "Open a workplace or personal pension",
                    "Contribute up to annual allowance",
                    "Claim tax relief on contributions"
                )
            ))
        }
        
        // Check for charitable donations
        val hasCharityDeduction = taxReturn.deductions.any { it.type == DeductionType.CHARITABLE_DONATION }
        if (!hasCharityDeduction && taxReturn.effectiveTaxRate > 0.20) {
            recommendations.add(OptimizationRecommendation(
                type = "Gift Aid",
                description = "Charitable donations can provide tax relief",
                potentialSavings = 0.0,
                riskLevel = RiskLevel.LOW,
                implementationSteps = listOf(
                    "Make donations to registered charities",
                    "Complete Gift Aid declaration",
                    "Claim higher rate relief on tax return"
                )
            ))
        }
        
        // Check for ISA/Tax-advantaged accounts (UK)
        if (taxReturn.taxPayer.jurisdiction == Jurisdiction.UK) {
            val interestIncome = taxReturn.incomeSources
                .filter { it.type == IncomeType.INTEREST || it.type == IncomeType.DIVIDENDS }
                .sumOf { it.amount }
            
            if (interestIncome > 1000) {
                val isaSavings = interestIncome * taxReturn.effectiveTaxRate
                potentialSavings += isaSavings
                recommendations.add(OptimizationRecommendation(
                    type = "ISA Investment",
                    description = "Move savings/investments to ISA for tax-free growth",
                    potentialSavings = isaSavings,
                    riskLevel = RiskLevel.LOW,
                    implementationSteps = listOf(
                        "Open a Stocks & Shares ISA or Cash ISA",
                        "Transfer up to £20,000 per year",
                        "All returns within ISA are tax-free"
                    )
                ))
            }
        }
        
        // Check for home office deduction
        val hasSelfEmployment = taxReturn.incomeSources.any { it.type == IncomeType.SELF_EMPLOYMENT }
        val hasHomeOffice = taxReturn.deductions.any { it.type == DeductionType.HOME_OFFICE }
        if (hasSelfEmployment && !hasHomeOffice) {
            val homeOfficeSavings = 312.0 * taxReturn.effectiveTaxRate // UK flat rate
            potentialSavings += homeOfficeSavings
            recommendations.add(OptimizationRecommendation(
                type = "Home Office Deduction",
                description = "Claim home office expenses for self-employment",
                potentialSavings = homeOfficeSavings,
                riskLevel = RiskLevel.LOW,
                implementationSteps = listOf(
                    "Calculate proportion of home used for business",
                    "Keep records of utility bills",
                    "Claim simplified expenses (£6/week) or actual costs"
                )
            ))
        }
        
        val optimizedTax = taxReturn.taxLiability - potentialSavings
        
        return TaxOptimization(
            currentTax = taxReturn.taxLiability,
            optimizedTax = optimizedTax.coerceAtLeast(0.0),
            savings = potentialSavings,
            recommendations = recommendations
        )
    }

    /**
     * Generate tax return summary
     */
    fun generateTaxReturnSummary(taxReturn: TaxReturn): String {
        val pricing = getPricing(taxReturn.taxPayer.jurisdiction, taxReturn.taxPayer.type)
        
        return buildString {
            appendLine("=" .repeat(60))
            appendLine("       VERUM OMNIS TAX RETURN SUMMARY")
            appendLine("=" .repeat(60))
            appendLine()
            appendLine("TAX PAYER INFORMATION")
            appendLine("-".repeat(60))
            appendLine("Name:              ${taxReturn.taxPayer.name}")
            appendLine("Type:              ${taxReturn.taxPayer.type}")
            appendLine("Jurisdiction:      ${taxReturn.taxPayer.jurisdiction}")
            appendLine("Tax Year:          ${taxReturn.taxYear}")
            appendLine("TIN:               ${taxReturn.taxPayer.taxIdentificationNumber ?: "Not provided"}")
            appendLine()
            appendLine("INCOME SUMMARY")
            appendLine("-".repeat(60))
            taxReturn.incomeSources.forEach { income ->
                appendLine("${income.type}: ${formatCurrency(income.amount, pricing.currency)}")
            }
            appendLine("-".repeat(60))
            appendLine("Total Income:      ${formatCurrency(taxReturn.totalIncome, pricing.currency)}")
            appendLine()
            appendLine("DEDUCTIONS")
            appendLine("-".repeat(60))
            if (taxReturn.deductions.isEmpty()) {
                appendLine("No deductions claimed")
            } else {
                taxReturn.deductions.forEach { deduction ->
                    appendLine("${deduction.type}: ${formatCurrency(deduction.amount, pricing.currency)}")
                }
            }
            appendLine("-".repeat(60))
            appendLine("Total Deductions:  ${formatCurrency(taxReturn.totalDeductions, pricing.currency)}")
            appendLine()
            appendLine("TAX CALCULATION")
            appendLine("-".repeat(60))
            appendLine("Taxable Income:    ${formatCurrency(taxReturn.taxableIncome, pricing.currency)}")
            appendLine("Tax Liability:     ${formatCurrency(taxReturn.taxLiability, pricing.currency)}")
            appendLine("Tax Already Paid:  ${formatCurrency(taxReturn.taxPaid, pricing.currency)}")
            appendLine("Effective Rate:    ${String.format("%.2f", taxReturn.effectiveTaxRate * 100)}%")
            appendLine()
            if (taxReturn.taxDue > 0) {
                appendLine("TAX DUE:           ${formatCurrency(taxReturn.taxDue, pricing.currency)}")
            } else if (taxReturn.refundDue > 0) {
                appendLine("REFUND DUE:        ${formatCurrency(taxReturn.refundDue, pricing.currency)}")
            } else {
                appendLine("BALANCE:           ${formatCurrency(0.0, pricing.currency)}")
            }
            appendLine()
            appendLine("VERUM OMNIS PRICING (50% CHEAPER)")
            appendLine("-".repeat(60))
            appendLine("Average Accountant: ${formatCurrency(pricing.averageAccountantFee, pricing.currency)}")
            appendLine("Verum Omnis Fee:    ${formatCurrency(pricing.verumOmnisFee, pricing.currency)}")
            appendLine("YOUR SAVINGS:       ${formatCurrency(pricing.savings, pricing.currency)} (${pricing.savingsPercentage.toInt()}%)")
            appendLine()
            appendLine("Filing Deadline:   ${java.util.Date(taxReturn.filingDeadline)}")
            appendLine("Status:            ${taxReturn.status}")
            appendLine()
            appendLine("=" .repeat(60))
            appendLine("          VERUM OMNIS TAX SERVICES")
            appendLine("         50% CHEAPER THAN ACCOUNTANTS")
            appendLine("=" .repeat(60))
        }
    }

    private fun formatCurrency(amount: Double, currency: String): String {
        val symbol = when (currency) {
            "GBP" -> "£"
            "EUR" -> "€"
            "AED" -> "AED "
            else -> "$"
        }
        return "$symbol${String.format("%,.2f", amount)}"
    }

    // Helper class for deadline calculation
    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
