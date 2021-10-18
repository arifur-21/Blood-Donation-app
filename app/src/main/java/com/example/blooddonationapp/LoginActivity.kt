package com.example.blooddonationapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.example.blooddonationapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)

        //check user login or logOut

        //check user login or logOut
        authStateListener = FirebaseAuth.AuthStateListener {
            val user = mAuth.getCurrentUser()
            if (user != null) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


        binding.loginSinUpTvId.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtnId.setOnClickListener(View.OnClickListener {
            val email = binding.loginEmailId.text.toString()
            val password = binding.loginPasswordId.text.toString()
            if (TextUtils.isEmpty(email)) {
                binding.loginEmailId.error = "Enter your Email"
                return@OnClickListener
            } else if (TextUtils.isEmpty(password)) {
                binding.loginPasswordId.error = "Enter your Password"
                return@OnClickListener
            } else {
                progressDialog.setMessage("Please wait....")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    progressDialog.dismiss()
                }
            }
        })


    }
}