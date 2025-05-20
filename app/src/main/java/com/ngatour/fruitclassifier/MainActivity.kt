package com.ngatour.fruitclassifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*

val LocalThemePreference = staticCompositionLocalOf<ThemePreferences> {
    error("No ThemePreferences provided")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = ThemePreferences(this)

        setContent {
            var isDarkMode by remember { mutableStateOf(prefs.isDarkMode) }

            CompositionLocalProvider(LocalThemePreference provides prefs) {
                MaterialTheme(
                    colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()
                ) {
                    MainNavigation(isDarkMode = isDarkMode, onThemeToggle = {
                        isDarkMode = !isDarkMode
                        prefs.isDarkMode = isDarkMode
                    })
                }
            }
        }
    }
}