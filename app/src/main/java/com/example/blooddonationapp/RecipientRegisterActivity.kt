package com.example.blooddonationapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.blooddonationapp.Repository.Repository
import com.example.blooddonationapp.databinding.ActivityRecipientRegisterBinding
import com.example.blooddonationapp.model.User
import com.example.blooddonationapp.viewmodel.UserViewModel
import com.example.blooddonationapp.viewmodel.ViewModelFactory

import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class RecipientRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipientRegisterBinding
    private var myUrl = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mAuth: FirebaseAuth
   // private lateinit var currentUserId: String
    private var imageUri: Uri? = null
    private lateinit var userDatabaseRef: DatabaseReference
    private var storagePostPicRef: StorageReference?=null
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipientRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        userDatabaseRef = FirebaseDatabase.getInstance().reference.child("users")
        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Recipient Image")

        val newsRepository = Repository()
        val userViewModelFactory = ViewModelFactory(newsRepository)
        userViewModel = ViewModelProvider(this, userViewModelFactory).get(UserViewModel::class.java)

        binding.recipientRegSinInTvId.setOnClickListener {
            val intent = Intent(this@RecipientRegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        ///Image picker from device

        ///Image picker from device
        binding.recipentRegProId.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 456)
        }

        binding.recipientRegSignUpBtnId.setOnClickListener{
            val email = binding.recipientRegEmailId.text.toString()
            val password = binding.recipinetRegPasswordId.text.toString()
            val fullName = binding.recipientRegFullNameId.text.toString()
            val idNumber = binding.recipientRegIdNumId.text.toString()
            val phoneNum = binding.recipientRegPhoneNumId.text.toString()
            val bloodGroup = binding.recipientRegSpinnerId.selectedItem.toString()

            if (email.isEmpty()){binding.recipientRegEmailId.setError("Enter your Email") }
            else if (password.isEmpty()){binding.recipinetRegPasswordId.setError("Enter your Password")}
            else if (fullName.isEmpty()){ binding.recipientRegFullNameId.setError("Enter your Full Name")}
            else if (idNumber.isEmpty()){binding.recipientRegIdNumId.setError("Enter your ID Number")}
            else if (phoneNum.isEmpty()){binding.recipientRegPhoneNumId.setError("Enter your phone number")}
            else if (bloodGroup == "Select Blood Groups"){ Toast.makeText(this, "Select Blood group", Toast.LENGTH_SHORT).show()}

            else {

                progressDialog.setTitle("Registering you....")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()


                /// store image firebaseStorage and get image url
                val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw  it
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()


                        //authentication
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Toast.makeText(this, "Error : ${task.exception.toString()}", Toast.LENGTH_SHORT)
                                    .show()
                            } else {

                                val userInfo: MutableMap<String, Any> = HashMap()
                               // userInfo["id"] = currentUserId
                                userInfo["name"] = fullName
                                userInfo["email"] = email
                                userInfo["phone"] = phoneNum
                                userInfo["idnumber"] = idNumber
                                userInfo["bloodgroup"] = bloodGroup
                                userInfo["type"] = "recipient"
                                userInfo["search"] = "donor$bloodGroup"
                                userInfo["image"] = myUrl

                                val type = "recipient"
                                var search = "recipient$bloodGroup"
                                val user = User(fullName,bloodGroup,idNumber,phoneNum,type,myUrl,search)
                                userViewModel.addUser(user)

                                ///user data store
                                userDatabaseRef.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(userInfo).addOnCompleteListener{task->
                                    if (!task.isSuccessful){
                                        Toast.makeText(this,"Error :${task.exception.toString()}",Toast.LENGTH_SHORT).show()
                                        Log.e("tag","error : ${task.exception.toString()}")
                                    }
                                    else{
                                        Toast.makeText(this,"user info added Successfull",Toast.LENGTH_SHORT).show()
                                        val intent = Intent(Intent(this,MainActivity::class.java))
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                        }
                    }

                })

            }
        }
    }
    ///Image picker from device
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 456 && resultCode == Activity.RESULT_OK && data != null)
        {
            imageUri = data.data
            binding.recipentRegProId.setImageURI(imageUri)
            Log.e("Tag", "Erron${imageUri}")
            Toast.makeText(this, "Error${imageUri}", Toast.LENGTH_LONG).show()
        }

    }
}