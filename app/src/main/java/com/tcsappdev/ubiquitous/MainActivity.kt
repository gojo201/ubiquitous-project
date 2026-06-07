package com.tcsappdev.ubiquitous

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.tcsappdev.ubiquitous.ui.navigation.NavGraph
import com.tcsappdev.ubiquitous.ui.theme.UbiquitousTheme
import androidx.core.content.edit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                android.util.Log.d("FCM", "Token: ${task.result}")
            }
        }

        setContent {
            val sharedPreferences = getSharedPreferences("ubiquitous_settings", Context.MODE_PRIVATE)

            setContent {
                val systemTheme = isSystemInDarkTheme()
                var isDarkTheme by remember {
                    mutableStateOf(sharedPreferences.getBoolean("dark_theme", systemTheme))
                }

                UbiquitousTheme(darkTheme = isDarkTheme) {
                    NavGraph(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = {
                            isDarkTheme = !isDarkTheme
                            sharedPreferences.edit { putBoolean("dark_theme", isDarkTheme) }
                        }
                    )
                }
            }
        }
    }
}
