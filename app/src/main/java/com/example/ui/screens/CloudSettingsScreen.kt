package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.viewmodel.VideoStudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudSettingsScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val cloudUrl = viewModel.cloudApiUrl.value
    var inputUrl by remember { mutableStateOf(cloudUrl) }
    var edgeSimulatorEnabled by remember { mutableStateOf(false) }

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
                    text = "CLOUD ARCHITECTURE & SETTINGS",
                    color = Color(0xFFF6AD55),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Enterprise backend, rendering workers, and security",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Cloud Telemetry Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("CLOUD STATUS", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Black)

                    CloudRow(label = "Render Backend", value = "Google Cloud Run (Python 3.11 / FFmpeg)", statusColor = Color(0xFF10B981))
                    CloudRow(label = "Cloud Storage", value = "GCS: gs://cinecraft-cloud-assets", statusColor = Color(0xFF10B981))
                    CloudRow(label = "Multi-Agent Queue", value = "Celery / Redis Asynchronous Workers", statusColor = Color(0xFF10B981))
                    CloudRow(label = "API Security", value = "Server-Side Vault (Zero frontend keys)", statusColor = Color(0xFF38BDF8))
                    CloudRow(label = "Container Engine", value = "Docker • Auto-Scaling 0-10 Instances", statusColor = Color(0xFF10B981))
                }
            }
        }

        // Custom Backend Endpoint
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("API ENDPOINT CONFIGURATION", color = Color(0xFFF6AD55), fontSize = 11.sp, fontWeight = FontWeight.Black)

                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        label = { Text("Cloud API Base URL") },
                        modifier = Modifier.fillMaxWidth().testTag("input_cloud_url"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF6AD55),
                            unfocusedBorderColor = Color(0xFF26334D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Button(
                        onClick = { viewModel.updateCloudApiUrl(inputUrl) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF6AD55)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Endpoint", color = Color(0xFF090D16), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // User Account & Session
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("AUTHENTICATED PRODUCER ACCOUNT", color = Color(0xFF818CF8), fontSize = 11.sp, fontWeight = FontWeight.Black)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("kg0150773@gmail.com", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Plan: Enterprise Studio Tier • Unlimited 4K Renders", color = Color(0xFF34D399), fontSize = 10.sp)
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E283F)) {
                            Text("PRO TIER", color = Color(0xFFF6AD55), fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CloudRow(
    label: String,
    value: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color(0xFFF8FAFC), fontSize = 11.sp)
        }
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(statusColor))
    }
}
