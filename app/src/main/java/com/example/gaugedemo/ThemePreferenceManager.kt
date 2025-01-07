package com.example.gaugedemo

import android.content.Context
import android.content.SharedPreferences

class ThemePreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_DARK_MODE = "isDarkMode"
    }

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_DARK_MODE, false)
    }

    fun setDarkModeEnabled(isEnabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_DARK_MODE, isEnabled)
        editor.apply()
    }
}