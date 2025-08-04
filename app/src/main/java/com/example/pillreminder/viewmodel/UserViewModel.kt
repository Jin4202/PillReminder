package com.example.pillreminder.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillreminder.model.db.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class UserViewModel(
    private val db: FirebaseFirestore,
    private val userId: String
) : ViewModel() {

    fun fetchReminders(onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
//            Log.d("UserIDTest", "User ID on fetch: ${userId}")

            val result = UserRepository.fetchReminders(db, userId)
            onComplete(result)
        }
    }

    fun updateReminders(onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
//            Log.d("UserIDTest", "User ID on update: ${userId}")

            val result = UserRepository.updateReminders(db, userId)
            onComplete(result)
        }
    }
}