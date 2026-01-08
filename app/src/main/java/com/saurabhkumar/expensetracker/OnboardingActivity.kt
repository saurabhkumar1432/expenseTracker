package com.saurabhkumar.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saurabhkumar.expensetracker.data.Prefs
import com.saurabhkumar.expensetracker.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Prefs.isOnboarded(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish(); return
        }
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener {
            val raw = binding.editModes.text.toString()
            val modes = raw.split(',', '\n').map { it.trim() }.filter { it.isNotEmpty() }
            if (modes.isEmpty()) {
                binding.editModes.error = "Enter at least one"
                return@setOnClickListener
            }
            Prefs.saveModes(this, modes)
            Prefs.setOnboarded(this)
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
