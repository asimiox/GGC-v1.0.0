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
 * Reads credentials from BuildConfig via Secrets Gradle Plugin (.env / .env.example)
 * with the official project URL: https://mhiudbdnrooipovvonfb.supabase.co
 *
 * Follows the project's Clean Architecture:
 * UI -> ViewModel -> Repository -> Data Source (Remote / Local).
 */
object SupabaseClientProvider {
    private const val TAG = "SupabaseClientProvider"
    const val OFFICIAL_SUPABASE_URL = "https://mhiudbdnrooipovvonfb.supabase.co"

    val client: SupabaseClient by lazy {
        val configUrl = try { BuildConfig.SUPABASE_URL.trim() } catch (_: Exception) { "" }
        val anonKey = try { BuildConfig.SUPABASE_ANON_KEY.trim() } catch (_: Exception) { "" }

        val resolvedUrl = when {
            configUrl.isNotBlank() && !configUrl.contains("your-project-id") && (configUrl.startsWith("http://") || configUrl.startsWith("https://")) -> configUrl
            else -> OFFICIAL_SUPABASE_URL
        }

        if (anonKey.isBlank() || anonKey.contains("your-supabase-public-anon-key")) {
            Log.w(TAG, "SUPABASE_ANON_KEY is using default placeholder. Make sure anon key is provided in .env")
        }

        val safeKey = if (anonKey.isNotBlank()) anonKey else "placeholder-anon-key"

        Log.i(TAG, "Initializing SupabaseClient with URL: $resolvedUrl")

        createSupabaseClient(
            supabaseUrl = resolvedUrl,
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
        val configUrl = try { BuildConfig.SUPABASE_URL.trim() } catch (_: Exception) { "" }
        val anonKey = try { BuildConfig.SUPABASE_ANON_KEY.trim() } catch (_: Exception) { "" }
        val effectiveUrl = if (configUrl.isNotBlank() && !configUrl.contains("your-project-id")) configUrl else OFFICIAL_SUPABASE_URL
        return effectiveUrl.isNotBlank() &&
                !effectiveUrl.contains("your-project-id") &&
                (effectiveUrl.startsWith("http://") || effectiveUrl.startsWith("https://")) &&
                anonKey.isNotBlank() &&
                !anonKey.contains("your-supabase-public-anon-key")
    }
}
