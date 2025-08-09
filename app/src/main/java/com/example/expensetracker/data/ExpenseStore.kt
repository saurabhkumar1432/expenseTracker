package com.example.expensetracker.data

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.concurrent.atomic.AtomicLong

/** Ultra-lightweight persistence: plain CSV in internal storage */
object TransactionStore {
    private const val FILE_NAME = "transactions.csv"
    private val idSeed = AtomicLong(System.currentTimeMillis())

    private fun file(ctx: Context): File = File(ctx.filesDir, FILE_NAME)

    fun addTransaction(ctx: Context, amount: Double, reason: String, mode: String, type: TransactionType): Transaction {
        val now = System.currentTimeMillis()
        val transaction = Transaction(idSeed.incrementAndGet(), amount, reason.trim(), mode, type, now)
        writeAppend(ctx, transaction)
        return transaction
    }

    fun getTransactions(ctx: Context): List<Transaction> {
        val f = file(ctx)
        if (!f.exists()) return emptyList()
        val list = mutableListOf<Transaction>()
        BufferedReader(FileReader(f)).use { br ->
            br.lineSequence().forEach { line ->
                // id,amount,reason,mode,type,time
                val parts = line.split('\t')
                if (parts.size == 6) {
                    val id = parts[0].toLongOrNull() ?: return@forEach
                    val amount = parts[1].toDoubleOrNull() ?: return@forEach
                    val reason = parts[2]
                    val mode = parts[3]
                    val type = try { TransactionType.valueOf(parts[4]) } catch (e: Exception) { return@forEach }
                    val time = parts[5].toLongOrNull() ?: return@forEach
                    list += Transaction(id, amount, reason, mode, type, time)
                }
            }
        }
        return list.sortedByDescending { it.time }
    }

    fun getSummary(ctx: Context): FinancialSummary {
        val transactions = getTransactions(ctx)
        val totalCredit = transactions.filter { it.type == TransactionType.CREDIT }.sumOf { it.amount }
        val totalDebit = transactions.filter { it.type == TransactionType.DEBIT }.sumOf { it.amount }
        return FinancialSummary(
            totalCredit = totalCredit,
            totalDebit = totalDebit,
            balance = totalCredit - totalDebit,
            transactionCount = transactions.size
        )
    }

    fun updateTransaction(ctx: Context, oldTransaction: Transaction, newTransaction: Transaction) {
        val transactions = getTransactions(ctx).toMutableList()
        val index = transactions.indexOfFirst { it.id == oldTransaction.id }
        if (index != -1) {
            transactions[index] = newTransaction.copy(id = oldTransaction.id, time = oldTransaction.time)
            rewriteAllTransactions(ctx, transactions)
        }
    }

    fun deleteTransaction(ctx: Context, transaction: Transaction) {
        val transactions = getTransactions(ctx).toMutableList()
        transactions.removeAll { it.id == transaction.id }
        rewriteAllTransactions(ctx, transactions)
    }

    private fun rewriteAllTransactions(ctx: Context, transactions: List<Transaction>) {
        val f = file(ctx)
        FileWriter(f, false).use { fw ->
            transactions.forEach { t ->
                fw.append(
                    listOf(
                        t.id.toString(),
                        t.amount.toString(),
                        t.reason.replace('\t',' '),
                        t.mode.replace('\t',' '),
                        t.type.name,
                        t.time.toString()
                    ).joinToString("\t")
                )
                fw.append('\n')
            }
        }
    }

    private fun writeAppend(ctx: Context, t: Transaction) {
        val f = file(ctx)
        FileWriter(f, true).use { fw ->
            fw.append(
                listOf(
                    t.id.toString(),
                    t.amount.toString(),
                    t.reason.replace('\t',' '),
                    t.mode.replace('\t',' '),
                    t.type.name,
                    t.time.toString()
                ).joinToString("\t")
            )
            fw.append('\n')
        }
    }
}
