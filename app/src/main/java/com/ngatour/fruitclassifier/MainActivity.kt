package com.ngatour.fruitclassifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import com.ngatour.fruitclassifier.data.pref.ThemePreferences
import com.ngatour.fruitclassifier.ui.nav.MainNavigation
import com.ngatour.fruitclassifier.ui.theme.FruitClassifierTheme

val LocalThemePreference = staticCompositionLocalOf<ThemePreferences> {
    error("No ThemePreferences provided")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setTheme(R.style.Theme_FruitClassifier)
        super.onCreate(savedInstanceState)

        val prefs = ThemePreferences(this)

        setContent {
            FruitClassifierTheme {
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
}