package com.example.pillreminder.model.db

import android.content.Context
import android.util.Log
import com.example.pillreminder.model.reminder.ReminderDTO
import com.example.pillreminder.model.reminder.ReminderManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

object UserRepository {
    private val gson = Gson()

    suspend fun fetchReminders(context: Context, db: FirebaseFirestore, userId: String): Boolean {
        return try {
            val documentSnapshot = db.collection("users").document(userId).get().await()
            val rawList = documentSnapshot.get("reminderList") as? List<Map<String, Any>> ?: emptyList()

            for (item in rawList) {
                val dto = gson.fromJson(gson.toJsonTree(item), ReminderDTO::class.java)
                val added = ReminderManager.getInstance().addReminderFromDTO(context, dto)
                if (!added) {
                    Log.e("UserRepository", "Failed to add reminder from DTO: $dto")
                    return false
                }
            }
            Log.d("UserRepository", "Fetched reminders successfully")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to fetch reminders", e)
            false
        }
    }

    suspend fun updateReminders(db: FirebaseFirestore, userId: String) : Boolean {
        return try {
            val reminderDTOs = ReminderManager.getInstance().getReminderDTOList()

            db.collection("users").document(userId).set(mapOf("reminderList" to reminderDTOs), SetOptions.merge()).await()
            Log.d("UserRepository", "Updated reminders successfully")
            true
        } catch (e: Exception) {
            false
        }
    }
}
