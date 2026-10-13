package com.egytour.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.egytour.app.core.theme.EgytourColors
import com.egytour.app.features.auth.LoginScreen
import com.egytour.app.features.audio.AudioPlayerScreen
import com.egytour.app.services.AudioService
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                    androidx.compose.material3.Scaffold(
                        bottomBar = {
                            androidx.compose.material3.NavigationBar(
                                containerColor = EgytourColors.BgSecondary
                            ) {
                                androidx.compose.material3.NavigationBarItem(
                                    selected = currentScreen == "audio",
                                    onClick = { currentScreen = "audio" },
                                    icon = { androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Default.PlayArrow, "Audio") },
                                    label = { androidx.compose.material3.Text("Audio") }
                                )
                                androidx.compose.material3.NavigationBarItem(
                                    selected = currentScreen == "map",
                                    onClick = { currentScreen = "map" },
                                    icon = { androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Default.LocationOn, "Map") },
                                    label = { androidx.compose.material3.Text("Map") }
                                )
                                androidx.compose.material3.NavigationBarItem(
                                    selected = currentScreen == "ai",
                                    onClick = { currentScreen = "ai" },
                                    icon = { androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Default.Star, "AI Guide") },
                                    label = { androidx.compose.material3.Text("AI Guide") }
                                )
                                androidx.compose.material3.NavigationBarItem(
                                    selected = currentScreen == "trips",
                                    onClick = { currentScreen = "trips" },
                                    icon = { androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Default.DateRange, "Trips") },
                                    label = { androidx.compose.material3.Text("Trips") }
                                )
                                androidx.compose.material3.NavigationBarItem(
                                    selected = currentScreen == "settings",
                                    onClick = { currentScreen = "settings" },
                                    icon = { androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Default.Settings, "Settings") },
                                    label = { androidx.compose.material3.Text("Settings") }
                                )
                            }
                        }
                    ) { padding ->
                        androidx.compose.foundation.layout.Box(androidx.compose.ui.Modifier.padding(padding)) {
                            when (currentScreen) {
                                "audio" -> AudioPlayerScreen(audioService = audioService)
                                "map" -> com.egytour.app.features.explore.ui.ExploreMapScreen(viewModel = exploreViewModel)
                                "ai" -> com.egytour.app.features.ai.ui.ChatScreen(viewModel = chatViewModel)
                                "trips" -> com.egytour.app.features.itinerary.ui.ItineraryScreen(viewModel = itineraryViewModel)
                                "settings" -> com.egytour.app.features.settings.ui.SettingsScreen(onSignOut = { isLoggedIn = false })
                            }
                        }
                    }
                }
            }

        }
    }
}
