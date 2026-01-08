package com.saurabhkumar.expensetracker.data

data class Transaction(
    val id: Long, // timestamp based id
    val amount: Double,
    val reason: String,
    val mode: String,
    val category: String,
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

data class MonthlyBudget(
    val category: String,
    val amount: Double,
    val month: Int, // 1-12
    val year: Int
)

data class MonthlyBudgetSummary(
    val category: String,
    val budgetAmount: Double,
    val spentAmount: Double,
    val remainingAmount: Double,
    val percentageUsed: Double,
    val month: Int,
    val year: Int
)
