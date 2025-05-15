package com.example.pillreminder.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.pillreminder.card.CameraPreviewView
import com.example.pillreminder.model.gemini.test.test_gemini_api

@Composable
fun CameraScreen() {
    var assetManager = LocalContext.current.assets

    // Check for camera permission
    var hasCameraPermission by remember { mutableStateOf(false) }
    CameraPermissionHandler(onPermissionGranted = {hasCameraPermission = true})

    if (hasCameraPermission) {
        var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

        CameraPreviewView(onImageCaptureReady = { imageCapture = it })
        Button(onClick = {
            imageCapture?.let { capture ->
                test_gemini_api(assetManager)
            }
        }) {
            Text("Take Photo")
        }
    }
}

@Composable
fun CameraPermissionHandler(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val cameraPermission = Manifest.permission.CAMERA
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        } else {
            permissionLauncher.launch(cameraPermission)
        }
    }
}
