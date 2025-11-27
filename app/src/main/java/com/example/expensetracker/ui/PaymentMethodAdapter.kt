package com.example.expensetracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ItemPaymentMethodBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PaymentMethodAdapter(
    private val paymentMethods: MutableList<String>,
    private val onEdit: (Int, String) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder>() {

    inner class PaymentMethodViewHolder(private val binding: ItemPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(paymentMethod: String, position: Int) {
            binding.txtPaymentMethodName.text = paymentMethod

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
                                // Show confirmation dialog for delete
                                MaterialAlertDialogBuilder(view.context)
                                    .setTitle("Delete Payment Method")
                                    .setMessage("Are you sure you want to delete '$paymentMethod'?")
                                    .setPositiveButton("Delete") { _, _ ->
                                        onDelete(position)
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .show()
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
        val binding = ItemPaymentMethodBinding.inflate(
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

    fun addPaymentMethod(method: String) {
        paymentMethods.add(method)
        notifyItemInserted(paymentMethods.size - 1)
    }

    fun editPaymentMethod(position: Int, newMethod: String) {
        paymentMethods[position] = newMethod
        notifyItemChanged(position)
    }

    fun removePaymentMethod(position: Int) {
        paymentMethods.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, paymentMethods.size)
    }
}
