package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.data.Prefs
import com.example.expensetracker.databinding.ActivityOnboardingModernBinding
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OnboardingModernActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingModernBinding
    private val selectedPaymentMethods = mutableSetOf<String>()
    private val selectedCategories = mutableSetOf<String>()
    
    // Predefined popular payment methods
    private val popularMethods = listOf(
        "Cash" to R.drawable.ic_money,
        "UPI" to R.drawable.ic_smartphone,
        "Credit Card" to R.drawable.ic_credit_card,
        "Debit Card" to R.drawable.ic_credit_card,
        "Net Banking" to R.drawable.ic_account_balance,
        "Digital Wallet" to R.drawable.ic_smartphone
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if already onboarded
        if (Prefs.isOnboarded(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        
        binding = ActivityOnboardingModernBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupPopularChips()
        setupCategoryChips()
        setupClickListeners()
        updateContinueButton()
    }
    
    private fun setupPopularChips() {
        // Clear any existing chips
        binding.chipGroupPopular.removeAllViews()
        
        // Add predefined popular payment method chips
        popularMethods.forEach { (method, iconRes) ->
            val chip = Chip(this).apply {
                text = method
                isCheckable = true
                setChipIconResource(iconRes)
                chipIconTint = getColorStateList(R.color.md_theme_light_onSurfaceVariant)
                
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedPaymentMethods.add(method)
                    } else {
                        selectedPaymentMethods.remove(method)
                    }
                    updateContinueButton()
                }
            }
            binding.chipGroupPopular.addView(chip)
        }
    }
    
    private fun setupCategoryChips() {
        // Setup category chips with checked listeners
        binding.chipFood.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedCategories.add("Food") else selectedCategories.remove("Food")
            updateContinueButton()
        }
        binding.chipFashion.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedCategories.add("Fashion") else selectedCategories.remove("Fashion")
            updateContinueButton()
        }
        binding.chipTransport.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedCategories.add("Transport") else selectedCategories.remove("Transport")
            updateContinueButton()
        }
        binding.chipGroceries.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedCategories.add("Groceries") else selectedCategories.remove("Groceries")
            updateContinueButton()
        }
        binding.chipBills.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedCategories.add("Bills") else selectedCategories.remove("Bills")
            updateContinueButton()
        }
        binding.chipOther.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedCategories.add("Other") else selectedCategories.remove("Other")
            updateContinueButton()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnAddCustom.setOnClickListener {
            showAddCustomMethodDialog()
        }
        
        binding.btnAddCustomCategory.setOnClickListener {
            showAddCustomCategoryDialog()
        }
        
        binding.btnSkip.setOnClickListener {
            // Skip onboarding - use default methods and categories
            val defaultMethods = listOf("Cash", "UPI", "Credit Card")
            val defaultCategories = listOf("Food", "Fashion", "Other")
            saveAndContinue(defaultMethods, defaultCategories)
        }
        
        binding.btnContinue.setOnClickListener {
            if (selectedPaymentMethods.isEmpty()) {
                Toast.makeText(this, "Please select at least one payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedCategories.isEmpty()) {
                Toast.makeText(this, "Please select at least one category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveAndContinue(selectedPaymentMethods.toList(), selectedCategories.toList())
        }
    }
    
    private fun showAddCustomMethodDialog() {
        val input = android.widget.EditText(this).apply {
            hint = "Enter payment method name"
            setPadding(48, 32, 48, 32)
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Add Custom Method")
            .setMessage("Enter a custom payment method")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val methodName = input.text.toString().trim()
                if (methodName.isNotEmpty() && !selectedPaymentMethods.contains(methodName)) {
                    addCustomChip(methodName)
                    selectedPaymentMethods.add(methodName)
                    updateContinueButton()
                    Toast.makeText(this, "'$methodName' added!", Toast.LENGTH_SHORT).show()
                } else if (selectedPaymentMethods.contains(methodName)) {
                    Toast.makeText(this, "This method already exists!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a valid name!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showAddCustomCategoryDialog() {
        val input = android.widget.EditText(this).apply {
            hint = "Enter category name"
            setPadding(48, 32, 48, 32)
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Add Custom Category")
            .setMessage("Enter a custom expense category")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val categoryName = input.text.toString().trim()
                if (categoryName.isNotEmpty() && !selectedCategories.contains(categoryName)) {
                    addCustomCategoryChip(categoryName)
                    selectedCategories.add(categoryName)
                    updateContinueButton()
                    Toast.makeText(this, "'$categoryName' added!", Toast.LENGTH_SHORT).show()
                } else if (selectedCategories.contains(categoryName)) {
                    Toast.makeText(this, "This category already exists!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a valid name!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun addCustomChip(methodName: String) {
        val chip = Chip(this).apply {
            text = methodName
            isCheckable = true
            isChecked = true
            setChipIconResource(R.drawable.ic_payment)
            chipIconTint = getColorStateList(R.color.md_theme_light_onSurfaceVariant)
            isCloseIconVisible = true
            
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedPaymentMethods.add(methodName)
                } else {
                    selectedPaymentMethods.remove(methodName)
                }
                updateContinueButton()
            }
            
            setOnCloseIconClickListener {
                selectedPaymentMethods.remove(methodName)
                binding.chipGroupCustom.removeView(this)
                updateContinueButton()
            }
        }
        binding.chipGroupCustom.addView(chip)
    }
    
    private fun addCustomCategoryChip(categoryName: String) {
        val chip = Chip(this).apply {
            text = categoryName
            isCheckable = true
            isChecked = true
            setChipIconResource(R.drawable.ic_settings)
            chipIconTint = getColorStateList(R.color.md_theme_light_onSurfaceVariant)
            isCloseIconVisible = true
            
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedCategories.add(categoryName)
                } else {
                    selectedCategories.remove(categoryName)
                }
                updateContinueButton()
            }
            
            setOnCloseIconClickListener {
                selectedCategories.remove(categoryName)
                binding.chipGroupCategories.removeView(this)
                updateContinueButton()
            }
        }
        binding.chipGroupCategories.addView(chip)
    }
    
    private fun updateContinueButton() {
        val hasSelection = selectedPaymentMethods.isNotEmpty() && selectedCategories.isNotEmpty()
        binding.btnContinue.isEnabled = hasSelection
        binding.btnContinue.alpha = if (hasSelection) 1.0f else 0.6f
        
        val buttonText = if (hasSelection) {
            "Get Started (${selectedPaymentMethods.size} methods, ${selectedCategories.size} categories)"
        } else {
            "Get Started"
        }
        binding.btnContinue.text = buttonText
    }
    
    private fun saveAndContinue(methods: List<String>, categories: List<String>) {
        // Save payment methods
        Prefs.saveModes(this, methods)
        
        // Save categories
        Prefs.saveCategories(this, categories)
        
        // Mark as onboarded
        Prefs.setOnboarded(this)
        
        // Show success message
        Toast.makeText(this, "Setup complete! ${methods.size} payment methods and ${categories.size} categories saved.", Toast.LENGTH_SHORT).show()
        
        // Navigate to main activity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
