package com.example.pillreminder.screen

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

    var currentUser by remember { mutableStateOf(auth.currentUser) }
    val isLoggedIn = currentUser != null
    val userName = currentUser?.displayName ?: "Failed to load the information"
    val email = currentUser?.email ?: "Failed to load the information"

    val context = LocalContext.current

    Column (
        modifier = Modifier.fillMaxSize().padding(25.dp),
    ) {
        Text(
            fontSize = 20.sp,
            text = "My Profile"
        )
        if (isLoggedIn) {
            Text("Name: $userName!")
            Text("Connected Email: $email")

            Spacer(modifier = Modifier.padding(10.dp))

            Button(onClick = {
                updateData()
                AuthUI.getInstance()
                    .signOut(context)
                    .addOnCompleteListener{
                        currentUser = FirebaseAuth.getInstance().currentUser
                    }
            }) {
                Text("Log Out")
            }
            Text("Data will remain in your phone even if you log out.")
        } else {
            SignInScreen(
                onSignIn = {
                    loadData()
                    currentUser = FirebaseAuth.getInstance().currentUser
                }
            )
        }
    }

}