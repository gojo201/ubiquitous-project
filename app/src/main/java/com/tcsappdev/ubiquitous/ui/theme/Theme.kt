package com.tcsappdev.ubiquitous.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    secondary = LightGreen80,
    tertiary = LightGreen40,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Black,
    onSecondary = Black,
    onBackground = White,
    onSurface = White,
    error = Red
)

private val LightColorScheme = lightColorScheme(
    primary = Green60,
    secondary = LightGreen40,
    tertiary = Green40,
    background = Gray90,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = Gray20,
    onSurface = Gray20,
    error = Red
)

@Composable
fun UbiquitousTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}