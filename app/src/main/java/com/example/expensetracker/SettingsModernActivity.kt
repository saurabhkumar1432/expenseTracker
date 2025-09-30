package com.example.expensetracker

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
}
