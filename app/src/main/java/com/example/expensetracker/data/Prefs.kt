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
    
    // Monthly Budget management
    private const val KEY_MONTHLY_BUDGETS = "monthly_budgets_json"
    
    fun setMonthlyBudget(ctx: Context, category: String, amount: Double, month: Int, year: Int) {
        val budgets = getMonthlyBudgets(ctx).toMutableList()
        // Remove existing budget for this category/month/year
        budgets.removeAll { it.category == category && it.month == month && it.year == year }
        // Add new budget
        budgets.add(com.example.expensetracker.data.MonthlyBudget(category, amount, month, year))
        saveMonthlyBudgets(ctx, budgets)
    }
    
    fun getMonthlyBudget(ctx: Context, category: String, month: Int, year: Int): Double {
        return getMonthlyBudgets(ctx)
            .find { it.category == category && it.month == month && it.year == year }
            ?.amount ?: 0.0
    }
    
    fun getMonthlyBudgets(ctx: Context): List<com.example.expensetracker.data.MonthlyBudget> {
        val json = prefs(ctx).getString(KEY_MONTHLY_BUDGETS, "") ?: ""
        if (json.isEmpty()) {
            // Try migrating from old budget format
            return migrateOldBudgets(ctx)
        }
        
        return try {
            json.split(";")
                .filter { it.isNotBlank() }
                .map {
                    val parts = it.split(":")
                    com.example.expensetracker.data.MonthlyBudget(
                        category = parts[0],
                        amount = parts[1].toDouble(),
                        month = parts[2].toInt(),
                        year = parts[3].toInt()
                    )
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getMonthlyBudgetsForMonth(ctx: Context, month: Int, year: Int): List<com.example.expensetracker.data.MonthlyBudget> {
        return getMonthlyBudgets(ctx).filter { it.month == month && it.year == year }
    }
    
    private fun saveMonthlyBudgets(ctx: Context, budgets: List<com.example.expensetracker.data.MonthlyBudget>) {
        val json = budgets.joinToString(";") { "${it.category}:${it.amount}:${it.month}:${it.year}" }
        prefs(ctx).edit().putString(KEY_MONTHLY_BUDGETS, json).apply()
    }
    
    fun removeMonthlyBudget(ctx: Context, category: String, month: Int, year: Int) {
        val budgets = getMonthlyBudgets(ctx).toMutableList()
        budgets.removeAll { it.category == category && it.month == month && it.year == year }
        saveMonthlyBudgets(ctx, budgets)
    }
    
    // Migration from old budget format
    private fun migrateOldBudgets(ctx: Context): List<com.example.expensetracker.data.MonthlyBudget> {
        val oldBudgets = getBudgets(ctx)
        if (oldBudgets.isEmpty()) return emptyList()
        
        val calendar = java.util.Calendar.getInstance()
        val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1
        val currentYear = calendar.get(java.util.Calendar.YEAR)
        
        val monthlyBudgets = oldBudgets.map { (category, amount) ->
            com.example.expensetracker.data.MonthlyBudget(category, amount, currentMonth, currentYear)
        }
        
        // Save migrated budgets
        saveMonthlyBudgets(ctx, monthlyBudgets)
        
        // Clear old budget data
        prefs(ctx).edit().putString(KEY_BUDGETS, "").apply()
        
        return monthlyBudgets
    }
    
    // Legacy budget methods (kept for backward compatibility)
    @Deprecated("Use monthly budgets instead")
    fun setBudget(ctx: Context, category: String, amount: Double) {
        val budgets = getBudgets(ctx).toMutableMap()
        budgets[category] = amount
        saveBudgets(ctx, budgets)
    }
    
    @Deprecated("Use monthly budgets instead")
    fun getBudget(ctx: Context, category: String): Double {
        return getBudgets(ctx)[category] ?: 0.0
    }
    
    @Deprecated("Use monthly budgets instead")
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
    
    @Deprecated("Use monthly budgets instead")
    private fun saveBudgets(ctx: Context, budgets: Map<String, Double>) {
        val json = budgets.entries.joinToString(";") { "${it.key}:${it.value}" }
        prefs(ctx).edit().putString(KEY_BUDGETS, json).apply()
    }
    
    @Deprecated("Use monthly budgets instead")
    fun removeBudget(ctx: Context, category: String) {
        val budgets = getBudgets(ctx).toMutableMap()
        budgets.remove(category)
        saveBudgets(ctx, budgets)
    }
}
