package com.example.pillreminder.model.db

import android.util.Log
import com.example.pillreminder.model.reminder.ReminderDTO
import com.example.pillreminder.model.reminder.ReminderManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

object UserRepository {
    private val gson = Gson()

    suspend fun fetchReminders(db: FirebaseFirestore, userId: String): Boolean {
        return try {
            val documentSnapshot = db.collection("users").document(userId).get().await()
            val rawList = documentSnapshot.get("reminderList") as? List<Map<String, Any>> ?: emptyList()

            val manager = ReminderManager.getInstance()
            for (item in rawList) {
                val dto = gson.fromJson(gson.toJsonTree(item), ReminderDTO::class.java)
                val added = manager.addReminderFromDTO(dto)
                if (!added) {
                    Log.e("UserRepository", "Failed to add reminder from DTO: $dto")
                    return false
                }
            }
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to fetch reminders", e)
            false
        }
    }

    suspend fun updateReminders(db: FirebaseFirestore, userId: String) : Boolean {
        return try {
            val reminderDTOs = ReminderManager.getInstance().getReminderDTOList()
            db.collection("users").document(userId).update("reminderList", reminderDTOs).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
