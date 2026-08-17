package com.example.data.datasource.remote

import android.util.Log
import com.example.data.model.AppRole
import com.example.data.model.AuthResult
import com.example.data.model.UserRoleDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class RbacRemoteDataSource {
    private val client = SupabaseClientProvider.client
    private val json = Json { ignoreUnknownKeys = true }
    private val TAG = "RbacRemoteDataSource"

    /**
     * Fetches the role information for the currently authenticated Supabase user.
     * Uses the security definer RPC function `get_my_role`, or falls back to direct select from `user_roles`.
     */
    suspend fun getMyRole(): AuthResult<UserRoleDto> {
        val currentUser = client.auth.currentUserOrNull()
        if (currentUser == null) {
            return AuthResult.Error("No active authenticated session found.", code = "UNAUTHENTICATED")
        }

        return try {
            // 1. Try secure RPC function
            try {
                val rpcResponse = client.postgrest.rpc("get_my_role")
                val bodyText = rpcResponse.data
                if (bodyText.isNotBlank()) {
                    val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                    val roleStr = jsonObj["role"]?.jsonPrimitive?.content
                    val department = jsonObj["department"]?.jsonPrimitive?.content
                    val assignedAt = jsonObj["assigned_at"]?.jsonPrimitive?.content
                    val authenticated = jsonObj["authenticated"]?.jsonPrimitive?.content?.toBoolean() ?: true

                    if (!roleStr.isNullOrBlank()) {
                        return AuthResult.Success(
                            UserRoleDto(
                                userId = currentUser.id,
                                role = roleStr,
                                department = department,
                                assignedAt = assignedAt,
                                authenticated = authenticated
                            )
                        )
                    }
                }
            } catch (rpcEx: Exception) {
                Log.d(TAG, "get_my_role RPC not available, querying user_roles table: ${rpcEx.message}")
            }

            // 2. Direct table query on user_roles
            val roles = client.from("user_roles")
                .select {
                    filter {
                        eq("user_id", currentUser.id)
                    }
                }.decodeList<UserRoleDto>()

            if (roles.isNotEmpty()) {
                AuthResult.Success(roles.first())
            } else {
                AuthResult.Error("No assigned role found for current user.", code = "ROLE_NOT_FOUND")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user role: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve role authorization.", code = "NETWORK_ERROR")
        }
    }

    /**
     * Queries role for a specific user ID (authorized admins or self only via RLS).
     */
    suspend fun getUserRole(userId: String): AuthResult<UserRoleDto> {
        return try {
            val roles = client.from("user_roles")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }.decodeList<UserRoleDto>()

            if (roles.isNotEmpty()) {
                AuthResult.Success(roles.first())
            } else {
                AuthResult.Error("No role record found for user $userId", code = "ROLE_NOT_FOUND")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching role for user $userId: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to get user role.", code = "NETWORK_ERROR")
        }
    }
}
