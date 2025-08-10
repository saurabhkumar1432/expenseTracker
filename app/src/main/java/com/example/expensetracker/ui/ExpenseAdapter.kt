package com.example.expensetracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.Transaction
import com.example.expensetracker.data.TransactionType
import com.example.expensetracker.databinding.ItemExpenseBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class TransactionAdapter(
    private val onEditTransaction: (Transaction) -> Unit = {},
    private val onDeleteTransaction: (Transaction) -> Unit = {}
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    private val transactions = mutableListOf<Transaction>()
    private val timeFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    fun setTransactions(newTransactions: List<Transaction>) {
        transactions.clear()
        transactions.addAll(newTransactions)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
        
        // Add smooth animation
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, android.R.anim.slide_in_left)
        animation.duration = 300
        animation.startOffset = (position * 50).toLong()
        holder.itemView.startAnimation(animation)
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(private val binding: ItemExpenseBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(transaction: Transaction) {
            val context = binding.root.context
            
            // Set basic transaction details
            binding.txtReason.text = if (transaction.reason.isBlank()) "No description" else transaction.reason
            binding.txtMode.text = transaction.mode
            binding.txtTime.text = getTimeAgo(transaction.time)
            
            // Configure based on transaction type using theme attributes
            when (transaction.type) {
                TransactionType.CREDIT -> {
                    // Use smart currency formatting from MainActivity
                    binding.txtAmount.text = "+${formatCurrency(transaction.amount)}"
                    binding.txtAmount.setTextColor(ContextCompat.getColor(context, R.color.credit_green))
                    binding.iconBackground.backgroundTintList = ContextCompat.getColorStateList(context, R.color.credit_green_light)
                    binding.iconTransaction.setImageResource(R.drawable.ic_arrow_upward)
                    binding.iconTransaction.setColorFilter(ContextCompat.getColor(context, R.color.credit_green))
                }
                TransactionType.DEBIT -> {
                    binding.txtAmount.text = "-${formatCurrency(transaction.amount)}"
                    binding.txtAmount.setTextColor(ContextCompat.getColor(context, R.color.debit_red))
                    binding.iconBackground.backgroundTintList = ContextCompat.getColorStateList(context, R.color.debit_red_light)
                    binding.iconTransaction.setImageResource(R.drawable.ic_arrow_downward)
                    binding.iconTransaction.setColorFilter(ContextCompat.getColor(context, R.color.debit_red))
                }
            }
            
            // Set up item click for editing (removed more options button)
            binding.root.setOnClickListener {
                onEditTransaction(transaction)
            }
            
            // Set up long click for delete
            binding.root.setOnLongClickListener {
                showDeleteConfirmation(binding.root.context, transaction)
                true
            }
        }
        
        private fun showDeleteConfirmation(context: android.content.Context, transaction: Transaction) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this ${if (transaction.type == TransactionType.CREDIT) "income" else "expense"} of ${formatCurrency(transaction.amount)}?\n\nThis action cannot be undone.")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Delete") { _, _ ->
                    onDeleteTransaction(transaction)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        
        private fun getTimeAgo(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            
            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
                diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} min ago"
                diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)} hours ago"
                diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)} days ago"
                else -> timeFormat.format(Date(timestamp))
            }
        }
        
        private fun formatCurrency(amount: Double): String {
            return when {
                amount >= 10000000 -> "₹%.1fCr".format(amount / 10000000)
                amount >= 100000 -> "₹%.1fL".format(amount / 100000)
                amount >= 1000 -> "₹%.1fK".format(amount / 1000)
                else -> "₹%.0f".format(amount)
            }
        }
    }
}
