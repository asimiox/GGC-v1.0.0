package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.AppNotificationDto

object SystemNotificationHelper {
    private const val TAG = "SystemNotification"
    const val CHANNEL_ID = "ggc_official_realtime_alerts"
    private const val CHANNEL_NAME = "GGC College Notices & Announcements"
    private const val CHANNEL_DESC = "Real-time updates, official announcements, exams, and event alerts from Govt. Graduate College Mandi Bahauddin"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
                setShowBadge(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created: $CHANNEL_ID")
        }
    }

    fun showSystemPushNotification(context: Context, notification: AppNotificationDto) {
        try {
            // Check permission on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.w(TAG, "POST_NOTIFICATIONS permission not granted yet.")
                    return
                }
            }

            initNotificationChannel(context)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("extra_notification_id", notification.id)
                putExtra("extra_content_type", notification.contentType)
                putExtra("extra_related_id", notification.relatedContentId)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                (notification.id?.hashCode() ?: System.currentTimeMillis().toInt()),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val titleText = notification.title.ifBlank { "GGC College Update" }
            val messageText = notification.message.ifBlank { "New official announcement published." }

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_ggc_logo)
                .setContentTitle(titleText)
                .setContentText(messageText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(longArrayOf(0, 300, 150, 300))
                .setContentIntent(pendingIntent)
                .setColor(0xFF061B52.toInt())

            val notificationManagerCompat = NotificationManagerCompat.from(context)
            val notificationId = (notification.id?.hashCode() ?: System.currentTimeMillis().toInt()) and 0x7FFFFFFF
            notificationManagerCompat.notify(notificationId, builder.build())
            Log.d(TAG, "System Heads-Up Notification dispatched: $titleText (ID: $notificationId)")
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying system notification: ${e.message}", e)
        }
    }
}
