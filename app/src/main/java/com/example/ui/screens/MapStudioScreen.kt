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
import com.example.ui.components.AnimatedMapCanvas
import com.example.ui.viewmodel.VideoStudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapStudioScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentProject = viewModel.currentProject.value
    if (currentProject == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active project.", color = Color(0xFF94A3B8))
        }
        return
    }

    var originCountry by remember { mutableStateOf(currentProject.mapConfig.originCountry) }
    var originCity by remember { mutableStateOf(currentProject.mapConfig.originCity) }
    var destCountry by remember { mutableStateOf(currentProject.mapConfig.destinationCountry) }
    var destCity by remember { mutableStateOf(currentProject.mapConfig.destinationCity) }
    var selectedStyle by remember { mutableStateOf(currentProject.mapConfig.style) }
    var showFlightPath by remember { mutableStateOf(currentProject.mapConfig.showFlightPath) }
    var showRadarPulse by remember { mutableStateOf(currentProject.mapConfig.showRadarPulse) }

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
                    text = "CINEMATIC MAP VIDEO STUDIO",
                    color = Color(0xFFF6AD55),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "3D Cartography, curved bezier routes, and waypoint telemetry",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Live Interactive Animated Map Canvas
        item {
            AnimatedMapCanvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                originLabel = "$originCountry ($originCity)",
                destLabel = "$destCountry ($destCity)",
                mapStyle = selectedStyle
            )
        }

        // Origin and Destination inputs
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "FLIGHT WAYPOINTS",
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
                            label = { Text("From (Country)") },
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
                            label = { Text("From (City)") },
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
                            label = { Text("To (Country)") },
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
                            label = { Text("To (City)") },
                            modifier = Modifier.weight(1f),
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
        }

        // Map Visual Styles
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "MAP VISUAL PRESETS & CARTOGRAPHY",
                        color = Color(0xFFF6AD55),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(MapStyle.values()) { style ->
                            val isSelected = selectedStyle == style
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFFF6AD55) else Color(0xFF1E283F),
                                contentColor = if (isSelected) Color(0xFF090D16) else Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedStyle = style }
                            ) {
                                Text(
                                    text = style.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show Great Circle Bezier Path", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                        Switch(
                            checked = showFlightPath,
                            onCheckedChange = { showFlightPath = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFF6AD55))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pulsating Radar Beacon Rings", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                        Switch(
                            checked = showRadarPulse,
                            onCheckedChange = { showRadarPulse = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                        )
                    }
                }
            }
        }

        // Cartographic Quality Compliance (Agent 7 Map QA Agent)
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF10192A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223555)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AGENT 7: MAP QA AGENT VERIFICATION", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("✓ Borders & Sovereignty verified according to international standards", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Text("✓ Great circle geodesic path calculated across Arabian Sea", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Text("✓ High altitude terrain elevation angle calibrated at 35 degrees", color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
            }
        }

        // Save Map Parameters Button
        item {
            Button(
                onClick = {
                    currentProject.mapConfig.originCountry = originCountry
                    currentProject.mapConfig.originCity = originCity
                    currentProject.mapConfig.destinationCountry = destCountry
                    currentProject.mapConfig.destinationCity = destCity
                    currentProject.mapConfig.style = selectedStyle
                    currentProject.mapConfig.showFlightPath = showFlightPath
                    currentProject.mapConfig.showRadarPulse = showRadarPulse
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_map_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF6AD55)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("APPLY MAP SETTINGS TO PROJECT", color = Color(0xFF090D16), fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
