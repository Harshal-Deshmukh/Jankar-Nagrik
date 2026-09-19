package com.ashstudios.JankarNagrik

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ashstudios.JankarNagrik.data.SchemesRepository
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun CalculatorScreen(
    navController: NavHostController,
    initialSchemeId: String = "",
    initialCost: Float = 0f
) {
    val isHindi = LocalIsHindi.current

    // Lookup matched scheme if initialSchemeId was passed from RecommenderScreen
    val matchedScheme = remember(initialSchemeId) {
        if (initialSchemeId.isNotBlank()) {
            SchemesRepository.VERIFIED_DEFAULT_SCHEMES.find { it.scheme_id == initialSchemeId }
        } else null
    }

    // Auto-populate loan amount: min(initialCost * 0.9, scheme.max_amount * 0.9)
    val initialLoanAmount = if (matchedScheme != null && initialCost > 0f) {
        min(initialCost * 0.9, (matchedScheme.max_amount ?: 5000000.0) * 0.9).toFloat()
    } else {
        100000f
    }

    val initialInterestRate = matchedScheme?.interest_rate_min?.toFloat() ?: 6.5f

    val initialTenureYears = when (matchedScheme?.scheme_id) {
        "micro_finance", "mahila_samriddhi", "aajeevika_microfinance" -> 3f
        "udyam_nidhi" -> 5f
        "term_loan" -> 7f
        "educational_loan" -> 10f
        else -> 5f
    }

    val initialMoratoriumMonths = matchedScheme?.moratorium_months?.firstOrNull()?.toFloat() ?: 6f

    // Slider state variables (fully interactive)
    var loanAmount by remember(initialSchemeId, initialCost) {
        mutableFloatStateOf(initialLoanAmount.coerceIn(10000f, 5000000f))
    }
    var interestRate by remember(initialSchemeId) {
        mutableFloatStateOf(initialInterestRate.coerceIn(4.0f, 18.0f))
    }
    var tenureYears by remember(initialSchemeId) {
        mutableFloatStateOf(initialTenureYears.coerceIn(1f, 15f))
    }
    var moratoriumMonths by remember(initialSchemeId) {
        mutableFloatStateOf(initialMoratoriumMonths.coerceIn(0f, 24f))
    }

    // Pre-calculate initial EMI if values were auto-populated
    var emi by remember(initialSchemeId, initialCost) {
        val p = initialLoanAmount.coerceIn(10000f, 5000000f).toDouble()
        val r = (initialInterestRate.toDouble() / 12) / 100
        val n = (initialTenureYears * 12) - initialMoratoriumMonths
        val calculatedEmi = if (n > 0) {
            (p * r * (1 + r).pow(n.toDouble())) / ((1 + r).pow(n.toDouble()) - 1)
        } else 0.0
        mutableDoubleStateOf(if (matchedScheme != null) calculatedEmi else 0.0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step Indicator: Step 2 of 3
        FlowStepIndicator(currentStep = 2)

        // Contextual Banner when pre-filled from recommendation
        if (matchedScheme != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) {
                            "✓ ${matchedScheme.name_hi} के अनुसार पैरामीटर स्वतः भरे गए"
                        } else {
                            "✓ Pre-filled with parameters from ${matchedScheme.name_en}"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // Loan Amount Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHindi) "ऋण राशि:" else "Loan Amount:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "₹${loanAmount.roundToInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = loanAmount,
                onValueChange = { loanAmount = it },
                valueRange = 10000f..5000000f,
                steps = 499
            )
        }

        // Interest Rate Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHindi) "ब्याज दर:" else "Interest Rate:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${"%.1f".format(interestRate)}% p.a.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = interestRate,
                onValueChange = { interestRate = it },
                valueRange = 4.0f..18.0f,
                steps = 140
            )
        }

        // Tenure Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHindi) "पुनर्भुगतान अवधि:" else "Repayment Tenure:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${tenureYears.roundToInt()} ${if (isHindi) "वर्ष" else "Years"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = tenureYears,
                onValueChange = { tenureYears = it },
                valueRange = 1f..15f,
                steps = 14
            )
        }

        // Moratorium Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHindi) "मोराटोरियम अवधि:" else "Moratorium Period:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${moratoriumMonths.roundToInt()} ${if (isHindi) "महीने" else "Months"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = moratoriumMonths,
                onValueChange = { moratoriumMonths = it },
                valueRange = 0f..24f,
                steps = 24
            )
        }

        // Calculate Button
        Button(
            onClick = {
                val p = loanAmount.toDouble()
                val r = (interestRate.toDouble() / 12) / 100
                val n = (tenureYears * 12) - moratoriumMonths

                if (n > 0) {
                    val emiValue = (p * r * (1 + r).pow(n.toDouble())) / ((1 + r).pow(n.toDouble()) - 1)
                    emi = emiValue
                } else {
                    emi = 0.0
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                if (isHindi) "ईएमआई की गणना करें" else "Calculate EMI",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // EMI Result Card
        if (emi > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isHindi) "अनुमानित ईएमआई" else "Estimated Monthly EMI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "₹${emi.roundToInt()} ${if (isHindi) "/ माह" else "/ month"}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) "नोट: मोराटोरियम अवधि के दौरान ब्याज को पूंजीकृत किया जा सकता है।" else "Note: Interest during the moratorium period may be capitalized.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        // Step 3 Navigation
        Button(
            onClick = { navController.navigate("maps") },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                if (isHindi) "पार्टनर लोकेटर पर आगे बढ़ें →" else "Proceed to Partner Locator →",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
