package com.example.ui.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log

/**
 * Utility functions for extracting file names, sizes, and byte arrays from Android Content URIs.
 */
object FileUtils {
    private const val TAG = "FileUtils"

    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            result = cursor.getString(nameIndex)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error resolving display name from uri: ${e.message}", e)
            }
        }
        if (result.isNullOrBlank()) {
            result = uri.path?.substringAfterLast('/')
        }
        return result?.ifBlank { null } ?: "file_${System.currentTimeMillis()}"
    }

    fun getFileSize(context: Context, uri: Uri): Long {
        var size: Long = -1
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (sizeIndex != -1) {
                            size = cursor.getLong(sizeIndex)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error resolving size from uri: ${e.message}", e)
            }
        }
        return size
    }

    fun getFileBytes(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading file bytes: ${e.message}", e)
            null
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        val formatted = String.format(java.util.Locale.US, "%.1f", bytes / Math.pow(1024.0, digitGroups.toDouble()))
        return "$formatted ${units.getOrElse(digitGroups) { "MB" }}"
    }
}
