package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.data.TransactionStore
import com.example.expensetracker.data.TransactionType
import com.example.expensetracker.data.Prefs
import com.example.expensetracker.databinding.ActivityMainBinding
import com.example.expensetracker.ui.TransactionAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.button.MaterialButtonToggleGroup

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val adapter = TransactionAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check onboarding
        if (!Prefs.isOnboarded(this)) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupRecyclerView()
        setupClickListeners()
        
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
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun refreshData() {
        val summary = TransactionStore.getSummary(this)
        val transactions = TransactionStore.getTransactions(this)
        
        // Update financial summary with animations
        updateFinancialSummary(summary)
        
        // Update transaction list
        adapter.setTransactions(transactions)
        binding.txtTransactionCount.text = "${transactions.size} entries"
        
        // Animate summary cards
        animateSummaryCards()
    }

    private fun updateFinancialSummary(summary: com.example.expensetracker.data.FinancialSummary) {
        binding.txtBalance.text = "₹${String.format("%.2f", summary.balance)}"
        binding.txtCredit.text = "₹${String.format("%.2f", summary.totalCredit)}"
        binding.txtDebit.text = "₹${String.format("%.2f", summary.totalDebit)}"
        
        // Color code balance based on positive/negative
        val balanceColor = if (summary.balance >= 0) {
            getColor(R.color.credit_green)
        } else {
            getColor(R.color.debit_red)
        }
        binding.txtBalance.setTextColor(balanceColor)
    }

    private fun animateSummaryCards() {
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeIn.duration = 600
        binding.txtBalance.startAnimation(fadeIn)
        binding.txtCredit.startAnimation(fadeIn)
        binding.txtDebit.startAnimation(fadeIn)
    }    private fun showAddTransactionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_expense, null)
        val modes = Prefs.getModes(this)
        
        // Find views - using the correct types from the Material Design layout
        val editAmount = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editAmount)
        val editReason = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editReason)
        val spinnerMode = dialogView.findViewById<AutoCompleteTextView>(R.id.spinnerMode)
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
            TransactionStore.addTransaction(this, amount, reason, mode, selectedType)
            refreshData()
            dialog.dismiss()
            
            // Show success message with rupee symbol
            val typeText = if (selectedType == TransactionType.CREDIT) "Income" else "Expense"
            Toast.makeText(this, "$typeText of ₹${String.format("%.0f", amount)} added!", Toast.LENGTH_SHORT).show()
        }
        
        dialog.show()
    }
}
