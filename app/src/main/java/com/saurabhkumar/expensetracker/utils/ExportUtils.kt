package com.saurabhkumar.expensetracker.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.saurabhkumar.expensetracker.data.Transaction
import com.saurabhkumar.expensetracker.data.TransactionType
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {

    /**
     * Export transactions to CSV format
     * @param context Android context
     * @param transactions List of transactions to export
     * @return URI of the exported file, or null if failed
     */
    fun exportToCSV(context: Context, transactions: List<Transaction>): Uri? {
        return try {
            val fileName = "ExpenseTracker_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
            val file = File(context.cacheDir, fileName)

            FileWriter(file).use { writer ->
                // Write CSV header
                writer.append("Date,Time,Type,Category,Payment Method,Reason,Amount\n")

                // Write transaction data
                transactions.forEach { transaction ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    val date = Date(transaction.time)

                    writer.append("${dateFormat.format(date)},")
                    writer.append("${timeFormat.format(date)},")
                    writer.append("${transaction.type.name},")
                    writer.append("\"${escapeCsvField(transaction.category)}\",")
                    writer.append("\"${escapeCsvField(transaction.mode)}\",")
                    writer.append("\"${escapeCsvField(transaction.reason)}\",")
                    writer.append("${transaction.amount}\n")
                }
            }

            // Return content URI using FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Escape CSV field to handle commas and quotes
     */
    private fun escapeCsvField(field: String): String {
        return field.replace("\"", "\"\"")
    }

    /**
     * Get a summary text of the export
     */
    fun getExportSummary(transactions: List<Transaction>): String {
        val creditTotal = transactions.filter { it.type == TransactionType.CREDIT }.sumOf { it.amount }
        val debitTotal = transactions.filter { it.type == TransactionType.DEBIT }.sumOf { it.amount }
        val balance = creditTotal - debitTotal

        return """
            Export Summary:
            - Total Transactions: ${transactions.size}
            - Total Income: ₹${String.format("%.2f", creditTotal)}
            - Total Expenses: ₹${String.format("%.2f", debitTotal)}
            - Balance: ₹${String.format("%.2f", balance)}
        """.trimIndent()
    }
}
