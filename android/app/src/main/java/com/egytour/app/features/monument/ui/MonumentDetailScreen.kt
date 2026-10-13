package com.egytour.app.features.monument.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egytour.app.R
import com.egytour.app.core.theme.EgytourColors

data class SubMonumentItem(
    val id: String,
    val title: String,
    val era: String,
    val audioDuration: String,
    val imageResId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonumentDetailScreen(
    title: String = "Giza Pyramid Complex",
    arabicTitle: String = "مجمع أهرامات الجيزة",
    region: String = "Giza Plateau, Greater Cairo",
    description: String = "The Giza pyramid complex is an archaeological site on the Giza Plateau. It includes the three Great Pyramids (Khufu, Khafre, Menkaure), the Great Sphinx, several cemeteries, and a workers' village. Constructed over 4,500 years ago during the Fourth Dynasty of the Old Kingdom.",
    isUnlocked: Boolean = false,
    onBack: () -> Unit = {},
    onPlayAudio: () -> Unit = {},
    onStartARNavigation: () -> Unit = {},
    onUnlockZone: () -> Unit = {}
) {
    val subItems = remember {
        listOf(
            SubMonumentItem("1", "Great Pyramid of Khufu", "4th Dynasty (2560 BCE)", "12 min", R.drawable.great_pyramid),
            SubMonumentItem("2", "The Great Sphinx", "Pharaoh Khafre", "8 min", R.drawable.pharaoh_golden_statue),
            SubMonumentItem("3", "Khafre Pyramid", "Old Kingdom", "9 min", R.drawable.giza_pyramids_purchase),
            SubMonumentItem("4", "Khan el-Khalili Gateway", "Mamluk Era", "15 min", R.drawable.khan_el_khalili_market),
            SubMonumentItem("5", "Luxor Colonnade", "New Kingdom", "14 min", R.drawable.luxor_temple)
        )
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = EgytourColors.BgPrimary,
                border = androidx.compose.foundation.BorderStroke(1.dp, EgytourColors.Border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isUnlocked) {
                        Button(
                            onClick = onPlayAudio,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EgytourColors.PrimaryGold)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play Audio Guide", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onStartARNavigation,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EgytourColors.Border)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = EgytourColors.PrimaryGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AR Wayfind", fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Button(
                            onClick = onUnlockZone,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EgytourColors.PrimaryGold)
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Unlock Zone Pass (EGP 450)", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = EgytourColors.BgPrimary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Header Image with Scrim
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.giza_pyramids_purchase),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top & Bottom gradient scrims
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.7f),
                                    Color.Transparent,
                                    EgytourColors.BgPrimary
                                )
                            )
                        )
                )

                // Top navigation bar controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Download Pack", tint = Color.White)
                        }
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    }
                }

                // Tags pinned to bottom of hero
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = EgytourColors.PrimaryGold,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "UNESCO HERITAGE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "4th Dynasty (c. 2560 BCE)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Body Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title & Subtitle
                Column {
                    Text(
                        title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = EgytourColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        arabicTitle,
                        fontSize = 16.sp,
                        color = EgytourColors.PrimaryGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = EgytourColors.TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            region,
                            fontSize = 13.sp,
                            color = EgytourColors.TextSecondary
                        )
                    }
                }

                // Stat Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatPill("Hours", "08:00 - 17:00", Icons.Default.AccessTime, Modifier.weight(1f))
                    StatPill("Entry Fee", "EGP 200 / 60", Icons.Default.ConfirmationNumber, Modifier.weight(1f))
                    StatPill("Audios", "45 Guides", Icons.Default.Headphones, Modifier.weight(1f))
                }

                // Audio Guide Preview Banner
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EgytourColors.Border, RoundedCornerShape(14.dp))
                        .clickable { onPlayAudio() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(EgytourColors.PrimaryGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Official Archaeological Narration",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EgytourColors.TextPrimary
                            )
                            Text(
                                "Curated by Egypt Antiquities Authority (3 Depths)",
                                fontSize = 12.sp,
                                color = EgytourColors.TextSecondary
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EgytourColors.TextSecondary)
                    }
                }

                // Description
                Column {
                    Text(
                        "Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EgytourColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        description,
                        fontSize = 14.sp,
                        color = EgytourColors.TextSecondary,
                        lineHeight = 22.sp
                    )
                }

                // Key POIs in this Zone (Horizontal Carousel)
                Column {
                    Text(
                        "Points of Interest Inside",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EgytourColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(subItems) { item ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary),
                                modifier = Modifier
                                    .width(180.dp)
                                    .border(1.dp, EgytourColors.Border, RoundedCornerShape(12.dp))
                            ) {
                                Column {
                                    Image(
                                        painter = painterResource(id = item.imageResId),
                                        contentDescription = item.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                    )
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            item.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EgytourColors.TextPrimary,
                                            maxLines = 1
                                        )
                                        Text(
                                            item.era,
                                            fontSize = 11.sp,
                                            color = EgytourColors.TextTertiary,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Headphones,
                                                contentDescription = null,
                                                tint = EgytourColors.PrimaryGold,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                item.audioDuration,
                                                fontSize = 11.sp,
                                                color = EgytourColors.PrimaryGold,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        color = EgytourColors.BgSecondary,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EgytourColors.Border),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = EgytourColors.PrimaryGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 10.sp, color = EgytourColors.TextTertiary)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EgytourColors.TextPrimary)
        }
    }
}
