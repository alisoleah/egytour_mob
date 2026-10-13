package com.egytour.app.features.itinerary.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.egytour.app.core.theme.EgytourColors
import com.egytour.app.features.itinerary.domain.Itinerary
import com.egytour.app.features.itinerary.domain.ItineraryDay
import com.egytour.app.features.itinerary.domain.ItineraryStop
import com.egytour.app.features.itinerary.viewmodel.ItineraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(viewModel: ItineraryViewModel) {
    val itineraries by viewModel.itineraries.collectAsState()
    val selectedItinerary by viewModel.selectedItinerary.collectAsState()
    val selectedDayNumber by viewModel.selectedDayNumber.collectAsState()
    val showDialog by viewModel.showGeneratorDialog.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedItinerary != null) "Trip Itinerary" else "Trip Plans",
                        fontWeight = FontWeight.Bold,
                        color = EgytourColors.TextPrimary
                    )
                },
                navigationIcon = {
                    if (selectedItinerary != null) {
                        IconButton(onClick = { viewModel.selectItinerary(null) }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = EgytourColors.PrimaryGold
                            )
                        }
                    }
                },
                actions = {
                    if (selectedItinerary == null) {
                        IconButton(onClick = { viewModel.setShowGeneratorDialog(true) }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Generate Itinerary",
                                tint = EgytourColors.PrimaryGold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EgytourColors.BgSecondary
                )
            )
        },
        containerColor = EgytourColors.BgPrimary
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val currentItinerary = selectedItinerary
            if (currentItinerary == null) {
                // List View
                if (itineraries.isEmpty()) {
                    EmptyItineraryView(onGenerateClick = { viewModel.setShowGeneratorDialog(true) })
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(itineraries) { itinerary ->
                            ItineraryCard(
                                itinerary = itinerary,
                                onClick = { viewModel.selectItinerary(itinerary) }
                            )
                        }
                    }
                }
            } else {
                // Detail View
                ItineraryDetailView(
                    itinerary = currentItinerary,
                    selectedDayNumber = selectedDayNumber,
                    onSelectDay = { viewModel.selectDay(it) }
                )
            }

            if (showDialog) {
                ItineraryGeneratorDialog(
                    viewModel = viewModel,
                    isGenerating = isGenerating,
                    onDismiss = { viewModel.setShowGeneratorDialog(false) }
                )
            }
        }
    }
}

@Composable
fun EmptyItineraryView(onGenerateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            tint = EgytourColors.PrimaryGold,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Trip Plans Yet",
            color = EgytourColors.TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Let our AI Egyptologist design an optimal, custom travel route for your stay in Egypt.",
            color = EgytourColors.TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onGenerateClick,
            colors = ButtonDefaults.buttonColors(containerColor = EgytourColors.PrimaryGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate Itinerary", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ItineraryCard(itinerary: Itinerary, onClick: () -> Unit) {
    val totalStops = itinerary.days.sumOf { it.stops.size }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = itinerary.title,
                color = EgytourColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = EgytourColors.PrimaryGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${itinerary.durationDays} Days",
                        color = EgytourColors.PrimaryGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = EgytourColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "$totalStops Stops",
                        color = EgytourColors.TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = EgytourColors.Border)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itinerary.regions.forEach { region ->
                        Box(
                            modifier = Modifier
                                .background(
                                    EgytourColors.PrimaryGold.copy(alpha = 0.18f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = region.uppercase(),
                                color = EgytourColors.PrimaryGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "AI Optimized",
                    color = EgytourColors.TextTertiary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ItineraryDetailView(
    itinerary: Itinerary,
    selectedDayNumber: Int,
    onSelectDay: (Int) -> Unit
) {
    val currentDay = itinerary.days.firstOrNull { it.dayNumber == selectedDayNumber } ?: itinerary.days.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Trip Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = itinerary.title,
                        color = EgytourColors.PrimaryGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "${itinerary.durationDays} Days",
                            color = EgytourColors.TextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${itinerary.days.sumOf { it.stops.size }} Total Stops",
                            color = EgytourColors.TextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        itinerary.regions.forEach { r ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        EgytourColors.PrimaryGold.copy(alpha = 0.18f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    r.uppercase(),
                                    color = EgytourColors.PrimaryGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Day Selector Tabs
        if (itinerary.days.size > 1) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(itinerary.days) { day ->
                        val isSelected = day.dayNumber == selectedDayNumber
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) EgytourColors.PrimaryGold else EgytourColors.BgSecondary)
                                .clickable { onSelectDay(day.dayNumber) }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "DAY ${day.dayNumber}",
                                    color = if (isSelected) Color.Black else EgytourColors.TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = day.region.replaceFirstChar { it.uppercase() },
                                    color = if (isSelected) Color.Black else EgytourColors.TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Current Day Section
        if (currentDay != null) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Day ${currentDay.dayNumber} Theme",
                        color = EgytourColors.PrimaryGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentDay.theme,
                        color = EgytourColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Timeline stops
            itemsIndexed(currentDay.stops) { index, stop ->
                TimelineStopRow(
                    stop = stop,
                    index = index + 1,
                    isLast = index == currentDay.stops.size - 1
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TimelineStopRow(stop: ItineraryStop, index: Int, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Node column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(EgytourColors.PrimaryGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$index",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(80.dp)
                        .background(EgytourColors.PrimaryGold.copy(alpha = 0.35f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Stop Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stop.title,
                        color = EgytourColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .background(
                                EgytourColors.PrimaryGold.copy(alpha = 0.15f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${stop.recommendedDurationMinutes}m",
                            color = EgytourColors.PrimaryGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (stop.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stop.notes,
                        color = EgytourColors.TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ItineraryGeneratorDialog(
    viewModel: ItineraryViewModel,
    isGenerating: Boolean,
    onDismiss: () -> Unit
) {
    val durationDays by viewModel.durationDays.collectAsState()
    val selectedRegions by viewModel.selectedRegions.collectAsState()
    val selectedInterests by viewModel.selectedInterests.collectAsState()

    val availableRegions = listOf(
        "cairo" to "Cairo & Pyramids",
        "luxor" to "Luxor (Thebes)",
        "aswan" to "Aswan & Nubia",
        "alexandria" to "Alexandria & Med"
    )

    val availableInterests = listOf(
        "ancient_history" to "Ancient History",
        "architecture" to "Architecture",
        "archaeology" to "Archaeological Digs",
        "art" to "Pharaonic Art"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = EgytourColors.BgSecondary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Plan Your Journey",
                    color = EgytourColors.PrimaryGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Trip Duration
                Text(
                    text = "Trip Duration: $durationDays Days",
                    color = EgytourColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.setDuration(durationDays - 1) },
                        enabled = durationDays > 1,
                        colors = ButtonDefaults.buttonColors(containerColor = EgytourColors.BgPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("-", fontSize = 18.sp, color = EgytourColors.PrimaryGold)
                    }

                    Text(
                        text = "$durationDays Days",
                        color = EgytourColors.PrimaryGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { viewModel.setDuration(durationDays + 1) },
                        enabled = durationDays < 14,
                        colors = ButtonDefaults.buttonColors(containerColor = EgytourColors.BgPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+", fontSize = 18.sp, color = EgytourColors.PrimaryGold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Regions
                Text(
                    text = "Regions to Explore",
                    color = EgytourColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    availableRegions.forEach { (id, label) ->
                        val selected = selectedRegions.contains(id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) EgytourColors.PrimaryGold.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { viewModel.toggleRegion(id) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, color = EgytourColors.TextPrimary, fontSize = 13.sp)
                            if (selected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EgytourColors.PrimaryGold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interests
                Text(
                    text = "Historical Interests",
                    color = EgytourColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    availableInterests.forEach { (id, label) ->
                        val selected = selectedInterests.contains(id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) EgytourColors.PrimaryGold.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { viewModel.toggleInterest(id) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, color = EgytourColors.TextPrimary, fontSize = 13.sp)
                            if (selected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EgytourColors.PrimaryGold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EgytourColors.TextSecondary)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { viewModel.generateItinerary() },
                        modifier = Modifier.weight(2f),
                        enabled = !isGenerating && selectedRegions.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = EgytourColors.PrimaryGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Crafting...", color = Color.Black, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate AI", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
