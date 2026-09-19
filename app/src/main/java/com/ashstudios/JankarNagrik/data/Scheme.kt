package com.ashstudios.JankarNagrik.data

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class Scheme(
    @get:PropertyName("scheme_id") @set:PropertyName("scheme_id")
    var scheme_id: String = "",

    @get:PropertyName("name_en") @set:PropertyName("name_en")
    var name_en: String = "",

    @get:PropertyName("name_hi") @set:PropertyName("name_hi")
    var name_hi: String = "",

    @get:PropertyName("min_amount") @set:PropertyName("min_amount")
    var min_amount: Double? = null,

    @get:PropertyName("max_amount") @set:PropertyName("max_amount")
    var max_amount: Double? = null,

    @get:PropertyName("interest_rate_min") @set:PropertyName("interest_rate_min")
    var interest_rate_min: Double? = null,

    @get:PropertyName("interest_rate_max") @set:PropertyName("interest_rate_max")
    var interest_rate_max: Double? = null,

    @get:PropertyName("interest_rate_note") @set:PropertyName("interest_rate_note")
    var interest_rate_note: String? = null,

    @get:PropertyName("moratorium_months") @set:PropertyName("moratorium_months")
    var moratorium_months: List<Int> = emptyList(),

    @get:PropertyName("income_limit") @set:PropertyName("income_limit")
    var income_limit: Double = 500000.0,

    @get:PropertyName("purpose") @set:PropertyName("purpose")
    var purpose: String = "",

    @get:PropertyName("eligibility_criteria") @set:PropertyName("eligibility_criteria")
    var eligibility_criteria: List<String> = emptyList(),

    @get:PropertyName("source_note") @set:PropertyName("source_note")
    var source_note: String = "Based on official NSFDC guidelines as referenced in SIH26092",

    @get:PropertyName("channel_partner_type") @set:PropertyName("channel_partner_type")
    var channel_partner_type: String? = null,

    @get:PropertyName("target_group") @set:PropertyName("target_group")
    var target_group: String? = null,

    @get:PropertyName("special_rebate") @set:PropertyName("special_rebate")
    var special_rebate: String? = null,

    @get:PropertyName("repayment_period") @set:PropertyName("repayment_period")
    var repayment_period: String? = null
)
