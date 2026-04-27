package com.example.myhabits.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandGreen,
    secondary = BrandBlue,
    tertiary = BrandCyan,
    background = BrandDark,
    surface = CardGray,
    onPrimary = BrandDark,
    onSecondary = SoftWhite,
    onTertiary = BrandDark,
    onBackground = SoftWhite,
    onSurface = SoftWhite,
    error = SoftRed
)

@Composable
fun MyHabitsTheme(
    darkTheme: Boolean = true, // Force dark theme for the "Pro" look
    dynamicColor: Boolean = false, // Disable dynamic color to maintain identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Even in light theme, we want our Pro/Dark look
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}