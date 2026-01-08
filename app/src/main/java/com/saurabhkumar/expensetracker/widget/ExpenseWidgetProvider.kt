package com.saurabhkumar.expensetracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.saurabhkumar.expensetracker.MainActivity
import com.saurabhkumar.expensetracker.R
import com.saurabhkumar.expensetracker.data.TransactionStore
import com.saurabhkumar.expensetracker.data.TransactionType
import java.util.Calendar

class ExpenseWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Get transaction data
            val transactions = TransactionStore.getTransactions(context)

            // Calculate today's expenses
            val calendar = Calendar.getInstance()
            val startOfDay = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val todayExpenses = transactions
                .filter { it.type == TransactionType.DEBIT && it.time >= startOfDay }
                .sumOf { it.amount }

            // Calculate this week's expenses
            val startOfWeek = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val weekExpenses = transactions
                .filter { it.type == TransactionType.DEBIT && it.time >= startOfWeek }
                .sumOf { it.amount }

            // Calculate this month's expenses
            val startOfMonth = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val monthExpenses = transactions
                .filter { it.type == TransactionType.DEBIT && it.time >= startOfMonth }
                .sumOf { it.amount }

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_expense_tracker)

            // Update widget values
            views.setTextViewText(R.id.txtTodayAmount, "₹${todayExpenses.toInt()}")
            views.setTextViewText(R.id.txtWeekAmount, "₹${weekExpenses.toInt()}")
            views.setTextViewText(R.id.txtMonthAmount, "₹${monthExpenses.toInt()}")

            // Set up click listeners
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.txtTodayAmount, pendingIntent)
            views.setOnClickPendingIntent(R.id.btnQuickAdd, pendingIntent)

            // Refresh button
            val refreshIntent = Intent(context, ExpenseWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btnRefresh, refreshPendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
