package com.example.pillreminder.viewmodel

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
            val result = UserRepository.fetchReminders(db, userId)
            onComplete(result)
        }
    }

    fun updateReminders(onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val result = UserRepository.updateReminders(db, userId)
            onComplete(result)
        }
    }
}