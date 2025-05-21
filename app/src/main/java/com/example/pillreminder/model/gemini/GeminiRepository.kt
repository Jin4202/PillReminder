package com.example.pillreminder.model.gemini

import android.graphics.BitmapFactory
import com.example.pillreminder.BuildConfig
import com.example.pillreminder.model.reminder.Reminder
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class GeminiRepository() {
    suspend fun analyzeImage(imageBytes: ByteArray): Reminder? {
        val defaultDosageTime = listOf(LocalTime.of(8,30))

        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )

        val bitmap_image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)


        val prompt = """
            This image shows a supplement or prescription label.
            Extract and return the information in exactly the following JSON format:
            {
              "name": "string",
              "times": ["08:30", "23:59"],
              "daysOfWeek": ["Monday", "Wednesday", "Friday"],
              "usage": "Take 2 capsules with water. Do not crush or chew",
              "cautions": "Do not take with alcohol"
            }
            Output Instructions:
            - "name": Product name as printed on the label.
            - "times": List of times in "HH:mm" format when the medicine should be taken. If not specified, return an empty list.
            - "daysOfWeek": List of days when the product should be taken. If the label says "daily", include all days: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"].
            - "usage": String of usage instructions found on the label, such as dosage, method of intake (e.g., "take with water"), and timing instructions (e.g., "before meal"). If none are found, return an empty string.
            - "cautions": String of all caution statements:
              First, extract any warnings or instructions from the label.
              Then, add general safety cautions based on the product name and type (e.g., probiotics).
              If none are found, return an empty string. (State at least one as possible)
            
            Return only a valid JSON object.
            """
        val requestInput = content {image(bitmap_image); text(prompt)}
        val response = generativeModel.generateContent(requestInput)
        val outputText = response.text ?: return null
        val jsonText = outputText
            .trim()
            .removePrefix("```json")
            .removeSuffix("```")
            .trim()
        val cleanJson = JSONObject(jsonText)

        // name
        val pillName = cleanJson.optString("name")

        // times
        val timesArray = cleanJson.optJSONArray("times")
        val times = if (timesArray != null && timesArray.length() > 0) {
            val timeStrings = List(timesArray.length()) { timesArray.getString(it) }
            parseStringToTime(timeStrings)
        } else {
            emptyList()
        }

        // daysOfWeek
        val daysArray = cleanJson.optJSONArray("daysOfWeek")
        val daysOfWeek = if (daysArray != null && daysArray.length() > 0) {
            val dayStrings = List(daysArray.length()) { daysArray.getString(it) }
            parseStringToDays(dayStrings)
        } else {
            emptySet()
        }

        // usage
        val usage = cleanJson.optString("usage")

        // caution
        val cautions = cleanJson.optString("cautions")

        return Reminder(
            pillName = pillName,
            times = times,
            daysOfWeek = daysOfWeek,
            usage = usage,
            cautions = cautions
        )
    }
}

fun parseStringToTime(timesString: List<String>): List<LocalTime> {
    val formatter = DateTimeFormatter.ofPattern("H:mm")
    return timesString.mapNotNull { timeStr ->
        try {
            LocalTime.parse(timeStr, formatter)
        } catch (e: Exception) {
            null
        }
    }.ifEmpty { listOf(LocalTime.of(8, 30)) }
}


fun parseStringToDays(days: List<String>): Set<DayOfWeek> {
    return days.mapNotNull { dayName ->
        try {
            DayOfWeek.valueOf(dayName.uppercase())
        } catch (e: Exception) {
            null
        }
    }.toSet()
}