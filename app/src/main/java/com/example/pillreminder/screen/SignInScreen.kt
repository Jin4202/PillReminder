package com.example.pillreminder.screen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pillreminder.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun SignInScreen(
    onSignIn: () -> Unit
) {
    val context = LocalContext.current

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        Toast.makeText(context, "Welcome ${FirebaseAuth.getInstance().currentUser?.displayName}", Toast.LENGTH_SHORT).show()
                        onSignIn()
                    } else {
                        Toast.makeText(context, "Firebase Auth Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Sign-In Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    // UI
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Please sign in to save your reminders in cloud!"
        )

        Button(onClick = {
            val signInIntent: Intent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }) {
            Text("Connect Google Account")
        }
    }
}
