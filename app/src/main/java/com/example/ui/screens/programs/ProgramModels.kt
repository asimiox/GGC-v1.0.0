package com.example.ui.screens.programs

import com.example.data.datasource.OfficialFacultyData

enum class ProgramLevel(val displayName: String, val subtitle: String, val countLabel: String) {
    INTERMEDIATE(
        displayName = "Intermediate",
        subtitle = "Higher Secondary School Certificate (2-Year Programs)",
        countLabel = "5 Programs"
    ),
    BS(
        displayName = "BS",
        subtitle = "4-Year Bachelor of Science Honors Degree Programs",
        countLabel = "10 Programs"
    )
}

data class CollegeProgram(
    val id: String,
    val name: String,
    val shortCode: String,
    val level: ProgramLevel,
    val duration: String,
    val eligibility: String,
    val departmentName: String,
    val description: String? = null
) {
    val hod: String
        get() = OfficialFacultyData.getHodForDepartment(departmentName)?.name ?: "Not listed"
}

object OfficialProgramsData {

    val intermediatePrograms = listOf(
        CollegeProgram(
            id = "ics",
            name = "Intermediate in Computer Science (ICS)",
            shortCode = "ICS",
            level = ProgramLevel.INTERMEDIATE,
            duration = "2 Years",
            eligibility = "Matric with Science / Equivalent",
            departmentName = "Information Technology",
            description = null
        ),
        CollegeProgram(
            id = "fsc_pre_eng",
            name = "F.Sc Pre-Engineering",
            shortCode = "F.Sc Pre-Eng",
            level = ProgramLevel.INTERMEDIATE,
            duration = "2 Years",
            eligibility = "Matric with Science / Equivalent",
            departmentName = "Physics",
            description = null
        ),
        CollegeProgram(
            id = "fsc_pre_med",
            name = "F.Sc Pre-Medical",
            shortCode = "F.Sc Pre-Med",
            level = ProgramLevel.INTERMEDIATE,
            duration = "2 Years",
            eligibility = "Matric with Science / Equivalent",
            departmentName = "Zoology",
            description = null
        ),
        CollegeProgram(
            id = "fa",
            name = "F.A (Faculty of Arts)",
            shortCode = "F.A",
            level = ProgramLevel.INTERMEDIATE,
            duration = "2 Years",
            eligibility = "Matric / Equivalent",
            departmentName = "English",
            description = null
        ),
        CollegeProgram(
            id = "icom",
            name = "I.Com (Intermediate in Commerce)",
            shortCode = "I.Com",
            level = ProgramLevel.INTERMEDIATE,
            duration = "2 Years",
            eligibility = "Matric / Equivalent",
            departmentName = "BBA",
            description = null
        )
    )

    val bsPrograms = listOf(
        CollegeProgram(
            id = "bs_it",
            name = "BS Information Technology",
            shortCode = "BS IT",
            level = ProgramLevel.BS,
            duration = "4 Years (8 Semesters)",
            eligibility = "F.Sc (Pre-Engg/ICS/Pre-Med) or equivalent",
            departmentName = "Information Technology",
            description = null
        ),
        CollegeProgram(
            id = "bs_bba",
            name = "BS Business Administration",
            shortCode = "BBA",
            level = ProgramLevel.BS,
            duration = "4 Years (8 Semesters)",
            eligibility = "FA/F.Sc/I.Com or equivalent",
            departmentName = "BBA",
            description = null
        ),
        CollegeProgram(
            id = "bs_english",
            name = "BS English",
            shortCode = "BS ENG",
            level = ProgramLevel.BS,
            duration = "4 Years (8 Semesters)",
            eligibility = "FA/F.Sc or equivalent",
            departmentName = "English",
            description = null
        ),
        CollegeProgram(
            id = "bs_islamic_studies",
            name = "BS Islamic Studies",
            shortCode = "BS ISL",
            level = ProgramLevel.BS,
            duration = "4 Years (8 Semesters)",
            eligibility = "FA or equivalent",
            departmentName = "Islamiyat",
            description = null
        ),
        CollegeProgram(
            id = "bs_physics",
            name = "BS Physics",
            shortCode = "BS PHY",
            level = ProgramLevel.BS,
            duration = "4 Years (8 Semesters)",
            eligibility = "F.Sc Pre-Engineering / Equivalent",
            departmentName = "Physics",
            description = null
        ),
        CollegeProgram(
            id = "bs_mathematics",
            name = "BS Mathematics",
            shortCode = "BS MATH",
            level = ProgramLevel.BS,
            duration = "4 Years (8 Semesters)",
            eligibility = "F.Sc Pre-Engineering / Equivalent",
            departmentName = "Mathematics",
            description = null
        ),
        CollegeProgram(
            id = "bs_political_science",
            name = "BS Political Science",
            shortCode = "BS POL",
            level = ProgramLevel.BS,
            duration = "4 Years (8 Semesters)",
            eligibility = "Intermediate / Equivalent",
            departmentName = "Political Science",
            description = null
        ),
        CollegeProgram(
            id = "bs_urdu",
            name = "BS Urdu",
            shortCode = "BS URDU",
            level = ProgramLevel.BS,
            duration = "4 Years (8 Semesters)",
            eligibility = "Intermediate / Equivalent",
            departmentName = "Urdu",
            description = null
        ),
        CollegeProgram(
            id = "bs_chemistry",
            name = "BS Chemistry",
            shortCode = "BS CHEM",
            level = ProgramLevel.BS,
            duration = "4 Years (8 Semesters)",
            eligibility = "F.Sc Pre-Engineering / Equivalent",
            departmentName = "Chemistry",
            description = null
        ),
        CollegeProgram(
            id = "bs_zoology",
            name = "BS Zoology",
            shortCode = "BS ZOO",
            level = ProgramLevel.BS,
            duration = "4 Years (8 Semesters)",
            eligibility = "F.Sc Pre-Medical / Equivalent",
            departmentName = "Zoology",
            description = null
        )
    )

    fun getProgramsByLevel(level: ProgramLevel): List<CollegeProgram> = when (level) {
        ProgramLevel.INTERMEDIATE -> intermediatePrograms
        ProgramLevel.BS -> bsPrograms
    }
}

