package com.ngatour.fruitclassifier.data.pref

import android.content.Context

class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var name: String
        get() = prefs.getString("name", "") ?: ""
        set(value) = prefs.edit().putString("name", value).apply()

    var email: String
        get() = prefs.getString("email", "") ?: ""
        set(value) = prefs.edit().putString("email", value).apply()
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun saveUser(name: String, email: String) {
        prefs.edit()
            .putString("name", name)
            .putString("email", email)
            .apply()
    }
}