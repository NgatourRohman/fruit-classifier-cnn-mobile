package com.ngatour.fruitclassifier

import android.content.Context

class ThemePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    var isDarkMode: Boolean
        get() = prefs.getBoolean("dark_mode", false)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()
}
