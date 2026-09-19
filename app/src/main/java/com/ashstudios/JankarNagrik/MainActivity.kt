package com.ashstudios.JankarNagrik

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ashstudios.JankarNagrik.ui.theme.SchemesTheme
import kotlinx.coroutines.launch

import androidx.lifecycle.lifecycleScope
import com.ashstudios.JankarNagrik.data.ChannelPartnersRepository
import com.ashstudios.JankarNagrik.data.SchemesRepository

val LocalIsHindi = compositionLocalOf { false }

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Seed verified NSFDC schemes & sample Channel Partners once if collection is empty
        lifecycleScope.launch {
            SchemesRepository.seedSchemesIfEmpty()
            ChannelPartnersRepository.seedChannelPartnersIfEmpty()
        }
        
        val sharedPreferences = getSharedPreferences("SchemesAppPrefs", Context.MODE_PRIVATE)
        val initialRoute = if (sharedPreferences.getBoolean("isLoggedIn", false)) "home" else "login"

        enableEdgeToEdge()
        setContent {
            val systemTheme = androidx.compose.foundation.isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(sharedPreferences.getBoolean("isDarkTheme", systemTheme)) }
            
            SchemesTheme(darkTheme = isDarkTheme) {
                var isHindi by remember { mutableStateOf(false) }
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                CompositionLocalProvider(LocalIsHindi provides isHindi) {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                modifier = Modifier.fillMaxWidth(0.75f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.tertiary
                                                )
                                            )
                                        )
                                        .padding(vertical = 40.dp, horizontal = 24.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .background(androidx.compose.ui.graphics.Color.White, shape = androidx.compose.foundation.shape.CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccountCircle,
                                                contentDescription = "User Profile",
                                                modifier = Modifier.size(64.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = if (isHindi) "उपयोगकर्ता प्रोफ़ाइल" else "User Profile",
                                            fontSize = 22.sp,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                                            color = androidx.compose.ui.graphics.Color.White
                                        )
                                        Text(
                                            text = "user@example.com",
                                            fontSize = 14.sp,
                                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Spacer(modifier = Modifier.height(16.dp))

                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                    label = { Text(if (isDarkTheme) (if (isHindi) "लाइट मोड" else "Light Mode") else (if (isHindi) "डार्क मोड" else "Dark Mode")) },
                                    selected = false,
                                    onClick = {
                                        isDarkTheme = !isDarkTheme
                                        val prefs = getSharedPreferences("SchemesAppPrefs", Context.MODE_PRIVATE)
                                        prefs.edit().putBoolean("isDarkTheme", isDarkTheme).apply()
                                    },
                                    badge = {
                                        Switch(
                                            checked = isDarkTheme,
                                            onCheckedChange = {
                                                isDarkTheme = it
                                                val prefs = getSharedPreferences("SchemesAppPrefs", Context.MODE_PRIVATE)
                                                prefs.edit().putBoolean("isDarkTheme", isDarkTheme).apply()
                                            }
                                        )
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))

                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                    label = { Text(if (isHindi) "Switch to English" else "हिंदी में बदलें") },
                                    selected = false,
                                    onClick = {
                                        isHindi = !isHindi
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))

                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                                    label = { Text(if (isHindi) "लॉग आउट" else "Logout") },
                                    selected = false,
                                    onClick = {
                                        val prefs = getSharedPreferences("SchemesAppPrefs", Context.MODE_PRIVATE)
                                        prefs.edit().putBoolean("isLoggedIn", false).apply()
                                        scope.launch { drawerState.close() }
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    ) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                if (currentRoute == "home") {
                                    CenterAlignedTopAppBar(
                                        title = { Text(if (isHindi) "SIH 2026 प्लेटफॉर्म" else "Schemes", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                        navigationIcon = {
                                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                                            }
                                        },
                                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                            containerColor = androidx.compose.ui.graphics.Color.Transparent
                                        )
                                    )
                                } else if (currentRoute != "home" && currentRoute != "login" && currentRoute != "signup") {
                                    CenterAlignedTopAppBar(
                                        title = { 
                                            val titleText = when {
                                                currentRoute == "recommender" -> if (isHindi) "योजनाएं खोजें" else "Find Schemes"
                                                currentRoute?.startsWith("calculator") == true -> if (isHindi) "कैलक्यूलेटर" else "Calculator"
                                                currentRoute == "maps" -> if (isHindi) "नक्शा" else "Maps"
                                                currentRoute == "chatbot" -> if (isHindi) "सहायक" else "Chatbot"
                                                else -> if (isHindi) "SIH 2026 प्लेटफॉर्म" else "Schemes"
                                            }
                                            Text(titleText, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) 
                                        },
                                        navigationIcon = {
                                            IconButton(onClick = { navController.popBackStack() }) {
                                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                            }
                                        },
                                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                            containerColor = androidx.compose.ui.graphics.Color.Transparent
                                        )
                                    )
                                }
                            }
                        ) { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = initialRoute,
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("login") { LoginScreen(navController) }
                                composable("signup") { SignupScreen(navController) }
                                composable("home") { HomeScreen(navController) }
                                composable("recommender") { RecommenderScreen(navController) }
                                composable(
                                    route = "calculator?schemeId={schemeId}&cost={cost}",
                                    arguments = listOf(
                                        navArgument("schemeId") {
                                            type = NavType.StringType
                                            defaultValue = ""
                                            nullable = true
                                        },
                                        navArgument("cost") {
                                            type = NavType.FloatType
                                            defaultValue = 0f
                                        }
                                    )
                                ) { backStackEntry ->
                                    val schemeId = backStackEntry.arguments?.getString("schemeId") ?: ""
                                    val cost = backStackEntry.arguments?.getFloat("cost") ?: 0f
                                    CalculatorScreen(navController, schemeId, cost)
                                }
                                composable("maps") { MapsScreen(navController) }
                                composable("chatbot") { ChatbotScreen() }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommenderScreenStub() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Recommender Screen")
    }
}

@Composable
fun CalculatorScreenStub() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Calculator Screen")
    }
}

@Composable
fun MapsScreenStub() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Maps Screen")
    }
}