package com.example.pillreminder.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ProfileScreen(
    loadData: () -> Unit,
    updateData: () -> Unit
) {
    var user = remember { mutableStateOf("username_placeholder") }
    var email = remember { mutableStateOf("email_placeholder") }
    var reminderList = remember { mutableListOf<String>() }


    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = loadData
        ) {
            Text("Load data")
        }

        Button(
            onClick = updateData
        ) {
            Text("Save data")
        }

        Text("User: ${user.value}")
        Text("Email: ${email.value}")
        for (reminder in reminderList) {
            Text("ReminderList: $reminder")
        }
    }
}