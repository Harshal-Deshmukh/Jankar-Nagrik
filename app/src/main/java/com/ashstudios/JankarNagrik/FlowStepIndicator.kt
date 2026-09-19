package com.ashstudios.JankarNagrik

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Guided workflow step indicator displayed across the 3 core steps:
 * Step 1: Find Schemes (RecommenderScreen)
 * Step 2: EMI Calculator (CalculatorScreen)
 * Step 3: Find Partners (MapsScreen)
 */
@Composable
fun FlowStepIndicator(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val isHindi = LocalIsHindi.current
    val stepTitle = when (currentStep) {
        1 -> if (isHindi) "चरण 1 का 3: योजनाएं खोजें" else "Step 1 of 3: Find Schemes"
        2 -> if (isHindi) "चरण 2 का 3: ईएमआई कैलकुलेटर" else "Step 2 of 3: EMI Calculator"
        3 -> if (isHindi) "चरण 3 का 3: पार्टनर खोजें" else "Step 3 of 3: Find Partners"
        else -> ""
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stepTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$currentStep / 3",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 3-segmented visual progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in 1..3) {
                    val isActive = i <= currentStep
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                    )
                }
            }
        }
    }
}
