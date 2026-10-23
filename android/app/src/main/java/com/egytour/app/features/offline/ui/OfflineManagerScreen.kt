package com.egytour.app.features.offline.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.egytour.app.features.offline.OfflineSyncManager

enum class PackDownloadState {
    DOWNLOADED,
    DOWNLOADING,
    NOT_DOWNLOADED
}

data class OfflineRegionPack(
    val id: String,
    val title: String,
    val subtitle: String,
    val sizeMb: Int,
    val state: PackDownloadState,
    val progress: Float = 0f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineManagerScreen(
    syncManager: OfflineSyncManager? = null,
    onBack: () -> Unit = {}
) {
    var wifiOnly by remember { mutableStateOf(true) }
    var autoSyncNearby by remember { mutableStateOf(false) }

    val defaultPacks = listOf(
        OfflineRegionPack("1", "Giza Plateau & Pyramids", "All 5 POIs, 45 Audio Guides, 3D AR Assets", 450, PackDownloadState.DOWNLOADED, 1f),
        OfflineRegionPack("2", "Luxor & Karnak Temples", "Great Hypostyle Hall, Sacred Lake, Avenue of Sphinxes", 620, PackDownloadState.NOT_DOWNLOADED, 0f),
        OfflineRegionPack("3", "Valley of the Kings", "Tutankhamun, Ramses VI, Seti I tombs", 380, PackDownloadState.NOT_DOWNLOADED, 0f),
        OfflineRegionPack("4", "Historic Islamic & Coptic Cairo", "Khan el-Khalili, Citadel, Hanging Church", 290, PackDownloadState.NOT_DOWNLOADED, 0f),
        OfflineRegionPack("5", "Aswan & Abu Simbel Temples", "Philae Island, Colossal Ramses II Statues", 410, PackDownloadState.NOT_DOWNLOADED, 0f)
    )

    val packsFromManager by (syncManager?.packs?.collectAsState() ?: remember { mutableStateOf(defaultPacks) })
    var localPacks by remember { mutableStateOf(defaultPacks) }
    val packs = if (syncManager != null) packsFromManager else localPacks

    val storageBreakdown by (syncManager?.storageBreakdown?.collectAsState() ?: remember {
        mutableStateOf(com.egytour.app.features.offline.StorageBreakdown(450, 42000, 85550, 128000))
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Offline Manager",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EgytourColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = EgytourColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EgytourColors.BgPrimary
                )
            )
        },
        containerColor = EgytourColors.BgPrimary
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Storage Meter Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EgytourColors.Border, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "DEVICE STORAGE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EgytourColors.TextTertiary,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                color = EgytourColors.PrimaryGold.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "High-Speed SSD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EgytourColors.PrimaryGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    "EgyTour Used",
                                    fontSize = 12.sp,
                                    color = EgytourColors.TextSecondary
                                )
                                Text(
                                    "${String.format("%.1f", storageBreakdown.appUsedMb / 1024f)} GB",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EgytourColors.PrimaryGold
                                )
                            }
                            Text(
                                "Total: ${storageBreakdown.totalDeviceMb / 1024} GB",
                                fontSize = 12.sp,
                                color = EgytourColors.TextTertiary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Gauge
                        val usedFraction = if (storageBreakdown.totalDeviceMb > 0) {
                            (storageBreakdown.appUsedMb.toFloat() / storageBreakdown.totalDeviceMb.toFloat()).coerceIn(0.02f, 1f)
                        } else 0.05f

                        LinearProgressIndicator(
                            progress = usedFraction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = EgytourColors.PrimaryGold,
                            trackColor = Color.White.copy(alpha = 0.08f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "EgyTour audio & 3D packs use ${String.format("%.1f", usedFraction * 100)}% of storage (${storageBreakdown.freeSpaceMb / 1024} GB free).",
                            fontSize = 11.sp,
                            color = EgytourColors.TextTertiary
                        )
                    }
                }
            }

            // Sync Settings
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EgytourColors.Border, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Download Over Wi-Fi Only",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EgytourColors.TextPrimary
                                )
                                Text(
                                    "Avoid roaming & cellular data charges",
                                    fontSize = 12.sp,
                                    color = EgytourColors.TextSecondary
                                )
                            }
                            Switch(
                                checked = wifiOnly,
                                onCheckedChange = { wifiOnly = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = EgytourColors.PrimaryGold
                                )
                            )
                        }

                        Divider(
                            color = EgytourColors.Border,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Auto-Sync Scheduled Trips",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EgytourColors.TextPrimary
                                )
                                Text(
                                    "Pre-download monuments in your active itinerary",
                                    fontSize = 12.sp,
                                    color = EgytourColors.TextSecondary
                                )
                            }
                            Switch(
                                checked = autoSyncNearby,
                                onCheckedChange = { autoSyncNearby = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = EgytourColors.PrimaryGold
                                )
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Text(
                    "Monument Offline Packs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EgytourColors.TextPrimary
                )
            }

            // List of Offline Packs
            items(packs) { pack ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EgytourColors.Border, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    pack.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EgytourColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    pack.subtitle,
                                    fontSize = 12.sp,
                                    color = EgytourColors.TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${pack.sizeMb} MB",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EgytourColors.PrimaryGold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            when (pack.state) {
                                PackDownloadState.DOWNLOADED -> {
                                    IconButton(
                                        onClick = {
                                            if (syncManager != null) {
                                                syncManager.cancelOrDeletePack(pack.id)
                                            } else {
                                                localPacks = localPacks.map {
                                                    if (it.id == pack.id) it.copy(state = PackDownloadState.NOT_DOWNLOADED) else it
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(Color(0xFF4CAF50).copy(alpha = 0.15f), CircleShape)
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Downloaded (tap to delete)",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                PackDownloadState.DOWNLOADING -> {
                                    IconButton(
                                        onClick = {
                                            if (syncManager != null) {
                                                syncManager.cancelOrDeletePack(pack.id)
                                            } else {
                                                localPacks = localPacks.map {
                                                    if (it.id == pack.id) it.copy(state = PackDownloadState.NOT_DOWNLOADED) else it
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(EgytourColors.PrimaryGold.copy(alpha = 0.2f), CircleShape)
                                    ) {
                                        Icon(
                                            Icons.Default.Pause,
                                            contentDescription = "Downloading",
                                            tint = EgytourColors.PrimaryGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                PackDownloadState.NOT_DOWNLOADED -> {
                                    IconButton(
                                        onClick = {
                                            if (syncManager != null) {
                                                syncManager.startDownload(pack.id)
                                            } else {
                                                localPacks = localPacks.map {
                                                    if (it.id == pack.id) it.copy(state = PackDownloadState.DOWNLOADING, progress = 0.1f) else it
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(Color.White.copy(alpha = 0.08f), CircleShape)
                                    ) {
                                        Icon(
                                            Icons.Default.FileDownload,
                                            contentDescription = "Download Pack",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (pack.state == PackDownloadState.DOWNLOADING) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = pack.progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = EgytourColors.PrimaryGold,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Downloading ${pack.title}... ${(pack.progress * 100).toInt()}%",
                                fontSize = 11.sp,
                                color = EgytourColors.TextTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}
