package com.yesmin.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object GeminiClient {
    suspend fun generate(apiKey: String, model: String, prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val safeModel = model.ifBlank { "gemini-2.5-flash" }
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/$safeModel:generateContent?key=$apiKey")
            val body = JSONObject().put("contents", JSONArray().put(
                JSONObject().put("parts", JSONArray().put(JSONObject().put(
                    "text", "You are YESMIN, a caring, friendly female AI companion and capable assistant. Reply in the user's language, often Bengali or Banglish. Be honest about limitations. User says: $prompt"
                )))
            )).put("generationConfig", JSONObject().put("temperature", 0.8))
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 20000
                readTimeout = 30000
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }
            conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
            val response = stream.bufferedReader().use { it.readText() }
            if (conn.responseCode !in 200..299) {
                val message = JSONObject(response).optJSONObject("error")?.optString("message")
                "Gemini API error (${conn.responseCode}): ${message ?: response.take(250)}"
            } else {
                val json = JSONObject(response)
                json.optJSONArray("candidates")?.optJSONObject(0)
                    ?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)
                    ?.optString("text") ?: "Gemini থেকে কোনো উত্তর পাওয়া যায়নি।"
            }.also { conn.disconnect() }
        } catch (e: Exception) {
            "Gemini সংযোগে সমস্যা: ${e.localizedMessage ?: "Unknown error"}"
        }
    }
}
