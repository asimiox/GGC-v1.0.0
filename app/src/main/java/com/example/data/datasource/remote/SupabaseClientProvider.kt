package com.example.data.datasource.remote

import android.util.Log
import com.example.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Singleton provider for the official GGC M.B.Din Supabase client instance.
 * Reads credentials from BuildConfig via Secrets Gradle Plugin (.env / .env.example)
 * with the official project URL: https://mhiudbdnrooipovvonfb.supabase.co
 *
 * Follows the project's Clean Architecture:
 * UI -> ViewModel -> Repository -> Data Source (Remote / Local).
 */
object SupabaseClientProvider {
    private const val TAG = "SupabaseClientProvider"
    const val OFFICIAL_SUPABASE_URL = "https://mhiudbdnrooipovvonfb.supabase.co"
    const val OFFICIAL_SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1oaXVkYmRucm9vaXBvdnZvbmZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODY4OTc0MjAsImV4cCI6MjEwMjQ3MzQyMH0.l3mcZ-oKTCRaGxba_P3s_De30IsawKTyY1lxF5Hv8Cs"

    val client: SupabaseClient by lazy {
        val configUrl = try { BuildConfig.SUPABASE_URL.trim() } catch (_: Exception) { "" }
        val anonKey = try { BuildConfig.SUPABASE_ANON_KEY.trim() } catch (_: Exception) { "" }

        val resolvedUrl = when {
            configUrl.isNotBlank() && !configUrl.contains("your-project-id") && (configUrl.startsWith("http://") || configUrl.startsWith("https://")) -> configUrl
            else -> OFFICIAL_SUPABASE_URL
        }

        val safeKey = when {
            anonKey.isNotBlank() && !anonKey.contains("your-supabase-public-anon-key") -> anonKey
            else -> OFFICIAL_SUPABASE_ANON_KEY
        }

        Log.i(TAG, "Initializing SupabaseClient with URL: $resolvedUrl")

        createSupabaseClient(
            supabaseUrl = resolvedUrl,
            supabaseKey = safeKey
        ) {
            httpEngine = OkHttp.create()
            install(Postgrest)
            install(Auth)
            install(Storage)
            install(Realtime)
        }
    }

    /**
     * Checks if real Supabase credentials have been configured in the environment.
     */
    fun isConfigured(): Boolean {
        val configUrl = try { BuildConfig.SUPABASE_URL.trim() } catch (_: Exception) { "" }
        val anonKey = try { BuildConfig.SUPABASE_ANON_KEY.trim() } catch (_: Exception) { "" }
        val effectiveUrl = if (configUrl.isNotBlank() && !configUrl.contains("your-project-id")) configUrl else OFFICIAL_SUPABASE_URL
        val effectiveKey = if (anonKey.isNotBlank() && !anonKey.contains("your-supabase-public-anon-key")) anonKey else OFFICIAL_SUPABASE_ANON_KEY
        return effectiveUrl.isNotBlank() &&
                !effectiveUrl.contains("your-project-id") &&
                (effectiveUrl.startsWith("http://") || effectiveUrl.startsWith("https://")) &&
                effectiveKey.isNotBlank() &&
                !effectiveKey.contains("your-supabase-public-anon-key")
    }

    /**
     * Translates common backend exceptions (like missing anon API key or auth errors) into clean user messages.
     */
    fun formatErrorMessage(e: Exception, fallback: String): String {
        val msg = e.localizedMessage ?: e.message ?: ""
        return when {
            msg.contains("Invalid API key", ignoreCase = true) || msg.contains("your-supabase-public-anon-key", ignoreCase = true) || msg.contains("401", ignoreCase = true) -> {
                "Supabase Anon Key is missing or invalid. Please check configuration."
            }
            msg.contains("Unable to resolve host", ignoreCase = true) || msg.contains("No address associated", ignoreCase = true) || msg.contains("ConnectException", ignoreCase = true) -> {
                "Unable to connect to the server. Please check your internet connection."
            }
            msg.contains("already registered", ignoreCase = true) || msg.contains("User already exists", ignoreCase = true) -> {
                "This username is already registered. Please login or choose a different username."
            }
            msg.contains("email_address_invalid", ignoreCase = true) -> {
                "Invalid username format. Please use letters and numbers only."
            }
            msg.contains("Invalid login credentials", ignoreCase = true) -> {
                "Incorrect username or password. Please verify your credentials."
            }
            msg.contains("duplicate key", ignoreCase = true) || msg.contains("unique", ignoreCase = true) -> {
                "This Roll Number, Registration Number, or Username is already registered."
            }
            msg.contains("URL:", ignoreCase = true) || msg.contains("Headers:", ignoreCase = true) -> {
                val cleanSummary = msg.substringBefore("\n").substringBefore("URL:").trim()
                if (cleanSummary.isNotBlank()) cleanSummary else fallback
            }
            msg.isNotBlank() -> msg
            else -> fallback
        }
    }
}
