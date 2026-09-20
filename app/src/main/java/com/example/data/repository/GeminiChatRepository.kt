package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChatBotRole
import com.example.data.model.ChatMessage
import com.example.data.model.ChatRole
import com.example.data.model.GeminiContent
import com.example.data.model.GeminiGenerateRequest
import com.example.data.model.GeminiGenerateResponse
import com.example.data.model.GeminiGenerationConfig
import com.example.data.model.GeminiModelType
import com.example.data.model.GeminiPart
import com.example.util.ChatTextFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class GeminiChatRepository(
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    companion object {
        private const val TAG = "GeminiChatRepository"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

        @Volatile
        private var INSTANCE: GeminiChatRepository? = null

        fun getInstance(): GeminiChatRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GeminiChatRepository().also { INSTANCE = it }
            }
        }
    }

    fun isApiKeyConfigured(customKey: String? = null): Boolean {
        if (!customKey.isNullOrBlank()) return true
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
        return buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY"
    }

    suspend fun sendMessage(
        history: List<ChatMessage>,
        newPrompt: String,
        role: ChatBotRole,
        modelType: GeminiModelType,
        customApiKey: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = when {
            !customApiKey.isNullOrBlank() -> customApiKey.trim()
            else -> {
                val defaultKey = try {
                    BuildConfig.GEMINI_API_KEY
                } catch (e: Throwable) {
                    ""
                }
                if (defaultKey.isNotBlank() && defaultKey != "MY_GEMINI_API_KEY") {
                    defaultKey.trim()
                } else {
                    ""
                }
            }
        }

        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException(
                    "Gemini API key is not configured. Please add GEMINI_API_KEY in the AI Studio Secrets panel, or configure it via the Key Settings icon above."
                )
            )
        }

        try {
            // Build conversation history for multi-turn chat
            val contentsList = mutableListOf<GeminiContent>()

            // Add previous conversational turns (skipping errors)
            history.filter { !it.isError && it.text.isNotBlank() }.forEach { msg ->
                contentsList.add(
                    GeminiContent(
                        role = if (msg.role == ChatRole.USER) "user" else "model",
                        parts = listOf(GeminiPart(text = msg.text))
                    )
                )
            }

            // Append the new user prompt
            contentsList.add(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = newPrompt))
                )
            )

            val systemInstructionContent = GeminiContent(
                parts = listOf(GeminiPart(text = role.systemInstruction))
            )

            val requestBodyObj = GeminiGenerateRequest(
                contents = contentsList,
                systemInstruction = systemInstructionContent,
                generationConfig = GeminiGenerationConfig(
                    temperature = when (modelType) {
                        GeminiModelType.COMPLEX -> 0.4f
                        GeminiModelType.GENERAL -> 0.7f
                        GeminiModelType.FAST -> 0.6f
                    }
                )
            )

            val requestJson = json.encodeToString(requestBodyObj)
            val url = "$BASE_URL/${modelType.modelId}:generateContent?key=$apiKey"

            val httpRequest = Request.Builder()
                .url(url)
                .post(requestJson.toRequestBody(jsonMediaType))
                .addHeader("Content-Type", "application/json")
                .build()

            val response = okHttpClient.newCall(httpRequest).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API HTTP ${response.code}: $responseBody")
                val errorMessage = try {
                    val errorDto = json.decodeFromString<GeminiGenerateResponse>(responseBody)
                    errorDto.error?.message ?: "API returned HTTP ${response.code}"
                } catch (e: Exception) {
                    "HTTP ${response.code}: ${response.message.ifBlank { "Request failed" }}"
                }
                return@withContext Result.failure(Exception(errorMessage))
            }

            val generateResponse = json.decodeFromString<GeminiGenerateResponse>(responseBody)
            val candidateText = generateResponse.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text

            if (candidateText.isNullOrBlank()) {
                return@withContext Result.failure(Exception("Received empty response from Gemini model."))
            }

            val cleanedText = ChatTextFormatter.clean(candidateText)
            Result.success(cleanedText.ifBlank { candidateText.trim() })
        } catch (e: Exception) {
            Log.e(TAG, "Exception calling Gemini API", e)
            Result.failure(e)
        }
    }
}
