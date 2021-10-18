package com.example.blooddonationapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonationapp.adapter.UserAdapter
import com.example.blooddonationapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategorySelectedActivity : AppCompatActivity() {
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: MutableList<User>
    private lateinit var userAdapter: UserAdapter
    private var title: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_selected)

        userList = arrayListOf()

        toolbar = findViewById(R.id.categoryToolbar)
         setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.categoryRecycleViewId)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        userAdapter = UserAdapter(this, userList)
        recyclerView.adapter = userAdapter

        if (intent.extras != null) {
            title = intent.getStringExtra("group")!!
            supportActionBar!!.title = "Blood Group $title"
            if (title == "Compatible with me") {
                getCompatibleUsers()
                supportActionBar!!.title = "Compatible with me"
            } else {
                readUsers()
            }
        }

    }

    private fun readUsers() {
        val reference = FirebaseDatabase.getInstance().reference.child("users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result: String
                val type = snapshot.child("type").value.toString()
                result = if (type == "donor") {
                    "recipient"
                } else {
                    "donor"
                }
                val reference1 = FirebaseDatabase.getInstance().reference.child("users")
                val query = reference1.orderByChild("search").equalTo(result + title)
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userList.clear()
                        for (dataSnapshot in snapshot.children) {
                            val user = dataSnapshot.getValue(User::class.java)
                            userList.add(user!!)
                        }
                        userAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getCompatibleUsers() {

        val reference = FirebaseDatabase.getInstance().reference.child("users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result: String
                val type = snapshot.child("type").value.toString()
                if (type == "donor") {
                    result = "recipient"
                } else {
                    result = "donor"
                }
                val bloodgroup = snapshot.child("bloodgroup").value.toString()
                val reference1 = FirebaseDatabase.getInstance().reference.child("users")
                val query = reference1.orderByChild("search").equalTo(result + bloodgroup)
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userList.clear()
                        for (dataSnapshot in snapshot.children) {
                            val user = dataSnapshot.getValue(User::class.java)
                            userList.add(user!!)
                        }
                        userAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}