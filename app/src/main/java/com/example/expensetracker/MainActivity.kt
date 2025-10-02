package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.data.TransactionStore
import com.example.expensetracker.data.TransactionType
import com.example.expensetracker.data.Transaction
import com.example.expensetracker.data.Prefs
import com.example.expensetracker.databinding.ActivityMainBinding
import com.example.expensetracker.ui.TransactionAdapter
import com.example.expensetracker.ui.FilterAdapter
import com.example.expensetracker.ui.FilterOption
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.button.MaterialButtonToggleGroup

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val adapter = TransactionAdapter(
        onEditTransaction = { transaction -> showEditTransactionDialog(transaction) },
        onDeleteTransaction = { transaction -> deleteTransaction(transaction) }
    )
    
    // Filter related properties
    private var allTransactions = emptyList<Transaction>()
    private var selectedPaymentMethods = emptySet<String>()
    private var selectedCategories = emptySet<String>()
    private var searchQuery: String = ""
    private var startDate: Long? = null
    private var endDate: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme before super.onCreate
        applyTheme()
        
        super.onCreate(savedInstanceState)
        
        // Check onboarding
        if (!Prefs.isOnboarded(this)) {
            startActivity(Intent(this, OnboardingModernActivity::class.java))
            finish()
            return
        }
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Load saved filter selections
        selectedPaymentMethods = Prefs.getSelectedPaymentFilters(this).toSet()
        selectedCategories = Prefs.getSelectedCategoryFilters(this).toSet()
        
        setupUI()
        setupRecyclerView()
        setupClickListeners()
        setupSearch()
        
        // Add entrance animation
        val slideIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        binding.root.startAnimation(slideIn)
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setupUI() {
        // Make status bar transparent for immersive experience
        window.statusBarColor = getColor(R.color.transparent)
    }

    private fun setupRecyclerView() {
        binding.recyclerTransactions.layoutManager = LinearLayoutManager(this)
        binding.recyclerTransactions.adapter = adapter
        
        // Add item animations
        binding.recyclerTransactions.itemAnimator?.apply {
            addDuration = 300
            removeDuration = 300
        }
    }

    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener { 
            showAddTransactionDialog() 
        }
        
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsModernActivity::class.java))
        }
        
        binding.btnGetStarted.setOnClickListener {
            showAddTransactionDialog()
        }
        
        // Add click listener to balance cards for detailed view
        binding.cardBalance.setOnClickListener {
            showDetailedBalanceDialog()
        }
        
        binding.cardIncome.setOnClickListener {
            showDetailedBalanceDialog()
        }
        
        binding.cardExpense.setOnClickListener {
            showDetailedBalanceDialog()
        }
        
        // Add filter button click listener
        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }
        
        // Add date filter button
        binding.btnDateFilter.setOnClickListener {
            showDateRangePicker()
        }
        
        // Clear search button
        binding.btnClearSearch.setOnClickListener {
            binding.searchEditText.text?.clear()
        }
        
        // Date range chip close
        binding.chipDateRange.setOnCloseIconClickListener {
            clearDateFilter()
        }
    }

    private fun refreshData() {
        val summary = TransactionStore.getSummary(this)
        allTransactions = TransactionStore.getTransactions(this)
        
        // Load persisted filter selections
        selectedPaymentMethods = Prefs.getSelectedPaymentFilters(this).toSet()
        selectedCategories = Prefs.getSelectedCategoryFilters(this).toSet()
        
        // Update financial summary with animations
        updateFinancialSummary(summary)
        
        // Apply current filter and update transaction list
        applyFilter()
        
        // Animate summary cards
        animateSummaryCards()
    }
    
    private fun applyFilter() {
        val filteredTransactions = allTransactions.filter { t ->
            val methodMatch = selectedPaymentMethods.isEmpty() || t.mode in selectedPaymentMethods
            val categoryMatch = selectedCategories.isEmpty() || t.category in selectedCategories
            
            // Search filter
            val searchMatch = if (searchQuery.isBlank()) {
                true
            } else {
                t.reason.contains(searchQuery, ignoreCase = true) ||
                t.amount.toString().contains(searchQuery) ||
                t.mode.contains(searchQuery, ignoreCase = true) ||
                t.category.contains(searchQuery, ignoreCase = true)
            }
            
            // Date filter
            val dateMatch = if (startDate == null || endDate == null) {
                true
            } else {
                t.time in startDate!!..endDate!!
            }
            
            methodMatch && categoryMatch && searchMatch && dateMatch
        }
         
         // Update transaction list
         adapter.setTransactions(filteredTransactions)
         
         // Update transaction count
         val totalCount = allTransactions.size
         val filteredCount = filteredTransactions.size
         val hasActiveFilters = selectedPaymentMethods.isNotEmpty() || selectedCategories.isNotEmpty() || 
                                searchQuery.isNotBlank() || (startDate != null && endDate != null)
         binding.txtTransactionCount.text = if (!hasActiveFilters) {
             "$totalCount entries"
         } else {
             "$filteredCount of $totalCount entries"
         }
         
         // Update filter status
         updateFilterStatus()
         // Update summary to reflect filtered transactions
         val filteredSummary = TransactionStore.computeSummary(filteredTransactions)
         updateFinancialSummary(filteredSummary)
         
         // Show/hide empty state
         if (filteredTransactions.isEmpty()) {
             binding.emptyStateLayout.visibility = android.view.View.VISIBLE
             binding.recyclerTransactions.visibility = android.view.View.GONE
             binding.transactionHeader.visibility = android.view.View.GONE
         } else {
             binding.emptyStateLayout.visibility = android.view.View.GONE
             binding.recyclerTransactions.visibility = android.view.View.VISIBLE
             binding.transactionHeader.visibility = android.view.View.VISIBLE
         }
    }
    
    private fun updateFilterStatus() {
        if (selectedPaymentMethods.isEmpty() && selectedCategories.isEmpty()) {
            binding.txtFilterStatus.visibility = android.view.View.GONE
            // Reset to default theme color - use null to let theme handle it
            binding.btnFilter.iconTint = null
        } else {
            binding.txtFilterStatus.visibility = android.view.View.VISIBLE
            val methodPart = if (selectedPaymentMethods.isNotEmpty()) "${selectedPaymentMethods.size} method(s)" else ""
            val categoryPart = if (selectedCategories.isNotEmpty()) "${selectedCategories.size} category(s)" else ""
            val combined = listOf(methodPart, categoryPart).filter { it.isNotBlank() }.joinToString(", ")
            binding.txtFilterStatus.text = combined
            // Use primary color to indicate active filter
            val typedValue = android.util.TypedValue()
            theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
            val primaryColor = android.content.res.ColorStateList.valueOf(typedValue.data)
            binding.btnFilter.iconTint = primaryColor
        }
    }

    private fun updateFinancialSummary(summary: com.example.expensetracker.data.FinancialSummary) {
        // Format numbers with proper Indian currency formatting
        binding.txtBalance.text = formatCurrency(summary.balance)
        binding.txtCredit.text = formatCurrency(summary.totalCredit)
        binding.txtDebit.text = formatCurrency(summary.totalDebit)
        
        // Color code balance based on positive/negative
        val balanceColor = if (summary.balance >= 0) {
            getColor(R.color.credit_green)
        } else {
            getColor(R.color.debit_red)
        }
        binding.txtBalance.setTextColor(balanceColor)
    }

    private fun formatCurrency(amount: Double): String {
        return when {
            amount >= 10000000 -> "₹${String.format("%.1f", amount / 10000000)}Cr" // Crores
            amount >= 100000 -> "₹${String.format("%.1f", amount / 100000)}L" // Lakhs
            amount >= 1000 -> "₹${String.format("%.1f", amount / 1000)}K" // Thousands
            else -> "₹${String.format("%.0f", amount)}" // Regular
        }
    }

    private fun formatExactCurrency(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            // No decimal places needed
            "₹${String.format("%.0f", amount)}"
        } else {
            // Show up to 2 decimal places, removing trailing zeros
            "₹${String.format("%.2f", amount).trimEnd('0').trimEnd('.')}"
        }
    }

    private fun showDetailedBalanceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_detailed_balance, null)
        val summary = TransactionStore.getSummary(this)
        
        // Find views
        val txtDetailedBalance = dialogView.findViewById<android.widget.TextView>(R.id.txtDetailedBalance)
        val txtDetailedIncome = dialogView.findViewById<android.widget.TextView>(R.id.txtDetailedIncome)
        val txtDetailedExpense = dialogView.findViewById<android.widget.TextView>(R.id.txtDetailedExpense)
        
        // Set exact values without rounding
        txtDetailedBalance.text = formatExactCurrency(summary.balance)
        txtDetailedIncome.text = formatExactCurrency(summary.totalCredit)
        txtDetailedExpense.text = formatExactCurrency(summary.totalDebit)
        
        // Color code balance based on positive/negative
        val balanceColor = if (summary.balance >= 0) {
            getColor(R.color.credit_green)
        } else {
            getColor(R.color.debit_red)
        }
        txtDetailedBalance.setTextColor(balanceColor)
        
        // Create and show the dialog
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .create()
            
        dialog.show()
        
        // Add entrance animation to the content
        val slideIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        slideIn.duration = 300
        dialogView.startAnimation(slideIn)
    }

    private fun animateSummaryCards() {
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeIn.duration = 600
        binding.txtBalance.startAnimation(fadeIn)
        binding.txtCredit.startAnimation(fadeIn)
        binding.txtDebit.startAnimation(fadeIn)
    }
    
    private fun showFilterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_filter_transactions, null)
        
        // Find views
        val recyclerFilterOptions = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerFilterOptions)
        val btnClearFilter = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnClearFilter)
        val btnApplyFilter = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnApplyFilter)
        
        // Setup RecyclerView
        recyclerFilterOptions.layoutManager = LinearLayoutManager(this)
        val filterAdapter = FilterAdapter {
            // Update apply button state when selection changes
        }
        recyclerFilterOptions.adapter = filterAdapter
        
        // Categories
        val recyclerCategoryOptions = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerCategoryOptions)
        recyclerCategoryOptions.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val categoryAdapter = FilterAdapter { }
        recyclerCategoryOptions.adapter = categoryAdapter
        
        // Get payment methods with their transaction counts - include all defined methods
        val allModes = Prefs.getModes(this).toSet()
        val transactionsByMode = allTransactions.groupBy { it.mode }
        val paymentMethodCounts = allModes.map { method ->
            val transactions = transactionsByMode[method] ?: emptyList()
            FilterOption(
                paymentMethod = method,
                transactionCount = transactions.size,
                isSelected = method in selectedPaymentMethods
            )
        }.sortedByDescending { it.transactionCount }
        
        filterAdapter.setFilterOptions(paymentMethodCounts)
        
        // Get categories with counts - include all defined categories
        val allCategories = Prefs.getCategories(this).toSet()
        val transactionsByCategory = allTransactions.groupBy { it.category }
        val categoryCounts = allCategories.map { cat ->
            val transactions = transactionsByCategory[cat] ?: emptyList()
            FilterOption(
                paymentMethod = cat,
                transactionCount = transactions.size,
                isSelected = cat in selectedCategories
            )
        }.sortedByDescending { it.transactionCount }
        categoryAdapter.setFilterOptions(categoryCounts)
        
        // Create and show dialog
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()
            
        // Setup button listeners
        btnClearFilter.setOnClickListener {
            filterAdapter.clearAll()
            categoryAdapter.clearAll()
            // Clear persisted filters as well
            Prefs.saveSelectedFilters(this, emptyList(), emptyList())
        }
        
        btnApplyFilter.setOnClickListener {
            selectedPaymentMethods = filterAdapter.getSelectedMethods().toSet()
            selectedCategories = categoryAdapter.getSelectedMethods().toSet()
            // Persist selected filters
            Prefs.saveSelectedFilters(this, selectedPaymentMethods.toList(), selectedCategories.toList())
            applyFilter()
            dialog.dismiss()
            
            // Show success message
            val methodMsg = if (selectedPaymentMethods.isEmpty()) "" else "${selectedPaymentMethods.size} payment method(s)"
            val catMsg = if (selectedCategories.isEmpty()) "" else "${selectedCategories.size} category(s)"
            val msg = listOf(methodMsg, catMsg).filter { it.isNotBlank() }.joinToString(", ").ifEmpty { "Filter cleared" }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        
        dialog.show()
        
        // Add entrance animation
        val slideIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        slideIn.duration = 300
        dialogView.startAnimation(slideIn)
    }    private fun showAddTransactionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_expense, null)
        val modes = Prefs.getModes(this)
        val categories = Prefs.getCategories(this)
        
        // Find views - using the correct types from the Material Design layout
        val editAmount = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editAmount)
        val editReason = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editReason)
        val spinnerMode = dialogView.findViewById<AutoCompleteTextView>(R.id.spinnerMode)
        val spinnerCategory = dialogView.findViewById<AutoCompleteTextView>(R.id.spinnerCategory)
        val toggleGroup = dialogView.findViewById<MaterialButtonToggleGroup>(R.id.toggleTransactionType)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)
        val btnExpense = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnExpense)
        val btnIncome = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnIncome)
        val iconTransactionType = dialogView.findViewById<android.widget.ImageView>(R.id.iconTransactionType)
        val titleTransaction = dialogView.findViewById<android.widget.TextView>(R.id.titleTransaction)
        
        // Quick amount buttons
        val btnAmount50 = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAmount50)
        val btnAmount100 = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAmount100)
        val btnAmount500 = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAmount500)
        val btnAmount1000 = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAmount1000)
        
        // Setup dropdown with payment modes
        val modeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, modes)
        spinnerMode.setAdapter(modeAdapter)
        if (modes.isNotEmpty()) {
            spinnerMode.setText(modes[0], false)
        }
        // Setup dropdown with categories
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        spinnerCategory.setAdapter(categoryAdapter)
        if (categories.isNotEmpty()) {
            spinnerCategory.setText(categories[0], false)
        }
        
        // Setup transaction type toggle (default to expense)
        var selectedType = TransactionType.DEBIT
        toggleGroup.check(R.id.btnExpense)
        
        // Function to update button colors and dialog header
        fun updateTransactionTypeUI(isCredit: Boolean) {
            if (isCredit) {
                // Income selected - update colors with animation
                btnIncome.setBackgroundColor(getColor(R.color.credit_green_dark))
                btnIncome.setTextColor(getColor(R.color.white))
                btnIncome.elevation = 8f
                btnExpense.setBackgroundColor(getColor(R.color.credit_green_light))
                btnExpense.setTextColor(getColor(R.color.credit_green))
                btnExpense.elevation = 0f
                
                // Update header with animation
                iconTransactionType.setImageResource(R.drawable.ic_arrow_upward)
                iconTransactionType.setColorFilter(getColor(R.color.credit_green_dark))
                titleTransaction.text = "Add Income"
                titleTransaction.setTextColor(getColor(R.color.credit_green_dark))
                
                // Animate icon
                iconTransactionType.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction {
                    iconTransactionType.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
                }.start()
            } else {
                // Expense selected - update colors with animation
                btnExpense.setBackgroundColor(getColor(R.color.debit_red_dark))
                btnExpense.setTextColor(getColor(R.color.white))
                btnExpense.elevation = 8f
                btnIncome.setBackgroundColor(getColor(R.color.debit_red_light))
                btnIncome.setTextColor(getColor(R.color.debit_red))
                btnIncome.elevation = 0f
                
                // Update header with animation
                iconTransactionType.setImageResource(R.drawable.ic_arrow_downward)
                iconTransactionType.setColorFilter(getColor(R.color.debit_red_dark))
                titleTransaction.text = "Add Expense"
                titleTransaction.setTextColor(getColor(R.color.debit_red_dark))
                
                // Animate icon
                iconTransactionType.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction {
                    iconTransactionType.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
                }.start()
            }
        }
        
        // Set initial state (expense)
        updateTransactionTypeUI(false)
        
        // Quick amount button listeners for Indian currency
        btnAmount50.setOnClickListener { editAmount.setText("50") }
        btnAmount100.setOnClickListener { editAmount.setText("100") }
        btnAmount500.setOnClickListener { editAmount.setText("500") }
        btnAmount1000.setOnClickListener { editAmount.setText("1000") }
        
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedType = if (checkedId == R.id.btnIncome) {
                    updateTransactionTypeUI(true)
                    TransactionType.CREDIT
                } else {
                    updateTransactionTypeUI(false)
                    TransactionType.DEBIT
                }
            }
        }
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()
        
        // Setup button click listeners
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        btnSave.setOnClickListener {
            val amountStr = editAmount.text.toString().trim()
            val reason = editReason.text.toString().trim().ifEmpty { 
                if (selectedType == TransactionType.CREDIT) "Income" else "Expense" 
            }
            val mode = spinnerMode.text.toString().trim()
            val category = spinnerCategory.text.toString().trim().ifEmpty { "Uncategorized" }
            
            // Validate input
            if (amountStr.isEmpty()) {
                editAmount.error = "Amount is required"
                return@setOnClickListener
            }
            
            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                editAmount.error = "Please enter a valid amount"
                return@setOnClickListener
            }
            
            if (mode.isEmpty()) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Add transaction
            TransactionStore.addTransaction(this, amount, reason, mode, category, selectedType)
            refreshData()
            dialog.dismiss()
            
            // Show success message with rupee symbol
            val typeText = if (selectedType == TransactionType.CREDIT) "Income" else "Expense"
            Toast.makeText(this, "$typeText of ₹${String.format("%.0f", amount)} added!", Toast.LENGTH_SHORT).show()
        }
        
        dialog.show()
        
        // Make dialog wider for better content visibility on S23
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    
    private fun showEditTransactionDialog(transaction: com.example.expensetracker.data.Transaction) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_expense, null)
        val modes = Prefs.getModes(this)
        val categories = Prefs.getCategories(this)
        
        // Find views
        val editAmount = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editAmount)
        val editReason = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editReason)
        val spinnerMode = dialogView.findViewById<AutoCompleteTextView>(R.id.spinnerMode)
        val spinnerCategory = dialogView.findViewById<AutoCompleteTextView>(R.id.spinnerCategory)
        val toggleGroup = dialogView.findViewById<MaterialButtonToggleGroup>(R.id.toggleTransactionType)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)
        val btnExpense = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnExpense)
        val btnIncome = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnIncome)
        val iconTransactionType = dialogView.findViewById<android.widget.ImageView>(R.id.iconTransactionType)
        val titleTransaction = dialogView.findViewById<android.widget.TextView>(R.id.titleTransaction)
        
        // Pre-fill with existing data
        editAmount.setText(transaction.amount.toString())
        editReason.setText(transaction.reason)
        
        // Setup dropdown with payment modes
        val modeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, modes)
        spinnerMode.setAdapter(modeAdapter)
        spinnerMode.setText(transaction.mode, false)
        // Setup dropdown with categories
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        spinnerCategory.setAdapter(categoryAdapter)
        spinnerCategory.setText(transaction.category, false)
        
        // Setup transaction type toggle
        var selectedType = transaction.type
        toggleGroup.check(if (transaction.type == TransactionType.CREDIT) R.id.btnIncome else R.id.btnExpense)
        
        // Function to update button colors and dialog header
        fun updateTransactionTypeUI(isCredit: Boolean) {
            if (isCredit) {
                btnIncome.setBackgroundColor(getColor(R.color.credit_green_dark))
                btnIncome.setTextColor(getColor(R.color.white))
                btnIncome.elevation = 8f
                btnExpense.setBackgroundColor(getColor(R.color.credit_green_light))
                btnExpense.setTextColor(getColor(R.color.credit_green))
                btnExpense.elevation = 0f
                
                iconTransactionType.setImageResource(R.drawable.ic_arrow_upward)
                iconTransactionType.setColorFilter(getColor(R.color.credit_green_dark))
                titleTransaction.text = "Edit Income"
                titleTransaction.setTextColor(getColor(R.color.credit_green_dark))
            } else {
                btnExpense.setBackgroundColor(getColor(R.color.debit_red_dark))
                btnExpense.setTextColor(getColor(R.color.white))
                btnExpense.elevation = 8f
                btnIncome.setBackgroundColor(getColor(R.color.debit_red_light))
                btnIncome.setTextColor(getColor(R.color.debit_red))
                btnIncome.elevation = 0f
                
                iconTransactionType.setImageResource(R.drawable.ic_arrow_downward)
                iconTransactionType.setColorFilter(getColor(R.color.debit_red_dark))
                titleTransaction.text = "Edit Expense"
                titleTransaction.setTextColor(getColor(R.color.debit_red_dark))
            }
        }
        
        // Set initial state
        updateTransactionTypeUI(selectedType == TransactionType.CREDIT)
        
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedType = if (checkedId == R.id.btnIncome) {
                    updateTransactionTypeUI(true)
                    TransactionType.CREDIT
                } else {
                    updateTransactionTypeUI(false)
                    TransactionType.DEBIT
                }
            }
        }
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        btnSave.setOnClickListener {
            val amountStr = editAmount.text.toString().trim()
            val reason = editReason.text.toString().trim().ifEmpty { 
                if (selectedType == TransactionType.CREDIT) "Income" else "Expense" 
            }
            val mode = spinnerMode.text.toString().trim()
            val category = spinnerCategory.text.toString().trim().ifEmpty { "Uncategorized" }
            
            // Validate input
            if (amountStr.isEmpty()) {
                editAmount.error = "Amount is required"
                return@setOnClickListener
            }
            
            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                editAmount.error = "Please enter a valid amount"
                return@setOnClickListener
            }
            
            if (mode.isEmpty()) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Update transaction
            val updatedTransaction = com.example.expensetracker.data.Transaction(
                id = transaction.id,
                amount = amount,
                reason = reason,
                mode = mode,
                category = category,
                type = selectedType,
                time = transaction.time
            )
            
            TransactionStore.updateTransaction(this, transaction, updatedTransaction)
            refreshData()
            dialog.dismiss()
            
            // Show success message with Snackbar
            com.google.android.material.snackbar.Snackbar.make(
                binding.root,
                "Transaction updated successfully!",
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
            ).show()
        }
        
        dialog.show()
        
        // Make dialog wider for better content visibility on S23
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    
    private fun deleteTransaction(transaction: com.example.expensetracker.data.Transaction) {
        TransactionStore.deleteTransaction(this, transaction)
        refreshData()
        
        // Show success message with undo option
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            "Transaction deleted",
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).setAction("UNDO") {
            TransactionStore.addTransaction(this, transaction.amount, transaction.reason, transaction.mode, transaction.category, transaction.type)
             refreshData()
         }.show()
    }
    
    private fun applyTheme() {
        when (Prefs.getThemeMode(this)) {
            Prefs.THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Prefs.THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
    
    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                searchQuery = s?.toString() ?: ""
                binding.btnClearSearch.visibility = if (searchQuery.isNotEmpty()) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
                applyFilter()
            }
        })
    }
    
    private fun showDateRangePicker() {
        // Show start date picker
        val startDatePicker = com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Start Date")
            .setSelection(startDate ?: com.google.android.material.datepicker.MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        
        startDatePicker.addOnPositiveButtonClickListener { startSelection ->
            // Show end date picker after start date is selected
            val endDatePicker = com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select End Date")
                .setSelection(endDate ?: com.google.android.material.datepicker.MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            
            endDatePicker.addOnPositiveButtonClickListener { endSelection ->
                // Set date range (normalize to start and end of day)
                val startCal = java.util.Calendar.getInstance().apply {
                    timeInMillis = startSelection
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                
                val endCal = java.util.Calendar.getInstance().apply {
                    timeInMillis = endSelection
                    set(java.util.Calendar.HOUR_OF_DAY, 23)
                    set(java.util.Calendar.MINUTE, 59)
                    set(java.util.Calendar.SECOND, 59)
                    set(java.util.Calendar.MILLISECOND, 999)
                }
                
                startDate = startCal.timeInMillis
                endDate = endCal.timeInMillis
                
                // Update UI
                val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                val rangeText = "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"
                binding.chipDateRange.text = rangeText
                binding.chipDateRange.visibility = android.view.View.VISIBLE
                
                // Apply filter
                applyFilter()
                
                Toast.makeText(this, "Date filter applied", Toast.LENGTH_SHORT).show()
            }
            
            endDatePicker.show(supportFragmentManager, "END_DATE_PICKER")
        }
        
        startDatePicker.show(supportFragmentManager, "START_DATE_PICKER")
    }
    
    private fun clearDateFilter() {
        startDate = null
        endDate = null
        binding.chipDateRange.visibility = android.view.View.GONE
        applyFilter()
        Toast.makeText(this, "Date filter cleared", Toast.LENGTH_SHORT).show()
    }
}
