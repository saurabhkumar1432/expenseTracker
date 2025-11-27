package com.example.expensetracker.ui

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.expensetracker.R

object CategoryUtils {
    // Map some well-known categories to colors
    fun getColorForCategory(context: Context, category: String): Int {
        return when (category.lowercase()) {
            "food", "groceries" -> ContextCompat.getColor(context, R.color.md_theme_light_primaryContainer)
            "fashion" -> ContextCompat.getColor(context, R.color.md_theme_light_secondaryContainer)
            "transport" -> ContextCompat.getColor(context, R.color.gradient_start)
            "bills" -> ContextCompat.getColor(context, R.color.debit_red_light)
            "unclassified", "uncategorized", "other" -> ContextCompat.getColor(
                context,
                R.color.md_theme_light_onSurfaceVariant
            )
            else -> {
                // Consistently generate a color from the hash for unknown categories
                val colors = listOf(
                    R.color.gradient_start,
                    R.color.gradient_end,
                    R.color.credit_green_light,
                    R.color.debit_red_light,
                    R.color.md_theme_light_secondaryContainer
                )
                val idx = (category.hashCode().absoluteValue() % colors.size)
                ContextCompat.getColor(context, colors[idx])
            }
        }
    }

    private fun Int.absoluteValue(): Int = if (this < 0) -this else this
}
