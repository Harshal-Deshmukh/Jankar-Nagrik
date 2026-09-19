package com.ashstudios.JankarNagrik

import com.ashstudios.JankarNagrik.data.Scheme
import com.ashstudios.JankarNagrik.data.SchemesRepository
import org.junit.Assert.*
import org.junit.Test

class SchemeMatchingTest {

    private val schemes = SchemesRepository.VERIFIED_DEFAULT_SCHEMES

    // Core rule matching logic extracted exactly as used in RecommenderScreen
    private fun evaluateScheme(projectType: String, cost: Double, income: Double): Pair<Boolean, String> {
        val microFinance = schemes.find { it.scheme_id == "micro_finance" }
        val termLoan = schemes.find { it.scheme_id == "term_loan" }
        val educationalLoan = schemes.find { it.scheme_id == "educational_loan" }

        val incomeLimit = microFinance?.income_limit ?: 500000.0

        return if (income > incomeLimit) {
            Pair(false, "Not eligible for concessional schemes (Annual family income exceeds ₹5,00,000)")
        } else if (projectType.contains("Education", ignoreCase = true)) {
            val maxEdu = educationalLoan?.max_amount ?: 3000000.0
            if (cost <= maxEdu) {
                Pair(true, educationalLoan?.name_en ?: "Educational Loan Scheme")
            } else {
                Pair(false, "Course fee exceeds maximum educational loan ceiling of ₹30,00,000")
            }
        } else {
            val mfMax = microFinance?.max_amount ?: 140000.0
            val tlMin = termLoan?.min_amount ?: 140001.0
            val tlMax = termLoan?.max_amount ?: 5000000.0

            when {
                cost in 0.0..mfMax -> Pair(true, microFinance?.name_en ?: "Micro Finance Scheme")
                cost in tlMin..tlMax -> Pair(true, termLoan?.name_en ?: "Term Loan Scheme")
                cost > tlMax -> Pair(false, "Project cost exceeds maximum concessional scheme ceiling of ₹50,00,000")
                else -> Pair(false, "Please enter a valid amount")
            }
        }
    }

    @Test
    fun testCase1_microFinanceMatch() {
        println("=== TEST CASE 1: Micro Finance Match ===")
        val projectType = "Retail Grocery Store"
        val cost = 100000.0 // ₹1,00,000 (within 0 - 1,40,000)
        val income = 250000.0 // ₹2,50,000 (<= 5,00,000)

        val (isEligible, schemeName) = evaluateScheme(projectType, cost, income)
        println("Input: Project='$projectType', Cost=₹$cost, Income=₹$income")
        println("Output: Eligible=$isEligible, Scheme='$schemeName'")

        assertTrue(isEligible)
        assertTrue(schemeName.startsWith("Micro Finance Scheme"))
    }

    @Test
    fun testCase2_educationalLoanMatch() {
        println("=== TEST CASE 2: Educational Loan Match ===")
        val projectType = "Higher Education - B.Tech Computer Science"
        val cost = 1200000.0 // ₹12,00,000 (within course fee limit <= 30,00,000)
        val income = 350000.0 // ₹3,50,000 (<= 5,00,000)

        val (isEligible, schemeName) = evaluateScheme(projectType, cost, income)
        println("Input: Project='$projectType', Cost=₹$cost, Income=₹$income")
        println("Output: Eligible=$isEligible, Scheme='$schemeName'")

        assertTrue(isEligible)
        assertTrue(schemeName.startsWith("Educational Loan Scheme"))
    }

    @Test
    fun testCase3_incomeIneligible() {
        println("=== TEST CASE 3: Income Ineligible ===")
        val projectType = "Dairy Farming"
        val cost = 80000.0 // Cost qualifies for Micro Finance
        val income = 650000.0 // Income ₹6,50,000 exceeds ₹5,00,000 ceiling

        val (isEligible, resultMessage) = evaluateScheme(projectType, cost, income)
        println("Input: Project='$projectType', Cost=₹$cost, Income=₹$income")
        println("Output: Eligible=$isEligible, Result='$resultMessage'")

        assertFalse(isEligible)
        assertTrue(resultMessage.contains("exceeds ₹5,00,000"))
    }

    @Test
    fun testCase4_termLoanMatch() {
        println("=== TEST CASE 4: Term Loan Match ===")
        val projectType = "Small Manufacturing Unit"
        val cost = 1500000.0 // ₹15,00,000 (between 1,40,001 and 50,00,000)
        val income = 400000.0

        val (isEligible, schemeName) = evaluateScheme(projectType, cost, income)
        println("Input: Project='$projectType', Cost=₹$cost, Income=₹$income")
        println("Output: Eligible=$isEligible, Scheme='$schemeName'")

        assertTrue(isEligible)
        assertEquals("Term Loan Scheme", schemeName)
    }
}
