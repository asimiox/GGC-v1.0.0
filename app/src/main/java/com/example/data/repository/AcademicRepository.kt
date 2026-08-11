package com.example.data.repository

import com.example.data.datasource.AcademicLocalDataSource
import com.example.ui.screens.academics.models.AcademicResource
import com.example.ui.screens.academics.models.Department
import com.example.ui.screens.academics.models.FacultyMember
import com.example.ui.screens.academics.models.Program
import com.example.ui.screens.academics.models.ResourceType
import com.example.ui.screens.academics.models.Subject
import kotlinx.coroutines.flow.Flow

interface AcademicRepository {
    suspend fun getDepartments(): Result<List<Department>>
    suspend fun getDepartmentById(id: String): Result<Department?>
    suspend fun getFacultyByDepartment(departmentId: String): Result<List<FacultyMember>>
    suspend fun searchAcademicContent(query: String, category: String): Result<List<Department>>
    fun isDownloaded(resourceId: String): Boolean
    fun isBookmarked(resourceId: String): Boolean
    fun markDownloaded(resourceId: String)
    fun toggleBookmark(resourceId: String): Boolean
    fun getDownloadedIds(): Flow<Set<String>>
    fun getBookmarkedIds(): Flow<Set<String>>
}

class AcademicRepositoryImpl(
    private val localDataSource: AcademicLocalDataSource = AcademicLocalDataSource()
) : AcademicRepository {

    override suspend fun getDepartments(): Result<List<Department>> {
        return try {
            val list = localDataSource.fetchDepartments()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDepartmentById(id: String): Result<Department?> {
        return try {
            val list = localDataSource.fetchDepartments()
            Result.success(list.find { it.id == id })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFacultyByDepartment(departmentId: String): Result<List<FacultyMember>> {
        return try {
            val faculty = localDataSource.fetchFaculty(departmentId)
            Result.success(faculty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchAcademicContent(query: String, category: String): Result<List<Department>> {
        return try {
            val all = localDataSource.fetchDepartments()
            val filtered = all.filter { dept ->
                val matchesCategory = category == "All" || dept.category.equals(category, ignoreCase = true)
                val matchesQuery = query.isBlank() ||
                        dept.name.contains(query, ignoreCase = true) ||
                        dept.code.contains(query, ignoreCase = true) ||
                        dept.programs.any { prog ->
                            prog.title.contains(query, ignoreCase = true) ||
                                    prog.code.contains(query, ignoreCase = true) ||
                                    prog.semesters.any { sem ->
                                        sem.subjects.any { sub ->
                                            sub.title.contains(query, ignoreCase = true) ||
                                                    sub.code.contains(query, ignoreCase = true)
                                        }
                                    }
                        }
                matchesCategory && matchesQuery
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isDownloaded(resourceId: String): Boolean = localDataSource.isResourceDownloaded(resourceId)

    override fun isBookmarked(resourceId: String): Boolean = localDataSource.isResourceBookmarked(resourceId)

    override fun markDownloaded(resourceId: String) = localDataSource.markResourceDownloaded(resourceId)

    override fun toggleBookmark(resourceId: String): Boolean = localDataSource.toggleResourceBookmark(resourceId)

    override fun getDownloadedIds(): Flow<Set<String>> = localDataSource.getDownloadedResourceIds()

    override fun getBookmarkedIds(): Flow<Set<String>> = localDataSource.getBookmarkedResourceIds()
}
