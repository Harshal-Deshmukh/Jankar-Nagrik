package com.ashstudios.JankarNagrik

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ashstudios.JankarNagrik.data.Scheme
import com.ashstudios.JankarNagrik.data.SchemesRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommenderScreen(navController: NavHostController) {
    val isHindi = LocalIsHindi.current
    val coroutineScope = rememberCoroutineScope()

    // Form inputs
    var businessIdea by remember { mutableStateOf("") }
    var projectType by remember { mutableStateOf("") }
    var projectTypeExpanded by remember { mutableStateOf(false) }
    val projectTypeOptions = listOf(
        "Agriculture",
        "Retail/Trade",
        "Manufacturing",
        "Service Sector",
        "Artisan & Traditional Crafts",
        "Education",
        "Green/Renewable Energy",
        "Other"
    )

    val tradeQuickChips = listOf(
        "🛺 E-Rickshaw / Clean Transport" to "Green/Renewable Energy",
        "🧵 Tailoring & Boutique" to "Service Sector",
        "👞 Traditional Artisan" to "Artisan & Traditional Crafts",
        "🛒 Kirana & Retail" to "Retail/Trade",
        "🐄 Dairy & Farming" to "Agriculture",
        "💻 Cyber Cafe / Tech" to "Service Sector",
        "🎓 Higher Education" to "Education"
    )

    var costStr by remember { mutableStateOf("") }
    var incomeStr by remember { mutableStateOf("") }
    var areaType by remember { mutableStateOf("Rural") } // "Rural" or "Urban"

    var education by remember { mutableStateOf("") }
    var educationExpanded by remember { mutableStateOf(false) }
    val educationOptions = listOf(
        "Below 10th",
        "10th Pass",
        "12th Pass",
        "Graduate",
        "Post-Graduate",
        "Professional Course (Engineering/Medical/Law/MBA)"
    )

    var isFemaleBeneficiary by remember { mutableStateOf(false) }

    var channelPreference by remember { mutableStateOf("No Preference") }
    var channelPreferenceExpanded by remember { mutableStateOf(false) }
    val channelPreferenceOptions = listOf(
        "No Preference",
        "Cooperative Bank / Small Finance Bank",
        "NBFC-MFI",
        "SCA / Commercial Bank"
    )

    // Repository state
    var schemes by remember { mutableStateOf<List<Scheme>>(emptyList()) }
    var isFetchingSchemes by remember { mutableStateOf(true) }
    var schemesError by remember { mutableStateOf<String?>(null) }

    // Recommendation & AI state
    var recommendedSchemeName by remember { mutableStateOf<String?>(null) }
    var matchedSchemeObj by remember { mutableStateOf<Scheme?>(null) }
    var matchedSchemes by remember { mutableStateOf<List<Scheme>>(emptyList()) }
    var isEligible by remember { mutableStateOf(true) }

    var aiMatchScore by remember { mutableStateOf<Int?>(null) }
    var aiFitReasoning by remember { mutableStateOf<String?>(null) }
    var aiGapAdvice by remember { mutableStateOf<String?>(null) }
    var aiDocumentChecklist by remember { mutableStateOf<List<String>>(emptyList()) }
    var checkedDocuments by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isAiLoading by remember { mutableStateOf(false) }

    // Load schemes from SchemesRepository
    fun loadSchemes() {
        isFetchingSchemes = true
        schemesError = null
        coroutineScope.launch {
            val result = SchemesRepository.getSchemes()
            result.onSuccess {
                schemes = it
                isFetchingSchemes = false
            }.onFailure { err ->
                schemesError = err.localizedMessage ?: "Failed to load schemes"
                isFetchingSchemes = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadSchemes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step Indicator: Step 1 of 3
        FlowStepIndicator(currentStep = 1)

        // Loading State for Repository
        if (isFetchingSchemes) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(16.dp))
                    Text(
                        if (isHindi) "योजना डेटा लोड हो रहा है..." else "Loading verified government schemes...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Error State with Retry
        if (schemesError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = schemesError ?: "Error loading schemes",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    IconButton(onClick = { loadSchemes() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Section Title: Business Idea
        Text(
            text = if (isHindi) "1. आपका व्यावसायिक विचार (Business Idea)" else "1. Your Business Idea & Sector",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Quick Select Trade Chips
        Text(
            text = if (isHindi) "त्वरित चयन (टैप करें):" else "Quick Suggestions (Tap to select):",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tradeQuickChips.forEach { (chipText, sector) ->
                val isSelected = businessIdea == chipText
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable {
                        businessIdea = chipText
                        projectType = sector
                    }
                ) {
                    Text(
                        text = chipText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Custom Business Idea Input
        OutlinedTextField(
            value = businessIdea,
            onValueChange = { businessIdea = it },
            label = { Text(if (isHindi) "व्यवसाय या गतिविधि का विवरण" else "Business or Activity Description") },
            placeholder = { Text(if (isHindi) "उदा. ई-रिक्शा, सिलाई की दुकान, डेयरी फार्म..." else "e.g. Electric rickshaw, tailoring shop, dairy...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Input Form: Project Type Dropdown
        ExposedDropdownMenuBox(
            expanded = projectTypeExpanded,
            onExpandedChange = { projectTypeExpanded = !projectTypeExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = projectType,
                onValueChange = {},
                label = { Text(if (isHindi) "परियोजना का प्रकार (Sector)" else "Project Sector") },
                placeholder = { Text(if (isHindi) "परियोजना प्रकार चुनें" else "Select Sector") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projectTypeExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = projectTypeExpanded,
                onDismissRequest = { projectTypeExpanded = false }
            ) {
                projectTypeOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            projectType = selectionOption
                            projectTypeExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        // Section Title: Financial Profile
        Text(
            text = if (isHindi) "2. वित्तीय विवरण (Financial Parameters)" else "2. Financial Parameters",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = costStr,
            onValueChange = { costStr = it },
            label = { Text(if (isHindi) "आवश्यक परियोजना लागत (₹)" else "Required Project Capital (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = incomeStr,
            onValueChange = { incomeStr = it },
            label = { Text(if (isHindi) "वार्षिक पारिवारिक आय (₹)" else "Annual Family Income (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Area Type Selection (Rural vs Urban for PMEGP subsidy differences)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isHindi) "क्षेत्र का प्रकार:" else "Area Location:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Rural" to (if (isHindi) "ग्रामीण (35% सब्सिडी)" else "Rural (35% Subsidy)"),
                       "Urban" to (if (isHindi) "शहरी (25% सब्सिडी)" else "Urban (25% Subsidy)")).forEach { (type, label) ->
                    val isSelected = areaType == type
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { areaType = type }
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Input Form: Educational Qualification Dropdown
        ExposedDropdownMenuBox(
            expanded = educationExpanded,
            onExpandedChange = { educationExpanded = !educationExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = education,
                onValueChange = {},
                label = { Text(if (isHindi) "शैक्षणिक योग्यता" else "Educational Qualification") },
                placeholder = { Text(if (isHindi) "शैक्षणिक योग्यता चुनें" else "Select Educational Qualification") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = educationExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = educationExpanded,
                onDismissRequest = { educationExpanded = false }
            ) {
                educationOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            education = selectionOption
                            educationExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        // Input Form: Channel Partner Preference Dropdown
        ExposedDropdownMenuBox(
            expanded = channelPreferenceExpanded,
            onExpandedChange = { channelPreferenceExpanded = !channelPreferenceExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = when (channelPreference) {
                    "Cooperative Bank / Small Finance Bank" -> if (isHindi) "सहकारी बैंक / लघु वित्त बैंक (SFB)" else "Cooperative Bank / Small Finance Bank"
                    "NBFC-MFI" -> if (isHindi) "एनबीएफसी-एमएफआई (NBFC-MFI)" else "NBFC-MFI"
                    "SCA / Commercial Bank" -> if (isHindi) "राज्य चैनल एजेंसी (SCA) / वाणिज्यिक बैंक" else "SCA / Commercial Bank"
                    else -> if (isHindi) "कोई प्राथमिकता नहीं (सभी)" else "No Preference / All"
                },
                onValueChange = {},
                label = { Text(if (isHindi) "चैनल पार्टनर प्राथमिकता" else "Channel Partner Preference") },
                placeholder = { Text(if (isHindi) "प्राथमिकता चुनें" else "Select Preference") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = channelPreferenceExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = channelPreferenceExpanded,
                onDismissRequest = { channelPreferenceExpanded = false }
            ) {
                channelPreferenceOptions.forEach { selectionOption ->
                    val label = when (selectionOption) {
                        "Cooperative Bank / Small Finance Bank" -> if (isHindi) "सहकारी बैंक / लघु वित्त बैंक (SFB)" else "Cooperative Bank / Small Finance Bank"
                        "NBFC-MFI" -> if (isHindi) "एनबीएफसी-एमएफआई (NBFC-MFI)" else "NBFC-MFI"
                        "SCA / Commercial Bank" -> if (isHindi) "राज्य चैनल एजेंसी (SCA) / वाणिज्यिक बैंक" else "SCA / Commercial Bank"
                        else -> if (isHindi) "कोई प्राथमिकता नहीं (सभी)" else "No Preference / All"
                    }
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            channelPreference = selectionOption
                            channelPreferenceExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        // Women Beneficiary Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isFemaleBeneficiary,
                onCheckedChange = { isFemaleBeneficiary = it }
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isHindi) "महिला उद्यमी (विशेष 4% रियायती ब्याज दर एवं स्टैंड-अप इंडिया पात्रता)" else "Female Entrepreneur (Eligible for 4% MSY rate & Stand-Up India)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        // MATCH SCHEMES BUTTON
        Button(
            onClick = {
                val cost = costStr.toDoubleOrNull() ?: 0.0
                val income = incomeStr.toDoubleOrNull() ?: 0.0

                // Lookup all 10 verified schemes dynamically from repository data
                val microFinance = schemes.find { it.scheme_id == "micro_finance" }
                val termLoan = schemes.find { it.scheme_id == "term_loan" }
                val educationalLoan = schemes.find { it.scheme_id == "educational_loan" }
                val udyamNidhi = schemes.find { it.scheme_id == "udyam_nidhi" }
                val aajeevika = schemes.find { it.scheme_id == "aajeevika_microfinance" }
                val mahilaSamriddhi = schemes.find { it.scheme_id == "mahila_samriddhi" }
                val greenBusiness = schemes.find { it.scheme_id == "green_business" }
                val pmVishwakarma = schemes.find { it.scheme_id == "pm_vishwakarma" }
                val standUpIndia = schemes.find { it.scheme_id == "stand_up_india" }
                val pmegp = schemes.find { it.scheme_id == "pmegp_marginalized" }

                val isNbfcMfi = channelPreference == "NBFC-MFI"
                val isCoopOrSfb = channelPreference == "Cooperative Bank / Small Finance Bank"
                val isScaOrBank = channelPreference == "SCA / Commercial Bank"

                // 1. Deterministic Rule Layer (Instant Offline Regulatory Safety Net)
                val isGreen = projectType == "Green/Renewable Energy" ||
                        businessIdea.contains("rickshaw", ignoreCase = true) ||
                        businessIdea.contains("solar", ignoreCase = true) ||
                        businessIdea.contains("electric", ignoreCase = true)

                val isArtisan = projectType == "Artisan & Traditional Crafts" ||
                        businessIdea.contains("tailor", ignoreCase = true) ||
                        businessIdea.contains("artisan", ignoreCase = true) ||
                        businessIdea.contains("cobbler", ignoreCase = true) ||
                        businessIdea.contains("सिलाई", ignoreCase = true) ||
                        businessIdea.contains("दस्तकार", ignoreCase = true)

                if (income > 500000.0) {
                    // Over NSFDC 5L cap -> Route to Stand-Up India or PMEGP
                    if (cost in 1000000.0..10000000.0 && (isFemaleBeneficiary || standUpIndia != null)) {
                        isEligible = true
                        matchedSchemeObj = standUpIndia
                        matchedSchemes = listOfNotNull(standUpIndia, pmegp)
                        recommendedSchemeName = if (isHindi) standUpIndia?.name_hi ?: "स्टैंड-अप इंडिया योजना" else standUpIndia?.name_en ?: "Stand-Up India Scheme"
                    } else if (cost <= 5000000.0 && pmegp != null) {
                        isEligible = true
                        matchedSchemeObj = pmegp
                        matchedSchemes = listOfNotNull(pmegp)
                        recommendedSchemeName = if (isHindi) pmegp.name_hi else pmegp.name_en
                    } else {
                        isEligible = false
                        matchedSchemeObj = null
                        matchedSchemes = emptyList()
                        recommendedSchemeName = if (isHindi) {
                            "एनएसएफडीसी रियायती योजनाओं के लिए पात्र नहीं (वार्षिक पारिवारिक आय ₹5,00,000 से अधिक है)"
                        } else {
                            "Not eligible for NSFDC concessional credit (Annual family income exceeds ₹5,00,000)"
                        }
                    }
                } else if (projectType.equals("Education", ignoreCase = true) ||
                    (projectType.isEmpty() && education.contains("Professional Course", ignoreCase = true))
                ) {
                    val maxEdu = educationalLoan?.max_amount ?: 4000000.0
                    if (cost <= maxEdu) {
                        isEligible = true
                        matchedSchemeObj = educationalLoan
                        matchedSchemes = listOfNotNull(educationalLoan)
                        recommendedSchemeName = if (isHindi) educationalLoan?.name_hi ?: "शिक्षा ऋण योजना" else educationalLoan?.name_en ?: "Educational Loan Scheme (ELS)"
                    } else {
                        isEligible = false
                        matchedSchemeObj = null
                        matchedSchemes = emptyList()
                        recommendedSchemeName = if (isHindi) "कोर्स शुल्क अधिकतम शिक्षा ऋण सीमा (₹40,00,000) से अधिक है" else "Course fee exceeds maximum educational loan ceiling of ₹40,00,000"
                    }
                } else if (isGreen && cost in 50000.0..3000000.0 && greenBusiness != null) {
                    isEligible = true
                    matchedSchemeObj = greenBusiness
                    matchedSchemes = listOfNotNull(greenBusiness, termLoan)
                    recommendedSchemeName = if (isHindi) greenBusiness.name_hi else greenBusiness.name_en
                } else if (isArtisan && cost in 10000.0..300000.0 && pmVishwakarma != null) {
                    isEligible = true
                    matchedSchemeObj = pmVishwakarma
                    matchedSchemes = listOfNotNull(pmVishwakarma, microFinance)
                    recommendedSchemeName = if (isHindi) pmVishwakarma.name_hi else pmVishwakarma.name_en
                } else {
                    val mfMax = microFinance?.max_amount ?: 140000.0
                    val tlMax = termLoan?.max_amount ?: 5000000.0

                    when {
                        cost in 0.0..mfMax -> {
                            isEligible = true
                            if (isNbfcMfi && aajeevika != null) {
                                matchedSchemeObj = aajeevika
                                matchedSchemes = listOf(aajeevika)
                                recommendedSchemeName = if (isHindi) aajeevika.name_hi else aajeevika.name_en
                            } else if (isFemaleBeneficiary && mahilaSamriddhi != null) {
                                matchedSchemeObj = mahilaSamriddhi
                                matchedSchemes = listOf(mahilaSamriddhi)
                                recommendedSchemeName = if (isHindi) mahilaSamriddhi.name_hi else mahilaSamriddhi.name_en
                            } else {
                                matchedSchemeObj = microFinance
                                matchedSchemes = listOfNotNull(microFinance)
                                recommendedSchemeName = if (isHindi) microFinance?.name_hi ?: "लघु वित्त योजना" else microFinance?.name_en ?: "Micro Finance Scheme (MFS)"
                            }
                        }
                        cost in (mfMax + 1)..500000.0 -> {
                            isEligible = true
                            if (isCoopOrSfb && udyamNidhi != null) {
                                matchedSchemeObj = udyamNidhi
                                matchedSchemes = listOf(udyamNidhi)
                                recommendedSchemeName = if (isHindi) udyamNidhi.name_hi else udyamNidhi.name_en
                            } else if (isScaOrBank && termLoan != null) {
                                matchedSchemeObj = termLoan
                                matchedSchemes = listOf(termLoan)
                                recommendedSchemeName = if (isHindi) termLoan.name_hi else termLoan.name_en
                            } else {
                                val dualOptions = listOfNotNull(termLoan, udyamNidhi)
                                matchedSchemes = dualOptions
                                matchedSchemeObj = dualOptions.firstOrNull()
                                recommendedSchemeName = if (isHindi) "2 योजनाएं पात्र (सावधि ऋण एवं उद्यम निधि)" else "2 Matching Schemes (Term Loan & Udyam Nidhi)"
                            }
                        }
                        cost in 500001.0..tlMax -> {
                            isEligible = true
                            matchedSchemeObj = termLoan
                            matchedSchemes = listOfNotNull(termLoan, pmegp)
                            recommendedSchemeName = if (isHindi) termLoan?.name_hi ?: "सावधि ऋण योजना" else termLoan?.name_en ?: "Term Loan Scheme"
                        }
                        cost in (tlMax + 1)..10000000.0 && standUpIndia != null -> {
                            isEligible = true
                            matchedSchemeObj = standUpIndia
                            matchedSchemes = listOf(standUpIndia)
                            recommendedSchemeName = if (isHindi) standUpIndia.name_hi else standUpIndia.name_en
                        }
                        else -> {
                            isEligible = false
                            matchedSchemeObj = null
                            matchedSchemes = emptyList()
                            recommendedSchemeName = if (isHindi) "कृपया एक मान्य राशि दर्ज करें" else "Please enter a valid amount"
                        }
                    }
                }

                // Default checklist while AI loads
                val defaultChecklist = if (isHindi) listOf(
                    "जाति प्रमाण पत्र (Scheduled Caste Certificate)",
                    "आय प्रमाण पत्र (Income Certificate)",
                    "परियोजना लागत कोटेशन / प्राक्कलन (Cost Estimate / Quotation)",
                    "आधार कार्ड एवं निवास प्रमाण पत्र (Aadhaar & Residence Proof)",
                    "बैंक खाता पासबुक एवं 2 पासपोर्ट फोटो"
                ) else listOf(
                    "Scheduled Caste (SC) Category Certificate",
                    "Annual Family Income Certificate",
                    "Project Cost Estimate / Machine Quotation",
                    "Aadhaar Card & Residence Proof",
                    "Bank Account Passbook & 2 Passport Photos"
                )
                aiDocumentChecklist = defaultChecklist

                // 2. Trigger Gemini 3.6 Flash Semantic Matching & Scoring Engine
                isAiLoading = true
                aiMatchScore = null
                aiFitReasoning = null
                aiGapAdvice = null

                coroutineScope.launch {
                    try {
                        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
                            throw IllegalStateException("GEMINI_API_KEY is empty. Please check local.properties and rebuild.")
                        }
                        val generativeModel = com.google.ai.client.generativeai.GenerativeModel(
                            modelName = "gemini-3.6-flash",
                            apiKey = BuildConfig.GEMINI_API_KEY
                        )

                        val prompt = """
You are the official AI Scheme Matcher for the Ministry of Social Justice and Empowerment (MoSJE) under SIH Problem Statement 26092.
Given an entrepreneur's profile and 10 available schemes, output ONLY a valid JSON object:
{
  "primary_scheme_id": "<scheme_id>",
  "alternative_scheme_id": "<scheme_id or null>",
  "match_percentage": <integer between 75 and 98>,
  "fit_reasoning": "<1-2 sentences strictly in ${if (isHindi) "simple Hindi (हिन्दी)" else "English"} explaining why this scheme fits the business activity and capital>",
  "gap_advice": "<1 sentence strictly in ${if (isHindi) "simple Hindi (हिन्दी)" else "English"} offering strategic advice on subsidies, women rebates, or statutory requirements>",
  "required_documents": ["<doc1>", "<doc2>", "<doc3>", "<doc4>"]
}

User Profile:
- Category: Scheduled Caste (SC)
- Gender: ${if (isFemaleBeneficiary) "Female" else "Male"}
- Annual Family Income: ₹$income
- Business Idea / Trade: ${if (businessIdea.isNotBlank()) businessIdea else projectType}
- Project Sector: $projectType
- Capital Needed: ₹$cost
- Location: $areaType
- Channel Preference: $channelPreference
- Language: ${if (isHindi) "Hindi" else "English"}

Available Schemes (10 Verified Schemes):
1. micro_finance (NSFDC Micro Finance, max ₹1.4L, 6.5% interest, 3-mo moratorium)
2. term_loan (NSFDC Term Loan, >₹1.4L to ₹50L, 8.0% interest, 6-12 mo moratorium)
3. mahila_samriddhi (NSFDC Mahila Samriddhi, max ₹1.4L, 4.0% interest for SC women)
4. green_business (NSFDC Green Business Scheme, max ₹30L, 8.0% interest for e-rickshaws, solar, bio-gas)
5. udyam_nidhi (NSFDC Udyam Nidhi, max ₹5L, 13%-15% interest via Coop/Small Finance Banks)
6. aajeevika_microfinance (NSFDC Aajeevika, max ₹1.4L, 15% via NBFC-MFIs)
7. educational_loan (NSFDC Educational Loan, max ₹40L, 6.5% interest with 0.5% female rebate)
8. pm_vishwakarma (PM Vishwakarma, max ₹3L, 5.0% subsidized interest + ₹15,000 toolkit voucher for 18 artisan trades)
9. stand_up_india (Stand-Up India, ₹10L to ₹1Cr for SC/women greenfield enterprises, no family income cap)
10. pmegp_marginalized (PMEGP, max ₹50L with 35% rural / 25% urban government margin money capital subsidy)
""".trimIndent()

                        val response = generativeModel.generateContent(prompt)
                        val rawText = response.text?.trim() ?: ""
                        val cleanJson = rawText.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()

                        try {
                            val jsonObj = JSONObject(cleanJson)
                            val primaryId = jsonObj.optString("primary_scheme_id")
                            val altId = jsonObj.optString("alternative_scheme_id")
                            val score = jsonObj.optInt("match_percentage", 92)
                            val reasoning = jsonObj.optString("fit_reasoning")
                            val advice = jsonObj.optString("gap_advice")

                            val docsArray = jsonObj.optJSONArray("required_documents")
                            val docsList = mutableListOf<String>()
                            if (docsArray != null) {
                                for (i in 0 until docsArray.length()) {
                                    docsList.add(docsArray.getString(i))
                                }
                            }

                            val aiPrimary = schemes.find { it.scheme_id == primaryId }
                            val aiAlt = schemes.find { it.scheme_id == altId }

                            if (aiPrimary != null) {
                                matchedSchemeObj = aiPrimary
                                val dual = listOfNotNull(aiPrimary, aiAlt).distinctBy { it.scheme_id }
                                if (dual.isNotEmpty()) {
                                    matchedSchemes = dual
                                }
                                recommendedSchemeName = if (isHindi) aiPrimary.name_hi else aiPrimary.name_en
                                isEligible = true
                            }

                            aiMatchScore = score
                            aiFitReasoning = reasoning
                            aiGapAdvice = advice
                            if (docsList.isNotEmpty()) {
                                aiDocumentChecklist = docsList
                            }
                        } catch (parseEx: Exception) {
                            Log.w("RecommenderScreen", "JSON parsing failed, using raw response", parseEx)
                            aiMatchScore = 90
                            aiFitReasoning = rawText
                        }
                    } catch (e: Exception) {
                        Log.e("RecommenderScreen", "Gemini AI matching failed: ${e.message}", e)
                        aiMatchScore = if (isEligible) 88 else null
                        aiFitReasoning = if (isEligible) {
                            if (isHindi) {
                                "आधिकारिक एनएसएफडीसी दिशानिर्देशों के अनुसार: ₹5,00,000 तक पारिवारिक आय वाले अनुसूचित जाति के उद्यमी इस योजना वर्ग के लिए पात्र हैं (रियायती ब्याज दर एवं मोराटोरियम लाभ)।"
                            } else {
                                "Based on official NSFDC guidelines: Marginalized SC beneficiary with income ≤ ₹5,00,000 qualifies for this scheme bracket with subsidized interest rates and moratorium benefits."
                            }
                        } else {
                            if (isHindi) {
                                "आधिकारिक एनएसएफडीसी दिशानिर्देशों के अनुसार: आवेदन योजना पात्रता से बाहर है (पारिवारिक आय ₹5,00,000 से अधिक है या परियोजना लागत अधिकतम सीमा से अधिक है)।"
                            } else {
                                "Based on official NSFDC guidelines: Application falls outside scheme eligibility parameters (either income exceeds ₹5,00,000 or project cost exceeds scheme ceiling)."
                            }
                        }
                        aiGapAdvice = if (isFemaleBeneficiary) {
                            if (isHindi) "महिला उद्यमियों को महिला समृद्धि योजना के तहत 4% रियायती ब्याज दर का विशेष लाभ मिलता है।" else "Female entrepreneurs qualify for a 4.0% concessional interest rate under Mahila Samriddhi Yojana."
                        } else {
                            if (isHindi) "समय पर ऋण पुनर्भुगतान करने पर राज्य चैनल एजेंसियों द्वारा ब्याज में अतिरिक्त छूट प्रोत्साहन मिल सकता है।" else "Prompt repayment makes borrowers eligible for additional interest rebate incentives."
                        }
                        aiDocumentChecklist = defaultChecklist
                    } finally {
                        isAiLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                if (isHindi) "✨ एआई योजना मिलान करें (AI Match)" else "✨ Run AI Scheme Matcher",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Recommendation Result Card
        if (recommendedSchemeName != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEligible) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isEligible) Icons.Default.Info else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isEligible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "सिफ़ारिश परिणाम" else "Recommendation Result",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Prominent AI Match Score Badge
                        if (aiMatchScore != null && isEligible) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "$aiMatchScore% ${if (isHindi) "एआई मैच" else "AI Match"}",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = recommendedSchemeName!!,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isEligible) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) "ℹ️ पात्रता एवं योजना नियम SIH26092 आधिकारिक MoSJE/NSFDC दिशानिर्देशों पर आधारित हैं" else "ℹ️ Eligibility figures based on SIH26092 official MoSJE/NSFDC guidelines",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isEligible) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.75f)
                    )

                    // Multiple Schemes Comparison Section
                    if (matchedSchemes.size > 1) {
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = if (isHindi) "उपलब्ध योजनाओं की तुलना करें (चयन करने के लिए टैप करें):" else "Compare Matched Schemes (Tap to select):",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))

                        matchedSchemes.forEachIndexed { index, scheme ->
                            val isSelected = scheme.scheme_id == matchedSchemeObj?.scheme_id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { matchedSchemeObj = scheme },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${if (isHindi) "विकल्प" else "Option"} ${index + 1}: ${if (isHindi) scheme.name_hi else scheme.name_en}",
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isSelected) {
                                            Spacer(Modifier.width(8.dp))
                                            Surface(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = if (isHindi) "✓ चयनित" else "✓ Selected",
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "${if (isHindi) "ब्याज दर:" else "Interest:"} ${scheme.interest_rate_note ?: "${scheme.interest_rate_min}%"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${if (isHindi) "चैनल पार्टनर:" else "Channel Partner:"} ${scheme.channel_partner_type ?: "N/A"}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "${if (isHindi) "पुनर्भुगतान:" else "Repayment:"} ${scheme.repayment_period ?: "N/A"} | ${if (isHindi) "मोराटोरियम:" else "Moratorium:"} ${scheme.moratorium_months.joinToString("/")} ${if (isHindi) "महीने" else "mo"}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    // Display details from matched Scheme document
                    matchedSchemeObj?.let { scheme ->
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "${if (isHindi) "उद्देश्य:" else "Purpose:"} ${scheme.purpose}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        // Interest Rate Note
                        scheme.interest_rate_note?.let { rateNote ->
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "ℹ️ ${if (isHindi) "ब्याज दर विवरण:" else "Interest Rate:"} $rateNote",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Moratorium
                        if (scheme.moratorium_months.isNotEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "${if (isHindi) "मोराटोरियम अवधि:" else "Moratorium Period:"} ${scheme.moratorium_months.joinToString(" / ")} ${if (isHindi) "महीने" else "months"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Repayment Period
                        scheme.repayment_period?.let { period ->
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "${if (isHindi) "पुनर्भुगतान अवधि:" else "Repayment Period:"} $period",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Channel Partner Type
                        scheme.channel_partner_type?.let { cpType ->
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "${if (isHindi) "लेंडिंग चैनल पार्टनर:" else "Lending Channel Partner:"} $cpType",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Special Rebate
                        scheme.special_rebate?.let { rebate ->
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "🎁 ${if (isHindi) "विशेष लाभ / सब्सिडी:" else "Special Benefit / Subsidy:"} $rebate",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Source Citation
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "📜 ${scheme.source_note}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }

                    // AI Insights & Strategic Fit Card
                    Spacer(Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "💡 एआई विश्लेषण एवं सामरिक लाभ" else "💡 AI Strategic Fit & Guidance",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(Modifier.height(6.dp))

                            if (isAiLoading) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = if (isHindi) "जेमिनी एआई व्यवसाय मिलान और सब्सिडी का विश्लेषण कर रहा है..." else "Gemini AI analyzing business viability and subsidies...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                aiFitReasoning?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                aiGapAdvice?.let {
                                    Spacer(Modifier.height(8.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "🚀 ${if (isHindi) "पात्रता एवं लाभ सलाह:" else "Eligibility Optimization:"} $it",
                                            modifier = Modifier.padding(8.dp),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Interactive Document Checklist Card
                    if (aiDocumentChecklist.isNotEmpty()) {
                        Spacer(Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = if (isHindi) "📋 आवश्यक दस्तावेज़ चेकलिस्ट" else "📋 Required Document Checklist",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = if (isHindi) "आवेदन करने से पहले इन दस्तावेज़ों को तैयार रखें (टिक करें):" else "Check off the documents you have ready before visiting the channel partner:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))

                                aiDocumentChecklist.forEach { doc ->
                                    val isChecked = doc in checkedDocuments
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                checkedDocuments = if (isChecked) checkedDocuments - doc else checkedDocuments + doc
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                checkedDocuments = if (checked) checkedDocuments + doc else checkedDocuments - doc
                                            }
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = doc,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        Button(
            onClick = {
                val schemeId = matchedSchemeObj?.scheme_id ?: ""
                val cost = costStr.toFloatOrNull() ?: 0f
                navController.navigate("calculator?schemeId=$schemeId&cost=$cost")
            },
            modifier = Modifier.fillMaxWidth().height(52.dp).padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isHindi) "ईएमआई कैलकुलेटर पर आगे बढ़ें →" else "Proceed to EMI Calculator →", fontWeight = FontWeight.Bold)
        }
    }
}
