package com.egytour.app.features.ar.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egytour.app.core.theme.EgytourColors
import com.egytour.app.features.ar.viewmodel.ARViewModel
import com.egytour.app.features.explore.domain.POI
import kotlin.math.abs

@Composable
fun ARNavigationScreen(
    destination: POI,
    onDismiss: () -> Unit,
    viewModel: ARViewModel
) {
    val distance by viewModel.distanceToDestination.collectAsState()
    val relativeBearing by viewModel.relativeBearing.collectAsState()
    val targetBearing by viewModel.targetBearing.collectAsState()
    val guidanceText by viewModel.guidanceText.collectAsState()
    val isFacing by viewModel.isFacingDestination.collectAsState()

    val animatedRotation by animateFloatAsState(
        targetValue = relativeBearing,
        animationSpec = tween(durationMillis = 300),
        label = "bearingRotation"
    )

    LaunchedEffect(destination) {
        viewModel.updateNavigation(destination)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Placeholder (Simulating AR Viewport)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
        ) {
            Text(
                "AR Camera Viewfinder\n(CameraX / ARCore Surface)",
                color = Color.White.copy(alpha = 0.4f),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Center)
            )

            // Center HUD Rotating Directional Compass Arrow
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .border(
                            width = 2.dp,
                            color = if (isFacing) Color(0xFF4CAF50) else EgytourColors.PrimaryGold.copy(alpha = 0.6f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Direction Arrow",
                        tint = if (isFacing) Color(0xFF4CAF50) else EgytourColors.PrimaryGold,
                        modifier = Modifier
                            .size(52.dp)
                            .rotate(animatedRotation)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${targetBearing.toInt()}° AZIMUTH",
                    color = EgytourColors.PrimaryGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }

        // Top Header & Guidance Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Exit AR", tint = Color.White)
            }

            // Guidance Status Pill
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier.border(
                    width = 1.5.dp,
                    color = if (isFacing) Color(0xFF4CAF50) else EgytourColors.PrimaryGold.copy(alpha = 0.5f),
                    shape = CircleShape
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isFacing) Color(0xFF4CAF50) else EgytourColors.PrimaryGold,
                        modifier = Modifier.size(8.dp)
                    ) {}

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = guidanceText,
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Bottom Destination & Turn Summary Card
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.85f)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(EgytourColors.PrimaryGold.copy(alpha = 0.4f))
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = destination.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = EgytourColors.PrimaryGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${distance.toInt()}m away (~${maxOf(1, (distance / 75).toInt())} min walk)",
                            color = EgytourColors.PrimaryGold,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Bearing angle badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${abs(relativeBearing.toInt())}°",
                            style = MaterialTheme.typography.titleMedium,
                            color = EgytourColors.PrimaryGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (relativeBearing >= 0) "RIGHT" else "LEFT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

