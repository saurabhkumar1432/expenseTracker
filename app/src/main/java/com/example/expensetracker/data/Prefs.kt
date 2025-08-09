package com.example.expensetracker.data

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val PREF_NAME = "expense_prefs"
    private const val KEY_MODES = "modes_csv"
    private const val KEY_ONBOARDED = "onboarded"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isOnboarded(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_ONBOARDED, false)

    fun setOnboarded(ctx: Context) {
        prefs(ctx).edit().putBoolean(KEY_ONBOARDED, true).apply()
    }

    fun getModes(ctx: Context): List<String> =
        prefs(ctx).getString(KEY_MODES, "")
            ?.split(',')
            ?.filter { it.isNotBlank() }
            ?: emptyList()

    fun saveModes(ctx: Context, modes: List<String>) {
        prefs(ctx).edit().putString(KEY_MODES, modes.joinToString(",")).apply()
    }
}
