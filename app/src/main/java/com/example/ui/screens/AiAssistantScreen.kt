package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@Composable
fun AiAssistantScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.aiChatMessages.collectAsState()
    val isAiProcessing by viewModel.isAiProcessing.collectAsState()
    var promptInput by remember { mutableStateOf("") }

    val quickPrompts = listOf(
        "Summarize active video",
        "Generate Spanish & Arabic subtitles",
        "Extract audio stems (Vocals & Bass)",
        "Categorize media & generate ID3 tags",
        "Transcribe speech with timestamps"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OmniObsidian)
            .padding(16.dp)
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(OmniCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OmniCyan, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text(
                        text = "AI MULTIMEDIA ASSISTANT",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Powered by Google Gemini • Summaries, Subtitles & Audio AI",
                        color = OmniTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Surface(
                color = OmniCyan.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "FREE AI",
                    color = OmniCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        // Quick Action Chips Row
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    color = OmniSurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OmniBorder),
                    modifier = Modifier.clickable {
                        viewModel.sendAiPrompt(prompt)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = OmniCyan, modifier = Modifier.size(12.dp))
                        Text(text = prompt, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Chat Messages Stream
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(chatMessages) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(OmniCyan.copy(alpha = 0.2f))
                                .padding(end = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OmniCyan, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Card(
                        modifier = Modifier.widthIn(max = 300.dp),
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isUser) 14.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 14.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) OmniCyan else OmniSurfaceElevated
                        ),
                        border = if (!isUser) CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniBorder))) else null
                    ) {
                        Text(
                            text = msg.message,
                            color = if (isUser) OmniObsidian else Color.White,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (isAiProcessing) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = OmniCyan, strokeWidth = 2.dp)
                        Text(text = "OmniPlay AI is analyzing multimedia...", color = OmniCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 72.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                modifier = Modifier.weight(1f).testTag("ai_prompt_input"),
                placeholder = { Text("Ask AI to summarize, translate, extract stems...", fontSize = 11.sp, color = OmniTextMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OmniCyan,
                    unfocusedBorderColor = OmniBorder,
                    focusedContainerColor = OmniSurfaceDark,
                    unfocusedContainerColor = OmniSurfaceDark
                ),
                shape = RoundedCornerShape(14.dp)
            )

            IconButton(
                onClick = {
                    if (promptInput.isNotBlank()) {
                        viewModel.sendAiPrompt(promptInput)
                        promptInput = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(OmniCyan, OmniViolet)))
                    .testTag("ai_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = OmniObsidian, modifier = Modifier.size(20.dp))
            }
        }
    }
}
