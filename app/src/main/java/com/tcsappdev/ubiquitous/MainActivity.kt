package com.tcsappdev.ubiquitous

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tcsappdev.ubiquitous.ui.navigation.NavGraph
import com.tcsappdev.ubiquitous.ui.theme.UbiquitousTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false)}
            UbiquitousTheme(darkTheme = isDarkTheme){
                NavGraph(
                    modifier = Modifier.fillMaxSize(),
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = {isDarkTheme = !isDarkTheme}
                )
            }
        }
    }
}
