package com.example.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.data.Prefs
import com.example.expensetracker.data.TransactionStore
import com.example.expensetracker.data.TransactionType
import com.example.expensetracker.databinding.ActivityBudgetManagementBinding
import com.example.expensetracker.databinding.DialogBudgetManagementBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.util.Calendar

class BudgetManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBudgetManagementBinding
    private lateinit var adapter: BudgetAdapter
    private val budgetItems = mutableListOf<BudgetItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadBudgets()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = BudgetAdapter(
            budgetItems = budgetItems,
            onEditBudget = { item -> showBudgetDialog(item.category, item.budgetAmount) }
        )
        binding.recyclerViewBudgets.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewBudgets.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.fabAddBudget.setOnClickListener {
            showCategorySelectionDialog()
        }
    }

    private fun loadBudgets() {
        val categories = Prefs.getCategories(this)
        val budgets = Prefs.getBudgets(this)
        val transactions = TransactionStore.getTransactions(this)

        // Calculate start of month
        val startOfMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        budgetItems.clear()
        categories.forEach { category ->
            val budget = budgets[category] ?: 0.0
            if (budget > 0) {
                // Calculate spent amount for this category this month
                val spent = transactions
                    .filter { it.category == category && it.type == TransactionType.DEBIT && it.time >= startOfMonth }
                    .sumOf { it.amount }

                budgetItems.add(BudgetItem(category, budget, spent))
            }
        }

        adapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (budgetItems.isEmpty()) {
            binding.emptyStateContainer.visibility = View.VISIBLE
            binding.recyclerViewBudgets.visibility = View.GONE
        } else {
            binding.emptyStateContainer.visibility = View.GONE
            binding.recyclerViewBudgets.visibility = View.VISIBLE
        }
    }

    private fun showCategorySelectionDialog() {
        val categories = Prefs.getCategories(this)
        val budgets = Prefs.getBudgets(this)
        
        // Filter out categories that already have budgets
        val availableCategories = categories.filter { budgets[it] == null || budgets[it] == 0.0 }
        
        if (availableCategories.isEmpty()) {
            Toast.makeText(this, "All categories already have budgets", Toast.LENGTH_SHORT).show()
            return
        }

        // Inflate custom dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_category_selection, null)
        val chipGroup: com.google.android.material.chip.ChipGroup = dialogView.findViewById(R.id.chipGroupCategories)
        val btnSelect: com.google.android.material.button.MaterialButton = dialogView.findViewById(R.id.btnSelectCategory)
        val btnCancel: com.google.android.material.button.MaterialButton = dialogView.findViewById(R.id.btnCancelCategory)
        
        var selectedCategory: String? = null
        
        // Add category chips with colors
        availableCategories.forEach { category ->
            val chip = com.google.android.material.chip.Chip(this).apply {
                text = category
                isCheckable = true
                chipIcon = getDrawable(R.drawable.ic_description)
                
                // Apply category color
                val color = com.example.expensetracker.ui.CategoryUtils.getColorForCategory(this@BudgetManagementActivity, category)
                chipBackgroundColor = android.content.res.ColorStateList.valueOf(color)
                
                // Set text color based on background luminance
                val luminance = (android.graphics.Color.red(color) * 0.299 + 
                                android.graphics.Color.green(color) * 0.587 + 
                                android.graphics.Color.blue(color) * 0.114)
                setTextColor(if (luminance < 150) android.graphics.Color.WHITE else android.graphics.Color.DKGRAY)
                
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedCategory = category
                        btnSelect.isEnabled = true
                    }
                }
            }
            chipGroup.addView(chip)
        }
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()
        
        btnSelect.setOnClickListener {
            selectedCategory?.let { category ->
                dialog.dismiss()
                showBudgetDialog(category, 0.0)
            }
        }
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun showBudgetDialog(category: String, currentBudget: Double) {
        val dialogBinding = DialogBudgetManagementBinding.inflate(layoutInflater)
        
        // Get current month spending for this category
        val startOfMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val spent = TransactionStore.getTransactions(this)
            .filter { it.category == category && it.type == TransactionType.DEBIT && it.time >= startOfMonth }
            .sumOf { it.amount }

        // Setup dialog
        dialogBinding.txtBudgetCategory.text = "Category: $category"
        dialogBinding.txtCurrentSpending.text = "₹${spent.toInt()}"
        if (currentBudget > 0) {
            dialogBinding.editBudgetAmount.setText(currentBudget.toInt().toString())
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnSaveBudget.setOnClickListener {
            val amount = dialogBinding.editBudgetAmount.text.toString().toDoubleOrNull()
            if (amount != null && amount > 0) {
                Prefs.setBudget(this, category, amount)
                Toast.makeText(this, "Budget set: ₹${amount.toInt()} for $category", Toast.LENGTH_SHORT).show()
                loadBudgets()
                dialog.dismiss()
            } else {
                dialogBinding.editBudgetAmount.error = "Enter valid amount"
            }
        }

        dialogBinding.btnCancelBudget.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnRemoveBudget.setOnClickListener {
            Prefs.removeBudget(this, category)
            Toast.makeText(this, "Budget removed", Toast.LENGTH_SHORT).show()
            loadBudgets()
            dialog.dismiss()
        }

        dialog.show()
        dialogBinding.editBudgetAmount.requestFocus()
    }

    data class BudgetItem(
        val category: String,
        val budgetAmount: Double,
        val spentAmount: Double
    )

    class BudgetAdapter(
        private val budgetItems: List<BudgetItem>,
        private val onEditBudget: (BudgetItem) -> Unit
    ) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_budget, parent, false)
            return BudgetViewHolder(view)
        }

        override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
            holder.bind(budgetItems[position], onEditBudget)
        }

        override fun getItemCount() = budgetItems.size

        class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val txtCategory: TextView = itemView.findViewById(R.id.txtBudgetCategory)
            private val txtBudgetAmount: TextView = itemView.findViewById(R.id.txtBudgetAmount)
            private val txtSpentAmount: TextView = itemView.findViewById(R.id.txtSpentAmount)
            private val txtRemainingAmount: TextView = itemView.findViewById(R.id.txtRemainingAmount)
            private val progressBudget: LinearProgressIndicator = itemView.findViewById(R.id.progressBudget)
            private val txtBudgetStatus: TextView = itemView.findViewById(R.id.txtBudgetStatus)

            fun bind(item: BudgetItem, onEdit: (BudgetItem) -> Unit) {
                txtCategory.text = item.category
                txtBudgetAmount.text = "₹${item.budgetAmount.toInt()}"
                txtSpentAmount.text = "Spent: ₹${item.spentAmount.toInt()}"

                val remaining = item.budgetAmount - item.spentAmount
                txtRemainingAmount.text = if (remaining >= 0) {
                    "₹${remaining.toInt()} left"
                } else {
                    "₹${(-remaining).toInt()} over"
                }

                val percentage = ((item.spentAmount / item.budgetAmount) * 100).toInt().coerceIn(0, 100)
                progressBudget.progress = percentage

                // Change color based on budget usage
                val context = itemView.context
                when {
                    percentage >= 100 -> {
                        progressBudget.setIndicatorColor(context.getColor(R.color.debit_red))
                        txtBudgetStatus.text = "Budget exceeded!"
                        txtBudgetStatus.setTextColor(context.getColor(R.color.debit_red))
                    }
                    percentage >= 80 -> {
                        progressBudget.setIndicatorColor(context.getColor(R.color.md_theme_light_error))
                        txtBudgetStatus.text = "$percentage% of budget used"
                        txtBudgetStatus.setTextColor(context.getColor(R.color.md_theme_light_error))
                    }
                    else -> {
                        txtBudgetStatus.text = "$percentage% of budget used"
                    }
                }

                itemView.setOnClickListener { onEdit(item) }
            }
        }
    }
}
