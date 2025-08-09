package com.example.expensetracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.data.Prefs
import com.example.expensetracker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val current = Prefs.getModes(this).joinToString("\n")
        binding.editModes.setText(current)

        binding.btnSave.setOnClickListener {
            val modes = binding.editModes.text.toString().split('\n', ',').map { it.trim() }.filter { it.isNotEmpty() }
            if (modes.isEmpty()) {
                binding.editModes.error = "Enter at least one"; return@setOnClickListener
            }
            Prefs.saveModes(this, modes)
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
