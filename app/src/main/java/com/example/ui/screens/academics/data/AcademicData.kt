package com.example.ui.screens.academics.data

import com.example.ui.screens.academics.models.AcademicResource
import com.example.ui.screens.academics.models.Department
import com.example.ui.screens.academics.models.FacultyMember
import com.example.ui.screens.academics.models.Program
import com.example.ui.screens.academics.models.ResourceType
import com.example.ui.screens.academics.models.SemesterData
import com.example.ui.screens.academics.models.Subject

object AcademicData {

    val sampleFaculty = listOf(
        FacultyMember(
            id = "f1",
            departmentId = "cs",
            name = "Dr. Muhammad Imran",
            designation = "Associate Professor & HOD",
            qualification = "Ph.D. Computer Science (PU), PostDoc (UK)",
            specialization = "Artificial Intelligence & Data Mining",
            email = "cs.hod@ggcmbdin.edu.pk"
        ),
        FacultyMember(
            id = "f2",
            departmentId = "cs",
            name = "Prof. Tariq Mahmood",
            designation = "Assistant Professor",
            qualification = "M.Phil Computer Science (QAU)",
            specialization = "Software Engineering & Mobile Development",
            email = "tariq.cs@ggcmbdin.edu.pk"
        ),
        FacultyMember(
            id = "f3",
            departmentId = "cs",
            name = "Mr. Usman Ali",
            designation = "Lecturer",
            qualification = "M.S. Information Technology (UET)",
            specialization = "Database Systems & Web Development",
            email = "usman.cs@ggcmbdin.edu.pk"
        ),
        FacultyMember(
            id = "f4",
            departmentId = "phy",
            name = "Dr. Abdul Rehman",
            designation = "Associate Professor & HOD",
            qualification = "Ph.D. Physics (QAU)",
            specialization = "Condensed Matter Physics & Nanotechnology",
            email = "physics.hod@ggcmbdin.edu.pk"
        ),
        FacultyMember(
            id = "f5",
            departmentId = "chem",
            name = "Dr. Bushra Fatima",
            designation = "Assistant Professor & HOD",
            qualification = "Ph.D. Organic Chemistry (UAF)",
            specialization = "Medicinal & Organic Synthesis",
            email = "chemistry.hod@ggcmbdin.edu.pk"
        ),
        FacultyMember(
            id = "f6",
            departmentId = "eng",
            name = "Prof. Sajid Gondal",
            designation = "Assistant Professor & HOD",
            qualification = "M.Phil English Literature (GCU)",
            specialization = "Postcolonial Literature & Linguistics",
            email = "english.hod@ggcmbdin.edu.pk"
        )
    )

    // Helper resources creator
    private fun createSampleResources(subjectId: String, subjectCode: String): List<AcademicResource> {
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
                title = "$subjectCode Annual/Finalterm Past Examination Paper 2021",
                type = ResourceType.PAST_PAPER,
                year = "2021",
                examTerm = "Finalterm",
                fileSize = "2.8 MB",
                uploadedBy = "Examination Department"
            )
        )
    }

    val sampleDepartments: List<Department> = listOf(
        Department(
            id = "cs",
            name = "Computer Science & Information Technology",
            code = "CS-IT",
            category = "IT & CS",
            description = "The Department of Computer Science & IT provides rigorous academic training in modern software engineering, computer science theory, systems programming, web technologies, and artificial intelligence.",
            hodName = "Dr. Muhammad Imran",
            hodQualification = "Ph.D. Computer Science (PU)",
            hodEmail = "cs.hod@ggcmbdin.edu.pk",
            iconName = "Computer",
            facultyCount = 12,
            programs = listOf(
                Program(
                    id = "bscs",
                    departmentId = "cs",
                    title = "BS Computer Science (BSCS)",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSCS",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 132,
                    eligibility = "F.Sc. Pre-Engineering / ICS / General Science with Mathematics (Min 50% Marks)",
                    description = "A comprehensive 4-year undergraduate degree program aligned with HEC guidelines, equipping students with core algorithm design, programming, database systems, software design, mobile engineering, and computer networking skills.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bscs_s1_cs101",
                                    code = "CS-101",
                                    title = "Programming Fundamentals",
                                    creditHours = "4 (3-1)",
                                    category = "Major Core",
                                    description = "Introduction to programming concepts, variables, control structures, functions, arrays, pointers, and problem-solving techniques using C++.",
                                    syllabusTopics = listOf(
                                        "Problem Solving & Flowcharts",
                                        "Data Types & Control Structures",
                                        "Functions & Pass by Reference",
                                        "Arrays, Pointers & Memory Allocation",
                                        "File Handling & Structural Logic"
                                    ),
                                    recommendedBooks = listOf(
                                        "C++ How to Program by Paul Deitel & Harvey Deitel",
                                        "Starting Out with C++ by Tony Gaddis"
                                    ),
                                    resources = createSampleResources("bscs_s1_cs101", "CS-101")
                                ),
                                Subject(
                                    id = "bscs_s1_cs102",
                                    code = "CS-102",
                                    title = "Information & Communication Technologies",
                                    creditHours = "3 (2-1)",
                                    category = "University Core",
                                    description = "Overview of computer hardware, operating systems, networking fundamentals, web design, and digital literacy.",
                                    syllabusTopics = listOf(
                                        "Computer Architecture Basics",
                                        "Operating System Concepts",
                                        "Networking & Internet Infrastructure",
                                        "Cybersecurity & Privacy Principles"
                                    ),
                                    recommendedBooks = listOf(
                                        "Introduction to Computers by Peter Norton"
                                    ),
                                    resources = createSampleResources("bscs_s1_cs102", "CS-102")
                                ),
                                Subject(
                                    id = "bscs_s1_mth101",
                                    code = "MTH-101",
                                    title = "Calculus & Analytical Geometry",
                                    creditHours = "3 (3-0)",
                                    category = "General Math Core",
                                    description = "Limits, continuity, differentiation, integration techniques, applications of derivatives, and plane analytical geometry.",
                                    syllabusTopics = listOf(
                                        "Limits and Continuity",
                                        "Derivatives & Rates of Change",
                                        "Applications of Differentiation",
                                        "Definite & Indefinite Integration"
                                    ),
                                    recommendedBooks = listOf(
                                        "Calculus and Analytic Geometry by Thomas & Finney"
                                    ),
                                    resources = createSampleResources("bscs_s1_mth101", "MTH-101")
                                )
                            )
                        ),
                        SemesterData(
                            semesterNumber = 2,
                            title = "Semester 2",
                            subjects = listOf(
                                Subject(
                                    id = "bscs_s2_cs201",
                                    code = "CS-201",
                                    title = "Object-Oriented Programming",
                                    creditHours = "4 (3-1)",
                                    category = "Major Core",
                                    description = "Encapsulation, inheritance, polymorphism, abstract classes, operator overloading, exception handling, and templates in C++/Java.",
                                    syllabusTopics = listOf(
                                        "Classes, Objects & Constructors",
                                        "Inheritance & Polymorphism",
                                        "Operator Overloading & Virtual Functions",
                                        "Exception Handling & File I/O Streams"
                                    ),
                                    recommendedBooks = listOf(
                                        "Object-Oriented Programming in C++ by Robert Lafore"
                                    ),
                                    resources = createSampleResources("bscs_s2_cs201", "CS-201")
                                ),
                                Subject(
                                    id = "bscs_s2_cs202",
                                    code = "CS-202",
                                    title = "Digital Logic Design",
                                    creditHours = "3 (2-1)",
                                    category = "Major Core",
                                    description = "Number systems, Boolean algebra, logic gates, combinational and sequential circuit analysis, multiplexers, and flip-flops.",
                                    syllabusTopics = listOf(
                                        "Binary Systems & Karnaugh Maps",
                                        "Combinational Logic Circuits",
                                        "Decoders, Encoders & Multiplexers",
                                        "Flip-Flops, Counters & Shift Registers"
                                    ),
                                    recommendedBooks = listOf(
                                        "Digital Design by M. Morris Mano"
                                    ),
                                    resources = createSampleResources("bscs_s2_cs202", "CS-202")
                                )
                            )
                        ),
                        SemesterData(
                            semesterNumber = 3,
                            title = "Semester 3",
                            subjects = listOf(
                                Subject(
                                    id = "bscs_s3_cs301",
                                    code = "CS-301",
                                    title = "Data Structures & Algorithms",
                                    creditHours = "4 (3-1)",
                                    category = "Major Core",
                                    description = "Arrays, stacks, queues, linked lists, binary trees, heaps, graphs, hashing, searching and sorting algorithm analysis.",
                                    syllabusTopics = listOf(
                                        "Abstract Data Types & Time Complexity (Big O)",
                                        "Linked Lists, Stacks & Queues",
                                        "Binary Search Trees & AVL Trees",
                                        "Graph Algorithms (DFS, BFS, Dijkstra)",
                                        "Sorting Algorithms & Hash Tables"
                                    ),
                                    recommendedBooks = listOf(
                                        "Introduction to Algorithms by Cormen, Leiserson, Rivest, Stein",
                                        "Data Structures and Algorithm Analysis in C++ by Mark Allen Weiss"
                                    ),
                                    resources = createSampleResources("bscs_s3_cs301", "CS-301")
                                ),
                                Subject(
                                    id = "bscs_s3_cs302",
                                    code = "CS-302",
                                    title = "Database Systems",
                                    creditHours = "4 (3-1)",
                                    category = "Major Core",
                                    description = "Relational database concepts, ER modeling, SQL, normalization (1NF, 2NF, 3NF, BCNF), transaction handling, and index design.",
                                    syllabusTopics = listOf(
                                        "Entity-Relationship (ER) Modeling",
                                        "Relational Algebra & Tuple Calculus",
                                        "Structured Query Language (SQL)",
                                        "Database Normalization & Functional Dependencies"
                                    ),
                                    recommendedBooks = listOf(
                                        "Database System Concepts by Silberschatz, Korth, Sudarshan"
                                    ),
                                    resources = createSampleResources("bscs_s3_cs302", "CS-302")
                                )
                            )
                        ),
                        SemesterData(
                            semesterNumber = 4,
                            title = "Semester 4",
                            subjects = listOf(
                                Subject(
                                    id = "bscs_s4_cs401",
                                    code = "CS-401",
                                    title = "Operating Systems",
                                    creditHours = "4 (3-1)",
                                    category = "Major Core",
                                    description = "Process management, CPU scheduling, thread synchronization, deadlocks, memory management, virtual memory, and storage systems.",
                                    syllabusTopics = listOf(
                                        "Processes & Threads Synchronization",
                                        "CPU Scheduling Algorithms",
                                        "Deadlock Handling & Prevention",
                                        "Paging, Segmentation & Virtual Memory"
                                    ),
                                    recommendedBooks = listOf(
                                        "Operating System Concepts by Silberschatz, Galvin, Gagne"
                                    ),
                                    resources = createSampleResources("bscs_s4_cs401", "CS-401")
                                )
                            )
                        )
                    )
                ),
                Program(
                    id = "bsit",
                    departmentId = "cs",
                    title = "BS Information Technology (BSIT)",
                    degreeType = "BS 4-Years (8 Semesters)",
                    code = "BSIT",
                    durationYears = 4,
                    totalSemesters = 8,
                    totalCreditHours = 130,
                    eligibility = "F.Sc. Pre-Engineering / ICS / General Science (Min 50% Marks)",
                    description = "Focuses on applied computing, system administration, web architecture, enterprise networking, IT security, and mobile application frameworks.",
                    semesters = listOf(
                        SemesterData(
                            semesterNumber = 1,
                            title = "Semester 1",
                            subjects = listOf(
                                Subject(
                                    id = "bsit_s1_it101",
                                    code = "IT-101",
                                    title = "Introduction to Information Technology",
                                    creditHours = "3 (2-1)",
                                    category = "Major Core",
                                    description = "Foundations of IT systems, computing hardware, operating systems, internet services, and enterprise software.",
                                    syllabusTopics = listOf("Hardware Systems", "Software Ecosystems", "Web Architecture", "IT Security"),
                                    recommendedBooks = listOf("Information Technology Principles by Efraim Turban"),
                                    resources = createSampleResources("bsit_s1_it101", "IT-101")
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
            hodName = "Dr. Abdul Rehman",
            hodQualification = "Ph.D. Physics (QAU)",
            hodEmail = "physics.hod@ggcmbdin.edu.pk",
            iconName = "Science",
            facultyCount = 10,
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
            hodName = "Dr. Bushra Fatima",
            hodQualification = "Ph.D. Organic Chemistry (UAF)",
            hodEmail = "chemistry.hod@ggcmbdin.edu.pk",
            iconName = "Science",
            facultyCount = 14,
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
            hodName = "Prof. Sajid Gondal",
            hodQualification = "M.Phil English Literature (GCU)",
            hodEmail = "english.hod@ggcmbdin.edu.pk",
            iconName = "Book",
            facultyCount = 15,
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
            hodName = "Prof. Muhammad Raza",
            hodQualification = "M.Phil Economics (QAU)",
            hodEmail = "economics.hod@ggcmbdin.edu.pk",
            iconName = "Psychology",
            facultyCount = 9,
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
        )
    )
}
