package com.egytour.app.features.audio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.egytour.app.services.AudioService

@Composable
fun AudioPlayerScreen(
    audioService: AudioService, // In real app, inject ViewModel
    poiName: String = "Great Pyramid of Khufu"
) {
    val isPlaying by audioService.isPlaying.collectAsState()
    val progress by audioService.progress.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Placeholder Cover Art
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text("Cover Art", color = Color.Gray)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = poiName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = "Audio Guide",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Progress
        Slider(
            value = progress,
            onValueChange = { audioService.seekTo(it) },
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFE6A800), // Pharaoh's Gold
                activeTrackColor = Color(0xFFE6A800)
            )
        )
        
        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { audioService.seekTo((progress - 0.1f).coerceAtLeast(0f)) }) {
                Icon(Icons.Default.Replay10, "Rewind 10s", modifier = Modifier.size(32.dp))
            }
            
            FloatingActionButton(
                onClick = {
                    if (isPlaying) audioService.pause() else audioService.play()
                },
                containerColor = Color(0xFFE6A800), // Pharaoh's Gold
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(32.dp)
                )
            }
            
            IconButton(onClick = { audioService.seekTo((progress + 0.1f).coerceAtMost(1f)) }) {
                Icon(Icons.Default.Forward10, "Forward 10s", modifier = Modifier.size(32.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}
