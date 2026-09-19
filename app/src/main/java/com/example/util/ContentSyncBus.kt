package com.example.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Shared reactive bus for invalidating and synchronizing content (Announcements, Events, Documents)
 * across ViewModels (NoticesViewModel, EventsViewModel, HodViewModel, ContentManagementViewModel).
 */
object ContentSyncBus {
    private val _contentChangedEvents = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val contentChangedEvents: SharedFlow<String> = _contentChangedEvents.asSharedFlow()

    fun emitContentChanged(type: String = "ALL") {
        _contentChangedEvents.tryEmit(type)
    }
}
