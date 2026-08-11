package com.example.data.datasource

import com.example.ui.screens.academics.data.AcademicData
import com.example.ui.screens.academics.models.AcademicResource
import com.example.ui.screens.academics.models.Department
import com.example.ui.screens.academics.models.FacultyMember
import com.example.ui.screens.academics.models.Program
import com.example.ui.screens.academics.models.ResourceType
import com.example.ui.screens.academics.models.Subject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Data source providing academic data locally (and cached).
 * Structured for easy future connection to Supabase / Remote API.
 */
class AcademicLocalDataSource {

    private val _departments = MutableStateFlow<List<Department>>(AcademicData.sampleDepartments)
    val departments: Flow<List<Department>> = _departments.asStateFlow()

    private val _faculty = MutableStateFlow<List<FacultyMember>>(AcademicData.sampleFaculty)
    val faculty: Flow<List<FacultyMember>> = _faculty.asStateFlow()

    private val downloadedResourceIds = MutableStateFlow<Set<String>>(emptySet())
    private val bookmarkedResourceIds = MutableStateFlow<Set<String>>(emptySet())

    suspend fun fetchDepartments(): List<Department> {
        delay(300) // Simulate slight network/DB latency
        return _departments.value
    }

    suspend fun fetchFaculty(departmentId: String? = null): List<FacultyMember> {
        delay(200)
        val all = _faculty.value
        return if (departmentId != null) {
            all.filter { it.departmentId == departmentId }
        } else {
            all
        }
    }

    fun isResourceDownloaded(resourceId: String): Boolean {
        return downloadedResourceIds.value.contains(resourceId)
    }

    fun isResourceBookmarked(resourceId: String): Boolean {
        return bookmarkedResourceIds.value.contains(resourceId)
    }

    fun markResourceDownloaded(resourceId: String) {
        downloadedResourceIds.value = downloadedResourceIds.value + resourceId
    }

    fun toggleResourceBookmark(resourceId: String): Boolean {
        val current = bookmarkedResourceIds.value
        return if (current.contains(resourceId)) {
            bookmarkedResourceIds.value = current - resourceId
            false
        } else {
            bookmarkedResourceIds.value = current + resourceId
            true
        }
    }

    fun getDownloadedResourceIds(): Flow<Set<String>> = downloadedResourceIds.asStateFlow()
    fun getBookmarkedResourceIds(): Flow<Set<String>> = bookmarkedResourceIds.asStateFlow()
}
