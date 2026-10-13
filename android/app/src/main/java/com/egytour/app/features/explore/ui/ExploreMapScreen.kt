package com.egytour.app.features.explore.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.egytour.app.core.theme.EgytourColors
import com.egytour.app.features.explore.domain.POI
import com.egytour.app.features.explore.viewmodel.ExploreViewModel
import com.egytour.app.features.explore.viewmodel.RouteStep
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun ExploreMapScreen(
    viewModel: ExploreViewModel
) {
    val pois by viewModel.pois.collectAsState()
    val selectedPOI by viewModel.selectedPOI.collectAsState()
    val routeCoordinates by viewModel.routeCoordinates.collectAsState()
    val routeSteps by viewModel.routeSteps.collectAsState()
    val isNavigating by viewModel.isNavigating.collectAsState()
    val currentStepIndex by viewModel.currentStepIndex.collectAsState()
    val totalDistance by viewModel.totalDistanceMeters.collectAsState()
    val estimatedMinutes by viewModel.estimatedMinutes.collectAsState()
    
    var isARVisible by remember { mutableStateOf(false) }

    if (isARVisible && selectedPOI != null) {
        val arViewModel = remember { com.egytour.app.features.ar.viewmodel.ARViewModel() }
        com.egytour.app.features.ar.ui.ARNavigationScreen(
            destination = selectedPOI!!,
            onDismiss = { isARVisible = false },
            viewModel = arViewModel
        )
    } else {
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(29.9770, 31.1345), // Giza Plateau
                14.5f
            )
        }

        LaunchedEffect(selectedPOI) {
            selectedPOI?.let {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(it.coordinates, 15.5f),
                    1000
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Google Map View
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    compassEnabled = true
                )
            ) {
                // POI Markers
                pois.forEach { poi ->
                    Marker(
                        state = rememberMarkerState(key = poi.id, position = poi.coordinates),
                        title = poi.name,
                        snippet = poi.historicalPeriod,
                        onClick = {
                            viewModel.selectPOI(poi)
                            true
                        }
                    )
                }

                // Route Walking Polyline
                if (routeCoordinates.isNotEmpty()) {
                    Polyline(
                        points = routeCoordinates,
                        color = EgytourColors.PrimaryGold,
                        width = 12f
                    )
                }
            }

            // Top Turn-by-Turn Guidance Banner
            AnimatedVisibility(
                visible = isNavigating && routeSteps.isNotEmpty(),
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                if (routeSteps.isNotEmpty()) {
                    TurnByTurnBanner(
                        currentStep = routeSteps[currentStepIndex],
                        stepIndex = currentStepIndex + 1,
                        totalSteps = routeSteps.size,
                        totalDistance = totalDistance,
                        etaMinutes = estimatedMinutes,
                        onNextStep = { viewModel.nextStep() },
                        onExit = { viewModel.exitNavigation() }
                    )
                }
            }

            // Bottom POI Details & Navigation Action Card
            AnimatedVisibility(
                visible = selectedPOI != null && !isNavigating,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                selectedPOI?.let { poi ->
                    POICard(
                        poi = poi,
                        hasRoute = routeCoordinates.isNotEmpty(),
                        distanceMeters = totalDistance,
                        etaMinutes = estimatedMinutes,
                        onDismiss = { viewModel.selectPOI(null) },
                        onCalculateRoute = { viewModel.calculateRoute(poi) },
                        onStartNavigation = { viewModel.startNavigation() },
                        onExplore = { isARVisible = true }
                    )
                }
            }
        }
    }
}

@Composable
fun TurnByTurnBanner(
    currentStep: RouteStep,
    stepIndex: Int,
    totalSteps: Int,
    totalDistance: Int,
    etaMinutes: Int,
    onNextStep: () -> Unit,
    onExit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = EgytourColors.PrimaryGold,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "In ${currentStep.distanceMeters}m",
                        color = EgytourColors.PrimaryGold,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentStep.instruction,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(onClick = onExit) {
                    Icon(Icons.Default.Close, contentDescription = "Exit Navigation", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.White.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$etaMinutes min ($totalDistance m remaining)",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Step $stepIndex/$totalSteps",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (stepIndex < totalSteps) {
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onNextStep) {
                            Text("Next >", color = EgytourColors.PrimaryGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun POICard(
    poi: POI,
    hasRoute: Boolean,
    distanceMeters: Int,
    etaMinutes: Int,
    onDismiss: () -> Unit,
    onCalculateRoute: () -> Unit,
    onStartNavigation: () -> Unit,
    onExplore: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = poi.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = EgytourColors.PrimaryGold,
                        fontWeight = FontWeight.Bold
                    )
                    poi.historicalPeriod?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = poi.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            if (hasRoute) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = EgytourColors.PrimaryGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$distanceMeters m walking (~$etaMinutes min)",
                        color = EgytourColors.PrimaryGold,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!hasRoute) {
                    Button(
                        onClick = onCalculateRoute,
                        colors = ButtonDefaults.buttonColors(containerColor = EgytourColors.PrimaryGold),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Directions")
                    }
                } else {
                    Button(
                        onClick = onStartNavigation,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Start Walk")
                    }
                }

                OutlinedButton(
                    onClick = onExplore,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EgytourColors.PrimaryGold)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AR View")
                }
            }
        }
    }
}

