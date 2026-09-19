package com.ashstudios.JankarNagrik.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object SchemesRepository {
    private const val TAG = "SchemesRepository"
    private const val COLLECTION_NAME = "schemes"

    /**
     * VERIFIED NSFDC SCHEMES CATALOG
     * 
     * Sources:
     * 1. Official live schemes portal: https://nsfdc.nic.in/scheme (Items #1, #2, #3, #4, #5)
     * 2. Official audited NSFDC Annual Report 2023-24 (pp. 180, 188-190)
     * 
     * Specific audit notes:
     * - micro_finance: Figures kept as-is (project cost up to ₹1.40L, max loan ₹1.25L, 6.5% interest, 3-mo moratorium, 3-yr repayment).
     * - term_loan: Updated to match live nsfdc.nic.in/scheme (Item #2) — 8.0% flat interest to beneficiary via SCAs (NSFDC charges 4%),
     *   repayment within 7 years, moratorium 6 months (12 months for plantation/construction), project cost >₹1.40L up to ₹50L, max loan ₹45L.
     *   (Previous note cited generic 6.5%-15% range; now pinned to official 8.0% SCA benchmark).
     * - educational_loan: Updated to match live nsfdc.nic.in/scheme (Item #5) — loan ceiling up to ₹40.00 Lakh (updated from previous ₹30L),
     *   official published interest rate of 6.5% p.a. to beneficiary (NSFDC charges 2.5%), with 0.5% interest rebate for women (effective 6.0%),
     *   repayment up to 10-12 years, moratorium course period + 1 year (or 6 months where repayment started).
     * - udyam_nidhi: Added from nsfdc.nic.in/scheme (Item #4) — project cost up to ₹5.00 Lakh (loan up to ₹4.50L), interest 13% via Cooperative Banks
     *   and 15% via Small Finance Banks (NSFDC charges 5%), 5-year repayment, 3-month moratorium.
     * - aajeevika_microfinance: Added from nsfdc.nic.in/scheme (Item #3) — project cost up to ₹1.40 Lakh (loan up to ₹1.25L), interest 15% via NBFC-MFIs
     *   (NSFDC charges 5%), 3-year repayment, 3-month moratorium.
     * - mahila_samriddhi: Added from NSFDC Annual Report 2023-24 (pp. 180 & 188) — ₹36.84 Crore disbursed to 23,185 SC women beneficiaries in FY 2023-24.
     *   4.0% p.a. concessional interest rate for women (NSFDC charges 1%-2% + max 4% SCA margin). Exact loan ceiling (₹1.40L) and moratorium (3 months)
     *   are inferred from general NSFDC Micro Credit norms per RBI notification, not separately stated in guidelines.
     */
    val VERIFIED_DEFAULT_SCHEMES = listOf(
        Scheme(
            scheme_id = "micro_finance",
            name_en = "Micro Finance Scheme (MFS)",
            name_hi = "लघु वित्त योजना",
            min_amount = 0.0,
            max_amount = 140000.0, // Max project cost ₹1,40,000; Max loan (90%): ₹1,25,000
            interest_rate_min = 6.5,
            interest_rate_max = 6.5,
            interest_rate_note = "6.5% per annum to beneficiary (NSFDC charges 2.5% to SCAs/CAs)",
            moratorium_months = listOf(3),
            income_limit = 500000.0,
            purpose = "small business / income-generation projects up to ₹1,40,000",
            eligibility_criteria = listOf(
                "Scheduled Caste (SC) category beneficiary",
                "Annual family income up to ₹5,00,000",
                "Project cost up to ₹1,40,000 (loan amount up to 90%, max ₹1,25,000)",
                "Interest rate 6.5% per annum",
                "Moratorium period of 3 months",
                "Repayment period up to 3 years in quarterly installments"
            ),
            source_note = "Official NSFDC scheme published on nsfdc.nic.in/scheme (Item #1)",
            channel_partner_type = "SCA / PSB / RRB",
            target_group = "all",
            repayment_period = "Up to 3 years (quarterly installments)"
        ),
        Scheme(
            scheme_id = "term_loan",
            name_en = "Term Loan Scheme",
            name_hi = "सावधि ऋण योजना",
            min_amount = 140001.0,
            max_amount = 5000000.0, // Project cost > ₹1,40,000 up to ₹50,00,000 per nsfdc.nic.in
            interest_rate_min = 8.0,
            interest_rate_max = 8.0,
            interest_rate_note = "8.0% p.a. flat to beneficiary via SCAs/CAs (NSFDC charges 4%). NBFC-MFIs on-lend at up to 15%",
            moratorium_months = listOf(6, 12),
            income_limit = 500000.0,
            purpose = "medium and larger commercial, industrial, service, or transport projects (up to ₹50,00,000)",
            eligibility_criteria = listOf(
                "Scheduled Caste (SC) category beneficiary",
                "Annual family income up to ₹5,00,000",
                "Project cost > ₹1,40,000 up to ₹50,00,000 (loan amount up to 90%, max ₹45,00,000)",
                "Interest rate 8.0% per annum via SCAs/CAs",
                "Moratorium period: 6 months (12 months for plantation and construction activities)",
                "Repayment period: within 7 years in quarterly installments"
            ),
            source_note = "Official NSFDC scheme published on nsfdc.nic.in/scheme (Item #2)",
            channel_partner_type = "SCA / CA / PSB / RRB",
            target_group = "all",
            repayment_period = "Within 7 years (quarterly installments)"
        ),
        Scheme(
            scheme_id = "educational_loan",
            name_en = "Educational Loan Scheme (ELS)",
            name_hi = "शिक्षा ऋण योजना",
            min_amount = 0.0,
            max_amount = 4000000.0, // Updated to ₹40.00 Lakh per official nsfdc.nic.in/scheme guidelines
            interest_rate_min = 6.5,
            interest_rate_max = 6.5,
            interest_rate_note = "6.5% p.a. to beneficiary (NSFDC charges 2.5% to CAs). 0.5% rebate for women (effective 6.0%)",
            moratorium_months = listOf(6, 12),
            income_limit = 500000.0,
            purpose = "regular full-time professional/technical courses approved by Government in India or abroad (Engineering, Medical, Management, Law, CA, etc.)",
            eligibility_criteria = listOf(
                "Scheduled Caste (SC) category student/beneficiary",
                "Annual family income up to ₹5,00,000",
                "Regular full-time professional/technical courses in India or abroad",
                "Max loan: up to 90% of course fee or ₹40,00,000, whichever is less",
                "0.5% interest rebate for women beneficiaries (effective 6.0% p.a.)",
                "Moratorium: course period plus 1 year (where repayment not started) / up to 6 months (where repayment started)",
                "Repayment period: up to 12 years (where repayment not started) / up to 10 years (where repayment started)"
            ),
            source_note = "Official NSFDC scheme published on nsfdc.nic.in/scheme (Item #5)",
            channel_partner_type = "SCA / PSB / RRB",
            target_group = "students",
            special_rebate = "0.5% interest rebate for women beneficiaries",
            repayment_period = "Up to 10–12 years"
        ),
        Scheme(
            scheme_id = "udyam_nidhi",
            name_en = "Udyam Nidhi Yojana (UNY)",
            name_hi = "उद्यम निधि योजना",
            min_amount = 10000.0,
            max_amount = 500000.0, // Projects/units costing up to ₹5.00 Lakh
            interest_rate_min = 13.0,
            interest_rate_max = 15.0,
            interest_rate_note = "13.0% p.a. via Cooperative Banks/Societies; 15.0% p.a. via Small Finance Banks (NSFDC charges 5%)",
            moratorium_months = listOf(3),
            income_limit = 500000.0,
            purpose = "small or micro activities / enterprise setup costing up to ₹5.00 Lakh channeled through Cooperative Banks, Societies, and Small Finance Banks",
            eligibility_criteria = listOf(
                "Scheduled Caste (SC) category beneficiary",
                "Annual family income up to ₹5,00,000",
                "Project cost up to ₹5,00,000 (loan amount up to 90%, max ₹4,50,000)",
                "Channeled through Cooperative Societies, Cooperative Banks, or Small Finance Banks (SFBs)",
                "Interest rate: 13.0% (Cooperative Banks/Societies) or 15.0% (Small Finance Banks)",
                "Moratorium period: 3 months",
                "Repayment period: up to 5 years in quarterly or half-yearly installments"
            ),
            source_note = "Official NSFDC scheme published on nsfdc.nic.in/scheme (Item #4)",
            channel_partner_type = "Cooperative Bank / Cooperative Society / Small Finance Bank (SFB)",
            target_group = "all",
            repayment_period = "Up to 5 years (quarterly or half-yearly installments)"
        ),
        Scheme(
            scheme_id = "aajeevika_microfinance",
            name_en = "Aajeevika Micro-Finance Yojana",
            name_hi = "आजीविका सूक्ष्म वित्त योजना",
            min_amount = 0.0,
            max_amount = 140000.0, // Projects costing up to ₹1.40 Lakh
            interest_rate_min = 15.0,
            interest_rate_max = 15.0,
            interest_rate_note = "15.0% p.a. to beneficiary charged by NBFC-MFIs (NSFDC charges 5% to NBFC-MFIs)",
            moratorium_months = listOf(3),
            income_limit = 500000.0,
            purpose = "prompt and need-based micro finance for small/micro business activities channeled through selected NBFC-MFIs",
            eligibility_criteria = listOf(
                "Scheduled Caste (SC) category beneficiary",
                "Annual family income up to ₹5,00,000",
                "Project cost up to ₹1,40,000 (loan amount up to 90%, max ₹1,25,000)",
                "Channeled exclusively through selected RBI-registered NBFC-MFIs",
                "Interest rate: 15.0% per annum to beneficiaries",
                "Moratorium period: 3 months",
                "Repayment period: up to 3 years in quarterly installments"
            ),
            source_note = "Official NSFDC scheme published on nsfdc.nic.in/scheme (Item #3)",
            channel_partner_type = "NBFC-MFI",
            target_group = "all",
            repayment_period = "Up to 3 years (quarterly installments)"
        ),
        Scheme(
            scheme_id = "mahila_samriddhi",
            name_en = "Mahila Samriddhi Yojana (MSY)",
            name_hi = "महिला समृद्धि योजना",
            min_amount = 0.0,
            max_amount = 140000.0, // Micro Credit norm
            interest_rate_min = 4.0,
            interest_rate_max = 4.0,
            interest_rate_note = "4.0% p.a. highly subsidized rate for women beneficiaries (NSFDC charges 1%-2% to SCA, SCA margin capped at 4% per Annual Report p. 180)",
            moratorium_months = listOf(3),
            income_limit = 500000.0,
            purpose = "exclusive micro-credit finance for Scheduled Caste women entrepreneurs and women SHGs for tiny income-generating activities",
            eligibility_criteria = listOf(
                "Scheduled Caste (SC) female beneficiary or all-women Self-Help Group (SHG)",
                "Annual family income up to ₹5,00,000",
                "Project cost up to ₹1,40,000 (loan amount up to 90%, max ₹1,25,000; up to ₹15L for SHGs)",
                "Concessional interest rate of 4.0% per annum (2.5% lower than general Micro Finance)",
                "Moratorium period: 3 months",
                "Repayment period: up to 3 years in quarterly installments"
            ),
            source_note = "NSFDC Annual Report 2023-24 pp. 180 & 188 (₹36.84 Cr disbursed to 23,185 women). Exact loan ceiling (₹1.40L) & moratorium (3 mo) inferred from general Micro Credit norms per RBI notification, not separately stated.",
            channel_partner_type = "SCA / CA",
            target_group = "women",
            special_rebate = "Subsidized 4.0% interest rate (2.5% discount vs 6.5% general microfinance)",
            repayment_period = "Up to 3 years (quarterly installments)"
        ),
        Scheme(
            scheme_id = "green_business",
            name_en = "Green Business Scheme (GBS)",
            name_hi = "हरित व्यवसाय योजना",
            min_amount = 50000.0,
            max_amount = 3000000.0, // Up to ₹30 Lakhs per official NSFDC guidelines
            interest_rate_min = 8.0,
            interest_rate_max = 8.0,
            interest_rate_note = "8.0% p.a. flat to beneficiary via SCAs/CAs (NSFDC charges 4% to SCAs)",
            moratorium_months = listOf(6, 12),
            income_limit = 500000.0,
            purpose = "climate-friendly & clean technology activities such as E-rickshaws, battery passenger/load vehicles, solar panels, bio-gas, poly houses, and waste recycling",
            eligibility_criteria = listOf(
                "Scheduled Caste (SC) category beneficiary",
                "Annual family income up to ₹5,00,000",
                "Eco-friendly or green energy transport/enterprise project up to ₹30,00,000",
                "Interest rate 8.0% per annum",
                "Moratorium period: 6 to 12 months",
                "Repayment period up to 7 years in quarterly installments"
            ),
            source_note = "Official NSFDC Green Business Scheme guidelines (nsfdc.nic.in)",
            channel_partner_type = "SCA / CA / PSB",
            target_group = "all",
            special_rebate = "Concessional credit for climate-friendly and green micro-enterprises",
            repayment_period = "Up to 7 years (quarterly installments)"
        ),
        Scheme(
            scheme_id = "pm_vishwakarma",
            name_en = "PM Vishwakarma Scheme",
            name_hi = "पीएम विश्वकर्मा योजना",
            min_amount = 10000.0,
            max_amount = 300000.0, // ₹1 Lakh Tranche 1 + ₹2 Lakh Tranche 2
            interest_rate_min = 5.0,
            interest_rate_max = 5.0,
            interest_rate_note = "Subsidized 5.0% p.a. flat (GoI provides 8% interest subvention to lending banks)",
            moratorium_months = listOf(3, 6),
            income_limit = 1000000.0, // No strict ₹5L cap; targeted by trade
            purpose = "end-to-end support for traditional SC artisans & craftspeople (carpenters, cobblers, tailors, blacksmiths, potters, weavers, barbers, etc.) working with hands & tools",
            eligibility_criteria = listOf(
                "Artisan or craftsperson working with hands and tools in 18 notified family-based traditional trades",
                "Age 18+ years on registration date",
                "Collateral-free enterprise credit up to ₹3,00,000 in two tranches (Tranche 1: ₹1L @ 18 mo; Tranche 2: ₹2L @ 30 mo)",
                "Fixed 5.0% concessional interest rate",
                "₹15,000 e-voucher for modern toolkits + free skill upgradation training (with ₹500/day stipend)",
                "Digital transaction incentives (₹1 per transaction up to 100/mo)"
            ),
            source_note = "Ministry of MSME & MoSJE official PM Vishwakarma framework (pmvishwakarma.gov.in)",
            channel_partner_type = "Scheduled Commercial Bank / RRB / CSC",
            target_group = "artisans",
            special_rebate = "5% subsidized interest rate + ₹15,000 free toolkit grant",
            repayment_period = "18 months (Tranche 1) to 30 months (Tranche 2)"
        ),
        Scheme(
            scheme_id = "stand_up_india",
            name_en = "Stand-Up India Scheme (SC & Women)",
            name_hi = "स्टैंड-अप इंडिया योजना",
            min_amount = 1000000.0, // ₹10 Lakhs
            max_amount = 10000000.0, // ₹1.00 Crore
            interest_rate_min = 7.5,
            interest_rate_max = 9.5,
            interest_rate_note = "Lowest applicable bank rate (MCLR + 3% + tenor premium)",
            moratorium_months = listOf(18),
            income_limit = 99999999.0, // No family income ceiling
            purpose = "facilitating greenfield enterprise loans for Scheduled Caste and/or women entrepreneurs for manufacturing, services, agri-allied, or trading ventures",
            eligibility_criteria = listOf(
                "Scheduled Caste (SC), Scheduled Tribe (ST), and/or Woman entrepreneur",
                "Setting up a new (greenfield) enterprise in manufacturing, service, agri-allied, or trading sector",
                "Loan amount between ₹10 Lakhs and ₹1 Crore (covers up to 85% of project cost)",
                "No restrictive family income ceiling",
                "Repayment within 7 years with up to 18 months moratorium period"
            ),
            source_note = "Ministry of Finance & MoSJE Stand-Up India guidelines (standupmitra.in)",
            channel_partner_type = "All Scheduled Commercial Banks",
            target_group = "all",
            special_rebate = "Credit Guarantee Cover through CGFSI (Credit Guarantee Fund for Stand-Up India)",
            repayment_period = "Up to 7 years (up to 18-month moratorium)"
        ),
        Scheme(
            scheme_id = "pmegp_marginalized",
            name_en = "PMEGP (Marginalized Category Subsidy)",
            name_hi = "प्रधानमंत्री रोजगार सृजन कार्यक्रम (पीएमईजीपी)",
            min_amount = 100000.0,
            max_amount = 5000000.0, // Up to ₹50L manufacturing / ₹20L service
            interest_rate_min = 8.5,
            interest_rate_max = 10.5,
            interest_rate_note = "Commercial bank rate with 25% (urban) to 35% (rural) non-repayable Government Capital Subsidy",
            moratorium_months = listOf(6),
            income_limit = 99999999.0, // No income ceiling
            purpose = "credit-linked capital subsidy program for setting up self-employment micro-enterprises in manufacturing or service sectors",
            eligibility_criteria = listOf(
                "Special category applicant: Scheduled Caste (SC), ST, OBC, Women, or PwD",
                "Beneficiary contribution only 5% of project cost (general category is 10%)",
                "Government margin money subsidy: 35% in rural areas, 25% in urban areas",
                "Manufacturing projects up to ₹50 Lakhs; Service projects up to ₹20 Lakhs",
                "No family income ceiling to apply"
            ),
            source_note = "KVIC / Ministry of MSME & MoSJE Convergence Guidelines (kviconline.gov.in)",
            channel_partner_type = "Public Sector Banks / RRBs / KVIC",
            target_group = "all",
            special_rebate = "35% Rural / 25% Urban Government Capital Subsidy (Non-repayable margin money)",
            repayment_period = "3 to 7 years"
        )
    )

    val VERIFIED_SCHEME_IDS = VERIFIED_DEFAULT_SCHEMES.map { it.scheme_id }.toSet()
    private const val CLEANUP_TAG = "SchemeCleanup"

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            val db = FirebaseFirestore.getInstance()
            Log.d(TAG, "[FIRESTORE_INIT] FirebaseFirestore.getInstance() succeeded.")
            db
        } catch (e: Throwable) {
            Log.e(TAG, "[FIRESTORE_INIT] Failed to initialize FirebaseFirestore: ${e.javaClass.simpleName} - ${e.message}", e)
            null
        }
    }

    /**
     * Seeds or updates the Firestore 'schemes' collection with all verified NSFDC schemes.
     * Deletes any legacy/unverified documents (logging full ID and fields to Logcat under 'SchemeCleanup')
     * and upserts verified documents by scheme_id so that new schemes are added and existing ones
     * are updated to match live nsfdc.nic.in figures.
     */
    suspend fun seedSchemesIfEmpty(): Result<Unit> {
        Log.d(TAG, "[SEED_START] seedSchemesIfEmpty() started for ${VERIFIED_DEFAULT_SCHEMES.size} verified schemes.")
        val firestore = getFirestore()
        if (firestore == null) {
            Log.d(TAG, "[SEED_ABORT] Firestore instance is null. Skipping Firestore seeding (offline fallback).")
            return Result.success(Unit)
        }
        return try {
            // One-time cleanup migration: query existing documents in 'schemes'
            Log.d(TAG, "[SEED_CLEANUP] Scanning '$COLLECTION_NAME' for legacy unverified mock documents...")
            val existingSnapshot = firestore.collection(COLLECTION_NAME).get().await()
            for (doc in existingSnapshot.documents) {
                if (doc.id !in VERIFIED_SCHEME_IDS) {
                    // Requirement 1: Log document's full ID and field contents to Logcat with tag "SchemeCleanup" right before delete
                    Log.w(
                        CLEANUP_TAG,
                        "Deleting legacy unverified scheme document -> ID: '${doc.id}', Fields: ${doc.data}"
                    )
                    firestore.collection(COLLECTION_NAME).document(doc.id).delete().await()
                    Log.i(CLEANUP_TAG, "Successfully deleted legacy document: '${doc.id}'")
                }
            }

            Log.d(TAG, "[SEED_CHECK] Syncing all ${VERIFIED_DEFAULT_SCHEMES.size} verified schemes to '$COLLECTION_NAME'...")
            for (scheme in VERIFIED_DEFAULT_SCHEMES) {
                firestore.collection(COLLECTION_NAME)
                    .document(scheme.scheme_id)
                    .set(scheme)
                    .await()
                Log.d(TAG, "[SEED_DOC] Successfully synced document: '${scheme.scheme_id}'")
            }
            Log.d(TAG, "[SEED_SUCCESS] Successfully synced all ${VERIFIED_DEFAULT_SCHEMES.size} schemes to Firestore.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "[SEED_ERROR] Firestore seeding exception: ${e.javaClass.simpleName} - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches all schemes from Firestore, strictly filtering to verified NSFDC scheme IDs,
     * falling back to verified local defaults if offline.
     */
    suspend fun getSchemes(): Result<List<Scheme>> {
        Log.d(TAG, "[FETCH_START] getSchemes() started.")
        val firestore = getFirestore()
        if (firestore == null) {
            Log.d(TAG, "[FETCH_FALLBACK] Firestore is null. Using verified OFFLINE fallback defaults.")
            return Result.success(VERIFIED_DEFAULT_SCHEMES)
        }
        return try {
            Log.d(TAG, "[FETCH_QUERY] Querying Firestore collection '$COLLECTION_NAME'...")
            val snapshot = firestore.collection(COLLECTION_NAME).get().await()
            if (snapshot.isEmpty) {
                Log.d(TAG, "[FETCH_EMPTY] Collection '$COLLECTION_NAME' returned 0 documents. Triggering seed and returning default dataset.")
                seedSchemesIfEmpty()
                Result.success(VERIFIED_DEFAULT_SCHEMES)
            } else {
                // Strictly filter documents to verified IDs to prevent any unverified mock docs from appearing
                val validDocs = snapshot.documents.filter { it.id in VERIFIED_SCHEME_IDS }
                val list = validDocs.mapNotNull { it.toObject(Scheme::class.java) }
                
                val unverifiedDocs = snapshot.documents.filter { it.id !in VERIFIED_SCHEME_IDS }
                if (unverifiedDocs.isNotEmpty()) {
                    Log.w(
                        CLEANUP_TAG,
                        "Found ${unverifiedDocs.size} unverified document(s) in Firestore during getSchemes: ${unverifiedDocs.map { it.id }}. Excluded from results."
                    )
                }

                if (list.isNotEmpty()) {
                    Log.d(TAG, "[FETCH_SUCCESS] Successfully fetched ${list.size} schemes from REAL FIRESTORE! Scheme IDs: ${list.map { it.scheme_id }}")
                    Result.success(list)
                } else {
                    Log.d(TAG, "[FETCH_FALLBACK] Documents could not be parsed into Scheme objects. Falling back to default schemes.")
                    Result.success(VERIFIED_DEFAULT_SCHEMES)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[FETCH_ERROR] Exception querying Firestore: ${e.javaClass.simpleName} - ${e.message}. Falling back to offline defaults.", e)
            Result.success(VERIFIED_DEFAULT_SCHEMES)
        }
    }
}
