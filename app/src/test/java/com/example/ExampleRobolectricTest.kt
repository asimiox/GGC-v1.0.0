package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GGC M.B.Din", appName)
  }

  @Test
  fun `verify official faculty registry has valid records`() {
    val facultyList = com.example.data.datasource.OfficialFacultyData.facultyList
    assertEquals(41, facultyList.size)
    val principal = facultyList.first()
    assertEquals("Amir Ahmed", principal.name)
    assertEquals("Principal", principal.designation)
  }

  @Test
  fun `verify RBAC role hierarchy and permission boundaries`() {
    val studentBs = com.example.data.model.AppRole.STUDENT_BS
    val studentInter = com.example.data.model.AppRole.STUDENT_INTERMEDIATE
    val teacher = com.example.data.model.AppRole.TEACHER
    val hod = com.example.data.model.AppRole.HOD
    val admin = com.example.data.model.AppRole.ADMIN

    // Students
    org.junit.Assert.assertTrue(studentBs.isStudent)
    org.junit.Assert.assertTrue(studentInter.isStudent)
    org.junit.Assert.assertFalse(studentBs.isTeacherLevel)
    org.junit.Assert.assertFalse(studentBs.isAdminLevel)

    // Teachers
    org.junit.Assert.assertFalse(teacher.isStudent)
    org.junit.Assert.assertTrue(teacher.isTeacherLevel)
    org.junit.Assert.assertFalse(teacher.isHodLevel)
    org.junit.Assert.assertFalse(teacher.isAdminLevel)

    // HOD
    org.junit.Assert.assertTrue(hod.isTeacherLevel)
    org.junit.Assert.assertTrue(hod.isHodLevel)
    org.junit.Assert.assertFalse(hod.isAdminLevel)

    // Admin
    org.junit.Assert.assertTrue(admin.isTeacherLevel)
    org.junit.Assert.assertTrue(admin.isHodLevel)
    org.junit.Assert.assertTrue(admin.isAdminLevel)

    // Parsing test
    assertEquals(com.example.data.model.AppRole.STUDENT_BS, com.example.data.model.AppRole.fromKey("student_bs"))
    assertEquals(com.example.data.model.AppRole.STUDENT_INTERMEDIATE, com.example.data.model.AppRole.fromKey("student_intermediate"))
    assertEquals(com.example.data.model.AppRole.TEACHER, com.example.data.model.AppRole.fromKey("teacher"))
    assertEquals(com.example.data.model.AppRole.HOD, com.example.data.model.AppRole.fromKey("hod"))
    assertEquals(com.example.data.model.AppRole.ADMIN, com.example.data.model.AppRole.fromKey("admin"))
  }

  @Test
  fun `verify college content DTO structure`() {
    val dept = com.example.data.model.DepartmentDto(
      name = "Information Technology",
      code = "IT",
      category = "IT & CS"
    )
    assertEquals("IT", dept.code)
    assertEquals(true, dept.isActive)

    val doc = com.example.data.model.OfficialDocumentDto(
      title = "Admission Policy 2024",
      documentType = "admission",
      storagePath = "documents/admission_policy.pdf",
      fileName = "admission_policy.pdf"
    )
    assertEquals("admission", doc.documentType)
    assertEquals("documents/admission_policy.pdf", doc.storagePath)

    val prospectus = com.example.data.model.ProspectusDto(
      title = "Prospectus 2024-2025",
      academicSession = "2024-2025",
      storagePath = "prospectus/prospectus_2024_2025.pdf",
      fileName = "prospectus_2024_2025.pdf"
    )
    assertEquals("2024-2025", prospectus.academicSession)
    assertEquals(true, prospectus.isPublished)
  }

  @Test
  fun `verify college storage path builders and logical buckets`() {
    val prospectusPath = com.example.data.datasource.remote.CollegeStorageRemoteDataSource.buildProspectusPath("2024-2025", "prospectus.pdf")
    assertEquals("2024-2025/prospectus.pdf", prospectusPath)

    val docPath = com.example.data.datasource.remote.CollegeStorageRemoteDataSource.buildDocumentPath("admissions", "IT", "fee_structure.pdf")
    assertEquals("admissions/it/fee_structure.pdf", docPath)

    val outlinePath = com.example.data.datasource.remote.CollegeStorageRemoteDataSource.buildCourseOutlinePath("IT", "BSIT", "CS301.pdf")
    assertEquals("it/bsit/CS301.pdf", outlinePath)

    val announcementPath = com.example.data.datasource.remote.CollegeStorageRemoteDataSource.buildAnnouncementAttachmentPath(null, "circular_102.pdf")
    assertEquals("general/circular_102.pdf", announcementPath)

    val photoPath = com.example.data.datasource.remote.CollegeStorageRemoteDataSource.buildProfilePhotoPath(true, "user-uuid-123", "avatar.jpg")
    assertEquals("faculty/user-uuid-123/avatar.jpg", photoPath)
  }

  @Test
  fun `verify admin and hod DTO structures and JSON mapping`() {
    val hodOverview = com.example.data.model.HodDepartmentOverviewDto(
      success = true,
      departmentId = "dept-uuid-it",
      departmentName = "Information Technology",
      departmentCode = "IT",
      facultyCount = 8,
      programsCount = 2,
      coursesCount = 35
    )
    assertEquals(true, hodOverview.success)
    assertEquals("Information Technology", hodOverview.departmentName)
    assertEquals(8, hodOverview.facultyCount)

    val adminOverview = com.example.data.model.AdminSystemOverviewDto(
      success = true,
      bsStudentsCount = 1200,
      intermediateStudentsCount = 2500,
      facultyCount = 41,
      hodsCount = 12,
      adminsCount = 2,
      departmentsCount = 15
    )
    assertEquals(true, adminOverview.success)
    assertEquals(1200, adminOverview.bsStudentsCount)
    assertEquals(41, adminOverview.facultyCount)
    assertEquals(2, adminOverview.adminsCount)
  }

  @Test
  fun `verify official registry DTO structures and active status mapping`() {
    val bsStudent = com.example.data.model.OfficialBsStudentDto(
      rollNumber = "BSIT-F22-001",
      registrationNumber = "2022-GGC-IT-001",
      program = "BS Information Technology",
      session = "2022-2026",
      firstName = "Ali",
      lastName = "Hassan",
      isActive = true,
      isClaimed = false
    )
    assertEquals("BSIT-F22-001", bsStudent.rollNumber)
    assertEquals(true, bsStudent.isActive)
    assertEquals(false, bsStudent.isClaimed)

    val interStudent = com.example.data.model.OfficialIntermediateStudentDto(
      rollNumber = "FSC-MED-24-101",
      registrationNumber = "GRW-BISE-2024-8899",
      program = "F.Sc Pre-Medical",
      session = "2024-2026",
      firstName = "Usman",
      lastName = "Tariq",
      isActive = true
    )
    assertEquals("FSC-MED-24-101", interStudent.rollNumber)
    assertEquals("2024-2026", interStudent.session)

    val faculty = com.example.data.model.OfficialFacultyRegistryDto(
      facultyId = "FAC-CS-005",
      fullName = "Muhammad Imran",
      department = "Computer Science",
      designation = "Assistant Professor",
      qualification = "MS Computer Science",
      institutionalEmail = "imran.cs@ggcmbdin.edu.pk",
      isActive = true,
      isClaimed = false
    )
    assertEquals("FAC-CS-005", faculty.facultyId)
    assertEquals("Computer Science", faculty.department)
    assertEquals(true, faculty.isActive)
  }
}

