package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MapStyle
import com.example.data.model.Resolution
import com.example.data.model.VideoFormat
import com.example.data.model.VoiceStyle
import com.example.ui.viewmodel.VideoStudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProjectScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("Pakistan to Saudi Arabia: Cinematic Flight") }
    var prompt by remember {
        mutableStateOf("Create a cinematic map video showing Pakistan to Saudi Arabia with 3D flight trajectory, historical commentary, and orchestral soundtrack.")
    }
    var originCountry by remember { mutableStateOf("Pakistan") }
    var originCity by remember { mutableStateOf("Islamabad") }
    var destCountry by remember { mutableStateOf("Saudi Arabia") }
    var destCity by remember { mutableStateOf("Riyadh") }
    var selectedMapStyle by remember { mutableStateOf(MapStyle.CINEMATIC) }
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedVoiceStyle by remember { mutableStateOf(VoiceStyle.CINEMATIC) }
    var selectedFormat by remember { mutableStateOf(VideoFormat.PORTRAIT_9_16) }
    var selectedResolution by remember { mutableStateOf(Resolution.RES_1080P) }

    val presetIdeas = listOf(
        "Pakistan to Saudi Arabia Cinematic Map",
        "Ancient Egypt to Rome Chronicle",
        "Tokyo to London Global Flight Route",
        "The Rise of Artificial Intelligence",
        "Top 10 Deep Ocean Mysteries"
    )

    val supportedLanguages = listOf(
        "English", "Urdu", "Arabic", "Hindi", "Bengali", "Punjabi",
        "Persian", "Turkish", "French", "German", "Spanish", "Portuguese",
        "Russian", "Chinese", "Japanese", "Korean", "Indonesian"
    )

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
                    text = "AI VIDEO CREATOR",
                    color = Color(0xFFF6AD55),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Multi-agent engine transforms your concept into 4K video",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Quick Preset Inspiration Chips
        item {
            Text(
                text = "POPULAR IDEAS & TEMPLATES",
                color = Color(0xFF64748B),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presetIdeas) { idea ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF162035),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF243452)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                prompt = "Create a cinematic video about $idea with dynamic visuals and soundtrack."
                                if (idea.contains("Pakistan")) {
                                    originCountry = "Pakistan"
                                    originCity = "Islamabad"
                                    destCountry = "Saudi Arabia"
                                    destCity = "Riyadh"
                                    title = idea
                                }
                            }
                    ) {
                        Text(
                            text = idea,
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Title and Prompt Input
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "1. PROJECT CONCEPT",
                        color = Color(0xFFF6AD55),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Project Title") },
                        modifier = Modifier.fillMaxWidth().testTag("input_project_title"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF6AD55),
                            unfocusedBorderColor = Color(0xFF26334D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        label = { Text("Detailed Creative Idea / Prompt") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("input_project_prompt"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF6AD55),
                            unfocusedBorderColor = Color(0xFF26334D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }

        // Map Studio Settings Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "2. MAP ANIMATION STUDIO",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = originCountry,
                            onValueChange = { originCountry = it },
                            label = { Text("Origin Country") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF26334D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = originCity,
                            onValueChange = { originCity = it },
                            label = { Text("Origin City") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF26334D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = destCountry,
                            onValueChange = { destCountry = it },
                            label = { Text("Destination Country") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFF6AD55),
                                unfocusedBorderColor = Color(0xFF26334D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = destCity,
                            onValueChange = { destCity = it },
                            label = { Text("Destination City") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFF6AD55),
                                unfocusedBorderColor = Color(0xFF26334D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Text("Map Visual Style:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(MapStyle.values()) { style ->
                            val isSelected = selectedMapStyle == style
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFFF6AD55) else Color(0xFF1A2236),
                                contentColor = if (isSelected) Color(0xFF090D16) else Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedMapStyle = style }
                            ) {
                                Text(
                                    text = style.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Language & Voice Settings
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "3. MULTILINGUAL VOICE ENGINE",
                        color = Color(0xFF818CF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text("Language (25+ Supported):", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(supportedLanguages) { lang ->
                            val isSelected = selectedLanguage == lang
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF818CF8) else Color(0xFF1A2236),
                                contentColor = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedLanguage = lang }
                            ) {
                                Text(
                                    text = lang,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text("Voice Profile & Emotion:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(VoiceStyle.values()) { vStyle ->
                            val isSelected = selectedVoiceStyle == vStyle
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF818CF8) else Color(0xFF1A2236),
                                contentColor = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedVoiceStyle = vStyle }
                            ) {
                                Text(
                                    text = vStyle.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Format & Resolution
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "4. FORMAT & EXPORT QUALITY",
                        color = Color(0xFF34D399),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VideoFormat.values().forEach { fmt ->
                            val isSelected = selectedFormat == fmt
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF10B981) else Color(0xFF1A2236),
                                contentColor = if (isSelected) Color(0xFF090D16) else Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedFormat = fmt }
                            ) {
                                Text(
                                    text = fmt.badge,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Resolution.values().forEach { res ->
                            val isSelected = selectedResolution == res
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF10B981) else Color(0xFF1A2236),
                                contentColor = if (isSelected) Color(0xFF090D16) else Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedResolution = res }
                            ) {
                                Text(
                                    text = res.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Big Primary Action Button: Launch 13-Agent Production
        item {
            Button(
                onClick = {
                    viewModel.createProject(
                        title = title,
                        prompt = prompt,
                        originCountry = originCountry,
                        originCity = originCity,
                        destCountry = destCountry,
                        destCity = destCity,
                        mapStyle = selectedMapStyle,
                        language = selectedLanguage,
                        voiceStyle = selectedVoiceStyle,
                        format = selectedFormat,
                        resolution = selectedResolution
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("launch_pipeline_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF6AD55)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color(0xFF090D16))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "START PRODUCTION (13 AI AGENTS)",
                    color = Color(0xFF090D16),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}
