package com.example.blooddonationapp

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blooddonationapp.Repository.Repository
import com.example.blooddonationapp.adapter.UserAdapter
import com.example.blooddonationapp.databinding.ActivityMainBinding
import com.example.blooddonationapp.model.User
import com.example.blooddonationapp.viewmodel.UserViewModel
import com.example.blooddonationapp.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: MutableList<User>
    private lateinit var userRef: DatabaseReference
    private lateinit var userViewModel: UserViewModel
    private val COLLECTION_USER = "User"
    private lateinit var db: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        val nav_name = findViewById<TextView>(R.id.nav_headerUserNameId)
        val nav_bloodGroup = findViewById<TextView>(R.id.nav_headerBloodGroup)
        val nav_type = findViewById<TextView>(R.id.nav_userTypeId)
        val nav_profile = findViewById<ImageView>(R.id.nav_headerProImageId)
        val nav_email = findViewById<TextView>(R.id.nav_headerTvEmailId)

        val newsRepository = Repository()
        val userViewModelFactory = ViewModelFactory(newsRepository)
        userViewModel = ViewModelProvider(this, userViewModelFactory).get(UserViewModel::class.java)

        val reference = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        mAuth = FirebaseAuth.getInstance()
        //progressBar = ProgressBar(this)
        userList = arrayListOf()

        setSupportActionBar(binding.toolbarId)
        supportActionBar!!.title = "Blood Donation App"

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.recycleViewId.setLayoutManager(layoutManager)
        userAdapter = UserAdapter(this, userList)
        binding.recycleViewId.adapter = userAdapter



        val drawerToggle : ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this,
                binding.drawerLayoutId,
                binding.toolbarId,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ){
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                setTitle("Drawer Closeed")
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                setTitle("Drawer Opened")
            }
        }
        drawerToggle.isDrawerIndicatorEnabled = true
        binding.drawerLayoutId.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

       binding.navViewId.setNavigationItemSelectedListener { item: MenuItem ->
           when(item.itemId){
               R.id.aplusId -> {
                   val intent3 = Intent(this@MainActivity, CategorySelectedActivity::class.java)
                   intent3.putExtra("group", "A+")
                   startActivity(intent3)
               }
               R.id.aminusId -> {
                   val intent4 = Intent(this@MainActivity, CategorySelectedActivity::class.java)
                   intent4.putExtra("group", "A-")
                   startActivity(intent4)
               }
               R.id.bplusId -> {
                   val intent9 = Intent(this@MainActivity, CategorySelectedActivity::class.java)
                   intent9.putExtra("group", "B+")
                   startActivity(intent9)
               }
               R.id.bminusId -> {
                   val intent10 = Intent(this@MainActivity, CategorySelectedActivity::class.java)
                   intent10.putExtra("group", "B-")
                   startActivity(intent10)
               }
               R.id.abplusId -> {
                   val intent5 = Intent(this@MainActivity, CategorySelectedActivity::class.java)
                   intent5.putExtra("group", "AB+")
                   startActivity(intent5)
               }
               R.id.abminusId -> {
                   val intent6 = Intent(this@MainActivity, CategorySelectedActivity::class.java)
                   intent6.putExtra("group", "AB-")
                   startActivity(intent6)
               }
               R.id.oplusId -> {
                   val intent7 = Intent(this@MainActivity, CategorySelectedActivity::class.java)
                   intent7.putExtra("group", "O+")
                   startActivity(intent7)
               }
               R.id.ominusId -> {
                   val intent8 = Intent(this@MainActivity, CategorySelectedActivity::class.java)
                   intent8.putExtra("group", "O-")
                   startActivity(intent8)
               }
               R.id.headeProfileId -> {
                   val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                   startActivity(intent)
               }
               R.id.compatible -> {
                   val intent11 = Intent(this@MainActivity, CategorySelectedActivity::class.java)
                   intent11.putExtra("group", "Compatible with me")
                   startActivity(intent11)
               }
               R.id.logoutId -> {
                   FirebaseAuth.getInstance().signOut()
                   val intent1 = Intent(this@MainActivity, LoginActivity::class.java)
                   startActivity(intent1)
                   finish()
               }
           }

           true
       }


        /*reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue().toString()
                    nav_name.text = name
                    val email = snapshot.child("email").getValue().toString()
                    nav_email.text = email
                    val blood = snapshot.child("bloodgroup").getValue().toString()
                    nav_bloodGroup.text = blood
                    val type = snapshot.child("type").getValue().toString()
                    nav_type.text = type
                    val proifleImage = snapshot.child("image").getValue().toString()
                    Picasso.get().load(proifleImage).placeholder(R.drawable.profile).into(
                            nav_profile
                    )

                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })*/

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val type = snapshot.child("type").value.toString()
                    if (type == "donor") {
                        readRecipients()
                    } else {
                        readDonors()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

    }

    private fun readDonors() {

       /* userViewModel.featchAllUser().observe(this, Observer {
            userAdapter.setData(it as ArrayList<User>)
        })*/
        val reference = FirebaseDatabase.getInstance().reference.child("users")
        val query = reference.orderByChild("type").equalTo("donor")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    userList.add(user!!)
                }
                userAdapter.notifyDataSetChanged()
                // progressBar.visibility = View.INVISIBLE
                if (userList.isEmpty()) {
                    Toast.makeText(applicationContext, "No donor", Toast.LENGTH_SHORT).show()
                    // progressBar.visibility = View.INVISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun readRecipients() {

/*

        val userRef = db.collection(COLLECTION_USER)
        val userQuery = userRef.whereEqualTo("type", "recipient")
        userQuery.get().addOnSuccessListener { result ->
            for (doument in result) {
                val user: User = doument.toObject(User::class.java)
                userList.add(user)
                userAdapter.setData(userList as ArrayList<User>)
            }
        }
*/

         /*   userViewModel.featchAllUser().observe(this, Observer {
            userAdapter.setData(it as ArrayList<User>)
        })*/

          val reference = FirebaseDatabase.getInstance().reference.child("users")
       val query = reference.orderByChild("type").equalTo("recipient")
       query.addValueEventListener(object : ValueEventListener {
           override fun onDataChange(snapshot: DataSnapshot) {
               userList.clear()
               for (dataSnapshot in snapshot.children) {
                   val user = dataSnapshot.getValue(User::class.java)
                   userList.add(user!!)
               }
               userAdapter.notifyDataSetChanged()
               // progressBar.visibility = View.INVISIBLE
               if (userList.isEmpty()) {
                   Toast.makeText(applicationContext, "No donor", Toast.LENGTH_SHORT).show()
                   // progressBar.visibility = View.INVISIBLE
               }

           }

           override fun onCancelled(error: DatabaseError) {}
       })


        }

    }

