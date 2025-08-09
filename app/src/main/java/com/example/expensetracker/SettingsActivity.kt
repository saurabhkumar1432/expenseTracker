package com.example.expensetracker

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.data.Prefs
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {
    private val paymentMethods = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Load existing payment methods
        val existingMethods = Prefs.getModes(this)
        paymentMethods.clear()
        paymentMethods.addAll(existingMethods)
        
        // Show the payment methods management dialog immediately
        showPaymentMethodsManagementDialog()
    }

    private fun showPaymentMethodsManagementDialog() {
        val displayText = if (paymentMethods.isEmpty()) {
            "No payment methods added yet.\n\nUse the buttons below to get started!"
        } else {
            "Current Payment Methods:\n\n" + 
            paymentMethods.mapIndexed { index, method ->
                "${index + 1}. $method"
            }.joinToString("\n")
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Payment Methods")
            .setMessage(displayText)
            .setPositiveButton("Add New") { _, _ ->
                showAddPaymentMethodDialog()
            }
            .setNeutralButton("Manage") { _, _ ->
                if (paymentMethods.isNotEmpty()) {
                    showManageMethodsDialog()
                } else {
                    Toast.makeText(this, "No methods to manage. Add some first!", Toast.LENGTH_SHORT).show()
                    showAddPaymentMethodDialog()
                }
            }
            .setNegativeButton("Done") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showAddPaymentMethodDialog() {
        val editText = EditText(this).apply {
            hint = "Enter payment method name (e.g., UPI, Cash)"
            setPadding(50, 30, 50, 30)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Payment Method")
            .setMessage("Enter a new payment method for your expense tracking")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val methodName = editText.text.toString().trim()
                if (methodName.isNotEmpty() && !paymentMethods.contains(methodName)) {
                    paymentMethods.add(methodName)
                    savePaymentMethods()
                    Toast.makeText(this, "'$methodName' added successfully!", Toast.LENGTH_SHORT).show()
                    showPaymentMethodsManagementDialog()
                } else if (paymentMethods.contains(methodName)) {
                    Toast.makeText(this, "This method already exists!", Toast.LENGTH_SHORT).show()
                    showAddPaymentMethodDialog()
                } else {
                    Toast.makeText(this, "Please enter a valid name!", Toast.LENGTH_SHORT).show()
                    showAddPaymentMethodDialog()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                showPaymentMethodsManagementDialog()
            }
            .setCancelable(false)
            .show()
    }

    private fun showManageMethodsDialog() {
        val options = paymentMethods.toTypedArray()
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Method to Edit/Delete")
            .setItems(options) { _, which ->
                val selectedMethod = paymentMethods[which]
                showMethodOptionsDialog(selectedMethod, which)
            }
            .setNegativeButton("Back") { _, _ ->
                showPaymentMethodsManagementDialog()
            }
            .setCancelable(false)
            .show()
    }

    private fun showMethodOptionsDialog(methodName: String, position: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle("'$methodName'")
            .setMessage("What would you like to do with this payment method?")
            .setPositiveButton("Edit") { _, _ ->
                editPaymentMethod(position, methodName)
            }
            .setNegativeButton("Delete") { _, _ ->
                deletePaymentMethod(position, methodName)
            }
            .setNeutralButton("Back") { _, _ ->
                showManageMethodsDialog()
            }
            .setCancelable(false)
            .show()
    }

    private fun editPaymentMethod(position: Int, currentName: String) {
        val editText = EditText(this).apply {
            setText(currentName)
            setPadding(50, 30, 50, 30)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Payment Method")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty() && newName != currentName) {
                    if (!paymentMethods.contains(newName)) {
                        paymentMethods[position] = newName
                        savePaymentMethods()
                        Toast.makeText(this, "Updated to '$newName'!", Toast.LENGTH_SHORT).show()
                        showPaymentMethodsManagementDialog()
                    } else {
                        Toast.makeText(this, "This method already exists!", Toast.LENGTH_SHORT).show()
                        editPaymentMethod(position, currentName)
                    }
                } else {
                    showManageMethodsDialog()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                showManageMethodsDialog()
            }
            .setCancelable(false)
            .show()
    }

    private fun deletePaymentMethod(position: Int, methodName: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Payment Method")
            .setMessage("Are you sure you want to delete '$methodName'?\n\nThis action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                paymentMethods.removeAt(position)
                savePaymentMethods()
                Toast.makeText(this, "'$methodName' deleted!", Toast.LENGTH_SHORT).show()
                showPaymentMethodsManagementDialog()
            }
            .setNegativeButton("Cancel") { _, _ ->
                showManageMethodsDialog()
            }
            .setCancelable(false)
            .show()
    }

    private fun savePaymentMethods() {
        Prefs.saveModes(this, paymentMethods)
    }
}
