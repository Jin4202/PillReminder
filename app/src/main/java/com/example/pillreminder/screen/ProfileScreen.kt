package com.example.pillreminder.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    loadData: () -> Unit,
    updateData: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
    var user by remember { mutableStateOf("username_placeholder") }
    var email by remember { mutableStateOf("email_placeholder") }

    val context = LocalContext.current

    Column (
        modifier = Modifier.fillMaxSize().padding(25.dp),
    ) {
        Text(
            fontSize = 20.sp,
            text = "My Profile"
        )
        if (isLoggedIn) {
            user = auth.currentUser?.displayName ?: "Failed to load the information"
            email = auth.currentUser?.email ?: "Failed to load the information"
            Text("Name: ${user}!")
            Text("Connected Email: ${email}")

            Spacer(modifier = Modifier.padding(10.dp))

            Button(onClick = {
                updateData()
                Log.d("ProfileTest", "Before: ${FirebaseAuth.getInstance().currentUser?.uid}")

                AuthUI.getInstance()
                    .signOut(context)
                    .addOnCompleteListener {
                        isLoggedIn = false
                        Log.d("ProfileTest", "After: ${FirebaseAuth.getInstance().currentUser?.uid}")
                    }
                isLoggedIn = false
            }) {
                Text("Log Out")
            }
            Text("Data will remain in your phone even if you log out.")
        } else {
            SignInScreen(
                onSignIn = {
                    isLoggedIn = true
                    loadData()
                }
            )
        }
    }

}