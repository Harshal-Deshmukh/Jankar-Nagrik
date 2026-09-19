package com.ashstudios.JankarNagrik

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.ashstudios.JankarNagrik.data.ChannelPartner
import com.ashstudios.JankarNagrik.data.ChannelPartnersRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch

/**
 * Opens turn-by-turn navigation in the official Google Maps app using the partner's verified real address,
 * falling back to browser-based Google Maps if the app is not installed.
 */
fun openGoogleMapsDirections(context: Context, partner: ChannelPartner) {
    val encodedAddress = Uri.encode("${partner.name}, ${partner.address}")
    val gmmIntentUri = Uri.parse("google.navigation:q=$encodedAddress")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        setPackage("com.google.android.apps.maps")
    }
    try {
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val browserUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$encodedAddress")
            context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
        }
    } catch (e: Exception) {
        val browserUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$encodedAddress")
        context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
    }
}

/**
 * Type badge colored pill:
 * - PSB: Blue tint background, dark blue text
 * - SCA: Purple tint background, dark purple text
 * - RRB: Teal tint background, dark teal text
 * - NBFC-MFI: Orange/coral tint background, dark orange/coral text
 */
@Composable
fun PartnerTypeBadge(type: String) {
    val (backgroundColor, textColor) = when (type.uppercase().trim()) {
        "PSB" -> Pair(Color(0xFFDBEAFE), Color(0xFF1D4ED8)) // Blue
        "SCA" -> Pair(Color(0xFFF3E8FF), Color(0xFF6B21A8)) // Purple
        "RRB" -> Pair(Color(0xFFCCFBF1), Color(0xFF0F766E)) // Teal
        "NBFC-MFI", "NBFC" -> Pair(Color(0xFFFFEDD5), Color(0xFFC2410C)) // Coral/Orange
        "COOPERATIVE BANK" -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309)) // Amber
        "COOPERATIVE SOCIETY" -> Pair(Color(0xFFE0E7FF), Color(0xFF4338CA)) // Indigo
        "OTHER AGENCY" -> Pair(Color(0xFFF1F5F9), Color(0xFF475569)) // Slate
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = type,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

/**
 * Status badge:
 * - Healthy: Green background + dark green text ("Available" / "उपलब्ध")
 * - High Risk: Red background + dark red text ("High demand" / "उच्च मांग")
 */
@Composable
fun PartnerStatusBadge(isHighRisk: Boolean, isHindi: Boolean) {
    val backgroundColor = if (isHighRisk) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
    val textColor = if (isHighRisk) Color(0xFFB91C1C) else Color(0xFF15803D)
    val text = if (isHighRisk) {
        if (isHindi) "उच्च मांग" else "High demand"
    } else {
        if (isHindi) "उपलब्ध" else "Available"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(navController: NavHostController) {
    val isHindi = LocalIsHindi.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Default reference coordinates (New Delhi Center)
    val defaultLocation = remember {
        Location("default").apply {
            latitude = 28.6139
            longitude = 77.2090
        }
    }

    var userLocation by remember { mutableStateOf<Location?>(null) }
    var isLocating by remember { mutableStateOf(false) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Repository states
    var partners by remember { mutableStateOf<List<ChannelPartner>>(emptyList()) }
    var isLoadingPartners by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Fetch user location
    fun fetchLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            hasLocationPermission = false
            return
        }

        hasLocationPermission = true
        isLocating = true

        val cancellationToken = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
            .addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    userLocation = loc
                    isLocating = false
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc: Location? ->
                        if (lastLoc != null) {
                            userLocation = lastLoc
                        }
                        isLocating = false
                    }.addOnFailureListener {
                        isLocating = false
                    }
                }
            }
            .addOnFailureListener {
                isLocating = false
            }
    }

    // Permission launcher requested on screen load
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            hasLocationPermission = true
            fetchLocation()
        } else {
            hasLocationPermission = false
        }
    }

    // Load partners from ChannelPartnersRepository
    fun loadPartners() {
        isLoadingPartners = true
        errorMessage = null
        coroutineScope.launch {
            val result = ChannelPartnersRepository.getChannelPartners()
            result.onSuccess { list ->
                partners = list
                isLoadingPartners = false
            }.onFailure { err ->
                errorMessage = err.localizedMessage ?: "Failed to load channel partners"
                isLoadingPartners = false
            }
        }
    }

    // Initial lifecycle effect: Load data and request location permission on load
    LaunchedEffect(Unit) {
        loadPartners()

        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            fetchLocation()
        } else {
            // Automatically prompt for location permission on screen load per spec
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val activeLocation = userLocation ?: defaultLocation
    val sortedPartners = remember(activeLocation, partners) {
        partners.sortedBy { partner ->
            val distResults = FloatArray(1)
            Location.distanceBetween(
                activeLocation.latitude,
                activeLocation.longitude,
                partner.latitude,
                partner.longitude,
                distResults
            )
            distResults[0]
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Guided workflow step indicator: Step 3 of 3
        FlowStepIndicator(
            currentStep = 3,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // DESIGN REFERENCE Header Card: Location pin icon + "X channel partners found"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) {
                            "${sortedPartners.size} चैनल पार्टनर मिले"
                        } else {
                            "${sortedPartners.size} channel partners found"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // If location permission is missing, provide quick grant button
                if (!hasLocationPermission) {
                    TextButton(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isHindi) "स्थान सक्षम करें" else "Enable GPS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // State: Loading
        if (isLoadingPartners) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "चैनल पार्टनर्स खोजे जा रहे हैं..." else "Locating channel partners...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        // State: Error
        else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = errorMessage ?: "Failed to load partners",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { loadPartners() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (isHindi) "पुनः प्रयास करें" else "Retry")
                        }
                    }
                }
            }
        }
        // State: Success (Scrollable list of cards, nearest-first)
        else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedPartners, key = { it.partner_id }) { partner ->
                    // Calculate distance in km
                    val distResults = FloatArray(1)
                    Location.distanceBetween(
                        activeLocation.latitude,
                        activeLocation.longitude,
                        partner.latitude,
                        partner.longitude,
                        distResults
                    )
                    val distKm = distResults[0] / 1000f
                    val isHighRisk = partner.npa_status == "high_risk"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                openGoogleMapsDirections(
                                    context = context,
                                    partner = partner
                                )
                            },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            // DESIGN REFERENCE - Top Row:
                            // Left: Partner name (bold, 15sp) + Type badge pill
                            // Right: Distance ("X.X km") + Status badge ("Available" / "High demand")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f, fill = false),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = partner.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    PartnerTypeBadge(type = partner.type)
                                }

                                Spacer(Modifier.width(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "%.1f km".format(distKm),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    PartnerStatusBadge(isHighRisk = isHighRisk, isHindi = isHindi)
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // DESIGN REFERENCE - Middle Row:
                            // Map-pin icon + full address in secondary/muted text color
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(15.dp)
                                        .padding(top = 2.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = partner.address,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }

                            // High risk warning note (preserving eligibility logic)
                            if (isHighRisk) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = if (isHindi) {
                                        "⚠️ उच्च बकाया / एनपीए - इस मार्ग से ऋण प्रसंस्करण सीमित हो सकता है"
                                    } else {
                                        "⚠️ Elevated NPAs - lending route temporarily restricted"
                                    },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            // DESIGN REFERENCE - Bottom Row:
                            // Navigation icon + "Get directions" text in accent color (tappable action)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        openGoogleMapsDirections(
                                            context = context,
                                            partner = partner
                                        )
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NearMe,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "दिशा-निर्देश प्राप्त करें" else "Get directions",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom CTA Button: Proceed to Contextual Chatbot (unchanged)
        Button(
            onClick = { navController.navigate("chatbot") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = if (isHindi) "सहायक चैटबॉट पर आगे बढ़ें →" else "Proceed to Contextual Chatbot →",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
