package com.tcsappdev.ubiquitous

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.tcsappdev.ubiquitous.ui.navigation.NavGraph
import com.tcsappdev.ubiquitous.ui.theme.UbiquitousTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UbiquitousTheme{
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
