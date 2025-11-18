package com.momotracker.util

import com.momotracker.MomoApp

object PremiumPrefs {
    private const val PREFS = "premium_prefs"
    private const val KEY_PREMIUM = "is_premium"

    var isPremium: Boolean
        get() = MomoApp.context.getSharedPreferences(PREFS, 0).getBoolean(KEY_PREMIUM, false)
        set(value) = MomoApp.context.getSharedPreferences(PREFS, 0).edit().putBoolean(KEY_PREMIUM, value).apply()
}
