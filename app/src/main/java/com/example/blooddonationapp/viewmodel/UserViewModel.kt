package com.example.blooddonationapp.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blooddonationapp.Repository.Repository
import com.example.blooddonationapp.model.User

class UserViewModel(val repository: Repository): ViewModel() {

    fun addUser(user: User){
        repository.addUser(user)
    }


    fun featchAllUser(): MutableLiveData<List<User>>{
        return repository.getAllUser()
    }


}