package com.example.expensetracker.data

data class Transaction(
    val id: Long, // timestamp based id
    val amount: Double,
    val reason: String,
    val mode: String,
    val type: TransactionType, // CREDIT or DEBIT
    val time: Long
)

enum class TransactionType {
    CREDIT, DEBIT
}

data class FinancialSummary(
    val totalCredit: Double,
    val totalDebit: Double,
    val balance: Double,
    val transactionCount: Int
)
