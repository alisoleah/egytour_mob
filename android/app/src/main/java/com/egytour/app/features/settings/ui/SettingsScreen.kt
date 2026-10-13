package com.egytour.app.features.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egytour.app.core.theme.EgytourColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onSignOut: () -> Unit = {}) {
    var selectedLanguage by remember { mutableStateOf("en") }
    var expertiseLevel by remember { mutableStateOf("enthusiast") }
    var autoPlayAudio by remember { mutableStateOf(true) }
    var downloadWifiOnly by remember { mutableStateOf(true) }
    var hapticFeedback by remember { mutableStateOf(true) }
    var proximityAlerts by remember { mutableStateOf(true) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var cacheCleared by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        color = EgytourColors.TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EgytourColors.BgSecondary
                )
            )
        },
        containerColor = EgytourColors.BgPrimary
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Profile Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(EgytourColors.PrimaryGold.copy(alpha = 0.18f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = EgytourColors.PrimaryGold,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Archaeology Explorer",
                                color = EgytourColors.TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "explorer@egytour.app",
                                color = EgytourColors.TextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        EgytourColors.PrimaryGold.copy(alpha = 0.15f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "VIP PASS • ALL SITES UNLOCKED",
                                    color = EgytourColors.PrimaryGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            // Content Preferences
            item {
                SettingsSectionHeader("CONTENT & NARRATION")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Narration Language",
                            color = EgytourColors.TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("en" to "English", "ar" to "العربية", "fr" to "Français").forEach { (code, name) ->
                                val selected = selectedLanguage == code
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) EgytourColors.PrimaryGold else EgytourColors.BgPrimary)
                                        .clickable { selectedLanguage = code }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = name,
                                        color = if (selected) Color.Black else EgytourColors.TextSecondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = EgytourColors.Border)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Scholar Depth",
                            color = EgytourColors.TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("casual" to "Casual", "enthusiast" to "Enthusiast", "scholar" to "Scholar").forEach { (level, label) ->
                                val selected = expertiseLevel == level
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) EgytourColors.PrimaryGold else EgytourColors.BgPrimary)
                                        .clickable { expertiseLevel = level }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (selected) Color.Black else EgytourColors.TextSecondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Audio & AR
            item {
                SettingsSectionHeader("AUDIO & AR WAYFINDING")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsToggleRow("Auto-Play Geo-Audio", autoPlayAudio) { autoPlayAudio = it }
                        Divider(color = EgytourColors.Border, modifier = Modifier.padding(vertical = 8.dp))
                        SettingsToggleRow("Download On Wi-Fi Only", downloadWifiOnly) { downloadWifiOnly = it }
                        Divider(color = EgytourColors.Border, modifier = Modifier.padding(vertical = 8.dp))
                        SettingsToggleRow("Haptic Compass Guidance", hapticFeedback) { hapticFeedback = it }
                        Divider(color = EgytourColors.Border, modifier = Modifier.padding(vertical = 8.dp))
                        SettingsToggleRow("Proximity Monument Alerts", proximityAlerts) { proximityAlerts = it }
                    }
                }
            }

            // Offline Storage
            item {
                SettingsSectionHeader("OFFLINE STORAGE")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Offline Tours & Maps",
                                color = EgytourColors.TextPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (cacheCleared) "0 MB" else "142.8 MB",
                                color = EgytourColors.TextSecondary,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showClearCacheDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A1818)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Clear Offline Cache", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // About & Version
            item {
                SettingsSectionHeader("ABOUT EGYTOUR")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Version", color = EgytourColors.TextPrimary, fontSize = 14.sp)
                            Text("1.0.0 (Pharaoh Release)", color = EgytourColors.TextSecondary, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Archaeology Data", color = EgytourColors.TextPrimary, fontSize = 14.sp)
                            Text("SCA & Min. of Antiquities", color = EgytourColors.PrimaryGold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Sign Out
            item {
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showClearCacheDialog) {
            AlertDialog(
                onDismissRequest = { showClearCacheDialog = false },
                title = { Text("Clear Offline Storage?", color = EgytourColors.TextPrimary) },
                text = { Text("This will remove downloaded audio tours and map tiles. They will be re-downloaded when viewed.", color = EgytourColors.TextSecondary) },
                confirmButton = {
                    TextButton(onClick = {
                        cacheCleared = true
                        showClearCacheDialog = false
                    }) {
                        Text("Clear", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearCacheDialog = false }) {
                        Text("Cancel", color = EgytourColors.TextSecondary)
                    }
                },
                containerColor = EgytourColors.BgSecondary
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = EgytourColors.PrimaryGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@Composable
fun SettingsToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = EgytourColors.TextPrimary,
            fontSize = 14.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = EgytourColors.PrimaryGold,
                uncheckedThumbColor = EgytourColors.TextSecondary,
                uncheckedTrackColor = EgytourColors.BgPrimary
            )
        )
    }
}
