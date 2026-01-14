package com.company.inventaireit.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("inventaire_prefs", Context.MODE_PRIVATE)

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(value) = prefs.edit().putBoolean("vibration_enabled", value).apply()

    var isAutoFlashEnabled: Boolean
        get() = prefs.getBoolean("auto_flash_enabled", false)
        set(value) = prefs.edit().putBoolean("auto_flash_enabled", value).apply()
}