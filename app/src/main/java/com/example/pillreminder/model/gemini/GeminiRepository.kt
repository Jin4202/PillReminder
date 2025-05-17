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
        val generativeModel =
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = BuildConfig.GEMINI_API_KEY)

        val bitmap_image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)


        val prompt = """
            This image shows a supplement or prescription label.
            Extract and return the information in exactly the following JSON format:
            {
            "name": "string",
            "time": "14:30",
            "daysOfWeek": ["Monday", "Wednesday", "Friday"],
            "cautions": ["Do not take with alcohol"]
            }
            Output Instructions:
            "name": Product name as printed on the label.
            "time": Fixed time of day to take the medicine (e.g., "14:30"). If not specified, return null.
            "daysOfWeek": List of days when the product should be taken.
            If the label says "daily", include all days: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"].
            "cautions": List all caution statements:
            First, extract any warnings or instructions from the label.
            Then, add general safety cautions based on the product name and type (e.g., probiotics).
            Always return at least one caution.
            Return only the valid JSON object.
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

        val pillName = cleanJson.optString("name")
        var time = LocalTime.of(8,0)
        if (!cleanJson.optString("time").trim().equals("null")) {
            time = parseStringToTime(cleanJson.optString("time"))
        }
        val daysOfWeek = parseStringToDays(
            List(cleanJson.optJSONArray("daysOfWeek")?.length() ?: 0) {
                cleanJson.getJSONArray("daysOfWeek").getString(it)
            }
        )
        val cautions = List(cleanJson.optJSONArray("cautions")?.length() ?: 0) {
            cleanJson.getJSONArray("cautions").getString(it)
        }

        return Reminder(
            pillName = pillName,
            time = time,
            daysOfWeek = daysOfWeek,
            cautions = cautions
        )
    }
}

fun parseStringToTime(timeString: String): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("H:mm")
    return LocalTime.parse(timeString, formatter)
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