package com.example.blooddonationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.blooddonationapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.regDonarBtnId.setOnClickListener {
            val intent = Intent(this, DonorRegisterActivity::class.java)
            startActivity(intent)
        }
        binding.regRecipientBtnId.setOnClickListener {
            val intent = Intent(this, RecipientRegisterActivity::class.java)
            startActivity(intent)
        }

        binding.signInTextBtnId.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}