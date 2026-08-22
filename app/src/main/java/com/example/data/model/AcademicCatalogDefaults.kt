package com.example.data.model

import java.util.UUID

/**
 * Built-in standard Academic Catalog for Govt. Graduate College Mandi Bahauddin.
 * Ensures that Academic Programs and Course / Subject dropdowns are always rich,
 * accurate, and never blank, even before initial Supabase database sync.
 */
object AcademicCatalogDefaults {

    // Deterministic UUIDs for standard departments
    const val DEPT_ID_IT = "00000000-0000-0000-0000-000000000001"
    const val DEPT_ID_CS = "00000000-0000-0000-0000-000000000002"
    const val DEPT_ID_MATH = "00000000-0000-0000-0000-000000000003"
    const val DEPT_ID_PHY = "00000000-0000-0000-0000-000000000004"
    const val DEPT_ID_CHEM = "00000000-0000-0000-0000-000000000005"
    const val DEPT_ID_BOT = "00000000-0000-0000-0000-000000000006"
    const val DEPT_ID_ZOO = "00000000-0000-0000-0000-000000000007"
    const val DEPT_ID_ENG = "00000000-0000-0000-0000-000000000008"
    const val DEPT_ID_URDU = "00000000-0000-0000-0000-000000000009"
    const val DEPT_ID_ECON = "00000000-0000-0000-0000-000000000010"
    const val DEPT_ID_POL = "00000000-0000-0000-0000-000000000011"
    const val DEPT_ID_COMM = "00000000-0000-0000-0000-000000000012"

    // Deterministic UUIDs for standard programs
    const val PROG_ID_BSIT = "00000000-0000-0000-0001-000000000001"
    const val PROG_ID_BSCS = "00000000-0000-0000-0001-000000000002"
    const val PROG_ID_BSMATH = "00000000-0000-0000-0001-000000000003"
    const val PROG_ID_BSPHY = "00000000-0000-0000-0001-000000000004"
    const val PROG_ID_BSCHEM = "00000000-0000-0000-0001-000000000005"
    const val PROG_ID_BSBOT = "00000000-0000-0000-0001-000000000006"
    const val PROG_ID_BSZOO = "00000000-0000-0000-0001-000000000007"
    const val PROG_ID_BSENG = "00000000-0000-0000-0001-000000000008"
    const val PROG_ID_BSURDU = "00000000-0000-0000-0001-000000000009"
    const val PROG_ID_BSECON = "00000000-0000-0000-0001-000000000010"
    const val PROG_ID_BSPOL = "00000000-0000-0000-0001-000000000011"
    const val PROG_ID_BSCOMM = "00000000-0000-0000-0001-000000000012"
    const val PROG_ID_ICS = "00000000-0000-0000-0001-000000000013"
    const val PROG_ID_FSC_ENG = "00000000-0000-0000-0001-000000000014"
    const val PROG_ID_FSC_MED = "00000000-0000-0000-0001-000000000015"
    const val PROG_ID_ICOM = "00000000-0000-0000-0001-000000000016"

    val defaultDepartments = listOf(
        DepartmentDto(id = DEPT_ID_IT, name = "Information Technology", code = "IT", category = "IT & CS", description = "Department of Information Technology", hodName = "Dr. Muhammad Asif"),
        DepartmentDto(id = DEPT_ID_CS, name = "Computer Science", code = "CS", category = "IT & CS", description = "Department of Computer Science", hodName = "Prof. Tariq Mehmood"),
        DepartmentDto(id = DEPT_ID_MATH, name = "Mathematics", code = "MATH", category = "Sciences", description = "Department of Mathematics", hodName = "Prof. Ghulam Mustafa"),
        DepartmentDto(id = DEPT_ID_PHY, name = "Physics", code = "PHY", category = "Sciences", description = "Department of Physics", hodName = "Prof. Muhammad Nawaz"),
        DepartmentDto(id = DEPT_ID_CHEM, name = "Chemistry", code = "CHEM", category = "Sciences", description = "Department of Chemistry", hodName = "Dr. Shahid Imran"),
        DepartmentDto(id = DEPT_ID_BOT, name = "Botany", code = "BOT", category = "Life Sciences", description = "Department of Botany", hodName = "Prof. Riaz Ahmad"),
        DepartmentDto(id = DEPT_ID_ZOO, name = "Zoology", code = "ZOO", category = "Life Sciences", description = "Department of Zoology", hodName = "Prof. Khalid Hussain"),
        DepartmentDto(id = DEPT_ID_ENG, name = "English", code = "ENG", category = "Humanities", description = "Department of English", hodName = "Prof. Tanveer Ahmed"),
        DepartmentDto(id = DEPT_ID_URDU, name = "Urdu", code = "URDU", category = "Humanities", description = "Department of Urdu", hodName = "Prof. Altaf Hussain"),
        DepartmentDto(id = DEPT_ID_ECON, name = "Economics", code = "ECON", category = "Commerce", description = "Department of Economics", hodName = "Prof. Saeed Anwar"),
        DepartmentDto(id = DEPT_ID_POL, name = "Political Science", code = "POL", category = "Humanities", description = "Department of Political Science", hodName = "Prof. Zafar Iqbal"),
        DepartmentDto(id = DEPT_ID_COMM, name = "Commerce", code = "COMM", category = "Commerce", description = "Department of Commerce", hodName = "Prof. Imran Ali")
    )

    val defaultPrograms = listOf(
        AcademicProgramDto(
            id = PROG_ID_BSIT,
            departmentId = DEPT_ID_IT,
            title = "BS Information Technology",
            code = "BS-IT",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 130,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_BSCS,
            departmentId = DEPT_ID_CS,
            title = "BS Computer Science",
            code = "BS-CS",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 132,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_BSMATH,
            departmentId = DEPT_ID_MATH,
            title = "BS Mathematics",
            code = "BS-MATH",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 130,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_BSPHY,
            departmentId = DEPT_ID_PHY,
            title = "BS Physics",
            code = "BS-PHY",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 130,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_BSCHEM,
            departmentId = DEPT_ID_CHEM,
            title = "BS Chemistry",
            code = "BS-CHEM",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 130,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_BSBOT,
            departmentId = DEPT_ID_BOT,
            title = "BS Botany",
            code = "BS-BOT",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 130,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_BSZOO,
            departmentId = DEPT_ID_ZOO,
            title = "BS Zoology",
            code = "BS-ZOO",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 130,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_BSENG,
            departmentId = DEPT_ID_ENG,
            title = "BS English",
            code = "BS-ENG",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 130,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_BSURDU,
            departmentId = DEPT_ID_URDU,
            title = "BS Urdu",
            code = "BS-URDU",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 130,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_BSECON,
            departmentId = DEPT_ID_ECON,
            title = "BS Economics",
            code = "BS-ECON",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 130,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_BSPOL,
            departmentId = DEPT_ID_POL,
            title = "BS Political Science",
            code = "BS-POL",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 130,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_BSCOMM,
            departmentId = DEPT_ID_COMM,
            title = "BS Commerce (B.Com / BS)",
            code = "BS-COMM",
            degreeType = "BS 4-Years",
            durationYears = 4,
            totalSemesters = 8,
            totalCreditHours = 130,
            isIntermediate = false
        ),
        AcademicProgramDto(
            id = PROG_ID_ICS,
            departmentId = DEPT_ID_IT,
            title = "ICS Computer Science",
            code = "ICS",
            degreeType = "Intermediate 2-Years",
            durationYears = 2,
            totalSemesters = 2,
            totalCreditHours = 64,
            isIntermediate = true
        ),
        AcademicProgramDto(
            id = PROG_ID_FSC_ENG,
            departmentId = DEPT_ID_PHY,
            title = "F.Sc Pre-Engineering",
            code = "FSC-PE",
            degreeType = "Intermediate 2-Years",
            durationYears = 2,
            totalSemesters = 2,
            totalCreditHours = 64,
            isIntermediate = true
        ),
        AcademicProgramDto(
            id = PROG_ID_FSC_MED,
            departmentId = DEPT_ID_CHEM,
            title = "F.Sc Pre-Medical",
            code = "FSC-PM",
            degreeType = "Intermediate 2-Years",
            durationYears = 2,
            totalSemesters = 2,
            totalCreditHours = 64,
            isIntermediate = true
        ),
        AcademicProgramDto(
            id = PROG_ID_ICOM,
            departmentId = DEPT_ID_COMM,
            title = "I.Com Commerce",
            code = "ICOM",
            degreeType = "Intermediate 2-Years",
            durationYears = 2,
            totalSemesters = 2,
            totalCreditHours = 64,
            isIntermediate = true
        )
    )

    val defaultCourses: List<CourseDto> = listOf(
        // BS Information Technology
        CourseDto(id = "00000000-0000-0002-0001-000000000001", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "CS-101", title = "Programming Fundamentals", creditHours = "4 (3-1)", semesterNumber = 1, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000002", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "CS-102", title = "Introduction to ICT", creditHours = "3 (2-1)", semesterNumber = 1, category = "University Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000003", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "MTH-101", title = "Calculus & Analytical Geometry", creditHours = "3 (3-0)", semesterNumber = 1, category = "Math Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000004", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "ENG-101", title = "English Composition & Comprehension", creditHours = "3 (3-0)", semesterNumber = 1, category = "General"),
        CourseDto(id = "00000000-0000-0002-0001-000000000005", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "CS-201", title = "Object-Oriented Programming", creditHours = "4 (3-1)", semesterNumber = 2, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000006", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-202", title = "Discrete Structures", creditHours = "3 (3-0)", semesterNumber = 2, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000007", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "CS-203", title = "Data Structures & Algorithms", creditHours = "4 (3-1)", semesterNumber = 3, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000008", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-204", title = "Computer Organization & Architecture", creditHours = "3 (3-0)", semesterNumber = 3, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000009", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "CS-301", title = "Database Systems", creditHours = "4 (3-1)", semesterNumber = 4, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000010", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-302", title = "Operating Systems", creditHours = "4 (3-1)", semesterNumber = 4, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000011", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-303", title = "Computer Networks", creditHours = "4 (3-1)", semesterNumber = 5, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000012", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-304", title = "Web Systems & Technologies", creditHours = "3 (2-1)", semesterNumber = 5, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000013", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-305", title = "Software Engineering", creditHours = "3 (3-0)", semesterNumber = 5, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000014", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-306", title = "Information Security", creditHours = "3 (3-0)", semesterNumber = 6, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000015", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-307", title = "Mobile Application Development", creditHours = "3 (2-1)", semesterNumber = 6, category = "Elective"),
        CourseDto(id = "00000000-0000-0002-0001-000000000016", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-401", title = "Cloud Computing", creditHours = "3 (2-1)", semesterNumber = 7, category = "Elective"),
        CourseDto(id = "00000000-0000-0002-0001-000000000017", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-402", title = "Capstone Project I", creditHours = "3 (0-3)", semesterNumber = 7, category = "Core Project"),
        CourseDto(id = "00000000-0000-0002-0001-000000000018", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-403", title = "IT Project Management", creditHours = "3 (3-0)", semesterNumber = 8, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0001-000000000019", programId = PROG_ID_BSIT, departmentId = DEPT_ID_IT, code = "IT-404", title = "Capstone Project II", creditHours = "3 (0-3)", semesterNumber = 8, category = "Core Project"),

        // BS Computer Science
        CourseDto(id = "00000000-0000-0002-0002-000000000001", programId = PROG_ID_BSCS, departmentId = DEPT_ID_CS, code = "CS-101", title = "Programming Fundamentals", creditHours = "4 (3-1)", semesterNumber = 1, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0002-000000000002", programId = PROG_ID_BSCS, departmentId = DEPT_ID_CS, code = "CS-102", title = "Information & Communication Technologies", creditHours = "3 (2-1)", semesterNumber = 1, category = "University Core"),
        CourseDto(id = "00000000-0000-0002-0002-000000000003", programId = PROG_ID_BSCS, departmentId = DEPT_ID_CS, code = "CS-201", title = "Object-Oriented Programming", creditHours = "4 (3-1)", semesterNumber = 2, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0002-000000000004", programId = PROG_ID_BSCS, departmentId = DEPT_ID_CS, code = "CS-202", title = "Digital Logic Design", creditHours = "3 (2-1)", semesterNumber = 2, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0002-000000000005", programId = PROG_ID_BSCS, departmentId = DEPT_ID_CS, code = "CS-203", title = "Data Structures & Algorithms", creditHours = "4 (3-1)", semesterNumber = 3, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0002-000000000006", programId = PROG_ID_BSCS, departmentId = DEPT_ID_CS, code = "CS-301", title = "Theory of Automata", creditHours = "3 (3-0)", semesterNumber = 4, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0002-000000000007", programId = PROG_ID_BSCS, departmentId = DEPT_ID_CS, code = "CS-302", title = "Design & Analysis of Algorithms", creditHours = "3 (3-0)", semesterNumber = 5, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0002-000000000008", programId = PROG_ID_BSCS, departmentId = DEPT_ID_CS, code = "CS-303", title = "Artificial Intelligence", creditHours = "3 (2-1)", semesterNumber = 6, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0002-000000000009", programId = PROG_ID_BSCS, departmentId = DEPT_ID_CS, code = "CS-401", title = "Compiler Construction", creditHours = "3 (3-0)", semesterNumber = 7, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0002-000000000010", programId = PROG_ID_BSCS, departmentId = DEPT_ID_CS, code = "CS-402", title = "Machine Learning", creditHours = "3 (2-1)", semesterNumber = 8, category = "Elective"),

        // BS Mathematics
        CourseDto(id = "00000000-0000-0002-0003-000000000001", programId = PROG_ID_BSMATH, departmentId = DEPT_ID_MATH, code = "MATH-101", title = "Calculus I", creditHours = "3 (3-0)", semesterNumber = 1, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0003-000000000002", programId = PROG_ID_BSMATH, departmentId = DEPT_ID_MATH, code = "MATH-102", title = "Elements of Set Theory & Logic", creditHours = "3 (3-0)", semesterNumber = 1, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0003-000000000003", programId = PROG_ID_BSMATH, departmentId = DEPT_ID_MATH, code = "MATH-103", title = "Calculus II", creditHours = "3 (3-0)", semesterNumber = 2, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0003-000000000004", programId = PROG_ID_BSMATH, departmentId = DEPT_ID_MATH, code = "MATH-104", title = "Linear Algebra", creditHours = "3 (3-0)", semesterNumber = 2, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0003-000000000005", programId = PROG_ID_BSMATH, departmentId = DEPT_ID_MATH, code = "MATH-201", title = "Calculus III", creditHours = "3 (3-0)", semesterNumber = 3, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0003-000000000006", programId = PROG_ID_BSMATH, departmentId = DEPT_ID_MATH, code = "MATH-202", title = "Ordinary Differential Equations", creditHours = "3 (3-0)", semesterNumber = 4, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0003-000000000007", programId = PROG_ID_BSMATH, departmentId = DEPT_ID_MATH, code = "MATH-301", title = "Real Analysis I", creditHours = "3 (3-0)", semesterNumber = 5, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0003-000000000008", programId = PROG_ID_BSMATH, departmentId = DEPT_ID_MATH, code = "MATH-302", title = "Group Theory I", creditHours = "3 (3-0)", semesterNumber = 6, category = "Major Core"),

        // BS Physics
        CourseDto(id = "00000000-0000-0002-0004-000000000001", programId = PROG_ID_BSPHY, departmentId = DEPT_ID_PHY, code = "PHY-101", title = "Mechanics & Wave Motion", creditHours = "4 (3-1)", semesterNumber = 1, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0004-000000000002", programId = PROG_ID_BSPHY, departmentId = DEPT_ID_PHY, code = "PHY-102", title = "Electricity & Magnetism", creditHours = "4 (3-1)", semesterNumber = 2, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0004-000000000003", programId = PROG_ID_BSPHY, departmentId = DEPT_ID_PHY, code = "PHY-201", title = "Heat & Thermodynamics", creditHours = "3 (3-0)", semesterNumber = 3, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0004-000000000004", programId = PROG_ID_BSPHY, departmentId = DEPT_ID_PHY, code = "PHY-202", title = "Optics & Modern Physics", creditHours = "4 (3-1)", semesterNumber = 4, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0004-000000000005", programId = PROG_ID_BSPHY, departmentId = DEPT_ID_PHY, code = "PHY-301", title = "Quantum Mechanics I", creditHours = "3 (3-0)", semesterNumber = 5, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0004-000000000006", programId = PROG_ID_BSPHY, departmentId = DEPT_ID_PHY, code = "PHY-302", title = "Solid State Physics I", creditHours = "3 (3-0)", semesterNumber = 6, category = "Major Core"),

        // BS Chemistry
        CourseDto(id = "00000000-0000-0002-0005-000000000001", programId = PROG_ID_BSCHEM, departmentId = DEPT_ID_CHEM, code = "CHEM-101", title = "Physical Chemistry I", creditHours = "4 (3-1)", semesterNumber = 1, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0005-000000000002", programId = PROG_ID_BSCHEM, departmentId = DEPT_ID_CHEM, code = "CHEM-102", title = "Inorganic Chemistry I", creditHours = "4 (3-1)", semesterNumber = 2, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0005-000000000003", programId = PROG_ID_BSCHEM, departmentId = DEPT_ID_CHEM, code = "CHEM-201", title = "Organic Chemistry I", creditHours = "4 (3-1)", semesterNumber = 3, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0005-000000000004", programId = PROG_ID_BSCHEM, departmentId = DEPT_ID_CHEM, code = "CHEM-202", title = "Analytical Chemistry I", creditHours = "4 (3-1)", semesterNumber = 4, category = "Major Core"),

        // BS English
        CourseDto(id = "00000000-0000-0002-0008-000000000001", programId = PROG_ID_BSENG, departmentId = DEPT_ID_ENG, code = "ENG-101", title = "Introduction to Literature", creditHours = "3 (3-0)", semesterNumber = 1, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0008-000000000002", programId = PROG_ID_BSENG, departmentId = DEPT_ID_ENG, code = "ENG-102", title = "History of English Literature", creditHours = "3 (3-0)", semesterNumber = 2, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0008-000000000003", programId = PROG_ID_BSENG, departmentId = DEPT_ID_ENG, code = "ENG-201", title = "Classical Poetry", creditHours = "3 (3-0)", semesterNumber = 3, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0008-000000000004", programId = PROG_ID_BSENG, departmentId = DEPT_ID_ENG, code = "ENG-202", title = "Greek & Elizabethan Drama", creditHours = "3 (3-0)", semesterNumber = 4, category = "Major Core"),

        // BS Economics
        CourseDto(id = "00000000-0000-0002-0010-000000000001", programId = PROG_ID_BSECON, departmentId = DEPT_ID_ECON, code = "ECON-101", title = "Principles of Microeconomics", creditHours = "3 (3-0)", semesterNumber = 1, category = "Major Core"),
        CourseDto(id = "00000000-0000-0002-0010-000000000002", programId = PROG_ID_BSECON, departmentId = DEPT_ID_ECON, code = "ECON-102", title = "Principles of Macroeconomics", creditHours = "3 (3-0)", semesterNumber = 2, category = "Major Core"),

        // ICS Computer Science
        CourseDto(id = "00000000-0000-0002-0013-000000000001", programId = PROG_ID_ICS, departmentId = DEPT_ID_IT, code = "ICS-CS1", title = "Computer Science Part-I", creditHours = "4 (3-1)", semesterNumber = 1, category = "Core Subject"),
        CourseDto(id = "00000000-0000-0002-0013-000000000002", programId = PROG_ID_ICS, departmentId = DEPT_ID_IT, code = "ICS-CS2", title = "Computer Science Part-II", creditHours = "4 (3-1)", semesterNumber = 2, category = "Core Subject"),
        CourseDto(id = "00000000-0000-0002-0013-000000000003", programId = PROG_ID_ICS, departmentId = DEPT_ID_MATH, code = "ICS-MTH1", title = "Mathematics Part-I (FSc/ICS)", creditHours = "4 (4-0)", semesterNumber = 1, category = "Core Subject"),
        CourseDto(id = "00000000-0000-0002-0013-000000000004", programId = PROG_ID_ICS, departmentId = DEPT_ID_MATH, code = "ICS-MTH2", title = "Mathematics Part-II (FSc/ICS)", creditHours = "4 (4-0)", semesterNumber = 2, category = "Core Subject")
    )

    /**
     * Map a department ID or code to its default programs.
     */
    fun getProgramsForDepartment(deptId: String?, deptCode: String? = null): List<AcademicProgramDto> {
        if (deptId.isNullOrBlank() && deptCode.isNullOrBlank()) return defaultPrograms
        val matched = defaultPrograms.filter { prog ->
            prog.departmentId == deptId || (deptCode != null && prog.code.contains(deptCode, ignoreCase = true))
        }
        return if (matched.isNotEmpty()) matched else defaultPrograms
    }

    /**
     * Map a program ID to its default courses.
     */
    fun getCoursesForProgram(programId: String?, semester: Int? = null): List<CourseDto> {
        if (programId.isNullOrBlank()) {
            return if (semester != null) defaultCourses.filter { it.semesterNumber == semester } else defaultCourses
        }
        val byProgram = defaultCourses.filter { it.programId == programId }
        val filtered = if (semester != null) byProgram.filter { it.semesterNumber == semester } else byProgram
        return if (filtered.isNotEmpty()) filtered else if (byProgram.isNotEmpty()) byProgram else defaultCourses
    }
}
