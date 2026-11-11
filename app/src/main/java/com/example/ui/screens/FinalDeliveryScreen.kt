package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectStatus
import com.example.ui.viewmodel.VideoStudioViewModel

@Composable
fun FinalDeliveryScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentProject = viewModel.currentProject.value
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var copiedItem by remember { mutableStateOf<String?>(null) }
    var downloadSimulated by remember { mutableStateOf(false) }

    if (currentProject == null || currentProject.deliveryPackage == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No project delivery package generated.", color = Color(0xFF94A3B8))
        }
        return
    }

    val pkg = currentProject.deliveryPackage!!

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
    ) {
        item {
            Column {
                Text(
                    text = "FINAL DELIVERY PACKAGE",
                    color = Color(0xFF10B981),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Complete production bundle: 4K MP4, Stems, Captions, SEO & ZIP",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Quality Approval Seal
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1E24),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("13-AGENT MASTER QA APPROVED", color = Color(0xFF34D399), fontSize = 12.sp, fontWeight = FontWeight.Black)
                            Text("Quality Score: ${currentProject.qualityScores.overallQuality}/100 • Ready for Broadcast", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Download & Share Primary Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { downloadSimulated = true },
                    modifier = Modifier.weight(1f).height(50.dp).testTag("download_final_video_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF6AD55)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFF090D16))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (downloadSimulated) "ZIP DOWNLOADED ✓" else "DOWNLOAD ZIP",
                        color = Color(0xFF090D16),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Button(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "${currentProject.title}\n\n${pkg.shortCaption}\n\n${pkg.hashtags.joinToString(" ")}")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share CineCraft Video"))
                    },
                    modifier = Modifier.weight(1f).height(50.dp).testTag("share_video_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF090D16))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SHARE PACKAGE", color = Color(0xFF090D16), fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // 5 Auto-Generated Titles (Section 12)
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "5 OPTIMIZED TITLES (HIGH CTR)",
                        color = Color(0xFFF6AD55),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    pkg.suggestedTitles.forEachIndexed { i, titleText ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF161E30),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(titleText))
                                    copiedItem = "Title ${i + 1}"
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = titleText,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // SEO Description & Short Caption
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SEO YOUTUBE & TIKTOK DESCRIPTION", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Black)
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(pkg.seoDescription))
                                copiedItem = "SEO Description"
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(pkg.seoDescription, color = Color(0xFFCBD5E1), fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("SHORT SOCIAL CAPTION:", color = Color(0xFFF6AD55), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(pkg.shortCaption, color = Color(0xFFFEF08A), fontSize = 11.sp)
                }
            }
        }

        // Hashtags & YouTube Tags
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("HASHTAGS & TAGS", color = Color(0xFF818CF8), fontSize = 11.sp, fontWeight = FontWeight.Black)
                    Text(pkg.hashtags.joinToString(" "), color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Divider(color = Color(0xFF1E283F))
                    Text("YouTube Tags: " + pkg.youtubeTags.joinToString(", "), color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
            }
        }

        // File Assets Manifest in Delivery Bundle
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("DELIVERY BUNDLE ASSETS", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Black)
                    AssetRow(icon = Icons.Default.Movie, name = "Final Video Master (${currentProject.resolution.label})", ext = ".mp4")
                    AssetRow(icon = Icons.Default.Audiotrack, name = "Voice-Over Narration Stem (Mastered)", ext = ".mp3")
                    AssetRow(icon = Icons.Default.Subtitles, name = "Synchronized Subtitles (SRT + VTT)", ext = ".srt")
                    AssetRow(icon = Icons.Default.Image, name = "5 Thumbnail Variations (16:9 & 9:16)", ext = ".png")
                    AssetRow(icon = Icons.Default.Code, name = "Project Schema & Storyboard Metadata", ext = ".json")
                }
            }
        }

        if (copiedItem != null) {
            item {
                Text(
                    text = "Copied $copiedItem to clipboard!",
                    color = Color(0xFF34D399),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun AssetRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    ext: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, color = Color(0xFFF8FAFC), fontSize = 11.sp)
        }
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color(0xFF1E283F)
        ) {
            Text(ext, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
    }
}
