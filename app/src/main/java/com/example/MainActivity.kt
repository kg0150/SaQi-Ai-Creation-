package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.OmniPlayApp
import com.example.ui.theme.OmniObsidian
import com.example.ui.theme.OmniPlayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmniPlayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = OmniObsidian
                ) {
                    OmniPlayApp()
                }
            }
        }
    }
}
