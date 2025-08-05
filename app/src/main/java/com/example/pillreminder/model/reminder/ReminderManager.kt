package com.example.pillreminder.model.reminder

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.pillreminder.model.db.ReminderSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Locale

class ReminderManager private constructor() {
    private val reminders = mutableListOf<Reminder>()

    companion object {
        @Volatile
        private var instance: ReminderManager? = null

        fun getInstance(): ReminderManager {
            return instance ?: synchronized(this) {
                instance ?: ReminderManager().also { instance = it }
            }
        }
    }

    fun getReminders(): List<Reminder> {
        return reminders.toList()
    }

    fun addReminder(context: Context, reminder: Reminder) {
        reminders.add(reminder)
        scheduleReminder(context, reminder)
        CoroutineScope(Dispatchers.IO).launch {
            saveToDataStore(context)
        }
    }

    fun removeReminder(context: Context, reminder: Reminder) {
        reminders.remove(reminder)
        cancelReminder(context, reminder)
        CoroutineScope(Dispatchers.IO).launch {
            saveToDataStore(context)
        }
    }

    fun updateReminder(context: Context, oldReminderId: Int, newReminder: Reminder) {
        val index = reminders.indexOfFirst { it.getId() == oldReminderId }
        if (index != -1) {
            val oldReminder = reminders[index]
            cancelReminder(context, oldReminder)
            reminders[index] = newReminder
            scheduleReminder(context, newReminder)
            CoroutineScope(Dispatchers.IO).launch {
                saveToDataStore(context)
            }
        }
    }

    fun getTimeString(localTime: LocalTime) : String {
        val hour = if (localTime.hour % 12 == 0) 12 else localTime.hour % 12
        val minute = String.format(Locale.US, "%02d", localTime.minute)
        val period = if (localTime.hour < 12) "AM" else "PM"
        return "$hour:$minute $period"
    }

    fun addReminderFromDTO(context: Context, dto: ReminderDTO): Boolean {
        return try {
            val reminder = DTOUtils.toReminder(dto)
            var isAdded = false
            for (existReminder in reminders) {
                if (existReminder.getId() == reminder.getId()) {
                    isAdded = true
                }
            }
            if (!isAdded) {
                addReminder(context, reminder)
            }
            true
        } catch (e: Exception) {
            Log.e("ReminderManager", "Error converting ReminderDTO: $dto", e)
            false
        }
    }

    fun getReminderDTOList(): List<ReminderDTO> {
        return reminders.map { DTOUtils.toDTO(it) }
    }

    suspend fun saveToDataStore(context: Context) {
        context.reminderDataStore.updateData {
            ReminderList.newBuilder()
                .addAllReminders(reminders.map { ReminderProtoUtils.toProto(it) })
                .build()
        }
    }

    suspend fun loadFromDataStore(context: Context) {
        val reminderList = context.reminderDataStore.data.first()
        reminders.clear()
        reminders.addAll(reminderList.remindersList.map { ReminderProtoUtils.fromProto(it) })
    }
}

val Context.reminderDataStore: DataStore<ReminderList> by dataStore(
    fileName = "reminders.pb",
    serializer = ReminderSerializer
)
