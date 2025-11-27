package com.example.expensetracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.databinding.ItemFilterOptionBinding

data class FilterOption(
    val paymentMethod: String,
    val transactionCount: Int,
    var isSelected: Boolean = false
)

class FilterAdapter(
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    private val filterOptions = mutableListOf<FilterOption>()

    fun setFilterOptions(newOptions: List<FilterOption>) {
        filterOptions.clear()
        filterOptions.addAll(newOptions)
        notifyDataSetChanged()
    }

    fun getSelectedMethods(): List<String> {
        return filterOptions.filter { it.isSelected }.map { it.paymentMethod }
    }

    fun selectAll() {
        filterOptions.forEach { it.isSelected = true }
        notifyDataSetChanged()
        onSelectionChanged()
    }

    fun clearAll() {
        filterOptions.forEach { it.isSelected = false }
        notifyDataSetChanged()
        onSelectionChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = ItemFilterOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(filterOptions[position])
    }

    override fun getItemCount(): Int = filterOptions.size

    inner class FilterViewHolder(private val binding: ItemFilterOptionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(option: FilterOption) {
            binding.txtPaymentMethod.text = option.paymentMethod
            binding.txtTransactionCount.text = "${option.transactionCount} transactions"
            binding.checkbox.isChecked = option.isSelected

            // Handle item click
            binding.root.setOnClickListener {
                option.isSelected = !option.isSelected
                binding.checkbox.isChecked = option.isSelected
                onSelectionChanged()
            }

            // Handle checkbox click
            binding.checkbox.setOnClickListener {
                option.isSelected = binding.checkbox.isChecked
                onSelectionChanged()
            }
        }
    }
}
