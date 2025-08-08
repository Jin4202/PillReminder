package com.example.pillreminder.screen

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pillreminder.R
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignInScreen(
    onSignIn: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) { res ->
        if (res.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(context, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
            onSignIn()
        } else {
            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Please sign in to save your reminders in cloud!"
        )

        Button(onClick = {
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build()
            )

            val customLayout = AuthMethodPickerLayout.Builder(R.layout.auth_method_picker)
                .setGoogleButtonId(R.id.google_signin_button)
                .setEmailButtonId(R.id.email_signin_button)
                .build()

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                //.setAuthMethodPickerLayout(customLayout)
                .setAvailableProviders(providers)
                .setTheme(R.style.Theme_PillReminder)
                .build()

            launcher.launch(signInIntent)
        }) {
            Text("Sign In")
        }
    }
}
