package com.egytour.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.egytour.app.core.theme.EgytourColors
import com.egytour.app.features.auth.LoginScreen
import com.egytour.app.features.audio.AudioPlayerScreen
import com.egytour.app.services.AudioService

class MainActivity : ComponentActivity() {
    
    // In a real Hilt setup, this would be injected. 
    // For this manual setup, we'll instantiate it manually in onCreate for the demo.
    lateinit var audioService: AudioService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        audioService = AudioService(this) // Manual injection for demo

        setContent {
            var hasCompletedOnboarding by remember { mutableStateOf(false) }
            var isLoggedIn by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf("audio") } // "audio", "map", "ai", "trips", "settings"

            // Manual VM injection for demo
            val exploreViewModel = remember { com.egytour.app.features.explore.viewmodel.ExploreViewModel() }
            val chatViewModel = remember { com.egytour.app.features.ai.viewmodel.ChatViewModel() }
            val itineraryViewModel = remember { com.egytour.app.features.itinerary.viewmodel.ItineraryViewModel() }
            val syncManager = remember { com.egytour.app.features.offline.OfflineSyncManager(applicationContext) }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = EgytourColors.BgPrimary
            ) {
                if (!hasCompletedOnboarding) {
                    com.egytour.app.features.onboarding.ui.OnboardingScreen(
                        onComplete = { hasCompletedOnboarding = true }
                    )
                } else if (!isLoggedIn) {
                    LoginScreen(onLoginSuccess = { isLoggedIn = true })
                } else {
                    val isFullScreenModal = currentScreen in listOf("checkout", "purchase_success", "monument_detail", "offline_manager")
                    Scaffold(
                        bottomBar = {
                            if (!isFullScreenModal) {
                                NavigationBar(
                                    containerColor = EgytourColors.BgSecondary
                                ) {
                                    NavigationBarItem(
                                        selected = currentScreen == "audio",
                                        onClick = { currentScreen = "audio" },
                                        icon = { Icon(Icons.Default.PlayArrow, "Audio") },
                                        label = { Text("Audio") }
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == "map",
                                        onClick = { currentScreen = "map" },
                                        icon = { Icon(Icons.Default.LocationOn, "Map") },
                                        label = { Text("Map") }
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == "ai",
                                        onClick = { currentScreen = "ai" },
                                        icon = { Icon(Icons.Default.Star, "AI Guide") },
                                        label = { Text("AI Guide") }
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == "trips",
                                        onClick = { currentScreen = "trips" },
                                        icon = { Icon(Icons.Default.DateRange, "Trips") },
                                        label = { Text("Trips") }
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == "settings",
                                        onClick = { currentScreen = "settings" },
                                        icon = { Icon(Icons.Default.Settings, "Settings") },
                                        label = { Text("Settings") }
                                    )
                                }
                            }
                        }
                    ) { padding ->
                        Box(Modifier.padding(if (isFullScreenModal) PaddingValues(0.dp) else padding)) {
                            when (currentScreen) {
                                "audio" -> AudioPlayerScreen(audioService = audioService)
                                "map" -> com.egytour.app.features.explore.ui.ExploreMapScreen(
                                    viewModel = exploreViewModel,
                                    onViewDetails = { currentScreen = "monument_detail" }
                                )
                                "monument_detail" -> com.egytour.app.features.monument.ui.MonumentDetailScreen(
                                    onBack = { currentScreen = "map" },
                                    onUnlockZone = { currentScreen = "checkout" },
                                    onPlayAudio = {
                                        audioService.loadTrack("giza-1", "Great Pyramid of Khufu")
                                        currentScreen = "audio"
                                    },
                                    onStartARNavigation = { currentScreen = "map" }
                                )
                                "checkout" -> com.egytour.app.features.checkout.ui.CheckoutScreen(
                                    onBack = { currentScreen = "monument_detail" },
                                    onPaymentSuccess = { currentScreen = "purchase_success" }
                                )
                                "purchase_success" -> com.egytour.app.features.checkout.ui.PurchaseSuccessScreen(
                                    onClose = { currentScreen = "map" },
                                    onExploreClicked = { currentScreen = "map" },
                                    onDownloadPacksClicked = { currentScreen = "offline_manager" }
                                )
                                "offline_manager" -> com.egytour.app.features.offline.ui.OfflineManagerScreen(
                                    syncManager = syncManager,
                                    onBack = { currentScreen = "settings" }
                                )
                                "ai" -> com.egytour.app.features.ai.ui.ChatScreen(viewModel = chatViewModel)
                                "trips" -> com.egytour.app.features.itinerary.ui.ItineraryScreen(viewModel = itineraryViewModel)
                                "settings" -> com.egytour.app.features.settings.ui.SettingsScreen(
                                    onSignOut = { isLoggedIn = false },
                                    onNavigateToOffline = { currentScreen = "offline_manager" }
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}
