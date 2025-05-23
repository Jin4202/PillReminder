package com.example.pillreminder.model.gemini.test

import android.content.res.AssetManager
import kotlinx.coroutines.runBlocking
import com.example.pillreminder.model.gemini.GeminiRepository

fun test_gemini_api(assetManager: AssetManager) = runBlocking {
    val repository = GeminiRepository()

    println("Working directory: " + System.getProperty("user.dir"))
    val inputStream = assetManager.open("test_pill_image.jpg")
    val imageBytes = inputStream.readBytes()

    val result = repository.analyzeImage(imageBytes)

    if (result != null) {
        println("Success!")
        println("Name: ${result.pillName}")
        println("Time: ${result.times}")
        println("DaysOfWeek: ${result.daysOfWeek}")
        println("Cautions: ${result.cautions}")
    } else {
        println("Failed to analyze image.")
    }
}