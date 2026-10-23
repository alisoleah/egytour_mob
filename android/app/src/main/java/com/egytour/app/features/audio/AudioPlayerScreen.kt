package com.egytour.app.features.audio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egytour.app.core.theme.EgytourColors
import com.egytour.app.services.AudioService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(
    audioService: AudioService,
    onBack: (() -> Unit)? = null
) {
    val isPlaying by audioService.isPlaying.collectAsState()
    val progress by audioService.progress.collectAsState()
    val currentPositionSeconds by audioService.currentPositionSeconds.collectAsState()
    val durationSeconds by audioService.durationSeconds.collectAsState()
    val currentPOIName by audioService.currentPOIName.collectAsState()
    val currentLanguage by audioService.currentLanguage.collectAsState()
    val currentExpertise by audioService.currentExpertise.collectAsState()
    val playbackSpeed by audioService.playbackSpeed.collectAsState()
    val isOfflineAvailable by audioService.isOfflineAvailable.collectAsState()
    val currentTranscript by audioService.currentTranscript.collectAsState()

    var showTranscript by remember { mutableStateOf(false) }

    fun formatSeconds(sec: Int): String {
        val m = sec / 60
        val s = sec % 60
        return "%02d:%02d".format(m, s)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Audio Tour Guide",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EgytourColors.TextPrimary
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = EgytourColors.TextPrimary
                            )
                        }
                    }
                },
                actions = {
                    // Offline / Online Status Badge
                    Surface(
                        color = if (isOfflineAvailable) EgytourColors.PrimaryGold.opacity(0.15f) else Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                if (isOfflineAvailable) Icons.Default.OfflinePin else Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = if (isOfflineAvailable) EgytourColors.PrimaryGold else EgytourColors.TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (isOfflineAvailable) "Offline Saved" else "Streaming",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isOfflineAvailable) EgytourColors.PrimaryGold else EgytourColors.TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EgytourColors.BgPrimary)
            )
        },
        containerColor = EgytourColors.BgPrimary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Cover Artwork Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                EgytourColors.PrimaryGold.copy(alpha = 0.25f),
                                EgytourColors.BgSecondary
                            )
                        )
                    )
                    .border(1.dp, EgytourColors.Border, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(EgytourColors.PrimaryGold.copy(alpha = 0.2f))
                            .border(1.5.dp, EgytourColors.PrimaryGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Headphones,
                            contentDescription = null,
                            tint = EgytourColors.PrimaryGold,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        currentPOIName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EgytourColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Giza Necropolis • 4th Dynasty",
                        fontSize = 12.sp,
                        color = EgytourColors.PrimaryGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Language Selector Chips (EN, AR, FR)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val languages = listOf("en" to "English 🇬🇧", "ar" to "العربية 🇪🇬", "fr" to "Français 🇫🇷")
                languages.forEach { (code, label) ->
                    val isSelected = currentLanguage == code
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { audioService.setLanguage(code) },
                        color = if (isSelected) EgytourColors.PrimaryGold else EgytourColors.BgSecondary,
                        shape = RoundedCornerShape(12.dp),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, EgytourColors.Border)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else EgytourColors.TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Expertise Tier Segmented Control
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = EgytourColors.BgSecondary,
                border = androidx.compose.foundation.BorderStroke(1.dp, EgytourColors.Border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tiers = listOf(
                        Triple("casual", "Casual", "2 min"),
                        Triple("enthusiast", "Enthusiast", "4 min"),
                        Triple("scholar", "Scholar", "6 min")
                    )
                    tiers.forEach { (key, title, length) ->
                        val isSelected = currentExpertise == key
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { audioService.setExpertise(key) },
                            color = if (isSelected) EgytourColors.PrimaryGold else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) Color.Black else EgytourColors.TextPrimary
                                )
                                Text(
                                    length,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.Black.copy(alpha = 0.7f) else EgytourColors.TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Slider & Timestamps
            Slider(
                value = progress,
                onValueChange = { audioService.seekTo(it) },
                colors = SliderDefaults.colors(
                    thumbColor = EgytourColors.PrimaryGold,
                    activeTrackColor = EgytourColors.PrimaryGold,
                    inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatSeconds(currentPositionSeconds),
                    fontSize = 12.sp,
                    color = EgytourColors.TextSecondary
                )
                Text(
                    formatSeconds(durationSeconds),
                    fontSize = 12.sp,
                    color = EgytourColors.TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playback Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed Toggle Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EgytourColors.BgSecondary,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EgytourColors.Border),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable {
                        audioService.cyclePlaybackSpeed()
                    }
                ) {
                    Text(
                        "${playbackSpeed}x",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EgytourColors.PrimaryGold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                // Rewind 15s
                IconButton(
                    onClick = { audioService.seekRelative(-15) },
                    modifier = Modifier
                        .size(46.dp)
                        .background(EgytourColors.BgSecondary, CircleShape)
                        .border(1.dp, EgytourColors.Border, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Replay10,
                        contentDescription = "Rewind 15s",
                        tint = EgytourColors.TextPrimary
                    )
                }

                // Play / Pause FAB
                FloatingActionButton(
                    onClick = { audioService.togglePlayPause() },
                    containerColor = EgytourColors.PrimaryGold,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Fast Forward 15s
                IconButton(
                    onClick = { audioService.seekRelative(15) },
                    modifier = Modifier
                        .size(46.dp)
                        .background(EgytourColors.BgSecondary, CircleShape)
                        .border(1.dp, EgytourColors.Border, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Forward10,
                        contentDescription = "Forward 15s",
                        tint = EgytourColors.TextPrimary
                    )
                }

                // Transcript Toggle Button
                IconButton(
                    onClick = { showTranscript = !showTranscript },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (showTranscript) EgytourColors.PrimaryGold else EgytourColors.BgSecondary,
                            CircleShape
                        )
                        .border(1.dp, EgytourColors.Border, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = "Transcript",
                        tint = if (showTranscript) Color.Black else EgytourColors.TextPrimary
                    )
                }
            }

            // Expandable Transcript Section
            AnimatedVisibility(
                visible = showTranscript,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                        .border(1.dp, EgytourColors.Border, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = EgytourColors.PrimaryGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Egyptologist Audio Transcript",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EgytourColors.TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentTranscript,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = EgytourColors.TextPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// Extension helper for opacity color
private fun Color.opacity(ratio: Float): Color {
    return this.copy(alpha = ratio)
}
