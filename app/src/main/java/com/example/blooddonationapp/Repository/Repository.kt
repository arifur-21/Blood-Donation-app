package com.example.blooddonationapp.Repository

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.blooddonationapp.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.ArrayList

class Repository {
    private var db: FirebaseFirestore? = null
    private val COLLECTION_USER = "User"


   init {
       db = FirebaseFirestore.getInstance()
   }

    // adding user
    fun addUser(user: User) {
        val docRef = db!!.collection(COLLECTION_USER).document()
      //  user.id
        docRef.set(user).addOnCompleteListener { task: Task<Void?> ->
            if (task.isSuccessful) {
                Log.e(ContentValues.TAG, "Save")
            }
        }.addOnFailureListener { e: Exception -> Log.e(ContentValues.TAG, e.localizedMessage) }
    }



    fun getAllUser(): MutableLiveData<List<User>> {
        val userMutableList: MutableLiveData<List<User>> = MutableLiveData()

     /*  db!!.collection(COLLECTION_USER).get().addOnSuccessListener { result->

           val userList : MutableList<User> = ArrayList<User>()
           for (doument in result){
               val user: User = doument.toObject(User::class.java)

               userList.add(user)
           }
           userMutableList.postValue(userList)
       }*/

        val userRef = db!!.collection(COLLECTION_USER)
        val userQuery = userRef.whereEqualTo("type", "recipient")
        val userList : MutableList<User> = ArrayList<User>()
        userQuery.get().addOnSuccessListener { result ->
            for (doument in result) {
                val user: User = doument.toObject(User::class.java)
                userList.add(user)
            }
            userMutableList.postValue(userList)
        }
        return userMutableList
    }



}