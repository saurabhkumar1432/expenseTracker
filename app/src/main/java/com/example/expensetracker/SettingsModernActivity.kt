package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.data.Prefs
import com.example.expensetracker.databinding.ActivitySettingsModernBinding
import com.example.expensetracker.databinding.DialogAddEditPaymentMethodBinding
import com.example.expensetracker.ui.PaymentMethodModernAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class SettingsModernActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsModernBinding
    private lateinit var adapter: PaymentMethodModernAdapter
    private lateinit var categoryAdapter: PaymentMethodModernAdapter
    private val paymentMethods = mutableListOf<String>()
    private val categories = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsModernBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupThemeSelection()
        loadPaymentMethods()
        loadCategories()
        setupRecyclerView()
        setupCategoryRecyclerView()
        setupClickListeners()
        updateEmptyState()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    
    private fun setupThemeSelection() {
        // Load current theme
        val currentTheme = Prefs.getThemeMode(this)
        when (currentTheme) {
            Prefs.THEME_SYSTEM -> binding.chipThemeSystem.isChecked = true
            Prefs.THEME_LIGHT -> binding.chipThemeLight.isChecked = true
            Prefs.THEME_DARK -> binding.chipThemeDark.isChecked = true
        }
        
        // Setup theme change listeners
        binding.chipGroupTheme.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            
            val newTheme = when (checkedIds[0]) {
                R.id.chipThemeLight -> Prefs.THEME_LIGHT
                R.id.chipThemeDark -> Prefs.THEME_DARK
                else -> Prefs.THEME_SYSTEM
            }
            
            if (newTheme != currentTheme) {
                Prefs.setThemeMode(this, newTheme)
                applyTheme(newTheme)
                Toast.makeText(this, "Theme applied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun applyTheme(theme: Int) {
        when (theme) {
            Prefs.THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Prefs.THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
    
    private fun loadPaymentMethods() {
        val existingMethods = Prefs.getModes(this)
        paymentMethods.clear()
        paymentMethods.addAll(existingMethods)
    }
    
    private fun loadCategories() {
        val existingCategories = Prefs.getCategories(this)
        categories.clear()
        categories.addAll(existingCategories)
    }
    
    private fun setupRecyclerView() {
        adapter = PaymentMethodModernAdapter(
            paymentMethods = paymentMethods,
            onEdit = { position, method -> showEditDialog(position, method) },
            onDelete = { position -> showDeleteConfirmation(position) }
        )
        
        binding.recyclerViewPaymentMethods.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPaymentMethods.adapter = adapter
        
        // Add drag-to-reorder functionality
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition
                
                // Swap items in the list
                val item = paymentMethods.removeAt(fromPosition)
                paymentMethods.add(toPosition, item)
                
                // Notify adapter
                adapter.notifyItemMoved(fromPosition, toPosition)
                
                // Save updated order
                savePaymentMethods()
                
                return true
            }
            
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Not used, but required by interface
            }
        })
        
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewPaymentMethods)
    }
    
    private fun setupCategoryRecyclerView() {
        categoryAdapter = PaymentMethodModernAdapter(
            paymentMethods = categories,
            onEdit = { position, method -> showEditCategoryDialog(position, method) },
            onDelete = { position -> showDeleteCategoryConfirmation(position) }
        )
        
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCategories.adapter = categoryAdapter
        // Allow drag to reorder categories as well
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition
                val item = categories.removeAt(fromPosition)
                categories.add(toPosition, item)
                categoryAdapter.notifyItemMoved(fromPosition, toPosition)
                saveCategories()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewCategories)
    }
    
    private fun setupClickListeners() {
        binding.fabAddPaymentMethod.setOnClickListener {
            showAddDialog()
        }
        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
        
        binding.btnManageBudgets.setOnClickListener {
            startActivity(Intent(this, BudgetManagementActivity::class.java))
        }
    }
    
    private fun updateEmptyState() {
        if (paymentMethods.isEmpty()) {
            binding.emptyStateContainer.visibility = View.VISIBLE
            binding.recyclerViewPaymentMethods.visibility = View.GONE
        } else {
            binding.emptyStateContainer.visibility = View.GONE
            binding.recyclerViewPaymentMethods.visibility = View.VISIBLE
        }
    }
    
    private fun showAddDialog() {
        showPaymentMethodDialog(
            title = getString(R.string.add_payment_method_title),
            buttonText = getString(R.string.add_method),
            existingMethod = null,
            position = -1
        )
    }
    
    private fun showEditDialog(position: Int, method: String) {
        showPaymentMethodDialog(
            title = getString(R.string.edit_payment_method_title),
            buttonText = getString(R.string.save_changes),
            existingMethod = method,
            position = position
        )
    }
    
    private fun showPaymentMethodDialog(
        title: String,
        buttonText: String,
        existingMethod: String?,
        position: Int
    ) {
        val dialogBinding = DialogAddEditPaymentMethodBinding.inflate(layoutInflater)
        
        // Setup dialog
        dialogBinding.txtDialogTitle.text = title
        dialogBinding.btnSave.text = buttonText
        
        // Pre-fill if editing
        existingMethod?.let {
            dialogBinding.editPaymentMethodName.setText(it)
        }
        
        // Setup quick select chips
        setupQuickSelectChips(dialogBinding)
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()
        
        // Handle chip selection
        dialogBinding.chipGroupQuickSelect.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChip = dialogBinding.chipGroupQuickSelect.findViewById<com.google.android.material.chip.Chip>(checkedIds[0])
                dialogBinding.editPaymentMethodName.setText(selectedChip.text)
            }
        }
        
        // Handle button clicks
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialogBinding.btnSave.setOnClickListener {
            val methodName = dialogBinding.editPaymentMethodName.text.toString().trim()
            
            if (methodName.isEmpty()) {
                dialogBinding.editPaymentMethodName.error = "Please enter a payment method name"
                return@setOnClickListener
            }
            
            // Check for duplicates (excluding current item if editing)
            val isDuplicate = paymentMethods.any { method ->
                method.equals(methodName, ignoreCase = true) && 
                (position == -1 || paymentMethods.indexOf(method) != position)
            }
            
            if (isDuplicate) {
                dialogBinding.editPaymentMethodName.error = "This payment method already exists"
                return@setOnClickListener
            }
            
            if (position == -1) {
                // Adding new method
                paymentMethods.add(methodName)
                adapter.notifyItemInserted(paymentMethods.size - 1)
                showSnackbar("'$methodName' added successfully")
            } else {
                // Editing existing method
                paymentMethods[position] = methodName
                adapter.notifyItemChanged(position)
                showSnackbar("'$methodName' updated successfully")
            }
            
            savePaymentMethods()
            updateEmptyState()
            dialog.dismiss()
        }
        
        dialog.show()
        
        // Focus on input field
        dialogBinding.editPaymentMethodName.requestFocus()
    }
    
    private fun setupQuickSelectChips(dialogBinding: DialogAddEditPaymentMethodBinding) {
        val commonMethods = listOf("Cash", "UPI", "Credit Card", "Debit Card", "Net Banking", "Digital Wallet")
        
        commonMethods.forEach { method ->
            val chip = com.google.android.material.chip.Chip(this).apply {
                text = method
                isCheckable = true
            }
            dialogBinding.chipGroupQuickSelect.addView(chip)
        }
    }
    
    private fun showDeleteConfirmation(position: Int) {
        val methodName = paymentMethods[position]
        
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_payment_method))
            .setMessage(getString(R.string.delete_payment_method_confirm).replace("%s", methodName))
            .setPositiveButton("Delete") { _, _ ->
                deletePaymentMethod(position, methodName)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun deletePaymentMethod(position: Int, methodName: String) {
        paymentMethods.removeAt(position)
        adapter.notifyItemRemoved(position)
        savePaymentMethods()
        updateEmptyState()
        
        // Show snackbar with undo option
        Snackbar.make(binding.root, "'$methodName' deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                paymentMethods.add(position, methodName)
                adapter.notifyItemInserted(position)
                savePaymentMethods()
                updateEmptyState()
            }
            .show()
    }
    
    private fun savePaymentMethods() {
        Prefs.saveModes(this, paymentMethods)
    }
    
    private fun saveCategories() {
        Prefs.saveCategories(this, categories)
    }
    
    private fun showAddCategoryDialog() {
        showCategoryDialog(
            title = "Add Category",
            buttonText = "Add",
            existingCategory = null,
            position = -1
        )
    }
    
    private fun showEditCategoryDialog(position: Int, category: String) {
        showCategoryDialog(
            title = "Edit Category",
            buttonText = "Save",
            existingCategory = category,
            position = position
        )
    }
    
    private fun showCategoryDialog(title: String, buttonText: String, existingCategory: String?, position: Int) {
        val dialogBinding = DialogAddEditPaymentMethodBinding.inflate(layoutInflater)
        dialogBinding.txtDialogTitle.text = title
        dialogBinding.btnSave.text = buttonText
        existingCategory?.let { dialogBinding.editPaymentMethodName.setText(it) }

        // Setup quick select chips for categories
        dialogBinding.chipGroupQuickSelect.removeAllViews()
        val commonCats = listOf("Food", "Fashion", "Transport", "Bills", "Uncategorized")
        commonCats.forEach { c ->
            val chip = com.google.android.material.chip.Chip(this).apply {
                text = c
                isCheckable = true
                // Tint chip background to match category color
                val color = com.example.expensetracker.ui.CategoryUtils.getColorForCategory(this@SettingsModernActivity, c)
                chipBackgroundColor = android.content.res.ColorStateList.valueOf(color)
                setTextColor(if ((android.graphics.Color.red(color) * 0.299 + android.graphics.Color.green(color) * 0.587 + android.graphics.Color.blue(color) * 0.114) < 150) android.graphics.Color.WHITE else android.graphics.Color.DKGRAY)
            }
            dialogBinding.chipGroupQuickSelect.addView(chip)
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.chipGroupQuickSelect.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChip = dialogBinding.chipGroupQuickSelect.findViewById<com.google.android.material.chip.Chip>(checkedIds[0])
                dialogBinding.editPaymentMethodName.setText(selectedChip.text)
            }
        }

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.editPaymentMethodName.text.toString().trim()
            if (name.isEmpty()) {
                dialogBinding.editPaymentMethodName.error = "Please enter a category name"
                return@setOnClickListener
            }
            val isDuplicate = categories.any { it.equals(name, ignoreCase = true) && (position == -1 || categories.indexOf(it) != position) }
            if (isDuplicate) {
                dialogBinding.editPaymentMethodName.error = "This category already exists"
                return@setOnClickListener
            }
            if (position == -1) {
                categories.add(name)
                categoryAdapter.notifyItemInserted(categories.size - 1)
            } else {
                categories[position] = name
                categoryAdapter.notifyItemChanged(position)
            }
            saveCategories()
            dialog.dismiss()
        }
        dialog.show()
        dialogBinding.editPaymentMethodName.requestFocus()
    }
    
    private fun showDeleteCategoryConfirmation(position: Int) {
        val name = categories[position]
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete '$name'?")
            .setPositiveButton("Delete") { _, _ ->
                categories.removeAt(position)
                categoryAdapter.notifyItemRemoved(position)
                saveCategories()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
    
    private fun showBudgetManagementDialog() {
        val budgets = Prefs.getBudgets(this).toMutableMap()
        val availableCategories = categories.toList()
        
        if (availableCategories.isEmpty()) {
            Toast.makeText(this, "Please add categories first", Toast.LENGTH_SHORT).show()
            return
        }
        
        val budgetItems = availableCategories.map { category ->
            "$category: ₹${budgets[category]?.toInt() ?: 0}"
        }.toTypedArray()
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Manage Budgets")
            .setItems(budgetItems) { _, which ->
                val selectedCategory = availableCategories[which]
                showSetBudgetDialog(selectedCategory, budgets[selectedCategory] ?: 0.0)
            }
            .setNeutralButton("Clear All") { _, _ ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Clear All Budgets")
                    .setMessage("Are you sure you want to remove all budget limits?")
                    .setPositiveButton("Clear") { _, _ ->
                        availableCategories.forEach { Prefs.removeBudget(this, it) }
                        Toast.makeText(this, "All budgets cleared", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            .setNegativeButton("Close", null)
            .show()
    }
    
    private fun showSetBudgetDialog(category: String, currentBudget: Double) {
        val input = android.widget.EditText(this).apply {
            hint = "Enter budget amount"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(if (currentBudget > 0) currentBudget.toInt().toString() else "")
            setPadding(48, 32, 48, 32)
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Set Budget for $category")
            .setMessage("Enter your monthly budget limit for this category")
            .setView(input)
            .setPositiveButton("Set") { _, _ ->
                val amount = input.text.toString().toDoubleOrNull()
                if (amount != null && amount > 0) {
                    Prefs.setBudget(this, category, amount)
                    Toast.makeText(this, "Budget set: ₹${amount.toInt()} for $category", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            }
            .setNeutralButton("Remove") { _, _ ->
                Prefs.removeBudget(this, category)
                Toast.makeText(this, "Budget removed for $category", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
        
        input.requestFocus()
    }
}
