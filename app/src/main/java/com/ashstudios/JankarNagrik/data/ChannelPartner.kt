package com.ashstudios.JankarNagrik.data

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

/**
 * Represents a Channel Partner (SCA, PSB, RRB, NBFC-MFI) through which NSFDC schemes are channeled.
 *
 * NOTE: The [npa_status] field ("healthy" | "high_risk") is mocked for demo/hackathon evaluation purposes.
 * Real-time NPA / fund-utilization data is not publicly exposed via open APIs and is a placeholder
 * pending future direct NSFDC API integration.
 */
@IgnoreExtraProperties
data class ChannelPartner(
    @get:PropertyName("partner_id") @set:PropertyName("partner_id")
    var partner_id: String = "",

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("type") @set:PropertyName("type")
    var type: String = "", // SCA, PSB, RRB, NBFC-MFI

    @get:PropertyName("latitude") @set:PropertyName("latitude")
    var latitude: Double = 0.0,

    @get:PropertyName("longitude") @set:PropertyName("longitude")
    var longitude: Double = 0.0,

    @get:PropertyName("handles_schemes") @set:PropertyName("handles_schemes")
    var handles_schemes: List<String> = emptyList(),

    @get:PropertyName("npa_status") @set:PropertyName("npa_status")
    var npa_status: String = "healthy", // "healthy" or "high_risk"

    @get:PropertyName("address") @set:PropertyName("address")
    var address: String = ""
)
