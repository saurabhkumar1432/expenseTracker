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
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_BUDGETS = "budgets_json"
    
    // Theme modes
    const val THEME_SYSTEM = 0
    const val THEME_LIGHT = 1
    const val THEME_DARK = 2

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
    
    // Theme preferences
    fun getThemeMode(ctx: Context): Int = 
        prefs(ctx).getInt(KEY_THEME_MODE, THEME_SYSTEM)
    
    fun setThemeMode(ctx: Context, mode: Int) {
        prefs(ctx).edit().putInt(KEY_THEME_MODE, mode).apply()
    }
    
    // Budget management
    fun setBudget(ctx: Context, category: String, amount: Double) {
        val budgets = getBudgets(ctx).toMutableMap()
        budgets[category] = amount
        saveBudgets(ctx, budgets)
    }
    
    fun getBudget(ctx: Context, category: String): Double {
        return getBudgets(ctx)[category] ?: 0.0
    }
    
    fun getBudgets(ctx: Context): Map<String, Double> {
        val json = prefs(ctx).getString(KEY_BUDGETS, "") ?: ""
        if (json.isEmpty()) return emptyMap()
        
        return try {
            json.split(";")
                .filter { it.isNotBlank() }
                .associate {
                    val parts = it.split(":")
                    parts[0] to parts[1].toDouble()
                }
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    private fun saveBudgets(ctx: Context, budgets: Map<String, Double>) {
        val json = budgets.entries.joinToString(";") { "${it.key}:${it.value}" }
        prefs(ctx).edit().putString(KEY_BUDGETS, json).apply()
    }
    
    fun removeBudget(ctx: Context, category: String) {
        val budgets = getBudgets(ctx).toMutableMap()
        budgets.remove(category)
        saveBudgets(ctx, budgets)
    }
}
