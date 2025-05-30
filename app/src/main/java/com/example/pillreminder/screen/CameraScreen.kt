package com.example.pillreminder.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.pillreminder.card.AddPillCard
import com.example.pillreminder.card.CameraPreviewView
import com.example.pillreminder.model.gemini.GeminiRepository
import com.example.pillreminder.model.reminder.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executor


@Composable
fun CameraScreen() {
    val context = LocalContext.current
    // Check for camera permission
    var hasCameraPermission by remember { mutableStateOf(false) }
    var pillAdded by remember { mutableStateOf(false) }
    var pillInformation by remember { mutableStateOf<Reminder?>(null) }
    var showAddCard by remember { mutableStateOf(false) }

    CameraPermissionHandler(onPermissionGranted = {hasCameraPermission = true})

    if (hasCameraPermission) {
        var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
        //CameraPreview
        CameraPreviewView(onImageCaptureReady = { imageCapture = it })

        //Camera Buttons
        Box (
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.BottomCenter
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                imageCapture?.let { capture ->
                    val outputFile = File(context.cacheDir, "captured_image.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

                    val executor: Executor = ContextCompat.getMainExecutor(context)
                    capture.takePicture(
                        outputOptions,
                        executor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val imageBytes = outputFile.readBytes()
                                    val newReminder = getPillInformationFromCapturedImage(imageBytes)
                                    pillInformation = newReminder
                                    showAddCard = true
                                }
                            }
                            override fun onError(exception: ImageCaptureException) {
                                exception.printStackTrace()
                            }
                        }
                    )
                }
            }) {
                Text("Take Photo")
            }
        }

        // Appear on showAddCard is true
        AddPillCard(
            defaultReminder = pillInformation ?: Reminder(),
            showCard = showAddCard,
            onDismiss = { showAddCard = false },
            onUpdate = {}
        )
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

suspend fun getPillInformationFromCapturedImage(imageBytes: ByteArray) : Reminder {
    val repository = GeminiRepository()
    val result = repository.analyzeImage(imageBytes)

    if (result != null) {
        println("Success!")
        println("Name: ${result.pillName}")
        println("Time: ${result.times}")
        println("DaysOfWeek: ${result.daysOfWeek}")
        println("Cautions: ${result.cautions}")
        val newReminder = Reminder(
            pillName = result.pillName,
            times = result.times,
            daysOfWeek = result.daysOfWeek,
            cautions = result.cautions
        )
        return newReminder
    } else {
        println("Failed to analyze image.")
        return Reminder()
    }
}