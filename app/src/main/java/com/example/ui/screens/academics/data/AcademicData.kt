package com.example.ui.screens.academics.data

import com.example.ui.screens.academics.models.AcademicResource
import com.example.ui.screens.academics.models.Department
import com.example.ui.screens.academics.models.FacultyMember
import com.example.ui.screens.academics.models.Program
import com.example.ui.screens.academics.models.ResourceType
import com.example.ui.screens.academics.models.SemesterData
import com.example.ui.screens.academics.models.Subject

/**
 * Official Academic & Faculty Repository for Govt Graduate College Mandi Bahauddin.
 * Faculty and Administrative Staff verified from official college portal: https://ggcmbdin.edu.pk
 */
object AcademicData {

    val sampleFaculty: List<FacultyMember> = listOf(
        FacultyMember(
            id = "f1",
            departmentId = "urdu",
            name = "Faisal Shahzad",
            designation = "Lecturer",
            qualification = "M.Phil Urdu",
            specialization = "Urdu & Related Studies",
            email = "merab2009@gmail.com",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1786547677_PHOTO-2026-08-12-20-02-08.jpg",
            isStaff = false
        ),
        FacultyMember(
            id = "f2",
            departmentId = "bot",
            name = "Amir Ahmad",
            designation = "Principal",
            qualification = "MSc-Botany",
            specialization = "Botany & Related Studies",
            email = "amir.ahmad@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1779276322_m.amir.jpg",
            isStaff = false
        ),
        FacultyMember(
            id = "f3",
            departmentId = "isl",
            name = "Muhammad Husnain",
            designation = "Lecturer",
            qualification = "M-Phil Islamic Studies",
            specialization = "Islamiyat & Related Studies",
            email = "muhammad.husnain@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 11.31.41 AM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f4",
            departmentId = "isl",
            name = "Dr. Ghulam Murtaza",
            designation = "Lecturer",
            qualification = "PhD Islamic Studies",
            specialization = "Islamiyat & Related Studies",
            email = "ghulam.murtaza@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 11.33.14 AM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f5",
            departmentId = "phy",
            name = "Muhammad Shahzad",
            designation = "Lecturer",
            qualification = "BS - Physics",
            specialization = "Physics & Related Studies",
            email = "muhammad.shahzad@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 12.14.57 PM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f6",
            departmentId = "phy",
            name = "Muhammad Adnan",
            designation = "Lecturer",
            qualification = "M-Phil Physics",
            specialization = "Physics & Related Studies",
            email = "muhammad.adnan@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 1.41.28 PM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f7",
            departmentId = "eng",
            name = "Muhammad Ijaz",
            designation = "Lecturer",
            qualification = "BS - English",
            specialization = "English & Related Studies",
            email = "muhammad.ijaz@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 12.15.20 PM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f8",
            departmentId = "stat",
            name = "Tanvir Ahmad",
            designation = "Lecturer",
            qualification = "M.Phil Statistics",
            specialization = "Statistics & Related Studies",
            email = "tanvirahmad0512@gmail.com",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 11.05.02 AM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f9",
            departmentId = "zoo",
            name = "Kamran Saeed Pracha",
            designation = "Lecturer",
            qualification = "M-Phil Zoology",
            specialization = "Zoology & Related Studies",
            email = "kamran.saeed.pracha@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f10",
            departmentId = "cs",
            name = "Ubaid Ullah",
            designation = "Lecturer",
            qualification = "M.Sc Information Technology",
            specialization = "Information technology & Related Studies",
            email = "ubaid.ullah@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 12.14.25 PM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f11",
            departmentId = "zoo",
            name = "Waqas Arshad",
            designation = "Lecturer - HOD Zoology",
            qualification = "M-Phil Zoology",
            specialization = "Zoology & Related Studies",
            email = "waqas.arshad@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f12",
            departmentId = "stat",
            name = "Khuram Ijaz Aslam",
            designation = "Lecturer - HOD Statistics",
            qualification = "M-Phil Statistics",
            specialization = "Statistics & Related Studies",
            email = "khuram.ijaz.aslam@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1776352923_WhatsApp Image 2026-02-02 at 7.24.15 AM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f13",
            departmentId = "eng",
            name = "Muhammad Sajid Mehmood",
            designation = "Lecturer",
            qualification = "M-Phil English",
            specialization = "English & Related Studies",
            email = "muhammad.sajid.mehmood@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f14",
            departmentId = "bba",
            name = "Tariq Ashraf",
            designation = "Lecturer",
            qualification = "M-Phil Business Administration",
            specialization = "BBA & Related Studies",
            email = "tariq.ashraf@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1776352824_WhatsApp Image 2026-02-09 at 6.53.39 PM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f15",
            departmentId = "pol",
            name = "Asad Ali",
            designation = "Lecturer",
            qualification = "BS - Political Science",
            specialization = "Political Science & Related Studies",
            email = "asad.ali@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f16",
            departmentId = "eng",
            name = "Muhammad Faryad",
            designation = "Assistant Professor",
            qualification = "M.A English",
            specialization = "English & Related Studies",
            email = "muhammad.faryad@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1786589425_1001311263.jpg",
            isStaff = false
        ),
        FacultyMember(
            id = "f17",
            departmentId = "chem",
            name = "Dr. Khalid Mahmood",
            designation = "Assistant Professor",
            qualification = "PhD Chemistry",
            specialization = "Chemistry & Related Studies",
            email = "khalid.mahmood@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f18",
            departmentId = "urdu",
            name = "Zaman Niaz",
            designation = "Assistant Professor",
            qualification = "M-Phil Urdu",
            specialization = "Urdu & Related Studies",
            email = "zaman.niaz@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f19",
            departmentId = "per",
            name = "Naveed Akram",
            designation = "Assistant Professor",
            qualification = "M-Phil Persian",
            specialization = "Persian & Related Studies",
            email = "naveed.akram@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f20",
            departmentId = "hist",
            name = "Saif Ullah Warraich",
            designation = "Assistant Professor - HOD History",
            qualification = "M.A History",
            specialization = "History & Related Studies",
            email = "saif.ullah.warraich@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f21",
            departmentId = "math",
            name = "Shahid Imran",
            designation = "Assistant Professor",
            qualification = "M.Sc Mathematics",
            specialization = "Mathematics & Related Studies",
            email = "shahid.imran@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 12.13.55 PM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f22",
            departmentId = "isl",
            name = "Amjad Javaid Butt",
            designation = "Assistant Professor",
            qualification = "M-Phil Islamic Studies",
            specialization = "Islamiyat & Related Studies",
            email = "amjad.javaid.butt@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1776746383_WhatsApp Image 2026-04-20 at 8.22.52 PM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f23",
            departmentId = "pol",
            name = "Saqib Gulzar",
            designation = "Assistant Professor",
            qualification = "M-Phil Political Science",
            specialization = "Political Science & Related Studies",
            email = "saqib.gulzar@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 12.11.40 PM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f24",
            departmentId = "eng",
            name = "Majid Bashir",
            designation = "Assistant Professor",
            qualification = "M-Phil English",
            specialization = "English & Related Studies",
            email = "majid.bashir@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f25",
            departmentId = "eng",
            name = "Muhammad Ikram Bhatti",
            designation = "Assistant Professor - HOD English",
            qualification = "M.A English",
            specialization = "English & Related Studies",
            email = "muhammad.ikram.bhatti@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1776352798_WhatsApp Image 2026-02-12 at 11.14.51 AM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f26",
            departmentId = "cs",
            name = "Muhammad Faiyaz",
            designation = "Assistant Professor - HOD Information Technology",
            qualification = "M-Phil Computer Science",
            specialization = "Information technology & Related Studies",
            email = "faiyaz@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1779276335_m.faiyaz.jpg",
            isStaff = false
        ),
        FacultyMember(
            id = "f27",
            departmentId = "phy",
            name = "Dr. Adil Mubeen",
            designation = "Assistant Professor",
            qualification = "PhD Physics",
            specialization = "Physics & Related Studies",
            email = "adil.mubeen@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1776352702_WhatsApp Image 2026-02-21 at 9.38.22 AM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f28",
            departmentId = "math",
            name = "Muhammad Latif",
            designation = "Assistant Professor",
            qualification = "M-Phil Mathematics",
            specialization = "Mathematics & Related Studies",
            email = "muhammad.latif@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 12.03.20 PM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f29",
            departmentId = "isl",
            name = "Dr. Azhar Iqbal",
            designation = "Assistant Professor",
            qualification = "PhD Islamic Studies",
            specialization = "Islamiyat & Related Studies",
            email = "azhar.iqbal@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1786539726_1001310710.jpg",
            isStaff = false
        ),
        FacultyMember(
            id = "f30",
            departmentId = "chem",
            name = "Muhammad Umer Minhas",
            designation = "Assistant Professor - HOD Chemistry",
            qualification = "M.Sc Chemistry",
            specialization = "Chemistry & Related Studies",
            email = "muhammad.umer.minhas@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f31",
            departmentId = "isl",
            name = "Muhammad Attique",
            designation = "Assistant Professor",
            qualification = "M-Phil Islamic Studies",
            specialization = "Islamiyat & Related Studies",
            email = "muhammad.attique@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f32",
            departmentId = "math",
            name = "Mumtaz Hussain",
            designation = "Assistant Professor",
            qualification = "M.Sc Mathematics",
            specialization = "Mathematics & Related Studies",
            email = "mumtaz.hussain@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1776352643_MUMTAZ hussain.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f33",
            departmentId = "isl",
            name = "Saifullah",
            designation = "Assistant Professor - HOD Islamiyat",
            qualification = "M-Phil Islamiyat",
            specialization = "Islamiyat & Related Studies",
            email = "saifullah@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1786534994_saif sb pic1.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f34",
            departmentId = "phy",
            name = "Muhammad Asif Zaman",
            designation = "Assistant Professor - HOD Physics",
            qualification = "M.Sc Physics",
            specialization = "Physics & Related Studies",
            email = "muhammad.asif.zaman@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f35",
            departmentId = "eco",
            name = "Ansar Iqbal",
            designation = "Assistant Professor - HOD Economics",
            qualification = "M.A Economics",
            specialization = "Economics & Related Studies",
            email = "ansar.iqbal@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f36",
            departmentId = "pol",
            name = "Muhammad Mansha Khan",
            designation = "Assistant Professor",
            qualification = "M.A Political Science",
            specialization = "Political Science & Related Studies",
            email = "muhammad.mansha.khan@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/images/placeholder.png",
            isStaff = false
        ),
        FacultyMember(
            id = "f37",
            departmentId = "pol",
            name = "Afrasiab",
            designation = "Assistant Professor - HOD Political Science",
            qualification = "M-Phil Political Science",
            specialization = "Political Science & Related Studies",
            email = "afrasiab@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 12.13.22 PM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f38",
            departmentId = "urdu",
            name = "Muhammad Iqbal",
            designation = "Associate Professor - HOD Urdu",
            qualification = "M-Phil Urdu",
            specialization = "Urdu & Related Studies",
            email = "muhammad.iqbal@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1786549588_1001310989.jpg",
            isStaff = false
        ),
        FacultyMember(
            id = "f39",
            departmentId = "per",
            name = "Mujahid Ali",
            designation = "Associate Professor",
            qualification = "M-Phil Persian",
            specialization = "Persian & Related Studies",
            email = "mujahid.ali@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1786535375_Prof. Mujahid Ali pic.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "f40",
            departmentId = "math",
            name = "Dr. Abdul Manan",
            designation = "Vice Principal - Associate Professor - HOD Mathematics",
            qualification = "PhD in Mathematics",
            specialization = "Mathematics & Related Studies",
            email = "abdul.manan@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/1786547618_PHOTO-2026-08-11-10-13-17.jpg",
            isStaff = false
        ),
        FacultyMember(
            id = "f41",
            departmentId = "edu",
            name = "Muhammad Adnan Saghir",
            designation = "Lecturer - HOD Education",
            qualification = "M-Phil Computer Science",
            specialization = "Education & Related Studies",
            email = "adnanravian123@gmail.com",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/faculty/WhatsApp Image 2026-01-31 at 11.04.19 AM.jpeg",
            isStaff = false
        ),
        FacultyMember(
            id = "adm1",
            departmentId = "admin",
            name = "Abdul Razzaq",
            designation = "Head Clerk",
            qualification = "Graduate",
            specialization = "College Administration, Student Records & Accounts",
            email = "headclerk@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/administrative_staff/1787558668_Abdul_Razzq_HC.jpeg",
            isStaff = true
        ),
        FacultyMember(
            id = "adm2",
            departmentId = "admin",
            name = "Zulfqar Ahmad",
            designation = "Superintendent",
            qualification = "BA",
            specialization = "College Administration, Student Records & Accounts",
            email = "zulfqarahmadgondal@gmail.com",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/administrative_staff/1787558440_Zulfqar_Ahmad.jpeg",
            isStaff = true
        ),
        FacultyMember(
            id = "adm3",
            departmentId = "admin",
            name = "Sohail Imran",
            designation = "Superintendent",
            qualification = "MA",
            specialization = "College Administration, Student Records & Accounts",
            email = "sigsahna@gmail.com",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/administrative_staff/1787557514_Sohail_Imran.jpeg",
            isStaff = true
        ),
        FacultyMember(
            id = "adm4",
            departmentId = "admin",
            name = "Ansar Iqbal",
            designation = "Office Clerk",
            qualification = "BS",
            specialization = "College Administration, Student Records & Accounts",
            email = "iansar899@gmail.com",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/administrative_staff/1787560213_Ansar_Iqbal_pic.jpeg",
            isStaff = true
        ),
        FacultyMember(
            id = "adm5",
            departmentId = "admin",
            name = "Mazhar Iqbal",
            designation = "Junior Clerk",
            qualification = "Intermediate",
            specialization = "College Administration, Student Records & Accounts",
            email = "admin@ggcmbdin.edu.pk",
            phone = "+92 (546) 920000",
            photoResId = null,
            imageUrl = "https://ggcmbdin.edu.pk/uploads/administrative_staff/1787565632_mazhar_sb.jpeg",
            isStaff = true
        )
    )

    fun createSampleResources(subjectId: String, subjectCode: String): List<AcademicResource> {
        return listOf(
            AcademicResource(
                id = "${subjectId}_res_outline",
                subjectId = subjectId,
                title = "$subjectCode Official HEC/University Course Syllabus & Weekly Outline",
                type = ResourceType.COURSE_OUTLINE,
                year = "2024",
                fileSize = "1.8 MB",
                uploadedBy = "Academic Board"
            ),
            AcademicResource(
                id = "${subjectId}_res_notes1",
                subjectId = subjectId,
                title = "$subjectCode Unit 1-4 Complete Lecture Notes & Handouts",
                type = ResourceType.LECTURE_NOTES,
                year = "2023",
                fileSize = "4.2 MB",
                uploadedBy = "Course Instructor"
            ),
            AcademicResource(
                id = "${subjectId}_res_past_mid",
                subjectId = subjectId,
                title = "$subjectCode Midterm Past Paper 2023 (Solved with Answer Key)",
                type = ResourceType.PAST_PAPER,
                year = "2023",
                examTerm = "Midterm",
                fileSize = "2.1 MB",
                uploadedBy = "Student Society Archive"
            ),
            AcademicResource(
                id = "${subjectId}_res_past_final",
                subjectId = subjectId,
                title = "$subjectCode Annual/Finalterm Past Examination Paper 2022",
                type = ResourceType.PAST_PAPER,
                year = "2022",
                examTerm = "Finalterm",
                fileSize = "3.0 MB",
                uploadedBy = "Examination Department"
            ),
            AcademicResource(
                id = "${subjectId}_res_past_final2",
                subjectId = subjectId,
                title = "$subjectCode Final Examination Past Paper 2023 (Subjective & Objective)",
                type = ResourceType.PAST_PAPER,
                year = "2023",
                examTerm = "Finalterm",
                fileSize = "2.7 MB",
                uploadedBy = "Examination Department"
            ),
            AcademicResource(
                id = "${subjectId}_res_lab",
                subjectId = subjectId,
                title = "$subjectCode Laboratory Experiments Manual & Practical Guide",
                type = ResourceType.LAB_MANUAL,
                year = "2024",
                fileSize = "5.5 MB",
                uploadedBy = "Department Laboratory In-charge"
            )
        )
    }

    val sampleDepartments: List<Department> = listOf(
        Department(
            id = "cs",
            name = "Department of Information Technology",
            code = "IT",
            category = "IT & CS",
            description = "Fostering computing innovation, software engineering, algorithms, AI, web architectures, and advanced database administration.",
            hodName = "Muhammad Faiyaz",
            hodQualification = "Assistant Professor (M-Phil Computer Science)",
            hodEmail = "faiyaz@ggcmbdin.edu.pk",
            iconName = "Computer",
            facultyCount = 2,
            programs = listOf(
                Program(
                    id = "bsit",
                    departmentId = "cs",
                    title = "BS Information Technology",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSIT",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 132,
                    eligibility = "ICS / F.Sc. Pre-Engineering / FA with Math (Min 50% Marks)",
                    description = "Comprehensive computing program covering programming, networks, databases, web technologies, cybersecurity, and cloud computing.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bsit_s1_it101",
                                    code = "IT-101",
                                    title = "Introduction to Information & Communication Technologies",
                                    creditHours = "3 (2-1)",
                                    category = "University Core",
                                    description = "Basic computing concepts, hardware architecture, operating systems, internet technologies, and basic productivity software.",
                                    syllabusTopics = listOf("Computer Architecture & Organization", "Operating Systems", "Networking Basics", "Productivity Tools"),
                                    recommendedBooks = listOf("Introduction to Computers by Peter Norton"),
                                    resources = createSampleResources("bsit_s1_it101", "IT-101")
                                ),
                                Subject(
                                    id = "bsit_s1_cs102",
                                    code = "CS-102",
                                    title = "Programming Fundamentals",
                                    creditHours = "4 (3-1)",
                                    category = "Major Core",
                                    description = "Problem-solving techniques, procedural programming paradigms, control flow, functions, arrays, pointers, and file I/O in C++.",
                                    syllabusTopics = listOf("Problem Solving & Algorithms", "Variables & Control Structures", "Functions & Recursion", "Arrays & Pointers", "File Handling"),
                                    recommendedBooks = listOf("C++ How to Program by Deitel & Deitel"),
                                    resources = createSampleResources("bsit_s1_cs102", "CS-102")
                                )
                            )
                        )
                    )
                ),
                Program(
                    id = "bscs",
                    departmentId = "cs",
                    title = "BS Computer Science",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSCS",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 132,
                    eligibility = "ICS / F.Sc. Pre-Engineering (Min 50% Marks)",
                    description = "Rigorous computer science curriculum emphasizing algorithms, data structures, artificial intelligence, compiler construction, and software engineering.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bscs_s1_cs101",
                                    code = "CS-101",
                                    title = "Programming Fundamentals (C++)",
                                    creditHours = "4 (3-1)",
                                    category = "Major Core",
                                    description = "Core programming paradigms, syntax, memory management, and algorithmic thinking.",
                                    syllabusTopics = listOf("Variables & Logic", "Loops & Branches", "Functions & Structs", "Pointers & Arrays"),
                                    recommendedBooks = listOf("C++ How to Program by Deitel & Deitel"),
                                    resources = createSampleResources("bscs_s1_cs101", "CS-101")
                                )
                            )
                        )
                    )
                )
            )
        ),
        Department(
            id = "phy",
            name = "Department of Physics",
            code = "PHY",
            category = "Sciences",
            description = "Fostering deep understanding of theoretical and experimental physics, quantum mechanics, classical mechanics, electromagnetism, optics, and solid-state physics.",
            hodName = "Muhammad Asif Zaman",
            hodQualification = "Assistant Professor - HOD (M.Sc Physics)",
            hodEmail = "physics.hod@ggcmbdin.edu.pk",
            iconName = "Science",
            facultyCount = 4,
            programs = listOf(
                Program(
                    id = "bsphy",
                    departmentId = "phy",
                    title = "BS Physics",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSPHY",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 134,
                    eligibility = "F.Sc. Pre-Engineering (Min 50% Marks)",
                    description = "Comprehensive study of fundamental laws of nature, classical physics, electrodynamics, thermodynamics, quantum theory, and nuclear physics.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bsphy_s1_phy101",
                                    code = "PHY-101",
                                    title = "Mechanics & Waves",
                                    creditHours = "4 (3-1)",
                                    category = "Major Core",
                                    description = "Newtonian mechanics, rotational dynamics, gravitation, simple harmonic motion, wave interference and resonance.",
                                    syllabusTopics = listOf("Vectors & Kinematics", "Newton's Laws", "Rotational Dynamics", "Wave Phenomena"),
                                    recommendedBooks = listOf("Physics Vol 1 by Resnick, Halliday, Krane"),
                                    resources = createSampleResources("bsphy_s1_phy101", "PHY-101")
                                )
                            )
                        )
                    )
                )
            )
        ),
        Department(
            id = "chem",
            name = "Department of Chemistry",
            code = "CHEM",
            category = "Sciences",
            description = "Offering state-of-the-art laboratory training and theoretical education across Organic, Inorganic, Physical, Analytical, and Applied Chemistry.",
            hodName = "Muhammad Umer Minhas",
            hodQualification = "Assistant Professor - HOD (M.Sc Chemistry)",
            hodEmail = "chemistry.hod@ggcmbdin.edu.pk",
            iconName = "Science",
            facultyCount = 2,
            programs = listOf(
                Program(
                    id = "bschem",
                    departmentId = "chem",
                    title = "BS Chemistry",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSCHEM",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 136,
                    eligibility = "F.Sc. Pre-Medical / Pre-Engineering (Min 50% Marks)",
                    description = "Rigorous 4-year degree covering chemical reaction mechanisms, molecular spectroscopy, organic synthesis, electrochemistry, and industrial chemical processes.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bschem_s1_ch101",
                                    code = "CH-101",
                                    title = "Inorganic Chemistry I",
                                    creditHours = "4 (3-1)",
                                    category = "Major Core",
                                    description = "Atomic structure, periodic trends, chemical bonding, acid-base theories, and metallurgy.",
                                    syllabusTopics = listOf("Atomic Theory", "Chemical Bonding", "s and p Block Elements", "Qualitative Analysis"),
                                    recommendedBooks = listOf("Concise Inorganic Chemistry by J.D. Lee"),
                                    resources = createSampleResources("bschem_s1_ch101", "CH-101")
                                )
                            )
                        )
                    )
                )
            )
        ),
        Department(
            id = "eng",
            name = "Department of English Literature & Linguistics",
            code = "ENG",
            category = "Humanities",
            description = "Cultivating analytical thinking, creative expression, postcolonial literature, classical drama, poetry, and modern applied linguistics.",
            hodName = "Muhammad Ikram Bhatti",
            hodQualification = "Assistant Professor - HOD (M.A English)",
            hodEmail = "english.hod@ggcmbdin.edu.pk",
            iconName = "Book",
            facultyCount = 5,
            programs = listOf(
                Program(
                    id = "bseng",
                    departmentId = "eng",
                    title = "BS English",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSENG",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 130,
                    eligibility = "FA / F.Sc. / ICS with English Compulsory (Min 50% Marks)",
                    description = "Comprehensive exploration of Elizabethan drama, Victorian novel, Romantic poetry, literary criticism, phonetics, syntax, and discourse analysis.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bseng_s1_eng101",
                                    code = "ENG-101",
                                    title = "Introduction to English Literature",
                                    creditHours = "3 (3-0)",
                                    category = "Major Core",
                                    description = "Introduction to genres of literature: poetry, drama, novel, and short story with representative texts.",
                                    syllabusTopics = listOf("Literary Genres", "Poetry Analysis", "Short Stories", "Elements of Drama"),
                                    recommendedBooks = listOf("An Introduction to Literary Studies by Mario Klarer"),
                                    resources = createSampleResources("bseng_s1_eng101", "ENG-101")
                                )
                            )
                        )
                    )
                )
            )
        ),
        Department(
            id = "eco",
            name = "Economics & Social Sciences",
            code = "ECO",
            category = "Humanities",
            description = "Examining microeconomics, macroeconomics, econometrics, public finance, development economics, and political economy of Pakistan.",
            hodName = "Ansar Iqbal",
            hodQualification = "Assistant Professor - HOD (M.A Economics)",
            hodEmail = "economics.hod@ggcmbdin.edu.pk",
            iconName = "Psychology",
            facultyCount = 1,
            programs = listOf(
                Program(
                    id = "bseco",
                    departmentId = "eco",
                    title = "BS Economics",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSECO",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 130,
                    eligibility = "FA / F.Sc. / I.Com (Min 50% Marks)",
                    description = "Analytical degree in economic modeling, market dynamics, fiscal policy, international trade, and quantitative research methods.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bseco_s1_eco101",
                                    code = "ECO-101",
                                    title = "Principles of Microeconomics",
                                    creditHours = "3 (3-0)",
                                    category = "Major Core",
                                    description = "Consumer theory, supply & demand elasticities, production costs, market structures, and welfare economics.",
                                    syllabusTopics = listOf("Supply & Demand", "Consumer Behavior", "Cost Curves", "Market Structures"),
                                    recommendedBooks = listOf("Principles of Microeconomics by N. Gregory Mankiw"),
                                    resources = createSampleResources("bseco_s1_eco101", "ECO-101")
                                )
                            )
                        )
                    )
                )
            )
        ),
        Department(
            id = "math",
            name = "Department of Mathematics",
            code = "MATH",
            category = "Sciences",
            description = "Empowering students in pure and applied mathematics, calculus, linear algebra, numerical analysis, complex variables, and mathematical modeling.",
            hodName = "Dr. Abdul Manan",
            hodQualification = "Vice Principal - Associate Professor (PhD in Mathematics)",
            hodEmail = "mathematics.hod@ggcmbdin.edu.pk",
            iconName = "School",
            facultyCount = 4,
            programs = listOf(
                Program(
                    id = "bsmath",
                    departmentId = "math",
                    title = "BS Mathematics",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSMATH",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 132,
                    eligibility = "F.Sc. Pre-Engineering / ICS with Math (Min 50% Marks)",
                    description = "In-depth study of abstract algebra, calculus, mathematical physics, topology, and computational math.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bsmath_s1_math101",
                                    code = "MATH-101",
                                    title = "Calculus & Analytical Geometry",
                                    creditHours = "4 (4-0)",
                                    category = "Major Core",
                                    description = "Limits, differentiation, integration, curves, vectors in space, and analytic geometry.",
                                    syllabusTopics = listOf("Functions & Limits", "Techniques of Differentiation", "Applications of Integrals", "Conic Sections"),
                                    recommendedBooks = listOf("Calculus by Howard Anton"),
                                    resources = createSampleResources("bsmath_s1_math101", "MATH-101")
                                )
                            )
                        )
                    )
                )
            )
        ),
        Department(
            id = "zoo",
            name = "Department of Zoology",
            code = "ZOO",
            category = "Life Sciences",
            description = "Fostering excellence in biological sciences, biodiversity, wildlife conservation, developmental biology, animal physiology, and modern genetics.",
            hodName = "Waqas Arshad",
            hodQualification = "Lecturer - HOD (M-Phil Zoology)",
            hodEmail = "zoology.hod@ggcmbdin.edu.pk",
            iconName = "Science",
            facultyCount = 2,
            programs = listOf(
                Program(
                    id = "bszoo",
                    departmentId = "zoo",
                    title = "BS Zoology",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSZOO",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 134,
                    eligibility = "F.Sc. Pre-Medical (Min 50% Marks)",
                    description = "Rigorous biological education covering animal anatomy, embryology, biotechnology, environmental biology, and immunology.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bszoo_s1_zoo101",
                                    code = "ZOO-101",
                                    title = "Invertebrate Diversity & Classification",
                                    creditHours = "4 (3-1)",
                                    category = "Major Core",
                                    description = "Evolutionary diversity, functional morphology, and physiological adaptations of invertebrate fauna.",
                                    syllabusTopics = listOf("Protozoa to Echinodermata", "Comparative Anatomy", "Specimen Dissection", "Taxonomy"),
                                    recommendedBooks = listOf("Integrated Principles of Zoology by Hickman"),
                                    resources = createSampleResources("bszoo_s1_zoo101", "ZOO-101")
                                )
                            )
                        )
                    )
                )
            )
        ),
        Department(
            id = "pol",
            name = "Department of Political Science",
            code = "POL",
            category = "Humanities",
            description = "Studying state theories, constitutionalism, comparative politics, international law, strategic studies, and public policy administration.",
            hodName = "Afrasiab",
            hodQualification = "Assistant Professor - HOD (M-Phil Political Science)",
            hodEmail = "politicalscience.hod@ggcmbdin.edu.pk",
            iconName = "Book",
            facultyCount = 4,
            programs = listOf(
                Program(
                    id = "bspol",
                    departmentId = "pol",
                    title = "BS Political Science",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSPOL",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 130,
                    eligibility = "FA / F.Sc. / ICS / I.Com (Min 50% Marks)",
                    description = "Analytical curriculum covering Western and Islamic political thought, geopolitical affairs, diplomacy, and democratic institutions.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bspol_s1_pol101",
                                    code = "POL-101",
                                    title = "Introduction to Political Science",
                                    creditHours = "3 (3-0)",
                                    category = "Major Core",
                                    description = "Definitions of state, sovereignty, forms of government, political ideologies, and citizen rights.",
                                    syllabusTopics = listOf("The State & Sovereignty", "Forms of Government", "Law, Liberty & Equality", "Political Parties"),
                                    recommendedBooks = listOf("Political Science: An Introduction by Michael G. Roskin"),
                                    resources = createSampleResources("bspol_s1_pol101", "POL-101")
                                )
                            )
                        )
                    )
                )
            )
        ),
        Department(
            id = "urdu",
            name = "Department of Urdu",
            code = "URDU",
            category = "Humanities",
            description = "Preserving national linguistic heritage, classical Urdu ghazal, nazm, modern fiction, literary criticism, and creative writing.",
            hodName = "Muhammad Iqbal",
            hodQualification = "Associate Professor - HOD (M-Phil Urdu)",
            hodEmail = "urdu.hod@ggcmbdin.edu.pk",
            iconName = "Book",
            facultyCount = 3,
            programs = listOf(
                Program(
                    id = "bsurdu",
                    departmentId = "urdu",
                    title = "BS Urdu",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSURDU",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 130,
                    eligibility = "FA / F.Sc. / I.Com / ICS (Min 50% Marks)",
                    description = "In-depth literary examination of classical poets, progressive writers movement, linguistics, and research methodology.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bsurdu_s1_urd101",
                                    code = "URD-101",
                                    title = "Study of Classical Urdu Poetry",
                                    creditHours = "3 (3-0)",
                                    category = "Major Core",
                                    description = "Poetic traditions of Mir Taqi Mir, Mirza Ghalib, Momin, and early classical masters.",
                                    syllabusTopics = listOf("Evolution of Urdu Ghazal", "Deccan & Delhi Schools", "Ghalibiyat", "Prosody & Rhetoric"),
                                    recommendedBooks = listOf("Tareekh-e-Adab-e-Urdu by Dr. Jameel Jalibi"),
                                    resources = createSampleResources("bsurdu_s1_urd101", "URD-101")
                                )
                            )
                        )
                    )
                )
            )
        ),
        Department(
            id = "isl",
            name = "Department of Islamic Studies",
            code = "ISL",
            category = "Humanities",
            description = "Nurturing deep scholarship in Quranic Sciences (Tafseer), Hadith Usul, Islamic Jurisprudence (Fiqh), Seerah, and contemporary Islamic ethics.",
            hodName = "Saifullah",
            hodQualification = "Assistant Professor - HOD (M-Phil Islamiyat)",
            hodEmail = "islamiyat.hod@ggcmbdin.edu.pk",
            iconName = "Book",
            facultyCount = 6,
            programs = listOf(
                Program(
                    id = "bsisl",
                    departmentId = "isl",
                    title = "BS Islamic Studies",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSISL",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 132,
                    eligibility = "FA / F.Sc. / ICS / Dars-e-Nizami (Min 50% Marks)",
                    description = "Classical and contemporary Islamic scholarship covering comparative religions, Islamic economics, and Islamic civilization.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bsisl_s1_isl101",
                                    code = "ISL-101",
                                    title = "Ulum al-Quran & Exegesis Principles",
                                    creditHours = "3 (3-0)",
                                    category = "Major Core",
                                    description = "History of Quranic preservation, revelation circumstances (Asbab al-Nuzul), and classical Tafseer methods.",
                                    syllabusTopics = listOf("Compilation of Quran", "Makki & Madani Surahs", "Principles of Tafseer", "Exegesis of Selected Surahs"),
                                    recommendedBooks = listOf("Al-Burhan fi Ulum al-Quran by Imam Zarkashi"),
                                    resources = createSampleResources("bsisl_s1_isl101", "ISL-101")
                                )
                            )
                        )
                    )
                )
            )
        ),
        Department(
            id = "bba",
            name = "Department of Business Administration",
            code = "BBA",
            category = "Commerce",
            description = "Equipping future corporate managers, financial analysts, and entrepreneurs with practical business acumen and ethical leadership.",
            hodName = "Tariq Ashraf",
            hodQualification = "Lecturer (M-Phil Business Administration)",
            hodEmail = "bba.hod@ggcmbdin.edu.pk",
            iconName = "School",
            facultyCount = 1,
            programs = listOf(
                Program(
                    id = "bsbba",
                    departmentId = "bba",
                    title = "BS Business Administration",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BBA",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 132,
                    eligibility = "I.Com / F.Sc. / ICS / FA (Min 50% Marks)",
                    description = "Executive business training in organizational behavior, corporate finance, digital marketing, supply chain, and business strategy.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bsbba_s1_mgt101",
                                    code = "MGT-101",
                                    title = "Principles of Management",
                                    creditHours = "3 (3-0)",
                                    category = "Major Core",
                                    description = "Planning, organizing, leading, controlling, managerial ethics, and corporate decision making.",
                                    syllabusTopics = listOf("Management Theories", "Strategic Planning", "Organizational Design", "Leadership Paradigms"),
                                    recommendedBooks = listOf("Management by Robbins & Coulter"),
                                    resources = createSampleResources("bsbba_s1_mgt101", "MGT-101")
                                )
                            )
                        )
                    )
                )
            )
        ),
        Department(
            id = "stat",
            name = "Department of Statistics",
            code = "STAT",
            category = "Sciences",
            description = "Training analytical minds in quantitative methods, data inference, regression modeling, probability, and biostatistics.",
            hodName = "Khuram Ijaz Aslam",
            hodQualification = "Lecturer - HOD (M-Phil Statistics)",
            hodEmail = "statistics.hod@ggcmbdin.edu.pk",
            iconName = "Science",
            facultyCount = 2,
            programs = emptyList()
        ),
        Department(
            id = "bot",
            name = "Department of Botany",
            code = "BOT",
            category = "Life Sciences",
            description = "Instruction in plant biology, physiology, taxonomy, ecology, and biological sciences under guidance of college principal.",
            hodName = "Amir Ahmad",
            hodQualification = "Principal (MSc-Botany)",
            hodEmail = "principal@ggcmbdin.edu.pk",
            iconName = "Science",
            facultyCount = 1,
            programs = emptyList()
        ),
        Department(
            id = "hist",
            name = "Department of History",
            code = "HIST",
            category = "Humanities",
            description = "Exploring regional history, South Asian heritage, Mughal era, Freedom Movement, and contemporary global history.",
            hodName = "Saif Ullah Warraich",
            hodQualification = "Assistant Professor - HOD (M.A History)",
            hodEmail = "history.hod@ggcmbdin.edu.pk",
            iconName = "Book",
            facultyCount = 1,
            programs = emptyList()
        ),
        Department(
            id = "per",
            name = "Department of Persian",
            code = "PER",
            category = "Humanities",
            description = "Promoting rich Persian classical poetry, Sufi literature, grammar, translation, and historical cultural ties.",
            hodName = "Mujahid Ali",
            hodQualification = "Associate Professor (M-Phil Persian)",
            hodEmail = "persian.hod@ggcmbdin.edu.pk",
            iconName = "Book",
            facultyCount = 2,
            programs = emptyList()
        ),
        Department(
            id = "edu",
            name = "Department of Education",
            code = "EDU",
            category = "Humanities",
            description = "Educational psychology, pedagogy, curriculum design, school leadership, and modern instructional technology.",
            hodName = "Muhammad Adnan Saghir",
            hodQualification = "Lecturer - HOD (M-Phil Computer Science)",
            hodEmail = "adnanravian123@gmail.com",
            iconName = "School",
            facultyCount = 1,
            programs = emptyList()
        ),
        Department(
            id = "admin",
            name = "College Administrative & Office Staff",
            code = "ADMIN",
            category = "Administration",
            description = "Main administrative branch responsible for college admissions, fee collection, student records, examination cells, and day-to-day governance.",
            hodName = "Abdul Razzaq",
            hodQualification = "Head Clerk",
            hodEmail = "headclerk@ggcmbdin.edu.pk",
            iconName = "School",
            facultyCount = 5,
            programs = emptyList()
        )
    )
}
