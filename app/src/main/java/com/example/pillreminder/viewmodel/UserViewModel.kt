package com.example.pillreminder.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillreminder.model.db.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class UserViewModel(
) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun fetchReminders(context: Context, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            Log.d("UserViewModel", "User ID on fetch: ${userId}")
            if (userId.isEmpty()) {
                onComplete(false)
                return@launch
            }
            val result = UserRepository.fetchReminders(context, db, userId)
            onComplete(result)
        }
    }

    fun updateReminders(onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            Log.d("UserViewModel", "User ID on update: ${userId}")
            if (userId.isEmpty()) {
                onComplete(false)
                return@launch
            }
            val result = UserRepository.updateReminders(db, userId)
            onComplete(result)
        }
    }
}