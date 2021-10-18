package com.example.blooddonationapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonationapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "My Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        val reference = FirebaseDatabase.getInstance().reference.child("users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    binding.type.setText(snapshot.child("type").value.toString())
                    binding.name.setText(snapshot.child("name").value.toString())
                    binding.idNumber.setText(snapshot.child("idnumber").value.toString())
                    binding.email.setText(snapshot.child("email").value.toString())
                    binding.phoneNumber.setText(snapshot.child("phone").value.toString())
                    binding.bloodGroup.setText(snapshot.child("bloodgroup").value.toString())
                    val profileImage = snapshot.child("image").value.toString()
                    Picasso.get().load(profileImage).placeholder(R.drawable.profile)
                        .into(binding.profile)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        binding.backButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ProfileActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}