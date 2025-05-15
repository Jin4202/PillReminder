package com.example.pillreminder.model.gemini.test

import android.content.res.AssetManager
import com.example.pillreminder.model.gemini.GeminiRepository
import kotlinx.coroutines.runBlocking
import com.example.pillreminder.BuildConfig

fun test_gemini_api(assetManager: AssetManager) = runBlocking {
    val apiKey = BuildConfig.GEMINI_API_KEY
    val repository = GeminiRepository(apiKey)

    println("Working directory: " + System.getProperty("user.dir"))
    val inputStream = assetManager.open("test_pill_image.jpg")
    val imageBytes = inputStream.readBytes()

    val result = repository.analyzeImage(imageBytes)

    if (result != null) {
        println("Success!")
        println("Name: ${result.pillName}")
        println("Time: ${result.time}")
        println("DaysOfWeek: ${result.daysOfWeek}")
        println("Cautions: ${result.cautions.joinToString()}")
    } else {
        println("Failed to analyze image.")
    }
}