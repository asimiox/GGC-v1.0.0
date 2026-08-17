package com.example.data.datasource.remote

import android.util.Log
import com.example.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

/**
 * Singleton provider for the official GGC M.B.Din Supabase client instance.
 * Reads public client credentials securely from BuildConfig via Secrets Gradle Plugin (.env).
 *
 * Follows the project's Clean Architecture:
 * UI -> ViewModel -> Repository -> Data Source (Remote / Local).
 */
object SupabaseClientProvider {
    private const val TAG = "SupabaseClientProvider"

    val client: SupabaseClient by lazy {
        val url = BuildConfig.SUPABASE_URL.trim()
        val anonKey = BuildConfig.SUPABASE_ANON_KEY.trim()

        if (url.isBlank() || url.contains("your-project-id")) {
            Log.w(TAG, "SUPABASE_URL is not configured or using placeholder value.")
        }
        if (anonKey.isBlank() || anonKey.contains("your-supabase-public-anon-key")) {
            Log.w(TAG, "SUPABASE_ANON_KEY is not configured or using placeholder value.")
        }

        val safeUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "https://placeholder.supabase.co"
        }
        val safeKey = if (anonKey.isNotBlank()) anonKey else "placeholder-anon-key"

        createSupabaseClient(
            supabaseUrl = safeUrl,
            supabaseKey = safeKey
        ) {
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
        val url = BuildConfig.SUPABASE_URL.trim()
        val anonKey = BuildConfig.SUPABASE_ANON_KEY.trim()
        return url.isNotBlank() &&
                !url.contains("your-project-id") &&
                (url.startsWith("http://") || url.startsWith("https://")) &&
                anonKey.isNotBlank() &&
                !anonKey.contains("your-supabase-public-anon-key")
    }
}
