package com.example.expensetracker.data

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val PREF_NAME = "expense_prefs"
    private const val KEY_MODES = "modes_csv"
    private const val KEY_CATEGORIES = "categories_csv"
    private const val KEY_ONBOARDED = "onboarded"
    private const val KEY_FILTER_MODES = "filter_modes_csv"
    private const val KEY_FILTER_CATEGORIES = "filter_categories_csv"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isOnboarded(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_ONBOARDED, false)

    fun setOnboarded(ctx: Context) {
        prefs(ctx).edit().putBoolean(KEY_ONBOARDED, true).apply()
    }

    fun getModes(ctx: Context): List<String> =
        prefs(ctx).getString(KEY_MODES, "")
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: emptyList()

    fun saveModes(ctx: Context, modes: List<String>) {
        prefs(ctx).edit().putString(KEY_MODES, modes.joinToString(",")).apply()
    }

    // Categories support
    fun getCategories(ctx: Context): List<String> =
        prefs(ctx).getString(KEY_CATEGORIES, "Food,Fashion,Other")
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: listOf("Food", "Fashion", "Other")

    fun saveCategories(ctx: Context, categories: List<String>) {
        prefs(ctx).edit().putString(KEY_CATEGORIES, categories.joinToString(",")).apply()
    }

    // Filter persistence (persist selected filters across sessions)
    fun saveSelectedFilters(ctx: Context, modes: List<String>, categories: List<String>) {
        prefs(ctx).edit()
            .putString(KEY_FILTER_MODES, modes.joinToString(","))
            .putString(KEY_FILTER_CATEGORIES, categories.joinToString(","))
            .apply()
    }

    fun getSelectedPaymentFilters(ctx: Context): List<String> =
        prefs(ctx).getString(KEY_FILTER_MODES, "")
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: emptyList()

    fun getSelectedCategoryFilters(ctx: Context): List<String> =
        prefs(ctx).getString(KEY_FILTER_CATEGORIES, "")
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: emptyList()
}
