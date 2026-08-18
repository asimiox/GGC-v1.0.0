package com.example.data.datasource.remote

import android.util.Log
import com.example.data.model.AnnouncementDto
import com.example.data.model.AppNotificationDto
import com.example.data.model.AuthResult
import com.example.data.model.CollegeEventDto
import com.example.data.model.CourseOutlineDto
import com.example.data.model.NotificationType
import com.example.data.model.OfficialDocumentDto
import com.example.data.model.ProspectusDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class NotificationRemoteDataSource {
    private val client = SupabaseClientProvider.client
    private val TAG = "NotificationRemoteDS"
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private var realtimeChannel: RealtimeChannel? = null
    private var subscriptionJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _incomingNotifications = MutableSharedFlow<AppNotificationDto>(extraBufferCapacity = 64)
    val incomingNotifications: SharedFlow<AppNotificationDto> = _incomingNotifications.asSharedFlow()

    /**
     * Fetches paginated notifications from Supabase `notifications` table.
     * If the table is empty or doesn't exist yet, falls back to generating notifications from recent published content.
     */
    suspend fun getNotifications(
        limit: Int = 30,
        offset: Int = 0
    ): AuthResult<List<AppNotificationDto>> {
        return try {
            val list = client.from("notifications")
                .select {
                    order("created_at", Order.DESCENDING)
                    range(offset.toLong(), (offset + limit - 1).toLong())
                }.decodeList<AppNotificationDto>()

            if (list.isNotEmpty()) {
                AuthResult.Success(list)
            } else if (offset == 0) {
                // Fallback to synthesizing notifications from published content tables
                fetchSynthesizedNotifications()
            } else {
                AuthResult.Success(emptyList())
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get notifications table (falling back to content sync): ${e.message}")
            if (offset == 0) {
                fetchSynthesizedNotifications()
            } else {
                AuthResult.Error(e.message ?: "Failed to load notifications")
            }
        }
    }

    /**
     * Fallback synthesizer that constructs notifications from recent published announcements,
     * events, official documents, course outlines, and prospectus.
     */
    private suspend fun fetchSynthesizedNotifications(): AuthResult<List<AppNotificationDto>> {
        return try {
            val notifications = mutableListOf<AppNotificationDto>()

            // 1. Announcements
            try {
                val announcements = client.from("announcements")
                    .select {
                        filter { eq("is_published", true) }
                        order("published_at", Order.DESCENDING)
                        range(0, 15)
                    }.decodeList<AnnouncementDto>()

                announcements.forEach { a ->
                    notifications.add(
                        AppNotificationDto(
                            id = "notif_ann_${a.id ?: a.title.hashCode()}",
                            notificationType = if (a.isPinned) NotificationType.ANNOUNCEMENT_PRIORITY.key else NotificationType.ANNOUNCEMENT_NEW.key,
                            title = a.title,
                            message = a.content.take(160),
                            relatedContentId = a.id,
                            contentType = "announcement",
                            departmentId = a.departmentId,
                            isPriority = a.isPinned,
                            isPinned = a.isPinned,
                            createdAt = a.publishedAt ?: a.createdAt
                        )
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Fallback announcements sync skipped: ${e.message}")
            }

            // 2. Events
            try {
                val events = client.from("college_events")
                    .select {
                        filter { eq("is_published", true) }
                        order("created_at", Order.DESCENDING)
                        range(0, 10)
                    }.decodeList<CollegeEventDto>()

                events.forEach { e ->
                    notifications.add(
                        AppNotificationDto(
                            id = "notif_ev_${e.id ?: e.title.hashCode()}",
                            notificationType = NotificationType.EVENT_NEW.key,
                            title = "College Event: ${e.title}",
                            message = "${e.eventDate} at ${e.venue ?: "College Campus"}. ${e.description.take(120)}",
                            relatedContentId = e.id,
                            contentType = "event",
                            departmentId = e.departmentId,
                            createdAt = e.createdAt
                        )
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Fallback events sync skipped: ${e.message}")
            }

            // 3. Official Documents
            try {
                val docs = client.from("official_documents")
                    .select {
                        filter { eq("is_published", true) }
                        order("created_at", Order.DESCENDING)
                        range(0, 10)
                    }.decodeList<OfficialDocumentDto>()

                docs.forEach { d ->
                    notifications.add(
                        AppNotificationDto(
                            id = "notif_doc_${d.id ?: d.title.hashCode()}",
                            notificationType = NotificationType.DOCUMENT_NEW.key,
                            title = "Official Document: ${d.title}",
                            message = "New document published (${d.documentType.replace('_', ' ')}). File: ${d.fileName}",
                            relatedContentId = d.id,
                            contentType = "document",
                            departmentId = d.departmentId,
                            createdAt = d.createdAt
                        )
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Fallback docs sync skipped: ${e.message}")
            }

            // 4. Course Outlines
            try {
                val outlines = client.from("course_outlines")
                    .select {
                        filter { eq("is_published", true) }
                        order("created_at", Order.DESCENDING)
                        range(0, 8)
                    }.decodeList<CourseOutlineDto>()

                outlines.forEach { o ->
                    notifications.add(
                        AppNotificationDto(
                            id = "notif_out_${o.id ?: o.title.hashCode()}",
                            notificationType = NotificationType.COURSE_OUTLINE_NEW.key,
                            title = "Course Syllabus: ${o.title}",
                            message = "Updated syllabus outline uploaded for Semester ${o.semesterNumber}.",
                            relatedContentId = o.id,
                            contentType = "course_outline",
                            departmentId = o.departmentId,
                            createdAt = o.createdAt
                        )
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Fallback outlines sync skipped: ${e.message}")
            }

            // 5. Prospectus
            try {
                val prospectus = client.from("prospectus")
                    .select {
                        filter { eq("is_published", true) }
                        order("created_at", Order.DESCENDING)
                        range(0, 3)
                    }.decodeList<ProspectusDto>()

                prospectus.forEach { p ->
                    notifications.add(
                        AppNotificationDto(
                            id = "notif_pro_${p.id ?: p.title.hashCode()}",
                            notificationType = NotificationType.PROSPECTUS_NEW.key,
                            title = "College Prospectus: ${p.title}",
                            message = "Official admission prospectus for session ${p.academicSession} is available.",
                            relatedContentId = p.id,
                            contentType = "prospectus",
                            createdAt = p.createdAt
                        )
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Fallback prospectus sync skipped: ${e.message}")
            }

            // Sort all by created timestamp descending
            val sorted = notifications.sortedByDescending { it.createdAt ?: "" }
            AuthResult.Success(sorted)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to synthesize notifications: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to synthesize notifications")
        }
    }

    /**
     * Inserts a new notification record into the `notifications` table.
     */
    suspend fun insertNotification(notification: AppNotificationDto): AuthResult<AppNotificationDto> {
        return try {
            val inserted = client.from("notifications").insert(notification) {
                select()
            }.decodeSingle<AppNotificationDto>()
            AuthResult.Success(inserted)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to insert into notifications table directly: ${e.message}")
            // Emit directly to realtime flow locally as backup
            _incomingNotifications.tryEmit(notification)
            AuthResult.Success(notification)
        }
    }

    /**
     * Starts listening to Supabase Realtime changes for official notifications
     * and content tables (announcements, events, documents, outlines, prospectus).
     */
    fun startRealtimeSubscription() {
        if (subscriptionJob != null && subscriptionJob?.isActive == true) {
            Log.d(TAG, "Realtime subscription is already active. Skipping duplicate.")
            return
        }

        subscriptionJob = scope.launch {
            try {
                Log.d(TAG, "Starting Supabase Realtime channel subscription...")
                val channel = client.channel("ggc_official_notifications_channel")
                realtimeChannel = channel

                // 1. Listen for new records in `notifications` table
                try {
                    val notifFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                        table = "notifications"
                    }
                    launch {
                        notifFlow.collect { action ->
                            when (action) {
                                is PostgresAction.Insert -> {
                                    try {
                                        val notif = json.decodeFromJsonElement<AppNotificationDto>(action.record)
                                        Log.d(TAG, "Realtime notification received: ${notif.title}")
                                        _incomingNotifications.emit(notif)
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Error decoding realtime notification: ${e.message}")
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Could not listen to notifications table flow: ${e.message}")
                }

                // 2. Listen for new announcements
                try {
                    val announcementFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                        table = "announcements"
                    }
                    launch {
                        announcementFlow.collect { action ->
                            when (action) {
                                is PostgresAction.Insert -> {
                                    try {
                                        val ann = json.decodeFromJsonElement<AnnouncementDto>(action.record)
                                        if (ann.isPublished) {
                                            val notif = AppNotificationDto(
                                                id = "rt_ann_${ann.id ?: System.currentTimeMillis()}",
                                                notificationType = if (ann.isPinned) NotificationType.ANNOUNCEMENT_PRIORITY.key else NotificationType.ANNOUNCEMENT_NEW.key,
                                                title = ann.title,
                                                message = ann.content.take(150),
                                                relatedContentId = ann.id,
                                                contentType = "announcement",
                                                departmentId = ann.departmentId,
                                                isPriority = ann.isPinned,
                                                isPinned = ann.isPinned,
                                                createdAt = getCurrentIsoTimestamp()
                                            )
                                            _incomingNotifications.emit(notif)
                                        }
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Error processing realtime announcement: ${e.message}")
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Could not listen to announcements flow: ${e.message}")
                }

                // 3. Listen for college events
                try {
                    val eventFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                        table = "college_events"
                    }
                    launch {
                        eventFlow.collect { action ->
                            when (action) {
                                is PostgresAction.Insert -> {
                                    try {
                                        val event = json.decodeFromJsonElement<CollegeEventDto>(action.record)
                                        if (event.isPublished) {
                                            val notif = AppNotificationDto(
                                                id = "rt_ev_${event.id ?: System.currentTimeMillis()}",
                                                notificationType = NotificationType.EVENT_NEW.key,
                                                title = "New College Event: ${event.title}",
                                                message = "${event.eventDate} at ${event.venue ?: "Campus"}. ${event.description.take(120)}",
                                                relatedContentId = event.id,
                                                contentType = "event",
                                                departmentId = event.departmentId,
                                                createdAt = getCurrentIsoTimestamp()
                                            )
                                            _incomingNotifications.emit(notif)
                                        }
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Error processing realtime event: ${e.message}")
                                    }
                                }
                                is PostgresAction.Update -> {
                                    try {
                                        val event = json.decodeFromJsonElement<CollegeEventDto>(action.record)
                                        val notif = AppNotificationDto(
                                            id = "rt_ev_up_${event.id ?: System.currentTimeMillis()}",
                                            notificationType = NotificationType.EVENT_UPDATE.key,
                                            title = "Event Update: ${event.title}",
                                            message = "Schedule update for ${event.eventDate} at ${event.venue ?: "Campus"}.",
                                            relatedContentId = event.id,
                                            contentType = "event",
                                            departmentId = event.departmentId,
                                            createdAt = getCurrentIsoTimestamp()
                                        )
                                        _incomingNotifications.emit(notif)
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Error processing realtime event update: ${e.message}")
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Could not listen to events flow: ${e.message}")
                }

                // 4. Listen for official documents
                try {
                    val docFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                        table = "official_documents"
                    }
                    launch {
                        docFlow.collect { action ->
                            when (action) {
                                is PostgresAction.Insert -> {
                                    try {
                                        val doc = json.decodeFromJsonElement<OfficialDocumentDto>(action.record)
                                        if (doc.isPublished) {
                                            val notif = AppNotificationDto(
                                                id = "rt_doc_${doc.id ?: System.currentTimeMillis()}",
                                                notificationType = NotificationType.DOCUMENT_NEW.key,
                                                title = "New Official Document: ${doc.title}",
                                                message = "Uploaded: ${doc.fileName} (${doc.documentType.replace('_', ' ')})",
                                                relatedContentId = doc.id,
                                                contentType = "document",
                                                departmentId = doc.departmentId,
                                                createdAt = getCurrentIsoTimestamp()
                                            )
                                            _incomingNotifications.emit(notif)
                                        }
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Error processing realtime document: ${e.message}")
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Could not listen to documents flow: ${e.message}")
                }

                // 5. Listen for course outlines
                try {
                    val outlineFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                        table = "course_outlines"
                    }
                    launch {
                        outlineFlow.collect { action ->
                            when (action) {
                                is PostgresAction.Insert -> {
                                    try {
                                        val outline = json.decodeFromJsonElement<CourseOutlineDto>(action.record)
                                        if (outline.isPublished) {
                                            val notif = AppNotificationDto(
                                                id = "rt_out_${outline.id ?: System.currentTimeMillis()}",
                                                notificationType = NotificationType.COURSE_OUTLINE_NEW.key,
                                                title = "New Course Outline: ${outline.title}",
                                                message = "Semester ${outline.semesterNumber} syllabus outline is published.",
                                                relatedContentId = outline.id,
                                                contentType = "course_outline",
                                                departmentId = outline.departmentId,
                                                createdAt = getCurrentIsoTimestamp()
                                            )
                                            _incomingNotifications.emit(notif)
                                        }
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Error processing realtime outline: ${e.message}")
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Could not listen to outlines flow: ${e.message}")
                }

                // 6. Listen for prospectus updates
                try {
                    val prospectusFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                        table = "prospectus"
                    }
                    launch {
                        prospectusFlow.collect { action ->
                            when (action) {
                                is PostgresAction.Insert, is PostgresAction.Update -> {
                                    try {
                                        val prospectus = json.decodeFromJsonElement<ProspectusDto>(action.record)
                                        if (prospectus.isPublished) {
                                            val notif = AppNotificationDto(
                                                id = "rt_pro_${prospectus.id ?: System.currentTimeMillis()}",
                                                notificationType = NotificationType.PROSPECTUS_NEW.key,
                                                title = "College Prospectus: ${prospectus.title}",
                                                message = "Session ${prospectus.academicSession} prospectus has been updated.",
                                                relatedContentId = prospectus.id,
                                                contentType = "prospectus",
                                                createdAt = getCurrentIsoTimestamp()
                                            )
                                            _incomingNotifications.emit(notif)
                                        }
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Error processing realtime prospectus: ${e.message}")
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Could not listen to prospectus flow: ${e.message}")
                }

                // Subscribe to channel
                channel.subscribe()
                Log.d(TAG, "Supabase Realtime channel subscription active.")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Supabase Realtime channel: ${e.message}", e)
            }
        }
    }

    /**
     * Gracefully closes and unsubscribes from the Realtime channel.
     */
    fun stopRealtimeSubscription() {
        try {
            subscriptionJob?.cancel()
            subscriptionJob = null

            scope.launch {
                try {
                    realtimeChannel?.unsubscribe()
                    realtimeChannel = null
                    Log.d(TAG, "Supabase Realtime subscription stopped cleanly.")
                } catch (e: Exception) {
                    Log.w(TAG, "Error unsubscribing channel: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping subscription: ${e.message}")
        }
    }

    private fun getCurrentIsoTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
}
