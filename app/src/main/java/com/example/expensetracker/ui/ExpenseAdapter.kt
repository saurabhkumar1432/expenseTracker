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
                    binding.txtAmount.text = "+₹${String.format("%.2f", transaction.amount)}"
                    
                    binding.txtAmount.setTextColor(ContextCompat.getColor(context, R.color.credit_green))
                    binding.txtType.text = "INCOME"
                    binding.txtType.setTextColor(ContextCompat.getColor(context, R.color.credit_green))
                    binding.typeIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.credit_green))
                    binding.iconCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.credit_green_light))
                    binding.iconTransaction.setImageResource(R.drawable.ic_arrow_upward)
                    binding.iconTransaction.setColorFilter(ContextCompat.getColor(context, R.color.credit_green))
                }
                TransactionType.DEBIT -> {
                    binding.txtAmount.text = "-₹${String.format("%.2f", transaction.amount)}"
                    binding.txtAmount.setTextColor(ContextCompat.getColor(context, R.color.debit_red))
                    binding.txtType.text = "EXPENSE"
                    binding.txtType.setTextColor(ContextCompat.getColor(context, R.color.debit_red))
                    binding.typeIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.debit_red))
                    binding.iconCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.debit_red_light))
                    binding.iconTransaction.setImageResource(R.drawable.ic_arrow_downward)
                    binding.iconTransaction.setColorFilter(ContextCompat.getColor(context, R.color.debit_red))
                }
            }
            
            // Set up more options menu
            binding.moreOptionsButton.setOnClickListener { view ->
                showMoreOptionsMenu(view, transaction)
            }
        }
        
        private fun showMoreOptionsMenu(view: android.view.View, transaction: Transaction) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.transaction_options, popup.menu)
            
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        onEditTransaction(transaction)
                        true
                    }
                    R.id.action_delete -> {
                        showDeleteConfirmation(view.context, transaction)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
        
        private fun showDeleteConfirmation(context: android.content.Context, transaction: Transaction) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this ${if (transaction.type == TransactionType.CREDIT) "income" else "expense"} of ₹${String.format("%.2f", transaction.amount)}?\n\nThis action cannot be undone.")
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
    }
}
