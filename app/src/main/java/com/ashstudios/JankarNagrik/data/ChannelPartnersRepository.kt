package com.ashstudios.JankarNagrik.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ChannelPartnersRepository {
    private const val TAG = "ChannelPartnersRepo"
    private const val COLLECTION_NAME = "channel_partners"

    // PSB and Small Finance Bank dynamic branch lookup via Google Places API is planned as future scope —
    // requires Cloud Billing + Places API (New) enablement, not implemented in this MVP to avoid fabricated/placeholder branch data.

    /**
     * VERIFIED NSFDC CHANNEL PARTNERS DATASET (78 entries)
     * Sourced directly from National Scheduled Castes Finance & Development Corporation (nsfdc.nic.in).
     *
     * Categories:
     * - SCA (State Channelizing Agency): 38 entries
     * - RRB (Regional Rural Bank): 26 entries
     * - NBFC-MFI (Micro Finance Institution): 7 entries
     * - Cooperative Bank: 2 entries
     * - Other Agency: 3 entries
     * - Cooperative Society: 2 entries
     *
     * NOTE: [ChannelPartner.npa_status] is set to "healthy" for all verified entries;
     * real-time NPA and fund utilization metrics remain a demo placeholder pending direct regulatory API integration.
     */
    val VERIFIED_CHANNEL_PARTNERS: List<ChannelPartner> = listOf(
        ChannelPartner(
            partner_id = "sca_apsccfc",
            name = "Andhra Pradesh Scheduled Castes Cooperative Finance Corporation (APSCCFC)",
            type = "SCA",
            latitude = 16.5062,
            longitude = 80.648,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "SP River View Apartments, 3rd Floor, Tadepalli, Amaravathi – 522501"
        ),
        ChannelPartner(
            partner_id = "sca_apsfc",
            name = "Andhra Pradesh State Financial Corporation (APSFC)",
            type = "SCA",
            latitude = 16.5062,
            longitude = 80.648,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "APSFC Building, Industrial Park, Vijayawada – 520007"
        ),
        ChannelPartner(
            partner_id = "sca_ascdc",
            name = "Assam State Development Corporation for SCs (ASCDC)",
            type = "SCA",
            latitude = 26.1445,
            longitude = 91.7362,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Sarumotoria, Dispur, Guwahati – 781006"
        ),
        ChannelPartner(
            partner_id = "sca_bssccdc",
            name = "Bihar State SCs Co-operative Development Corporation (BSSCCDC)",
            type = "SCA",
            latitude = 25.5941,
            longitude = 85.1376,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Bailey Road, Patna – 800001"
        ),
        ChannelPartner(
            partner_id = "sca_cscfdc",
            name = "Chandigarh SCs, BCs & Minorities Financial Corporation (CSCFDC)",
            type = "SCA",
            latitude = 30.7333,
            longitude = 76.7794,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Sector-17-C, Chandigarh – 160017"
        ),
        ChannelPartner(
            partner_id = "sca_cgscfdc",
            name = "Chhattisgarh State Antavasayee Sahkari Fin. & Dev. Corp (CGSCFDC)",
            type = "SCA",
            latitude = 21.1704,
            longitude = 81.7519,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Naya Raipur, Chhattisgarh – 492101"
        ),
        ChannelPartner(
            partner_id = "sca_dndsfdc",
            name = "Dadra & Nagar Haveli, Daman & Diu Financial Development Corp (DNDSFDC)",
            type = "SCA",
            latitude = 20.2738,
            longitude = 73.0169,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Silvassa – 396230"
        ),
        ChannelPartner(
            partner_id = "sca_dsfdc",
            name = "Delhi SC/ST/OBC/Minorities & Handicapped Financial Corp (DSFDC)",
            type = "SCA",
            latitude = 28.718,
            longitude = 77.117,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Sector-16, Rohini, Delhi – 110085"
        ),
        ChannelPartner(
            partner_id = "sca_gscdc",
            name = "Gujarat SCs Development Corporation (GSCDC)",
            type = "SCA",
            latitude = 23.2156,
            longitude = 72.6369,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Old Sachivalaya, Gandhinagar – 382010"
        ),
        ChannelPartner(
            partner_id = "sca_daavn",
            name = "Dr. Ambedkar Antyodaya Vikas Nigam (DAAVN)",
            type = "SCA",
            latitude = 23.2156,
            longitude = 72.6369,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Sector-10/B, Gandhinagar, Gujarat"
        ),
        ChannelPartner(
            partner_id = "sca_gscobcdc",
            name = "Goa State SCs & OBCs Finance and Development Corporation (GSCOBCDC)",
            type = "SCA",
            latitude = 15.4909,
            longitude = 73.8278,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Patto Centre, Panaji, Goa – 403001"
        ),
        ChannelPartner(
            partner_id = "sca_hscdc",
            name = "Haryana SCs Finance and Development Corporation (HSCDC)",
            type = "SCA",
            latitude = 30.7333,
            longitude = 76.7794,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Sector 22-C, Chandigarh – 160022"
        ),
        ChannelPartner(
            partner_id = "sca_hpscstdc",
            name = "Himachal Pradesh SCs & STs Development Corporation (HPSCSTDC)",
            type = "SCA",
            latitude = 30.9045,
            longitude = 77.0967,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Near Ambusha Resort, Solan – 173212"
        ),
        ChannelPartner(
            partner_id = "sca_jscdc",
            name = "Jharkhand State SCs Cooperative Development Corporation (JSCDC)",
            type = "SCA",
            latitude = 23.3441,
            longitude = 85.3096,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Morabadi, Ranchi – 834008"
        ),
        ChannelPartner(
            partner_id = "sca_jkscstbcdc",
            name = "J&K SCs, STs & OBCs Development Corporation (JKSCSTBCDC)",
            type = "SCA",
            latitude = 34.0837,
            longitude = 74.7973,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Exchange Road, Srinagar – 190001"
        ),
        ChannelPartner(
            partner_id = "sca_dbradc",
            name = "Dr B.R. Ambedkar Development Corporation (DBRADC)",
            type = "SCA",
            latitude = 12.9716,
            longitude = 77.5946,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Dr Ambedkar Veedhi, Bengaluru – 560001"
        ),
        ChannelPartner(
            partner_id = "sca_ksdc",
            name = "Kerala State Development Corporation for SCs & STs (KSDC)",
            type = "SCA",
            latitude = 10.5276,
            longitude = 76.2144,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Town Hall Road, Thrissur – 680020"
        ),
        ChannelPartner(
            partner_id = "sca_kswdc",
            name = "Kerala State Women's Development Corporation (KSWDC)",
            type = "SCA",
            latitude = 8.5241,
            longitude = 76.9366,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "East Fort, Attakulangara – 695023"
        ),
        ChannelPartner(
            partner_id = "sca_mpscfdc",
            name = "MP State Cooperative SC Finance & Development Corporation (MPSCFDC)",
            type = "SCA",
            latitude = 23.2599,
            longitude = 77.4126,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Shyamala Hills, Bhopal – 462011"
        ),
        ChannelPartner(
            partner_id = "sca_mpbcdc",
            name = "Mahatma Phule BCs Development Corporation (MPBCDC)",
            type = "SCA",
            latitude = 19.1075,
            longitude = 72.8263,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "J.V.P.D. Scheme, Juhu, Mumbai – 400049"
        ),
        ChannelPartner(
            partner_id = "sca_slasdc",
            name = "Sahityaratna Lokshahir Annabhau Sathe Development Corp (SLASDC)",
            type = "SCA",
            latitude = 19.0522,
            longitude = 72.9006,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Chembur (E), Mumbai – 400071"
        ),
        ChannelPartner(
            partner_id = "sca_lidcom",
            name = "Sant Rohidas Leather Industries & Charmakar Dev Corp (LIDCOM)",
            type = "SCA",
            latitude = 18.9322,
            longitude = 72.8264,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Veer Nariman Road, Mumbai – 400001"
        ),
        ChannelPartner(
            partner_id = "sca_mtdc",
            name = "Manipur Tribal Development Corporation (MTDC)",
            type = "SCA",
            latitude = 24.817,
            longitude = 93.9368,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Lamphelpat, Imphal – 795004"
        ),
        ChannelPartner(
            partner_id = "sca_mstcb",
            name = "Manipur SCs & STs Cooperative Development Bank (MSTCB)",
            type = "SCA",
            latitude = 24.817,
            longitude = 93.9368,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Stadium Road, Imphal East – 795001"
        ),
        ChannelPartner(
            partner_id = "sca_mcab",
            name = "Meghalaya Cooperative Apex Bank (MCAB)",
            type = "SCA",
            latitude = 25.5788,
            longitude = 91.8933,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "M.G. Road, Shillong – 793001"
        ),
        ChannelPartner(
            partner_id = "sca_muco_bank",
            name = "Mizoram Urban Cooperative Development Bank (MUCO Bank)",
            type = "SCA",
            latitude = 23.7271,
            longitude = 92.7176,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Zarkawt, Aizawl – 796001"
        ),
        ChannelPartner(
            partner_id = "sca_mkvib",
            name = "Mizoram Khadi & Village Industries Board (MKVIB)",
            type = "SCA",
            latitude = 23.7271,
            longitude = 92.7176,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Zarkawt, Aizawl – 796007"
        ),
        ChannelPartner(
            partner_id = "sca_osfdc",
            name = "Odisha SCs & STs Dev. Finance Co-op Corporation (OSFDC)",
            type = "SCA",
            latitude = 20.2961,
            longitude = 85.8245,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Lewis Road, Bhubaneshwar – 751014"
        ),
        ChannelPartner(
            partner_id = "sca_padco",
            name = "Puducherry Adi Dravidar Development Corporation (PADCO)",
            type = "SCA",
            latitude = 11.9416,
            longitude = 79.8083,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Thattanchavady, Puducherry – 605009"
        ),
        ChannelPartner(
            partner_id = "sca_pscldfc",
            name = "Punjab Scheduled Castes Land Development & Finance Corp (PSCLDFC)",
            type = "SCA",
            latitude = 30.7333,
            longitude = 76.7794,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Sector 17-C, Chandigarh – 160017"
        ),
        ChannelPartner(
            partner_id = "sca_rscdc",
            name = "Rajasthan SCs & STs Fin. & Dev. Co-op Corporation (RSCDC)",
            type = "SCA",
            latitude = 26.9124,
            longitude = 75.7873,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Bhawani Singh Marg, Jaipur – 302005"
        ),
        ChannelPartner(
            partner_id = "sca_sscstbcdc",
            name = "Sikkim SCs STs & Backward Classes Development Corp (SSCSTBCDC)",
            type = "SCA",
            latitude = 27.3389,
            longitude = 88.6065,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Bhanupath, Gangtok – 737101"
        ),
        ChannelPartner(
            partner_id = "sca_tahdco",
            name = "Tamil Nadu Adi Dravidar Housing & Development Corp (TAHDCO)",
            type = "SCA",
            latitude = 13.0827,
            longitude = 80.2707,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Cenotaph Road, Teynampet, Chennai – 600018"
        ),
        ChannelPartner(
            partner_id = "sca_tscdc",
            name = "Tripura Scheduled Castes Co-op. Development Corp (TSCDC)",
            type = "SCA",
            latitude = 23.8315,
            longitude = 91.2868,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Lake Chomubani, Agartala – 799001"
        ),
        ChannelPartner(
            partner_id = "sca_ubvevn",
            name = "Uttarakhand Bahu-udeshiya Vitta Evam Vikas Nigam (UBVEVN)",
            type = "SCA",
            latitude = 30.3165,
            longitude = 78.0322,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Bhagat Singh Colony, Dehradun – 248001"
        ),
        ChannelPartner(
            partner_id = "sca_up_sahkari_gvb",
            name = "UP Sahkari Gram Vikas Bank Ltd.",
            type = "SCA",
            latitude = 26.8467,
            longitude = 80.9462,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Mall Avenue, Lucknow – 226001"
        ),
        ChannelPartner(
            partner_id = "sca_upscfdc",
            name = "UP Scheduled Castes Finance & Development Corp (UPSCFDC)",
            type = "SCA",
            latitude = 26.8467,
            longitude = 80.9462,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Mahanagar, Lucknow – 226006"
        ),
        ChannelPartner(
            partner_id = "sca_wbscstobcdfc",
            name = "West Bengal SCs, STs & OBC Development & Finance Corp (WBSCSTOBCDFC)",
            type = "SCA",
            latitude = 22.5726,
            longitude = 88.3639,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Salt Lake Sector-I, Kolkata – 700064"
        ),
        ChannelPartner(
            partner_id = "rrb_bihar_gramin_bank",
            name = "Bihar Gramin Bank",
            type = "RRB",
            latitude = 25.5941,
            longitude = 85.1376,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "NH-30, Asochak, Patna – 800016"
        ),
        ChannelPartner(
            partner_id = "rrb_maharashtra_gramin_bank",
            name = "Maharashtra Gramin Bank",
            type = "RRB",
            latitude = 19.8762,
            longitude = 75.3433,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "CIDCO, Aurangabad – 431003"
        ),
        ChannelPartner(
            partner_id = "rrb_jharkhand_gramin_bank",
            name = "Jharkhand Gramin Bank",
            type = "RRB",
            latitude = 23.3441,
            longitude = 85.3096,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Kutchery Road, Ranchi – 834001"
        ),
        ChannelPartner(
            partner_id = "rrb_haryana_gramin_bank",
            name = "Haryana Gramin Bank",
            type = "RRB",
            latitude = 28.8955,
            longitude = 76.6066,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Delhi Road, Rohtak – 124001"
        ),
        ChannelPartner(
            partner_id = "rrb_gujarat_gramin_bank",
            name = "Gujarat Gramin Bank",
            type = "RRB",
            latitude = 21.7051,
            longitude = 72.9959,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Near Shital Guest House, Bharuch – 392001"
        ),
        ChannelPartner(
            partner_id = "rrb_telangana_grameena_bank",
            name = "Telangana Grameena Bank",
            type = "RRB",
            latitude = 17.385,
            longitude = 78.4867,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Nallakunta, Hyderabad – 500044"
        ),
        ChannelPartner(
            partner_id = "rrb_rajasthan_gramin_bank",
            name = "Rajasthan Gramin Bank",
            type = "RRB",
            latitude = 26.2389,
            longitude = 73.0243,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Sardarpura, Jodhpur – 342003"
        ),
        ChannelPartner(
            partner_id = "rrb_uttar_pradesh_gramin_bank",
            name = "Uttar Pradesh Gramin Bank",
            type = "RRB",
            latitude = 26.8467,
            longitude = 80.9462,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Gomti Nagar Extension, Lucknow – 229001"
        ),
        ChannelPartner(
            partner_id = "rrb_kerala_grameena_bank",
            name = "Kerala Grameena Bank",
            type = "RRB",
            latitude = 11.051,
            longitude = 76.0711,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Uphill, Malappuram – 676505"
        ),
        ChannelPartner(
            partner_id = "rrb_uttarakhand_gramin_bank",
            name = "Uttarakhand Gramin Bank",
            type = "RRB",
            latitude = 30.3165,
            longitude = 78.0322,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "New Road, Dehradun"
        ),
        ChannelPartner(
            partner_id = "rrb_tripura_gramin_bank",
            name = "Tripura Gramin Bank",
            type = "RRB",
            latitude = 23.8315,
            longitude = 91.2868,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Airport Road, Agartala – 799005"
        ),
        ChannelPartner(
            partner_id = "rrb_karnataka_grameena_bank",
            name = "Karnataka Grameena Bank",
            type = "RRB",
            latitude = 12.2958,
            longitude = 76.6394,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Vijayanagar IInd Stage, Mysuru – 570017"
        ),
        ChannelPartner(
            partner_id = "rrb_assam_gramin_bank",
            name = "Assam Gramin Bank",
            type = "RRB",
            latitude = 26.1445,
            longitude = 91.7362,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "G.S. Road, Bhangagarh, Guwahati – 781005"
        ),
        ChannelPartner(
            partner_id = "rrb_andhra_pradesh_grameena_bank",
            name = "Andhra Pradesh Grameena Bank",
            type = "RRB",
            latitude = 16.3067,
            longitude = 80.4365,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Broadipet, Guntur – 522002"
        ),
        ChannelPartner(
            partner_id = "rrb_punjab_gramin_bank",
            name = "Punjab Gramin Bank",
            type = "RRB",
            latitude = 31.38,
            longitude = 75.38,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Jalandhar Road, Kapurthala – 144601"
        ),
        ChannelPartner(
            partner_id = "rrb_tamil_nadu_grama_bank",
            name = "Tamil Nadu Grama Bank",
            type = "RRB",
            latitude = 11.6643,
            longitude = 78.146,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Hasthampatti, Salem – 636007"
        ),
        ChannelPartner(
            partner_id = "rrb_madhya_pradesh_gramin_bank",
            name = "Madhya Pradesh Gramin Bank",
            type = "RRB",
            latitude = 22.7196,
            longitude = 75.8577,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "MR-10, Indore – 452010"
        ),
        ChannelPartner(
            partner_id = "rrb_himachal_pradesh_gramin_bank",
            name = "Himachal Pradesh Gramin Bank",
            type = "RRB",
            latitude = 31.7084,
            longitude = 76.9319,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Mandi – 175001"
        ),
        ChannelPartner(
            partner_id = "rrb_puducherry_grama_bank",
            name = "Puducherry Grama Bank",
            type = "RRB",
            latitude = 11.9416,
            longitude = 79.8083,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Mahatma Gandhi Road, Puducherry – 605003"
        ),
        ChannelPartner(
            partner_id = "rrb_west_bengal_gramin_bank",
            name = "West Bengal Gramin Bank",
            type = "RRB",
            latitude = 22.5958,
            longitude = 88.2636,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Tikiapara, Howrah – 711101"
        ),
        ChannelPartner(
            partner_id = "rrb_chhattisgarh_gramin_bank",
            name = "Chhattisgarh Gramin Bank",
            type = "RRB",
            latitude = 21.1704,
            longitude = 81.7519,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Atal Nagar, Naya Raipur – 492013"
        ),
        ChannelPartner(
            partner_id = "rrb_manipur_rural_bank",
            name = "Manipur Rural Bank",
            type = "RRB",
            latitude = 24.817,
            longitude = 93.9368,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Keishampat, Imphal – 795001"
        ),
        ChannelPartner(
            partner_id = "rrb_meghalaya_rural_bank",
            name = "Meghalaya Rural Bank",
            type = "RRB",
            latitude = 25.5788,
            longitude = 91.8933,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Police Bazar, Shillong – 793001"
        ),
        ChannelPartner(
            partner_id = "rrb_jk_grameen_bank",
            name = "J&K Grameen Bank",
            type = "RRB",
            latitude = 32.7266,
            longitude = 74.857,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Narwal, Jammu – 180006"
        ),
        ChannelPartner(
            partner_id = "rrb_odisha_grameen_bank",
            name = "Odisha Grameen Bank",
            type = "RRB",
            latitude = 20.2961,
            longitude = 85.8245,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Pokhariput, Bhubaneshwar – 751030"
        ),
        ChannelPartner(
            partner_id = "rrb_mizoram_rural_bank",
            name = "Mizoram Rural Bank",
            type = "RRB",
            latitude = 23.7271,
            longitude = 92.7176,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Khatla, Aizawl – 796001"
        ),
        ChannelPartner(
            partner_id = "nbfc_anik_financial",
            name = "Anik Financial Services Pvt Ltd",
            type = "NBFC-MFI",
            latitude = 18.4088,
            longitude = 76.5604,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Ambajogai Road, Latur – 413512"
        ),
        ChannelPartner(
            partner_id = "nbfc_grameen_dev",
            name = "Grameen Development & Finance Pvt Ltd",
            type = "NBFC-MFI",
            latitude = 25.9,
            longitude = 91.3,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Chhaygaon, Kamrup, Assam – 781124"
        ),
        ChannelPartner(
            partner_id = "nbfc_asa_international",
            name = "ASA International Microfinance Ltd",
            type = "NBFC-MFI",
            latitude = 22.5726,
            longitude = 88.3639,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Sector-V, Salt Lake City, Kolkata – 700091"
        ),
        ChannelPartner(
            partner_id = "nbfc_midland_microfin",
            name = "Midland Microfin Ltd",
            type = "NBFC-MFI",
            latitude = 31.326,
            longitude = 75.5762,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "G.T. Road, Jalandhar – 144001"
        ),
        ChannelPartner(
            partner_id = "nbfc_satin_creditcare",
            name = "Satin Creditcare Network Ltd",
            type = "NBFC-MFI",
            latitude = 28.4595,
            longitude = 77.0266,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Udyog Vihar Phase-III, Gurugram – 122016"
        ),
        ChannelPartner(
            partner_id = "nbfc_pahal_financial",
            name = "Pahal Financial Services Pvt Ltd",
            type = "NBFC-MFI",
            latitude = 23.0225,
            longitude = 72.5714,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Ambli-Iscon Road, Ahmedabad – 380054"
        ),
        ChannelPartner(
            partner_id = "nbfc_vector_finance",
            name = "Vector Finance Pvt Ltd",
            type = "NBFC-MFI",
            latitude = 20.2961,
            longitude = 85.8245,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Khandagiri, Bhubaneswar – 751029"
        ),
        ChannelPartner(
            partner_id = "coop_bank_shri_mahila_sewa",
            name = "Shri Mahila Sewa Sahakari Bank Ltd",
            type = "Cooperative Bank",
            latitude = 23.0225,
            longitude = 72.5714,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Ellisbridge, Ahmedabad – 380006"
        ),
        ChannelPartner(
            partner_id = "coop_bank_konoklata_mahila",
            name = "Konoklata Mahila Urban Cooperative Bank",
            type = "Cooperative Bank",
            latitude = 26.1445,
            longitude = 91.7362,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Assam"
        ),
        ChannelPartner(
            partner_id = "agency_nedfi",
            name = "North Eastern Development Finance Corporation (NEDFi)",
            type = "Other Agency",
            latitude = 26.1445,
            longitude = 91.7362,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Dispur, Guwahati, Assam – 781006"
        ),
        ChannelPartner(
            partner_id = "agency_jharcraft",
            name = "Jharkhand Silk Textile & Handicraft Development Corp (JHARCRAFT)",
            type = "Other Agency",
            latitude = 23.3441,
            longitude = 85.3096,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "DIC Campus Ratu Road, Ranchi – 834001"
        ),
        ChannelPartner(
            partner_id = "agency_sidbi",
            name = "Small Industries Development Bank of India (SIDBI)",
            type = "Other Agency",
            latitude = 26.8467,
            longitude = 80.9462,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Ashok Marg, Lucknow – 226001"
        ),
        ChannelPartner(
            partner_id = "coop_soc_streenidhi_ts",
            name = "Streenidhi",
            type = "Cooperative Society",
            latitude = 17.385,
            longitude = 78.4867,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Saifabad, Hyderabad, Telangana – 500004"
        ),
        ChannelPartner(
            partner_id = "coop_soc_streenidhi_ap",
            name = "Streenidhi AP",
            type = "Cooperative Society",
            latitude = 16.5062,
            longitude = 80.648,
            handles_schemes = listOf("micro_finance", "term_loan", "educational_loan"),
            npa_status = "healthy",
            address = "Vijayawada – 520013"
        )
    )

    // Backwards compatibility alias
    val SAMPLE_CHANNEL_PARTNERS: List<ChannelPartner> = VERIFIED_CHANNEL_PARTNERS

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
     * Seeds the Firestore 'channel_partners' collection using batch write if empty or under-populated.
     * Also cleans up any old sample documents so the collection reflects exactly the 78 verified entries.
     */
    suspend fun seedChannelPartnersIfEmpty(): Result<Unit> {
        Log.d(TAG, "[SEED_START] seedChannelPartnersIfEmpty() started.")
        val firestore = getFirestore()
        if (firestore == null) {
            Log.d(TAG, "[SEED_ABORT] Firestore instance is null. Skipping Firestore seeding (offline fallback).")
            return Result.success(Unit)
        }
        return try {
            Log.d(TAG, "[SEED_CHECK] Connecting to Firestore collection '$COLLECTION_NAME'...")
            val verifiedIds = VERIFIED_CHANNEL_PARTNERS.map { it.partner_id }.toSet()
            val snapshot = firestore.collection(COLLECTION_NAME).get().await()
            val existingIds = snapshot.documents.map { it.id }.toSet()
            val toDelete = existingIds - verifiedIds

            if (toDelete.isNotEmpty() || snapshot.size() != VERIFIED_CHANNEL_PARTNERS.size) {
                Log.d(TAG, "[SEED_EMPTY] Collection '$COLLECTION_NAME' has ${snapshot.size()} docs (expected ${VERIFIED_CHANNEL_PARTNERS.size}). Seeding ${VERIFIED_CHANNEL_PARTNERS.size} verified NSFDC Channel Partners via batch...")
                val batch = firestore.batch()
                for (docId in toDelete) {
                    batch.delete(firestore.collection(COLLECTION_NAME).document(docId))
                }
                for (partner in VERIFIED_CHANNEL_PARTNERS) {
                    val docRef = firestore.collection(COLLECTION_NAME).document(partner.partner_id)
                    batch.set(docRef, partner)
                }
                batch.commit().await()
                Log.d(TAG, "[SEED_SUCCESS] Successfully seeded all ${VERIFIED_CHANNEL_PARTNERS.size} channel partners to Firestore (cleaned up ${toDelete.size} old docs).")
            } else {
                Log.d(TAG, "[SEED_EXISTS] Collection '$COLLECTION_NAME' is ALREADY POPULATED with ${snapshot.size()} documents. Skipping seeding.")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "[SEED_ERROR] Firestore seeding exception: ${e.javaClass.simpleName} - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches all channel partners from Firestore, falling back to verified local dataset if offline.
     */
    suspend fun getChannelPartners(): Result<List<ChannelPartner>> {
        Log.d(TAG, "[FETCH_START] getChannelPartners() started.")
        val firestore = getFirestore()
        if (firestore == null) {
            Log.d(TAG, "[FETCH_FALLBACK] Firestore is null. Using verified OFFLINE fallback dataset (${VERIFIED_CHANNEL_PARTNERS.size} partners).")
            return Result.success(VERIFIED_CHANNEL_PARTNERS)
        }
        return try {
            Log.d(TAG, "[FETCH_QUERY] Querying Firestore collection '$COLLECTION_NAME'...")
            val snapshot = firestore.collection(COLLECTION_NAME).get().await()
            if (snapshot.isEmpty) {
                Log.d(TAG, "[FETCH_EMPTY] Collection '$COLLECTION_NAME' returned 0 documents. Triggering seed and returning verified dataset.")
                seedChannelPartnersIfEmpty()
                Result.success(VERIFIED_CHANNEL_PARTNERS)
            } else {
                val list = snapshot.documents.mapNotNull { it.toObject(ChannelPartner::class.java) }
                if (list.isNotEmpty()) {
                    Log.d(TAG, "[FETCH_SUCCESS] Successfully fetched ${list.size} channel partners from REAL FIRESTORE!")
                    Result.success(list)
                } else {
                    Log.d(TAG, "[FETCH_FALLBACK] Documents could not be parsed into ChannelPartner objects. Falling back to verified dataset.")
                    Result.success(VERIFIED_CHANNEL_PARTNERS)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[FETCH_ERROR] Exception querying Firestore: ${e.javaClass.simpleName} - ${e.message}. Falling back to verified dataset.", e)
            Result.success(VERIFIED_CHANNEL_PARTNERS)
        }
    }
}
