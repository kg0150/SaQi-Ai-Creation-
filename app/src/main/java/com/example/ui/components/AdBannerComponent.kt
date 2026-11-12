package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AdCampaign
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * High-Impact, Policy-Compliant Home Screen Sponsor Banner
 * Features clear "Sponsored" identification, rotation, and high-CTR CTA button.
 */
@Composable
fun HomeAdBanner(
    campaigns: List<AdCampaign>,
    onImpression: (String) -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (campaigns.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentAd = campaigns[currentIndex.coerceIn(0, campaigns.size - 1)]

    // Auto-rotation
    LaunchedEffect(currentAd.id) {
        onImpression(currentAd.id)
        delay(currentAd.rotationIntervalSec * 1000L)
        currentIndex = (currentIndex + 1) % campaigns.size
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_ad_banner")
            .clickable { onClick(currentAd.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniCyan.copy(alpha = 0.4f))))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header: "Sponsored" Tag & Brand
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = OmniCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OmniCyan.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "SPONSORED",
                            color = OmniCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = currentAd.brandName,
                        color = OmniTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "${currentIndex + 1}/${campaigns.size}",
                    color = OmniTextMuted,
                    fontSize = 10.sp
                )
            }

            // Body: Thumbnail + Headline + CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentAd.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = currentAd.imageUrl,
                        contentDescription = "Sponsored visual",
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(OmniSurfaceCard),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = currentAd.headline,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentAd.bodyText,
                        color = OmniTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = { onClick(currentAd.id) },
                    modifier = Modifier.testTag("ad_cta_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OmniCyan,
                        contentColor = OmniObsidian
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = currentAd.callToAction,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

/**
 * Non-Intrusive Video Player Sponsor Panel
 * Compliant: Never interrupts video playback, never covers controls, dismissible.
 */
@Composable
fun VideoPlayerAdPanel(
    campaign: AdCampaign?,
    onImpression: (String) -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (campaign == null) return
    var dismissed by remember { mutableStateOf(false) }
    if (dismissed) return

    LaunchedEffect(campaign.id) {
        onImpression(campaign.id)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_ad_panel")
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(campaign.id) },
        color = OmniSurfaceDark.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniViolet.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = OmniViolet.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "AD",
                        color = OmniViolet,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                Column {
                    Text(
                        text = campaign.headline,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${campaign.brandName} • Tap to view",
                        color = OmniTextSecondary,
                        fontSize = 9.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { onClick(campaign.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = OmniViolet),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(text = campaign.callToAction, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = { dismissed = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss ad",
                        tint = OmniTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Music Player Audio Sponsor Bar
 * Compliant: Zero audio interruption, positioned cleanly below playback controls.
 */
@Composable
fun MusicPlayerAdBar(
    campaign: AdCampaign?,
    onImpression: (String) -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (campaign == null) return

    LaunchedEffect(campaign.id) {
        onImpression(campaign.id)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("music_ad_bar")
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(campaign.id) },
        color = OmniSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniEmerald.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = OmniEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "SPONSORED",
                        color = OmniEmerald,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "${campaign.brandName}: ${campaign.headline}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = "Visit sponsor",
                tint = OmniCyan,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
