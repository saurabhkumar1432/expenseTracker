package com.example.expensetracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ItemPaymentMethodModernBinding

class PaymentMethodModernAdapter(
    private val paymentMethods: MutableList<String>,
    private val onEdit: (Int, String) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<PaymentMethodModernAdapter.PaymentMethodViewHolder>() {

    // Map payment method names to appropriate icons
    private fun getIconForPaymentMethod(method: String): Int {
        return when (method.lowercase()) {
            "cash" -> R.drawable.ic_money
            "upi", "phonepe", "google pay", "paytm", "gpay" -> R.drawable.ic_smartphone
            "credit card", "debit card" -> R.drawable.ic_credit_card
            "net banking", "bank" -> R.drawable.ic_account_balance
            else -> R.drawable.ic_payment
        }
    }

    inner class PaymentMethodViewHolder(private val binding: ItemPaymentMethodModernBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(paymentMethod: String, position: Int) {
            binding.txtPaymentMethodName.text = paymentMethod

            // Set appropriate icon based on payment method type
            val iconRes = getIconForPaymentMethod(paymentMethod)
            binding.imgPaymentMethodIcon.setImageResource(iconRes)

            // More options button click - shows popup menu
            binding.btnMoreOptions.setOnClickListener { view ->
                android.widget.PopupMenu(view.context, view).apply {
                    menuInflater.inflate(R.menu.payment_method_options, menu)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.action_edit -> {
                                onEdit(position, paymentMethod)
                                true
                            }
                            R.id.action_delete -> {
                                onDelete(position)
                                true
                            }
                            else -> false
                        }
                    }
                    show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val binding = ItemPaymentMethodModernBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentMethodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.bind(paymentMethods[position], position)
    }

    override fun getItemCount(): Int = paymentMethods.size
}
