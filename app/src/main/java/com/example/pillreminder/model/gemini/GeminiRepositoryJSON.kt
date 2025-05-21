package com.example.pillreminder.model.gemini

import android.util.Base64
import android.util.Log
import com.example.pillreminder.model.reminder.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class GeminiRepositoryJSON(private val apiKey: String) {

    suspend fun analyzeImage(imageBytes: ByteArray): Reminder? = withContext(Dispatchers.IO) {
        val base64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        val promptText = """
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
            """.trimIndent()

        val inlineData = JSONObject()
        inlineData.put("mime_type", "image/jpeg")
        inlineData.put("data", base64)

        val part1 = JSONObject()
        part1.put("inline_data", inlineData)

        val part2 = JSONObject()
        part2.put("text", promptText)

        val partsArray = org.json.JSONArray()
        partsArray.put(part1)
        partsArray.put(part2)

        val contentsObject = JSONObject()
        contentsObject.put("parts", partsArray)

        val contentsArray = org.json.JSONArray()
        contentsArray.put(contentsObject)

        val generationConfig = JSONObject()
        generationConfig.put("temperature", 0.2)

        val payload = JSONObject()
        payload.put("contents", contentsArray)
        payload.put("generation_config", generationConfig)
        val jsonBody = payload.toString()

        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()
        val body = jsonBody.toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: return@withContext null

        try {
            val text = JSONObject(responseBody)
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
            val rawJson = text
                .trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            Log.d("JSON", "JSON: $rawJson")
            val cleanJson = JSONObject(rawJson)
            val pillName = cleanJson.optString("name")
            var time = LocalTime.of(8,0)
            if (!cleanJson.optString("time").trim().equals("null")) {
                time = parseTime(cleanJson.optString("time"))
            }
            val daysOfWeek = parseDays(
                List(cleanJson.optJSONArray("daysOfWeek")?.length() ?: 0) {
                    cleanJson.getJSONArray("daysOfWeek").getString(it)
                }
            )
            val cautions = ""
            return@withContext Reminder(
                pillName = pillName,
                times = listOf(time),
                daysOfWeek = daysOfWeek,
                cautions = cautions
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}

fun parseTime(timeString: String): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("H:mm")
    return LocalTime.parse(timeString, formatter)
}

fun parseDays(days: List<String>): Set<DayOfWeek> {
    return days.mapNotNull { dayName ->
        try {
            DayOfWeek.valueOf(dayName.uppercase())
        } catch (e: Exception) {
            null
        }
    }.toSet()
}