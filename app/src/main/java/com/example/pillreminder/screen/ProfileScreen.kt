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
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen() {
    var user = remember { mutableStateOf("username_placeholder") }
    var email = remember { mutableStateOf("email_placeholder") }
    var reminderList = remember { mutableListOf<String>() }

    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users")
        .document("rrzcgqFzboo6YmF4s7mi")

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                docRef.get().addOnSuccessListener { document ->
                    if (document != null) {
                        user.value = document.getString("name") ?: "username_placeholder2"
                        email.value = document.getString("email") ?: "email_placeholder2"
                    }
                }
            }
        ) {
            Text("Read data")
        }

        Button(
            onClick = {

            },
        ) {
            Text("Write data")
        }

        Text("User: ${user.value}")
        Text("Email: ${email.value}")
        for (reminder in reminderList) {
            Text("ReminderList: $reminder")
        }
    }
}