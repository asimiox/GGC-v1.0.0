package com.example.data.datasource

import com.example.data.model.CourseItem
import com.example.data.model.ProgramCourseOutline
import com.example.data.model.SemesterOutline

object OfficialCoursesOutlineData {

    // 1. BS Information Technology
    val bsInformationTechnology = ProgramCourseOutline(
        id = "bs_it",
        name = "BS Information Technology",
        headOfDepartment = "Prof. Muhammad Faiyaz",
        duration = "4 Years (8 Semesters)",
        eligibility = "F.Sc (Pre-Engg/ICS/Pre-Med) or equivalent",
        about = "Dive into computing, programming, and emerging technologies with access to modern IT labs.",
        isIntermediate = false,
        semesters = listOf(
            SemesterOutline(
                semesterNumber = 1,
                courses = listOf(
                    CourseItem("GE-160-L", "Applications of ICT Lab", 1, "General Education Lab"),
                    CourseItem("HQ-001", "Translation of Holy Quran", 0, ""),
                    CourseItem("MD-001", "Math Deficiency - I", 3, "Non-Credit"),
                    CourseItem("GE-163", "Islamic Studies", 2, "General Education"),
                    CourseItem("GE-168", "Ideology and Constitution of Pakistan", 2, "General Education"),
                    CourseItem("GE-190", "Functional English", 3, "General Education"),
                    CourseItem("CC-110-L", "Digital Logic Design Lab", 1, "Computing Core Lab"),
                    CourseItem("CC-110", "Digital Logic Design", 2, "Computing Core"),
                    CourseItem("GE-169", "Applied Physics", 3, "General Education"),
                    CourseItem("GE-160", "Applications of Information & Communication Technologies", 2, "General Education")
                )
            ),
            SemesterOutline(
                semesterNumber = 2,
                courses = listOf(
                    CourseItem("HQ-002", "Translation of Holy Quran", 1, ""),
                    CourseItem("CC-112-L", "Programming Fundamentals Lab", 1, "Computing Core Lab"),
                    CourseItem("CC-112", "Programming Fundamentals", 3, "Computing Core"),
                    CourseItem("MD-002", "Math Deficiency - II", 3, "Non-Credit"),
                    CourseItem("UE-272", "Introduction to Marketing", 3, "University Elective"),
                    CourseItem("GE-191", "Expository Writing", 3, "General Education"),
                    CourseItem("GE-167", "Discrete Structures", 3, "General Education"),
                    CourseItem("CC-214", "Computer Networks", 3, "Computing Core")
                )
            ),
            SemesterOutline(
                semesterNumber = 3,
                courses = listOf(
                    CourseItem("HQ-003", "Translation of Holy Quran", 0, ""),
                    CourseItem("CC-212", "Software Engineering", 3, "Computing Core"),
                    CourseItem("CC-211-L", "Object Oriented Programming Lab", 1, "Computing Core Lab"),
                    CourseItem("CC-211", "Object Oriented Programming", 3, "Computing Core"),
                    CourseItem("CC-215-L", "Database Systems Lab", 1, "Computing Core Lab"),
                    CourseItem("CC-215", "Database Systems", 3, "Computing Core"),
                    CourseItem("CC-210", "Computer Organization & Assembly Language", 3, "Computing Core"),
                    CourseItem("GE-162", "Calculus and Analytical Geometry", 3, "General Education")
                )
            ),
            SemesterOutline(
                semesterNumber = 4,
                courses = listOf(
                    CourseItem("HQ-004", "Translation of Holy Quran", 1, ""),
                    CourseItem("CC-311", "Operating Systems", 3, "Computing Core"),
                    CourseItem("MS-253", "Multivariable Calculus", 3, "Mathematics"),
                    CourseItem("GE-192", "Introduction to Management", 2, "General Education"),
                    CourseItem("CC-312", "Information Security", 3, "Computing Core"),
                    CourseItem("CC-213-L", "Data Structures Lab", 1, "Computing Core Lab"),
                    CourseItem("CC-213", "Data Structures", 3, "Computing Core")
                )
            ),
            SemesterOutline(
                semesterNumber = 5,
                courses = listOf(
                    CourseItem("DI-322", "Web Technologies", 3, "IT Core"),
                    CourseItem("HQ-005", "Translation of Holy Quran", 0, ""),
                    CourseItem("GE-262", "Professional Practices", 2, "General Education"),
                    CourseItem("DI-328", "Parallel & Distributed Computing", 3, "IT Core"),
                    CourseItem("MS-252", "Linear Algebra", 3, "Mathematics"),
                    CourseItem("CC-310", "Artificial Intelligence", 2, "Computing Core"),
                    CourseItem("CC-313", "Analysis of Algorithms", 3, "Computing Core")
                )
            ),
            SemesterOutline(
                semesterNumber = 6,
                courses = listOf(
                    CourseItem("HQ-006", "Translation of Holy Quran", 1, ""),
                    CourseItem("EI-425", "Software Quality Engineering", 3, "IT Elective"),
                    CourseItem("EI-330", "Software Project Management", 3, "IT Elective"),
                    CourseItem("EI-331", "Software Construction & Development", 3, "IT Elective"),
                    CourseItem("EI-333", "Mobile Application Development", 3, "IT Elective"),
                    CourseItem("EI-335", "Machine Learning", 3, "IT Elective"),
                    CourseItem("GE-362", "Entrepreneurship", 2, "General Education")
                )
            ),
            SemesterOutline(
                semesterNumber = 7,
                courses = listOf(
                    CourseItem("HQ-007", "Translation of Holy Quran", 0, ""),
                    CourseItem("DI-323", "System and Network Administration", 3, "IT Core"),
                    CourseItem("EI-336", "Global IT Services and Workspace", 3, "IT Elective"),
                    CourseItem("CC-411", "Final Year Project - I", 2, "Computing Core Project"),
                    CourseItem("DI-324", "Database Administration and Management", 3, "IT Core"),
                    CourseItem("EI-339", "Cloud Computing", 3, "IT Elective")
                )
            ),
            SemesterOutline(
                semesterNumber = 8,
                courses = listOf(
                    CourseItem("GE-363", "Civics and Community Management", 2, "General Education"),
                    CourseItem("DI-325", "Cyber Security", 3, "IT Core"),
                    CourseItem("CC-412", "Final Year Project - II", 4, "Computing Core Project"),
                    CourseItem("DI-327", "Information Technology Infrastructure", 3, "IT Core"),
                    CourseItem("MS-254", "Technical and Business Writing", 3, "Support Course"),
                    CourseItem("HQ-008", "Translation of Holy Quran", 1, "")
                )
            )
        )
    )

    // 2. BS Business Administration
    val bsBusinessAdministration = ProgramCourseOutline(
        id = "bs_bba",
        name = "BS Business Administration",
        headOfDepartment = "Prof. Tariq Ashraf",
        duration = "4 Years (8 Semesters)",
        eligibility = "FA/F.Sc/I.Com or equivalent",
        about = "Build managerial, analytical, and leadership skills for the modern business world.",
        isIntermediate = false,
        semesters = listOf(
            SemesterOutline(
                semesterNumber = 1,
                courses = listOf(
                    CourseItem("COMP-111", "Computer (Introduction and Applications)", 3, "Core course"),
                    CourseItem("HQ-001", "Translation of Holy Quran", 0, ""),
                    CourseItem("ECON-101", "Principles of Micro Economics", 3, "Basic principles of microeconomics and market behavior"),
                    CourseItem("ISE-111", "Islamiat/Ethics", 2, "Core course"),
                    CourseItem("BBA-101", "Introduction to Business", 3, "Fundamental concepts of business operations and organizations"),
                    CourseItem("ENG-111", "English-I (Language in Use)", 3, "Core course"),
                    CourseItem("MATH-111", "Elementary Mathematics", 3, "Basic mathematical concepts for business")
                )
            ),
            SemesterOutline(
                semesterNumber = 2,
                courses = listOf(
                    CourseItem("HQ-002", "Translation of Holy Quran", 1, ""),
                    CourseItem("BBA-102", "Principles of Management", 3, "Fundamentals of organizational management and leadership"),
                    CourseItem("ECON-102", "Principles of Macro Economics", 3, "Macroeconomic theories and national economy analysis"),
                    CourseItem("PST-111", "Pakistan Studies", 2, "Core course"),
                    CourseItem("BBA-104", "Introduction to Psychology (BBA)", 3, "Basic psychological concepts applicable to business"),
                    CourseItem("BBA-103", "Financial Accounting (Basic)", 3, "Introduction to financial accounting principles and statements"),
                    CourseItem("ENG-112", "English-II (Academic Reading and Writing)", 3, "Core course")
                )
            ),
            SemesterOutline(
                semesterNumber = 3,
                courses = listOf(
                    CourseItem("HQ-003", "Translation of Holy Quran", 0, ""),
                    CourseItem("BBA-201", "Principles of Marketing", 3, "Core concepts of marketing, markets, and consumer needs"),
                    CourseItem("BBA-202", "Financial Management (Basic)", 3, "Introduction to corporate finance and financial decision making"),
                    CourseItem("ENG-221", "English-III (Business Communication-I)", 3, "Foundations of effective business communication"),
                    CourseItem("STAT-211", "Elementary Statistics", 3, "Basic statistical methods and data analysis"),
                    CourseItem("BBA-204", "Data Base Management System", 3, "Introduction to database design and management systems"),
                    CourseItem("BBA-203", "Cost Accounting", 3, "Principles of cost determination and control for manufacturing")
                )
            ),
            SemesterOutline(
                semesterNumber = 4,
                courses = listOf(
                    CourseItem("HQ-004", "Translation of Holy Quran", 1, ""),
                    CourseItem("BBA-207", "Money and Banking", 3, "Study of financial systems, banking operations, and monetary policy"),
                    CourseItem("BBA-206", "Mercantile Law", 3, "Legal principles governing commercial transactions and business contracts"),
                    CourseItem("SOC-211", "Introduction to Sociology", 3, "Core course"),
                    CourseItem("BBA-205", "Financial Accounting (Advanced)", 3, "Advanced topics in financial reporting and accounting standards"),
                    CourseItem("ENG-222", "English-IV (Business Communication-II)", 3, "Advanced business communication and presentation techniques")
                )
            ),
            SemesterOutline(
                semesterNumber = 5,
                courses = listOf(
                    CourseItem("BBA-304", "Web Engineering", 3, "Principles of developing and managing web-based applications"),
                    CourseItem("HQ-005", "Translation of Holy Quran", 0, ""),
                    CourseItem("BBA-305", "Human Resource Management", 3, "Managing workforce, recruitment, training, and employee relations"),
                    CourseItem("BBA-301", "Credit Management", 3, "Principles of credit assessment, risk management, and lending"),
                    CourseItem("BBA-303", "Company Law", 3, "Legal framework for corporate entities and company regulations"),
                    CourseItem("BBA-302", "Business Research Methods", 3, "Methodologies for conducting business and market research")
                )
            ),
            SemesterOutline(
                semesterNumber = 6,
                courses = listOf(
                    CourseItem("HQ-006", "Translation of Holy Quran", 1, ""),
                    CourseItem("BBA-307", "Taxation Management (Basic)", 3, "Introduction to tax systems, laws, and compliance"),
                    CourseItem("BBA-308", "Management Information System", 3, "Information systems for managerial decision making"),
                    CourseItem("BBA-309", "Development Economics", 3, "Economic development theories and policies for emerging markets"),
                    CourseItem("BBA-310", "Current Business Affairs", 3, "Analysis of contemporary issues and trends in the business world"),
                    CourseItem("BBA-311", "Corporate Governance", 3, "Frameworks for corporate accountability, ethics, and control"),
                    CourseItem("BBA-306", "Auditing", 3, "Principles and procedures of financial auditing and assurance")
                )
            ),
            SemesterOutline(
                semesterNumber = 7,
                courses = listOf(
                    CourseItem("HQ-007", "Translation of Holy Quran", 0, ""),
                    CourseItem("BBA-408", "Strategic Human Resource Management", 3, "Aligning HR strategies with overall corporate objectives"),
                    CourseItem("BBA-401", "Small Business Management", 3, "Strategies for starting and managing small enterprises"),
                    CourseItem("BBA-406", "Sales Management", 3, "Strategies for managing sales teams and sales operations"),
                    CourseItem("BBA-402", "Pakistan Economy", 3, "Analysis of the economic structure and policies of Pakistan"),
                    CourseItem("BBA-409", "Organizational Development", 3, "Strategies for managing organizational change and effectiveness"),
                    CourseItem("BBA-403", "Mathematics (Advanced)", 3, "Advanced mathematical techniques for business applications"),
                    CourseItem("BBA-407", "Marketing Research", 3, "Methods for gathering and analyzing marketing data"),
                    CourseItem("BBA-404", "Managerial Accounting", 3, "Use of accounting information for internal management decisions"),
                    CourseItem("BBA-405", "Financial Analysis", 3, "Techniques for analyzing financial statements and business performance")
                )
            ),
            SemesterOutline(
                semesterNumber = 8,
                courses = listOf(
                    CourseItem("BBA-414", "Taxation Management (Advanced)", 3, "Advanced tax planning and corporate taxation strategies"),
                    CourseItem("BBA-410", "Statistics (Advanced)", 3, "Advanced statistical tools for business modeling and forecasting"),
                    CourseItem("BBA-417", "Labour Laws in Pakistan", 3, "Legal framework governing employer-employee relations in Pakistan"),
                    CourseItem("BBA-418", "Human Resource Development", 3, "Strategies for employee training, development, and capacity building"),
                    CourseItem("BBA-413", "Financial Management (Advanced)", 3, "Advanced corporate finance, capital structuring, and investments"),
                    CourseItem("BBA-412", "Financial Institutions & Services", 3, "Operations of banks, investment firms, and financial markets"),
                    CourseItem("BBA-411", "E-Commerce", 3, "Principles and models of conducting business electronically"),
                    CourseItem("BBA-416", "Consumer Behaviour", 3, "Psychological and social factors influencing consumer purchasing"),
                    CourseItem("BBA-415", "Advertising", 3, "Principles of advertising campaigns, media planning, and promotion"),
                    CourseItem("HQ-008", "Translation of Holy Quran", 1, "")
                )
            )
        )
    )

    // 3. BS English
    val bsEnglish = ProgramCourseOutline(
        id = "bs_english",
        name = "BS English",
        headOfDepartment = "Prof. Ikram Bhatti",
        duration = "4 Years (8 Semesters)",
        eligibility = "FA/F.Sc or equivalent",
        about = "Study literature, linguistics, and creative writing to enhance critical and communication skills.",
        isIntermediate = false,
        semesters = listOf(
            SemesterOutline(
                semesterNumber = 1,
                courses = listOf(
                    CourseItem("COMP-111", "Computer (Introduction and Applications)", 3, "Core course"),
                    CourseItem("ENG-111", "English-I (Language in Use)", 3, "Core course"),
                    CourseItem("APSY-111", "Fundamentals of Psychology", 3, "Core course"),
                    CourseItem("ENG-102", "Introduction to Linguistics-I", 3, "Core course"),
                    CourseItem("ENG-101", "Introduction to Literature-I (History of English Literature-I)", 3, "Core course"),
                    CourseItem("ISE-111", "Islamiat/Ethics", 2, "Core course"),
                    CourseItem("HQ-001", "Translation of Holy Quran", 0, "")
                )
            ),
            SemesterOutline(
                semesterNumber = 2,
                courses = listOf(
                    CourseItem("HQ-002", "Translation of Holy Quran", 1, ""),
                    CourseItem("PST-111", "Pakistan Studies", 2, "Core course"),
                    CourseItem("BSCS-111", "Mass Communication", 3, "Core course"),
                    CourseItem("IR-111", "International Relations", 3, "Core course"),
                    CourseItem("ENG-112", "English-II (Academic Reading and Writing)", 3, "Core course")
                )
            ),
            SemesterOutline(
                semesterNumber = 3,
                courses = listOf(
                    CourseItem("HQ-003", "Translation of Holy Quran", 0, ""),
                    CourseItem("PHIL-211A", "Introduction to Philosophy (Rev)", 3, "Core course"),
                    CourseItem("ENG-202", "Introduction to Literature-III (Fiction and Non-Fiction)", 3, "Core course"),
                    CourseItem("ENG-103", "Introduction to Literature-II (Poetry and One Act Plays)", 3, "Core course"),
                    CourseItem("ENG-203", "Introduction to Linguistics-III (Phonetics and English Phonology)", 3, "Core course"),
                    CourseItem("ENG-104", "Introduction to Linguistics-II", 3, "Core course"),
                    CourseItem("HR-211", "Human Resource Management", 3, "Core course"),
                    CourseItem("BBA-211", "Entrepreneurship", 3, "Core course"),
                    CourseItem("ENG-201", "English-III (Advance Communication Skills)", 3, "Core course")
                )
            ),
            SemesterOutline(
                semesterNumber = 4,
                courses = listOf(
                    CourseItem("HQ-004", "Translation of Holy Quran", 1, ""),
                    CourseItem("SOC-211", "Introduction to Sociology", 3, "Core course"),
                    CourseItem("ENG-205", "Introduction to Literature-IV (History of Literature-II)", 3, "Core course"),
                    CourseItem("ENG-206", "Introduction to Linguistics-IV (The Structure of English)", 3, "Core course"),
                    CourseItem("GEOG-211", "Introduction to Geography", 3, "Core course"),
                    CourseItem("ENG-204", "English-IV (Advance Academic Reading and Writing)", 3, "Core course"),
                    CourseItem("HR-213", "Citizenship Education (Human Rights)", 3, "Core course")
                )
            ),
            SemesterOutline(
                semesterNumber = 5,
                courses = listOf(
                    CourseItem("ENG-306", "Visionary Discourse", 3, "Core course"),
                    CourseItem("HQ-005", "Translation of Holy Quran", 0, ""),
                    CourseItem("ENG-305", "Sociolinguistics", 3, "Core course"),
                    CourseItem("ENG-302", "Poetry (14th to 18th Century)", 3, "Core course"),
                    CourseItem("ENG-303", "Novel (18th & 19th Century)", 3, "Core course"),
                    CourseItem("ENG-304", "Journalistic Discourse", 3, "Core course"),
                    CourseItem("ENG-301", "Criticism and Theory-I", 3, "Core course")
                )
            ),
            SemesterOutline(
                semesterNumber = 6,
                courses = listOf(
                    CourseItem("ENG-311", "World Literatures in Translation", 3, "Core course"),
                    CourseItem("HQ-006", "Translation of Holy Quran", 1, ""),
                    CourseItem("ENG-310", "Fantasy", 3, "Core course"),
                    CourseItem("ENG-307", "Criticism and Theory-II", 3, "Core course"),
                    CourseItem("ENG-308", "Classics in Drama", 3, "Core course"),
                    CourseItem("ENG-309", "19th Century Poetry", 3, "Core course")
                )
            ),
            SemesterOutline(
                semesterNumber = 7,
                courses = listOf(
                    CourseItem("HQ-007", "Translation of Holy Quran", 0, ""),
                    CourseItem("ENG-404", "South Asian Literature", 3, "Core course"),
                    CourseItem("ENG-405", "Research Methodology", 3, "Core course"),
                    CourseItem("ENG-403", "American Literature", 3, "Core course"),
                    CourseItem("ENG-402", "20th Century Fiction & Prose", 3, "Core course"),
                    CourseItem("ENG-401", "20th Century British Literature: Poetry & Drama", 3, "Core course")
                )
            ),
            SemesterOutline(
                semesterNumber = 8,
                courses = listOf(
                    CourseItem("ENG-406", "Continental Literature", 3, "Core course"),
                    CourseItem("ENG-407", "Pakistani Literature", 3, "Core course"),
                    CourseItem("ENG-409", "Research Project", 6, "Core course"),
                    CourseItem("ENG-408", "Teaching of Literature", 3, "Core course"),
                    CourseItem("HQ-008", "Translation of Holy Quran", 1, "")
                )
            )
        )
    )

    // 4. BS Islamic Studies
    val bsIslamicStudies = ProgramCourseOutline(
        id = "bs_islamic_studies",
        name = "BS Islamic Studies",
        headOfDepartment = "Prof. Saif Ullah",
        duration = "4 Years (8 Semesters)",
        eligibility = "FA or equivalent",
        about = "Explore Islamic history, theology, and culture under esteemed faculty guidance.",
        isIntermediate = false,
        semesters = listOf(
            SemesterOutline(
                semesterNumber = 1,
                courses = listOf(
                    CourseItem("ARB-101", "Arabic-I", 3, "Basic Arabic language skills"),
                    CourseItem("HQ-001", "Translation of Holy Quran", 0, ""),
                    CourseItem("ISE-101", "Tafseer-I", 3, "Introduction to the exegesis of the Quran"),
                    CourseItem("ISE-102", "Hadith-I", 3, "Introduction to the traditions of the Prophet"),
                    CourseItem("ENG-111", "English-I (Language in Use)", 3, "Core course"),
                    CourseItem("COMP-101", "Computer-I", 3, "Introduction to basic computer applications")
                )
            ),
            SemesterOutline(
                semesterNumber = 2,
                courses = listOf(
                    CourseItem("HQ-002", "Translation of Holy Quran", 1, ""),
                    CourseItem("ISE-103", "Tafseer-II", 3, "Intermediate study of Quranic exegesis"),
                    CourseItem("ISE-105", "Seerat un Nabi", 3, "Biography of the Prophet Muhammad (PBUH)"),
                    CourseItem("PST-111", "Pakistan Studies", 2, "Core course"),
                    CourseItem("ISE-104", "Hadith-II", 3, "Intermediate study of Prophetic traditions"),
                    CourseItem("ENG-112", "English-II (Academic Reading and Writing)", 3, "Core course"),
                    CourseItem("COMP-103", "Computer-II", 3, "Intermediate computer applications"),
                    CourseItem("ARB-102", "Arabic-II", 3, "Intermediate Arabic language skills")
                )
            ),
            SemesterOutline(
                semesterNumber = 3,
                courses = listOf(
                    CourseItem("HQ-003", "Translation of Holy Quran", 0, ""),
                    CourseItem("ISE-202", "Fiqh-1", 3, "Introduction to Islamic jurisprudence"),
                    CourseItem("ENG-211", "English-III (Communication Skills)", 3, "Advanced communication and presentation skills"),
                    CourseItem("COM-201", "Computer-III", 3, "Advanced computer applications and tools"),
                    CourseItem("ARB-201", "Arabic-III", 3, "Advanced Arabic grammar and literature")
                )
            ),
            SemesterOutline(
                semesterNumber = 4,
                courses = listOf(
                    CourseItem("HQ-004", "Translation of Holy Quran", 1, ""),
                    CourseItem("ISE-205", "Tafseer-III", 3, "Advanced study of selected Quranic chapters"),
                    CourseItem("ISE-206", "Islamic and modern Political Thoughts", 3, "Comparison of Islamic and contemporary political theories"),
                    CourseItem("ENG-212", "English-IV (English for practical Aims)", 3, "English for practical and professional purposes"),
                    CourseItem("COM-203", "Computer-IV", 3, "Practical computing for Islamic studies"),
                    CourseItem("ARB-202", "Arabic-IV", 3, "Advanced Arabic composition and rhetoric")
                )
            ),
            SemesterOutline(
                semesterNumber = 5,
                courses = listOf(
                    CourseItem("HQ-005", "Translation of Holy Quran", 0, ""),
                    CourseItem("PER-111", "Persian", 3, "Basic Persian language skills"),
                    CourseItem("ISE-303", "Islamic & Modern Social Thought", 3, "Sociological perspectives in Islam and modernity"),
                    CourseItem("ISE-301", "Hadith-III", 3, "Advanced study of selected Hadith collections"),
                    CourseItem("ISE-302", "Fiqh-II", 3, "Intermediate Islamic jurisprudence and rulings"),
                    CourseItem("ARB-301", "Arabic-V", 3, "Specialized Arabic textual studies"),
                    CourseItem("ENG-321", "Analytical Study of English Literature", 3, "Analysis of classical and modern English literature")
                )
            ),
            SemesterOutline(
                semesterNumber = 6,
                courses = listOf(
                    CourseItem("HQ-006", "Translation of Holy Quran", 1, ""),
                    CourseItem("ISE-304", "Tafseer-IV", 3, "Thematic exegesis of the Quran"),
                    CourseItem("ISE-307", "Islamic Jurisprudence & its History", 3, "Historical development of Fiqh schools"),
                    CourseItem("ISE-305", "History & Principles of Hadith", 3, "Methodology of Hadith authentication and compilation"),
                    CourseItem("ISE-306", "Dawa o Irshad", 3, "Principles and methodologies of Islamic preaching"),
                    CourseItem("ARB-302", "Arabic-VI", 3, "Classical Arabic prose and poetry")
                )
            ),
            SemesterOutline(
                semesterNumber = 7,
                courses = listOf(
                    CourseItem("HQ-007", "Translation of Holy Quran", 0, ""),
                    CourseItem("ISE-404", "Modern Tools of Research", 3, "Research methodologies in Islamic studies"),
                    CourseItem("ISE-402", "Islamic Economics-I", 3, "Principles of the Islamic economic system"),
                    CourseItem("ISE-403", "History & Principles of Tafsir", 3, "Historical development and methodologies of Tafsir"),
                    CourseItem("ISE-401", "Comparative Study of Religions-I", 3, "Introduction to major world religions"),
                    CourseItem("ARB-401", "Arabic-VII", 3, "Advanced study of Arabic syntax and morphology")
                )
            ),
            SemesterOutline(
                semesterNumber = 8,
                courses = listOf(
                    CourseItem("ARB-402", "Arabic-VIII", 3, "Comprehensive review of Arabic language and literature"),
                    CourseItem("ISE-407", "Comparative Study of Religions-II", 3, "In-depth comparison of Abrahamic and non-Abrahamic faiths"),
                    CourseItem("ISE-406", "Fiqh-III", 3, "Advanced topics in Islamic jurisprudence"),
                    CourseItem("ISE-408", "Islamic Economics-II", 3, "Contemporary issues in Islamic banking and finance"),
                    CourseItem("ISE-405", "Sciences of Quran", 3, "Study of Uloom ul Quran (revelation, compilation, etc.)"),
                    CourseItem("HQ-008", "Translation of Holy Quran", 1, "")
                )
            )
        )
    )

    // 5. BS Physics
    val bsPhysics = ProgramCourseOutline(
        id = "bs_physics",
        name = "BS Physics",
        headOfDepartment = "Prof. Asif Zaman",
        duration = "4 Years (8 Semesters)",
        eligibility = "F.Sc Pre-Engineering / Equivalent",
        about = "A comprehensive four-year degree program focusing on classical and modern physics, experimental methods, and research-based learning.",
        isIntermediate = false,
        semesters = listOf(
            SemesterOutline(
                semesterNumber = 1,
                courses = listOf(
                    CourseItem("Math 1001", "Calculus-I", 3, "Introduction to limits, derivatives, and integrals"),
                    CourseItem("Phys 1002", "Waves and Optics", 3, "Study of wave phenomena and light"),
                    CourseItem("HQ-001", "Translation of Holy Quran", 0, ""),
                    CourseItem("Phys 1601L", "Physics Lab-I", 1, "Basic physics practical lab work"),
                    CourseItem("Gen 1002", "Pakistan Studies", 2, "History and culture of Pakistan"),
                    CourseItem("Phys 1001", "Mechanics", 3, "Study of classical mechanics and kinematics"),
                    CourseItem("Gen 1003", "Islamic Studies/Ethics", 2, "Basic Islamic principles and ethical values"),
                    CourseItem("Gen 1001", "English-I (Reading and Writing Skills)", 3, "Fundamental reading and writing techniques")
                )
            ),
            SemesterOutline(
                semesterNumber = 2,
                courses = listOf(
                    CourseItem("HQ-002", "Translation of Holy Quran", 1, ""),
                    CourseItem("Phys 1003", "Thermal Physics", 3, "Study of thermodynamics and heat transfer"),
                    CourseItem("Math 1003", "Probability and Statistics", 3, "Data analysis and statistical methods"),
                    CourseItem("Phys 1602L", "Physics Lab-II", 1, "Physics practical lab work"),
                    CourseItem("Gen 1004", "English-II (Composition Writing)", 3, "Advanced composition and writing skills"),
                    CourseItem("Phys 1004", "Electricity and Magnetism", 3, "Fundamentals of electric and magnetic fields"),
                    CourseItem("Math 1002", "Calculus-II", 3, "Advanced topics in calculus")
                )
            ),
            SemesterOutline(
                semesterNumber = 3,
                courses = listOf(
                    CourseItem("HQ-003", "Translation of Holy Quran", 0, ""),
                    CourseItem("Phys 2001", "Quantum Physics", 3, "Introduction to quantum mechanics concepts"),
                    CourseItem("Phys-2603L", "Physics Lab-III", 1, "Physics practical lab work"),
                    CourseItem("Gen 2005", "Introduction to Computing", 3, "Fundamentals of computer science and programming"),
                    CourseItem("Math 2003", "Differential Equations", 3, "Methods for solving differential equations"),
                    CourseItem("Gen 2003/2004", "Chemistry/Natural Science", 3, "Basic concepts of chemistry and natural sciences"),
                    CourseItem("Math 2004", "Analytical Geometry", 3, "Study of geometry using coordinate systems")
                )
            ),
            SemesterOutline(
                semesterNumber = 4,
                courses = listOf(
                    CourseItem("HQ-004", "Translation of Holy Quran", 1, ""),
                    CourseItem("Phys 2904", "Physics Lab-IV", 1, "Physics practical lab work"),
                    CourseItem("Phys 2002", "Modern Physics", 3, "Study of relativity and modern physical theories"),
                    CourseItem("Math 2005", "Linear Algebra", 3, "Study of vectors, matrices, and linear transformations"),
                    CourseItem("Gen 2006/2007", "Humanities/Social Science", 3, "Introduction to human society and culture"),
                    CourseItem("Phys 2003", "Basic Electronics", 3, "Introduction to electronic circuits and devices"),
                    CourseItem("Math 2006", "Applied Mathematics", 3, "Application of mathematical methods to physical problems")
                )
            ),
            SemesterOutline(
                semesterNumber = 5,
                courses = listOf(
                    CourseItem("HQ-005", "Translation of Holy Quran", 0, ""),
                    CourseItem("PHYS 3401", "Solid State Physics-I", 3, "Introduction to solid materials"),
                    CourseItem("PHYS 3605L", "Physics Lab-V", 2, "Physics practical lab work"),
                    CourseItem("PHYS 3501", "Mathematical Methods of Physics-I", 3, "Mathematical tools for physics"),
                    CourseItem("PHYS 3701", "Electronics-I", 3, "Basic electronics and circuits"),
                    CourseItem("PHYS 3502", "Computational Physics-I", 3, "Numerical and computational methods"),
                    CourseItem("PHYS 3101", "Classical Mechanics", 3, "Study of motion and forces")
                )
            ),
            SemesterOutline(
                semesterNumber = 6,
                courses = listOf(
                    CourseItem("HQ-006", "Translation of Holy Quran", 1, ""),
                    CourseItem("PHYS 3402", "Solid State Physics-II", 3, "Advanced solid state concepts"),
                    CourseItem("PHYS 3301", "Quantum Mechanics-I", 3, "Introduction to quantum mechanics"),
                    CourseItem("PHYS 3606L", "Physics Lab-VI", 2, "Advanced physics lab"),
                    CourseItem("PHYS 3503", "Mathematical Methods of Physics-II", 3, "Advanced mathematical methods"),
                    CourseItem("PHYS 3702", "Electronics-II", 3, "Advanced electronics"),
                    CourseItem("PHYS 3504", "Computational Physics-II", 3, "Advanced computational physics")
                )
            ),
            SemesterOutline(
                semesterNumber = 7,
                courses = listOf(
                    CourseItem("HQ-007", "Translation of Holy Quran", 0, ""),
                    CourseItem("PHYS 4403", "Statistical Physics", 3, "Thermodynamics and statistics"),
                    CourseItem("PHYS 4302", "Quantum Mechanics-II", 3, "Advanced quantum mechanics"),
                    CourseItem("PHYS 4303", "Nuclear Physics-I", 3, "Basics of nuclear physics"),
                    CourseItem("PHYS E2", "Elective-II", 3, "Department elective course"),
                    CourseItem("PHYS E1", "Elective-I", 3, "Department elective course"),
                    CourseItem("PHYS 4201", "Classical Electrodynamics-I", 3, "Electric and magnetic fields")
                )
            ),
            SemesterOutline(
                semesterNumber = 8,
                courses = listOf(
                    CourseItem("PHYS 4404", "Solid State Physics-III", 3, "Advanced solid state physics"),
                    CourseItem("PHYS 4102", "Relativity and Cosmology", 3, "Study of relativity and universe"),
                    CourseItem("PHYS 4304", "Nuclear Physics-II", 3, "Advanced nuclear physics"),
                    CourseItem("PHYS E4", "Elective-IV", 3, "Department elective course"),
                    CourseItem("PHYS E3", "Elective-III", 3, "Department elective course"),
                    CourseItem("PHYS 4202", "Classical Electrodynamics-II", 3, "Advanced electrodynamics"),
                    CourseItem("HQ-008", "Translation of Holy Quran", 1, "")
                )
            )
        )
    )

    // 6. BS Mathematics
    val bsMathematics = ProgramCourseOutline(
        id = "bs_math",
        name = "BS Mathematics",
        headOfDepartment = "Prof. Abdul Manan",
        duration = "4 Years (8 Semesters)",
        eligibility = "F.Sc Pre-Engineering / Equivalent",
        about = "An intensive program covering pure and applied mathematics, logical reasoning, and problem-solving skills for academia and industry.",
        isIntermediate = false,
        semesters = listOf(
            SemesterOutline(
                semesterNumber = 1,
                courses = listOf(
                    CourseItem("COMP-111", "Computer (Introduction and Applications)", 3, "Core course"),
                    CourseItem("HQ-001", "Translation of Holy Quran", 0, ""),
                    CourseItem("PHY-111", "Physics-I", 3, "Basic principles of mechanics and thermodynamics"),
                    CourseItem("PHY-112", "Physics Lab-I", 1, "Practical experiments in basic physics"),
                    CourseItem("MATH-102", "Mathematics B-I [Vectors & Mechanics (I)]", 4, "Vector algebra and introductory classical mechanics"),
                    CourseItem("MATH-101", "Mathematics A-I [Calculus (I)]", 4, "Introduction to limits, continuity, and derivatives"),
                    CourseItem("ISE-111", "Islamiat/Ethics", 2, "Core course"),
                    CourseItem("ENG-111", "English-I (Language in Use)", 3, "Core course")
                )
            ),
            SemesterOutline(
                semesterNumber = 2,
                courses = listOf(
                    CourseItem("HQ-002", "Translation of Holy Quran", 1, ""),
                    CourseItem("PHY-114", "Physics Lab-II", 1, "Practical experiments in electricity and magnetism"),
                    CourseItem("PHY-113", "Physics-II", 3, "Study of electromagnetism and wave phenomena"),
                    CourseItem("PST-111", "Pakistan Studies", 2, "Core course"),
                    CourseItem("MATH-104", "Mathematics B-II [Mechanics (II)]", 4, "Advanced concepts in classical mechanics"),
                    CourseItem("MATH-103", "Mathematics A-II [Plane Curves & Analytic Geometry]", 4, "Study of 2D geometry and coordinate systems"),
                    CourseItem("ENG-112", "English-II (Academic Reading and Writing)", 3, "Core course"),
                    CourseItem("MATH-105", "Discrete Mathematics", 2, "Study of mathematical structures that are fundamentally discrete")
                )
            ),
            SemesterOutline(
                semesterNumber = 3,
                courses = listOf(
                    CourseItem("HQ-003", "Translation of Holy Quran", 0, ""),
                    CourseItem("PHY-212", "Physics Lab-III", 1, "Advanced physics laboratory experiments"),
                    CourseItem("PHY-211", "Physics-III", 3, "Introduction to modern physics and optics"),
                    CourseItem("MATH-202", "Mathematics B-III [Calculus (II)]", 4, "Advanced calculus including integration and multivariable functions"),
                    CourseItem("MATH-201", "Mathematics A-III [Linear Algebra]", 4, "Study of vector spaces, matrices, and linear transformations"),
                    CourseItem("MATH-205", "Graph Theory", 2, "Introduction to graphs, networks, and their properties"),
                    CourseItem("ENG-211", "English-III (Communication Skills)", 3, "Advanced communication and presentation skills")
                )
            ),
            SemesterOutline(
                semesterNumber = 4,
                courses = listOf(
                    CourseItem("HQ-004", "Translation of Holy Quran", 1, ""),
                    CourseItem("PHY-214", "Physics Lab-IV", 1, "Practical applications of modern physics"),
                    CourseItem("PHY-213", "Physics-IV", 3, "Concepts of quantum mechanics and solid-state physics"),
                    CourseItem("MATH-204", "Mathematics B-IV [Metric Spaces & Group Theory]", 4, "Introduction to metric spaces and algebraic groups"),
                    CourseItem("MATH-203", "Mathematics A-IV [Ordinary Differential Equations]", 4, "Methods for solving ordinary differential equations"),
                    CourseItem("SOC-211", "Introduction to Sociology", 3, "Core course"),
                    CourseItem("ENG-212", "English-IV (English for practical Aims)", 3, "English for practical and professional purposes"),
                    CourseItem("MATH-206", "Elementary Number Theory", 2, "Properties and relationships of integers")
                )
            ),
            SemesterOutline(
                semesterNumber = 5,
                courses = listOf(
                    CourseItem("MATH-304", "Vector and Tensor Analysis", 3, "Advanced vector calculus and tensor algebra"),
                    CourseItem("HQ-005", "Translation of Holy Quran", 0, ""),
                    CourseItem("MATH-305", "Topology", 3, "Study of spatial properties preserved under continuous deformations"),
                    CourseItem("MATH-301", "Real Analysis-I", 3, "Rigorous study of real numbers and continuous functions"),
                    CourseItem("MATH-302", "Group Theory-I", 3, "Fundamental concepts of algebraic groups and symmetry"),
                    CourseItem("MATH-306", "Differential Geometry", 3, "Application of calculus to curves and surfaces"),
                    CourseItem("MATH-303", "Complex Analysis-I", 3, "Calculus of complex-valued functions")
                )
            ),
            SemesterOutline(
                semesterNumber = 6,
                courses = listOf(
                    CourseItem("HQ-006", "Translation of Holy Quran", 1, ""),
                    CourseItem("MATH-308", "Rings and Vector Spaces", 3, "Study of algebraic rings, fields, and vector spaces"),
                    CourseItem("MATH-307", "Real Analysis-II", 3, "Advanced topics in real analysis and integration"),
                    CourseItem("MATH-312", "Ordinary Differential Equations", 3, "Advanced techniques for differential equations"),
                    CourseItem("MATH-310", "Mechanics", 3, "Mathematical formulation of classical mechanics"),
                    CourseItem("MATH-311", "Functional Analysis-I", 3, "Study of vector spaces endowed with topological structure"),
                    CourseItem("MATH-309", "Complex Analysis-II", 3, "Advanced topics in complex function theory")
                )
            ),
            SemesterOutline(
                semesterNumber = 7,
                courses = listOf(
                    CourseItem("MATH-412", "Operations Research-I", 3, "Optimization and mathematical decision making"),
                    CourseItem("MATH-402", "Partial Differential Equations", 3, "Solving equations involving partial derivatives"),
                    CourseItem("MATH-409", "Quantum Mechanics-I", 3, "Mathematical foundations of quantum theory"),
                    CourseItem("MATH-407", "Ring Theory", 3, "In-depth study of algebraic rings and ideals"),
                    CourseItem("MATH-401", "Set Theory", 3, "Formal study of sets and their properties"),
                    CourseItem("MATH-413", "Theory of Approximation and Splines-I", 3, "Methods for function approximation"),
                    CourseItem("HQ-007", "Translation of Holy Quran", 0, ""),
                    CourseItem("MATH-403", "Numerical Analysis-I", 3, "Numerical methods for solving mathematical problems"),
                    CourseItem("MATH-408", "Number Theory-I", 3, "Advanced study of integers and prime numbers"),
                    CourseItem("MATH-410", "Analytical Dynamics", 3, "Advanced classical mechanics using Lagrangian and Hamiltonian formulations"),
                    CourseItem("MATH-411", "Electromagnetic Theory-I", 3, "Mathematical modeling of electric and magnetic fields"),
                    CourseItem("MATH-415", "Fluid Mechanics-I", 3, "Mathematical modeling of fluid flow"),
                    CourseItem("MATH-405", "Fortran Programming", 3, "Scientific programming using FORTRAN"),
                    CourseItem("MATH-414", "Functional Analysis-II", 3, "Advanced topics in functional analysis and operator theory"),
                    CourseItem("MATH-406", "Group Theory-II", 3, "Advanced topics in group theory"),
                    CourseItem("MATH-404", "Mathematical Statistics-I", 3, "Probability theory and statistical inference")
                )
            ),
            SemesterOutline(
                semesterNumber = 8,
                courses = listOf(
                    CourseItem("MATH-418", "Numerical Analysis-II", 3, "Advanced numerical computation methods"),
                    CourseItem("MATH-427", "Operations Research-II", 3, "Advanced linear programming and optimization models"),
                    CourseItem("MATH-424", "Quantum Mechanics-II", 3, "Advanced applications of quantum mechanics"),
                    CourseItem("MATH-425", "Special Theory of Relativity", 3, "Mathematical framework of special relativity"),
                    CourseItem("MATH-428", "Theory of Approximation and Splines-II", 3, "Advanced spline theory and applications"),
                    CourseItem("MATH-422", "Theory of Modules", 3, "Study of algebraic modules over rings"),
                    CourseItem("MATH-423", "Number Theory-II", 3, "Analytic and algebraic number theory"),
                    CourseItem("MATH-417", "Methods of Mathematical Physics", 3, "Mathematical techniques used in theoretical physics"),
                    CourseItem("MATH-416", "Measure Theory and Lebesgue Integration", 3, "Advanced integration theory and measure spaces"),
                    CourseItem("MATH-419", "Mathematical Statistics-II", 3, "Advanced statistical modeling and analysis"),
                    CourseItem("MATH-421", "Group Theory-III", 3, "Specialized topics in group theory"),
                    CourseItem("MATH-429", "Functional Analysis-III", 3, "Specialized topics in spectral theory and functional analysis"),
                    CourseItem("MATH-430", "Fluid Mechanics-II", 3, "Advanced fluid dynamics and viscous flow"),
                    CourseItem("MATH-426", "Electromagnetic Theory-II", 3, "Advanced topics in electrodynamics"),
                    CourseItem("MATH-420", "Computer Applications", 3, "Applied computational mathematics"),
                    CourseItem("HQ-008", "Translation of Holy Quran", 1, "")
                )
            )
        )
    )

    // 7. BS Political Science
    val bsPoliticalScience = ProgramCourseOutline(
        id = "bs_pol_science",
        name = "BS Political Science",
        headOfDepartment = "Prof. Afrasiab",
        duration = "4 Years (8 Semesters)",
        eligibility = "Intermediate / Equivalent",
        about = "Designed to help students understand political systems, governance, and global affairs with strong analytical training.",
        isIntermediate = false,
        semesters = listOf(
            SemesterOutline(
                semesterNumber = 1,
                courses = listOf(
                    CourseItem("COM-111", "Computer (Introduction and Applications)", 3, "Fundamentals of computing and software applications"),
                    CourseItem("HQ-001", "Translation of Holy Quran", 0, ""),
                    CourseItem("POL-101", "Political Science-I", 3, "Introduction to foundational concepts of political science"),
                    CourseItem("SOC-102", "Logic and Critical Thinking", 3, "Principles of logical reasoning and critical analysis"),
                    CourseItem("ISE-111", "Islamiat/Ethics", 2, "Core course"),
                    CourseItem("APSY-111", "Fundamentals of Psychology", 3, "Core course"),
                    CourseItem("ENG-111", "English-I (Language in Use)", 3, "Core course")
                )
            ),
            SemesterOutline(
                semesterNumber = 2,
                courses = listOf(
                    CourseItem("HQ-002", "Translation of Holy Quran", 1, ""),
                    CourseItem("POL-102", "Political Science-II", 3, "Advanced fundamental concepts of political science"),
                    CourseItem("PST-111", "Pakistan Studies", 2, "Core course"),
                    CourseItem("IR-111", "International Relations", 3, "Core course"),
                    CourseItem("ECON-111", "Fundamentals of Economics", 3, "Basic principles of micro and macroeconomics"),
                    CourseItem("ENG-112", "English-II (Academic Reading and Writing)", 3, "Core course"),
                    CourseItem("MATH-111", "Elementary Mathematics", 3, "Basic mathematical concepts for business")
                )
            ),
            SemesterOutline(
                semesterNumber = 3,
                courses = listOf(
                    CourseItem("HQ-003", "Translation of Holy Quran", 0, ""),
                    CourseItem("POL-201", "Political Systems (Developed)", 3, "Study of political systems in developed nations"),
                    CourseItem("PHIL-211", "Introduction to Philosophy", 3, "Basic philosophical theories and classical thinkers"),
                    CourseItem("GEOG-211", "Introduction to Geography", 3, "Core course"),
                    CourseItem("ENG-211", "English-III (Communication Skills)", 3, "Advanced communication and presentation skills"),
                    CourseItem("Math-211", "Elementary Mathematics-II", 3, "Compulsory")
                )
            ),
            SemesterOutline(
                semesterNumber = 4,
                courses = listOf(
                    CourseItem("HQ-004", "Translation of Holy Quran", 1, ""),
                    CourseItem("POL-202", "Political Systems (Developing)", 3, "Study of political structures in developing countries"),
                    CourseItem("POL-203", "Pakistan Movement", 3, "Historical analysis of the movement for Pakistan"),
                    CourseItem("SOC-211", "Introduction to Sociology", 3, "Core course"),
                    CourseItem("GS-211", "General Science", 3, "Overview of fundamental scientific principles"),
                    CourseItem("ENG-212", "English-IV (English for practical Aims)", 3, "English for practical and professional purposes")
                )
            ),
            SemesterOutline(
                semesterNumber = 5,
                courses = listOf(
                    CourseItem("POL-301", "Western Political Philosophy-I", 4, "Study of classical Western political thought"),
                    CourseItem("HQ-005", "Translation of Holy Quran", 0, ""),
                    CourseItem("POL-305", "Public Administration", 4, "Principles and practices of public administration"),
                    CourseItem("POL-304", "Political Ideologies", 3, "Overview of major political ideologies"),
                    CourseItem("POL-302", "Muslim Political Philosophy-I", 3, "Introduction to classical Muslim political thinkers"),
                    CourseItem("POL-303", "Comparative and Developmental Politics-I", 4, "Comparative analysis of political development")
                )
            ),
            SemesterOutline(
                semesterNumber = 6,
                courses = listOf(
                    CourseItem("POL-306", "Western Political Philosophy-II", 4, "Modern and contemporary Western political thought"),
                    CourseItem("HQ-006", "Translation of Holy Quran", 1, ""),
                    CourseItem("POL-307", "Muslim Political Philosophy-II", 3, "Modern Muslim political thought and movements"),
                    CourseItem("POL-310", "Introduction to Local Government", 3, "Structure and function of local government systems"),
                    CourseItem("POL-309", "History of International Relations", 4, "Historical evolution of international relations"),
                    CourseItem("POL-308", "Comparative and Developmental Politics-II", 3, "Advanced comparative political analysis")
                )
            ),
            SemesterOutline(
                semesterNumber = 7,
                courses = listOf(
                    CourseItem("HQ-007", "Translation of Holy Quran", 0, ""),
                    CourseItem("POL-403", "Public International Law-I", 3, "Fundamentals of international law and treaties"),
                    CourseItem("POL-401", "Methods of Study & Research", 3, "Research methodologies in political science"),
                    CourseItem("POL-404", "Foreign Policy of Pakistan", 3, "Analysis of Pakistan's foreign relations and policies"),
                    CourseItem("POL-402", "Foreign Policy Analysis", 3, "Frameworks for analyzing foreign policy decisions"),
                    CourseItem("POL-406", "Foreign Policies of USA and UK", 3, "Comparative study of US and UK foreign policies"),
                    CourseItem("POL-405", "Diplomacy", 3, "Principles and practices of international diplomacy")
                )
            ),
            SemesterOutline(
                semesterNumber = 8,
                courses = listOf(
                    CourseItem("POL-409", "International Organizations", 3, "Role and impact of international organizations like the UN"),
                    CourseItem("POL-407", "Local Government in Pakistan", 3, "Study of local government institutions in Pakistan"),
                    CourseItem("POL-411", "Political Ideologies", 3, "In-depth analysis of contemporary political ideologies"),
                    CourseItem("POL-408", "Public International Law-II", 3, "Advanced topics in international law and global governance"),
                    CourseItem("POL-410", "Regional Organizations", 3, "Study of regional bodies like EU, ASEAN, and SAARC"),
                    CourseItem("HQ-008", "Translation of Holy Quran", 1, "")
                )
            )
        )
    )

    // 8. BS Urdu
    val bsUrdu = ProgramCourseOutline(
        id = "bs_urdu",
        name = "BS Urdu",
        headOfDepartment = "Prof. Muhammad Iqbal",
        duration = "4 Years (8 Semesters)",
        eligibility = "Intermediate / Equivalent",
        about = "Focuses on Urdu language, literature, poetry, and research—developing linguistic expertise and cultural understanding.",
        isIntermediate = false,
        semesters = listOf(
            SemesterOutline(
                semesterNumber = 1,
                courses = listOf(
                    CourseItem("BSU 103", "A Science of Society I", 3, "Gen. Education"),
                    CourseItem("BSU 104", "Exploring Quantitative Skills", 3, "Gen. Education"),
                    CourseItem("BSU 105", "Fables, Wisdom Literature, and Epic", 3, "Gen. Education"),
                    CourseItem("BSU 101", "Introduction to Expository Writing", 3, "Gen. Education"),
                    CourseItem("HQ-001", "Translation of Holy Quran", 0, ""),
                    CourseItem("BSU 102", "What is Science?", 3, "Gen. Education")
                )
            ),
            SemesterOutline(
                semesterNumber = 2,
                courses = listOf(
                    CourseItem("BSU 110", "عالمی افسانہ", 3, "Gen. Education"),
                    CourseItem("HQ-002", "Translation of Holy Quran", 1, ""),
                    CourseItem("BSU 109", "Tools for Quantitative Reasoning", 3, "Gen. Education"),
                    CourseItem("BSU 107", "The Science of Global Challenges", 3, "Gen. Education"),
                    CourseItem("BSU 106", "Cross-Cultural Communication and Translation Skills", 3, "Gen. Education"),
                    CourseItem("BSU 108", "A Science of Society II", 3, "Gen. Education")
                )
            ),
            SemesterOutline(
                semesterNumber = 3,
                courses = listOf(
                    CourseItem("BSU 205", "شعری اصناف: تعارف اور تفہیم", 3, "Minor"),
                    CourseItem("BSU 204", "اردو زبان-تشکیل و ارتقا", 3, "Major"),
                    CourseItem("HQ-003", "Translation of Holy Quran", 0, ""),
                    CourseItem("PST-111", "Pakistan Studies", 2, "Core course"),
                    CourseItem("ISE-111", "Islamiat/Ethics", 2, "Core course"),
                    CourseItem("BSU 201", "Critical Reading and Academic Writing", 3, "Gen. Education")
                )
            ),
            SemesterOutline(
                semesterNumber = 4,
                courses = listOf(
                    CourseItem("BSU 209", "نثری اصناف: تعارف اور تفہیم", 3, "Minor"),
                    CourseItem("BSU 206", "تحقیق و تنقید - بنیادی مباحث", 3, "Major"),
                    CourseItem("BSU 207", "تحریر و انشا (عملی تربیت)", 3, "Minor"),
                    CourseItem("BSU 210", "اردو نثر کے اسالیب: مزاح، سفرنامہ، آپ بیتی", 3, "Major"),
                    CourseItem("BSU 208", "ادبی اصطلاحات", 3, "Minor"),
                    CourseItem("HQ-004", "Translation of Holy Quran", 1, "")
                )
            ),
            SemesterOutline(
                semesterNumber = 5,
                courses = listOf(
                    CourseItem("BSU 305", "کمپیوٹر", 3, "Distribution"),
                    CourseItem("BSU 303", "تاریخ ادب اردو: اولی تحریکیں", 3, "Major"),
                    CourseItem("BSU 302", "اردو غزل کا فکری و فنی مطالعہ", 3, "Major"),
                    CourseItem("BSU 301", "اردو داستان اور ناول: فکری و فنی مطالعہ", 3, "Major"),
                    CourseItem("BSU 304", "اردو افسانہ اور ڈراما: فکری و فنی مطالعہ", 3, "Major"),
                    CourseItem("HQ-005", "Translation of Holy Quran", 0, "")
                )
            ),
            SemesterOutline(
                semesterNumber = 6,
                courses = listOf(
                    CourseItem("BSU 310", "پنجابی زبان و ادب", 3, "Distribution"),
                    CourseItem("BSU 307", "لسانیات", 3, "Major"),
                    CourseItem("BSU 308", "اصول تحقیق و تدوین", 3, "Major"),
                    CourseItem("BSU 309", "اردو کے مکتب نثری: بین الاقوامی مراکز", 3, "Major"),
                    CourseItem("BSU 306", "اردو نظم: فکری و فنی مطالعہ", 3, "Major"),
                    CourseItem("HQ-006", "Translation of Holy Quran", 1, "")
                )
            ),
            SemesterOutline(
                semesterNumber = 7,
                courses = listOf(
                    CourseItem("BSU 405", "فارسی نثر", 3, "Distribution"),
                    CourseItem("BSU 404", "عربی زبان و ادب - اول", 3, "Distribution"),
                    CourseItem("BSU 401", "اردو صحافت: روایتی اور جدید", 3, "Major"),
                    CourseItem("BSU 403", "اردو ادب کا مابعد نوآبادیاتی مطالعہ", 3, "Minor"),
                    CourseItem("BSU 402", "اردو ادب کا تانیثی مطالعہ", 3, "Minor"),
                    CourseItem("HQ-007", "Translation of Holy Quran", 0, "")
                )
            ),
            SemesterOutline(
                semesterNumber = 8,
                courses = listOf(
                    CourseItem("HQ-008", "Translation of Holy Quran", 1, ""),
                    CourseItem("BSU 407", "اقبال کا خصوصی مطالعہ", 3, "Major"),
                    CourseItem("BSU 406", "ریسرچ پروجیکٹ", 3, "Major"),
                    CourseItem("BSU 409", "عربی زبان و ادب - دوم", 3, "Distribution"),
                    CourseItem("BSU 410", "فارسی شاعری", 3, "Distribution"),
                    CourseItem("BSU 408", "نئے تنقیدی مباحث", 3, "Minor")
                )
            )
        )
    )

    // 9. BS Chemistry
    val bsChemistry = ProgramCourseOutline(
        id = "bs_chemistry",
        name = "BS Chemistry",
        headOfDepartment = "Prof. Umer Minhas",
        duration = "4 Years (8 Semesters)",
        eligibility = "F.Sc Pre-Engineering / Equivalent",
        about = "BS Chemistry offers an in-depth understanding of chemical principles, laboratory techniques, and modern analytical methods.",
        isIntermediate = false,
        semesters = listOf(
            SemesterOutline(
                semesterNumber = 1,
                courses = listOf(
                    CourseItem("Eng-101", "English - I (Functional English)", 3, "Compulsory"),
                    CourseItem("HQ-001", "Translation of Holy Quran", 0, ""),
                    CourseItem("Bot-101 & 102", "Plant Diversity", 3, "General"),
                    CourseItem("Ise-101", "Islamic Studies", 2, "Compulsory"),
                    CourseItem("Zool-101 & 102", "Invertebrate Diversity", 3, "General"),
                    CourseItem("Comp-101", "Introduction to Computer", 3, "Compulsory"),
                    CourseItem("Chem-101 & 102", "Inorganic Chemistry", 4, "Foundation")
                )
            ),
            SemesterOutline(
                semesterNumber = 2,
                courses = listOf(
                    CourseItem("HQ-002", "Translation of Holy Quran", 1, ""),
                    CourseItem("Bot-103 & 104", "Plant Taxonomy, Anatomy and Development", 3, "General"),
                    CourseItem("Pst-101", "Pakistan Studies", 2, "Compulsory"),
                    CourseItem("Chem-103 & 104", "Organic Chemistry", 4, "Foundation"),
                    CourseItem("MATH-111", "Elementary Mathematics", 3, "Basic mathematical concepts for business"),
                    CourseItem("Zool-103 & 104", "Chordates Diversity", 3, "General")
                )
            ),
            SemesterOutline(
                semesterNumber = 3,
                courses = listOf(
                    CourseItem("HQ-003", "Translation of Holy Quran", 0, ""),
                    CourseItem("Chem-201 & 202", "Physical Chemistry", 4, "Foundation"),
                    CourseItem("ENG-201", "English-III (Advance Communication Skills)", 3, "Core course"),
                    CourseItem("Math-211", "Elementary Mathematics-II", 3, "Compulsory"),
                    CourseItem("Bot-201 & 202", "Cell Biology, Genetics and Evolution", 3, "General"),
                    CourseItem("Zool-201 & 202", "Animal Form & Function-I", 3, "General")
                )
            ),
            SemesterOutline(
                semesterNumber = 4,
                courses = listOf(
                    CourseItem("HQ-004", "Translation of Holy Quran", 1, ""),
                    CourseItem("Soc-201", "Sociology", 2, "General"),
                    CourseItem("ENG-202", "Introduction to Literature-III (Fiction and Non-Fiction)", 3, "Core course"),
                    CourseItem("Chem-203 & 204", "General Chemistry", 4, "Foundation"),
                    CourseItem("Bot-203 & 204", "Biodiversity and Conservation", 3, "General"),
                    CourseItem("Zool-203 & 204", "Animal Form & Function-II", 3, "General")
                )
            ),
            SemesterOutline(
                semesterNumber = 5,
                courses = listOf(
                    CourseItem("Chem-305", "Inorganic Chemistry-II (Chemical Bonding Theories)", 2, "Compulsory"),
                    CourseItem("Chem-309", "Organic Chemistry Lab", 1, "Compulsory"),
                    CourseItem("Chem-307", "Organic Chemistry-I (Fundamental Concepts)", 2, "Compulsory"),
                    CourseItem("Chem-308", "Organic Chemistry-II (Named Reactions)", 2, "Compulsory"),
                    CourseItem("Chem-303", "Physical Chemistry Lab", 1, "Compulsory"),
                    CourseItem("Chem-301", "Physical Chemistry-I (Electrochemistry)", 2, "Compulsory"),
                    CourseItem("Chem-302", "Physical Chemistry-II (Quantum Chemistry)", 2, "Compulsory"),
                    CourseItem("HQ-005", "Translation of Holy Quran", 0, ""),
                    CourseItem("Chem-304", "Inorganic Chemistry-I (Pi-Acceptor Ligands)", 2, "Compulsory"),
                    CourseItem("Chem-306", "Inorganic Chemistry Lab", 1, "Compulsory"),
                    CourseItem("Chem-317", "Bio Chemistry-II (General Biochemistry)", 2, "Optional"),
                    CourseItem("Chem-312", "Analytical Chemistry Lab", 1, "Optional"),
                    CourseItem("Chem-310", "Analytical Chemistry-I (Analytical Data Handling)", 2, "Optional"),
                    CourseItem("Chem-311", "Analytical Chemistry-II (Chromatography)", 2, "Optional"),
                    CourseItem("Chem-315", "Applied Chemistry Lab", 1, "Optional"),
                    CourseItem("Chem-313", "Applied Chemistry-I (Unit Operations & Chemicals)", 2, "Optional"),
                    CourseItem("Chem-314", "Applied Chemistry-II (Allied Chemical Industries)", 2, "Optional"),
                    CourseItem("Chem-318", "Bio Chemistry Lab", 1, "Optional"),
                    CourseItem("Chem-316", "Bio Chemistry-I (Carbohydrates)", 2, "Optional")
                )
            ),
            SemesterOutline(
                semesterNumber = 6,
                courses = listOf(
                    CourseItem("Chem-323", "Inorganic Chemistry-II (f-block elements)", 2, "Compulsory"),
                    CourseItem("Chem-327", "Organic Chemistry Lab", 1, "Compulsory"),
                    CourseItem("Chem-325", "Organic Chemistry-I (Reaction Mechanisms-I)", 2, "Compulsory"),
                    CourseItem("Chem-326", "Organic Chemistry-II (Spectroscopy)", 2, "Compulsory"),
                    CourseItem("Chem-321", "Physical Chemistry Lab", 1, "Compulsory"),
                    CourseItem("Chem-319", "Physical Chemistry-I (Chemical Kinetics)", 2, "Compulsory"),
                    CourseItem("Chem-320", "Physical Chemistry-II (Thermodynamics)", 2, "Compulsory"),
                    CourseItem("HQ-006", "Translation of Holy Quran", 1, ""),
                    CourseItem("Chem-322", "Inorganic Chemistry-I (Coordination Chemistry)", 2, "Compulsory"),
                    CourseItem("Chem-324", "Inorganic Chemistry Lab", 1, "Compulsory"),
                    CourseItem("Chem-335", "Bio Chemistry-II (Nutrition)", 2, "Optional"),
                    CourseItem("Chem-330", "Analytical Chemistry Lab", 1, "Optional"),
                    CourseItem("Chem-328", "Analytical Chemistry-I(Separation Techniques)", 2, "Optional"),
                    CourseItem("Chem-329", "Analytical Chemistry-II (Molecular Spectroscopy)", 2, "Optional"),
                    CourseItem("Chem-333", "Applied Chemistry Lab", 1, "Optional"),
                    CourseItem("Chem-331", "Applied Chemistry-I (Water Treatment & Cleansers)", 2, "Optional"),
                    CourseItem("Chem-332", "Applied Chemistry-II (Unit Processes & Chemical-I)", 2, "Optional"),
                    CourseItem("Chem-336", "Bio Chemistry Lab", 1, "Optional"),
                    CourseItem("Chem-334", "Bio Chemistry-I (Proteins)", 2, "Optional")
                )
            ),
            SemesterOutline(
                semesterNumber = 7,
                courses = listOf(
                    CourseItem("Chem-415", "Organic Chemistry-I (Reaction Mechanism-II)", 2, "Elective"),
                    CourseItem("Chem-420", "Organic Chemistry Lab - III", 1, "Elective"),
                    CourseItem("Chem-418", "Organic Chemistry Lab - II", 1, "Elective"),
                    CourseItem("Chem-416", "Organic Chemistry Lab - I", 1, "Elective"),
                    CourseItem("Chem-414", "Inorganic Chemistry-IV (Environmental Aspects)", 2, "Elective"),
                    CourseItem("Chem-412", "Inorganic Chemistry-III (Kinetic & Thermodynamic)", 2, "Elective"),
                    CourseItem("Chem-410", "Inorganic Chemistry-II (Reagents and Solvents)", 2, "Elective"),
                    CourseItem("Chem-408", "Inorganic Chemistry-I (Periodicity)", 2, "Elective"),
                    CourseItem("Chem-413", "Inorganic Chemistry Lab - III", 1, "Elective"),
                    CourseItem("Chem-417", "Organic Chemistry-II (Oxidation & Reduction)", 2, "Elective"),
                    CourseItem("Chem-419", "Organic Chemistry-III (Reaction Mechanism-III)", 2, "Elective"),
                    CourseItem("Chem-421", "Organic Chemistry-IV (NMR Spectroscopy)", 2, "Elective"),
                    CourseItem("HQ-007", "Translation of Holy Quran", 0, ""),
                    CourseItem("Chem-400", "Research / Thesis", 4, "Compulsory"),
                    CourseItem("Chem-407", "Physical Chemistry-IV (Solution Chemistry)", 2, "Elective"),
                    CourseItem("Chem-405", "Physical Chemistry-III (Molecular Spectroscopy)", 2, "Elective"),
                    CourseItem("Chem-403", "Physical Chemistry-II (Surface Chemistry)", 2, "Elective"),
                    CourseItem("Chem-401", "Physical Chemistry-I (Colloids)", 2, "Elective"),
                    CourseItem("Chem-406", "Physical Chemistry Lab – III", 1, "Elective"),
                    CourseItem("Chem-404", "Physical Chemistry Lab – II", 1, "Elective"),
                    CourseItem("Chem-402", "Physical Chemistry Lab – I", 1, "Elective"),
                    CourseItem("Chem-411", "Inorganic Chemistry Lab - II", 1, "Elective"),
                    CourseItem("Chem-423", "Analytical Chemistry Lab – I", 1, "Elective"),
                    CourseItem("Chem-434", "Applied Chemistry Lab – III", 1, "Elective"),
                    CourseItem("Chem-432", "Applied Chemistry Lab – II", 1, "Elective"),
                    CourseItem("Chem-430", "Applied Chemistry Lab – I", 1, "Elective"),
                    CourseItem("Chem-428", "Analytical Chemistry-IV (Environmental Chemistry)", 2, "Elective"),
                    CourseItem("Chem-426", "Analytical Chemistry-III (Advance Chromatography)", 2, "Elective"),
                    CourseItem("Chem-424", "Analytical Chemistry-II (Atomic Spectroscopy)", 2, "Elective"),
                    CourseItem("Chem-422", "Analytical Chemistry-I (Electroanalysis Method-I)", 2, "Elective"),
                    CourseItem("Chem-427", "Analytical Chemistry Lab – III", 1, "Elective"),
                    CourseItem("Chem-425", "Analytical Chemistry Lab – II", 1, "Elective"),
                    CourseItem("Chem-429", "Applied Chemistry-I (Fuel Chemistry)", 2, "Elective"),
                    CourseItem("Chem-431", "Applied Chemistry-II (Steel & Metal Finishing)", 2, "Elective"),
                    CourseItem("Chem-409", "Inorganic Chemistry Lab - I", 1, "Elective"),
                    CourseItem("Chem-442", "Bio Chemistry-IV (Immunochemistry)", 2, "Elective"),
                    CourseItem("Chem-440", "Bio Chemistry-III (Enzymology)", 2, "Elective"),
                    CourseItem("Chem-438", "Bio Chemistry-II (Human Physiology)", 2, "Elective"),
                    CourseItem("Chem-436", "Bio Chemistry-I (Nucleic Acids)", 2, "Elective"),
                    CourseItem("Chem-441", "Bio Chemistry Lab – III", 1, "Elective"),
                    CourseItem("Chem-437", "Bio Chemistry Lab – I", 1, "Elective"),
                    CourseItem("Chem-439", "Bio Chemistry Lab - II", 1, "Elective"),
                    CourseItem("Chem-435", "Applied Chemistry-IV (Processing Industries)", 2, "Elective"),
                    CourseItem("Chem-433", "Applied Chemistry-III (Analytical Techniques)", 2, "Elective")
                )
            ),
            SemesterOutline(
                semesterNumber = 8,
                courses = listOf(
                    CourseItem("Chem-457", "Organic Chemistry-I (Natural Products)", 2, "Elective"),
                    CourseItem("Chem-462", "Organic Chemistry Lab – III", 1, "Elective"),
                    CourseItem("Chem-460", "Organic Chemistry Lab – II", 1, "Elective"),
                    CourseItem("Chem-458", "Organic Chemistry Lab – I", 1, "Elective"),
                    CourseItem("Chem-456", "Inorganic Chemistry-IV (Inorganic Polymers)", 2, "Elective"),
                    CourseItem("Chem-454", "Inorganic Chemistry-III (Organometallic Chemistry)", 2, "Elective"),
                    CourseItem("Chem-452", "Inorganic Chemistry-II (Bio-inorganic Chemistry)", 2, "Elective"),
                    CourseItem("Chem-450", "Inorganic Chemistry-I (Radioactivity)", 2, "Elective"),
                    CourseItem("Chem-453", "Inorganic Chemistry Lab – II", 1, "Elective"),
                    CourseItem("Chem-459", "Organic Chemistry-II (Organic Synthesis)", 2, "Elective"),
                    CourseItem("Chem-461", "Organic Chemistry-III (Heterocyclic Chemistry)", 2, "Elective"),
                    CourseItem("HQ-008", "Translation of Holy Quran", 1, ""),
                    CourseItem("Chem-449", "Physical Chemistry-IV (Nuclear Chemistry)", 2, "Elective"),
                    CourseItem("Chem-447", "Physical Chemistry-III (Photochemistry)", 2, "Elective"),
                    CourseItem("Chem-445", "Physical Chemistry-II (UV & Raman Spectroscopy)", 2, "Elective"),
                    CourseItem("Chem-443", "Physical Chemistry-I (Polymer Chemistry)", 2, "Elective"),
                    CourseItem("Chem-448", "Physical Chemistry Lab – II", 1, "Elective"),
                    CourseItem("Chem-446", "Physical Chemistry Lab – II", 1, "Elective"),
                    CourseItem("Chem-444", "Physical Chemistry Lab – I", 1, "Elective"),
                    CourseItem("Chem-463", "Organic Chemistry-IV (Reaction Mechanism-IV)", 2, "Elective"),
                    CourseItem("Chem-451", "Inorganic Chemistry Lab – I", 1, "Elective"),
                    CourseItem("Chem-455", "Inorganic Chemistry Lab - III", 1, "Elective"),
                    CourseItem("Chem-484", "Bio Chemistry-IV (Biochemical Techniques)", 2, "Elective"),
                    CourseItem("Chem-474", "Applied Chemistry Lab – II", 1, "Elective"),
                    CourseItem("Chem-472", "Applied Chemistry Lab – I", 1, "Elective"),
                    CourseItem("Chem-470", "Analytical Chemistry-IV (Conducto/Oscillometry)", 2, "Elective"),
                    CourseItem("Chem-468", "Analytical Chemistry-III (Thermoanalysis Method)", 2, "Elective"),
                    CourseItem("Chem-466", "Analytical Chemistry-II (Compound Analysis)", 2, "Elective"),
                    CourseItem("Chem-464", "Analytical Chemistry-I (Electroanalysis Method-II)", 2, "Elective"),
                    CourseItem("Chem-469", "Analytical Chemistry Lab – III", 1, "Elective"),
                    CourseItem("Chem-467", "Analytical Chemistry Lab – II", 1, "Elective"),
                    CourseItem("Chem-476", "Applied Chemistry Lab – III", 1, "Elective"),
                    CourseItem("Chem-471", "Applied Chemistry-I (Polymers)", 2, "Elective"),
                    CourseItem("Chem-473", "Applied Chemistry-II (Agro-industries)", 2, "Elective"),
                    CourseItem("Chem-492", "Bio Chemistry-III (Microbiology & Drug Metabolism)", 2, "Elective"),
                    CourseItem("Chem-480", "Bio Chemistry-II (Molecular Biology)", 2, "Elective"),
                    CourseItem("Chem-478", "Bio Chemistry-I (Lipids)", 2, "Elective"),
                    CourseItem("Chem-481", "Bio Chemistry Lab – II", 1, "Elective"),
                    CourseItem("Chem-479", "Bio Chemistry Lab – I", 1, "Elective"),
                    CourseItem("Chem-483", "Bio Chemistry Lab - III", 1, "Elective"),
                    CourseItem("Chem-477", "Applied Chemistry-IV (Environmental Chemistry)", 2, "Elective"),
                    CourseItem("Chem-475", "Applied Chemistry-III (Textile Industries)", 2, "Elective"),
                    CourseItem("Chem-465", "Analytical Chemistry Lab – I", 1, "Elective")
                )
            )
        )
    )

    // 10. BS Zoology
    val bsZoology = ProgramCourseOutline(
        id = "bs_zoology",
        name = "BS Zoology",
        headOfDepartment = "Prof. Qualab Naveed",
        duration = "4 Years (8 Semesters)",
        eligibility = "F.Sc Pre-Medical / Equivalent",
        about = "Zoology",
        isIntermediate = false,
        semesters = listOf(
            SemesterOutline(
                semesterNumber = 1,
                courses = listOf(
                    CourseItem("ZOOL-101", "Animal Diversity-I", 2, "Major"),
                    CourseItem("NZ-117", "Lab. Environmental Biology (NS)", 1, "General"),
                    CourseItem("ZOOL-104", "Lab. Cell Biology", 1, "Major"),
                    CourseItem("ZOOL-102", "Lab. Animal Diversity-I", 1, "Major"),
                    CourseItem("GISL-101/GETH", "Islamic Studies / Ethics (for Non-Muslims)", 2, "General"),
                    CourseItem("GENG-101", "Functional English", 3, "General"),
                    CourseItem("HQ-001N", "Fehm-e-Quran", 0, "Compulsory"),
                    CourseItem("NZ-116", "Environmental Biology (NS)", 2, "General"),
                    CourseItem("GCCE-101", "Civics and Community Engagement", 2, "General"),
                    CourseItem("ZOOL-103", "Cell Biology", 2, "Major")
                )
            ),
            SemesterOutline(
                semesterNumber = 2,
                courses = listOf(
                    CourseItem("GQR-101", "Quantitative Reasoning (I)", 3, "General"),
                    CourseItem("ZOOL-108", "Lab. Biological Techniques", 2, "Major"),
                    CourseItem("ZOOL-106", "Lab. Animal Diversity-II", 1, "Major"),
                    CourseItem("APED-111", "Introduction to Health and Physical Education (AH)", 2, "General"),
                    CourseItem("GICP-101", "Ideology and Constitution of Pakistan", 2, "General"),
                    CourseItem("HQ-002N", "Fehm-e-Quran", 1, "Compulsory"),
                    CourseItem("GENT-101", "Entrepreneurship", 2, "General"),
                    CourseItem("ZOOL-107", "Biological Techniques", 1, "Major"),
                    CourseItem("ZOOL-105", "Animal Diversity-II", 2, "Major")
                )
            ),
            SemesterOutline(
                semesterNumber = 3,
                courses = listOf(
                    CourseItem("HQ-003", "Translation of Holy Quran", 0, ""),
                    CourseItem("GPST-201", "Pakistan Studies", 2, "General"),
                    CourseItem("ZOOL-206", "Lab. Economic Zoology", 1, "Major"),
                    CourseItem("ZOOL-204", "Lab. Animal Form & Function-I", 1, "Major"),
                    CourseItem("ZOOL-202", "Lab. Animal Diversity-III", 1, "Major"),
                    CourseItem("GENG-201", "Expository Writing", 3, "General"),
                    CourseItem("ZOOL-205", "Economic Zoology", 2, "Major"),
                    CourseItem("GICT-201", "Applications of Information and Communication Technologies", 3, "General"),
                    CourseItem("ZOOL-203", "Animal Form & Function-I", 2, "Major"),
                    CourseItem("ZOOL-201", "Animal Diversity-III", 2, "Major")
                )
            ),
            SemesterOutline(
                semesterNumber = 4,
                courses = listOf(
                    CourseItem("ZOOL-207", "Ecology", 2, "Major"),
                    CourseItem("ZOOL-208", "Ecology (Lab)", 1, "Major"),
                    CourseItem("GQR-202", "Quantitative Reasoning (II)", 3, "General"),
                    CourseItem("HQ-004", "Translation of Holy Quran", 1, ""),
                    CourseItem("ZOOL-214", "Biochemistry-I (Lab)", 1, "Major"),
                    CourseItem("ZOOL-213", "Biochemistry-I", 2, "Major"),
                    CourseItem("ZOOL-210", "Animal Form And Function-II (Lab)", 1, "Major"),
                    CourseItem("ZOOL-209", "Animal Form And Function-II", 2, "Major"),
                    CourseItem("ZOOL-212", "Animal Behavior (Lab)", 1, "Major"),
                    CourseItem("ZOOL-211", "Animal Behavior", 2, "Major")
                )
            ),
            SemesterOutline(
                semesterNumber = 5,
                courses = listOf(
                    CourseItem("ZOOL-310", "Lab. Genetics-I", 1, "Major"),
                    CourseItem("ZOOL-308", "Lab. Physiology-I", 1, "Major"),
                    CourseItem("ZOOL-307", "Physiology-I", 2, "Major"),
                    CourseItem("HQ-005", "Translation of Holy Quran", 0, ""),
                    CourseItem("ZOOL-304", "Lab. Environmental Biology", 1, "Interdisciplinary"),
                    CourseItem("ZOOL-302", "Lab. Entomology", 1, "Interdisciplinary"),
                    CourseItem("ZOOL-306", "Lab. Biochemistry-II", 1, "Major"),
                    CourseItem("ZOOL-309", "Genetics-I", 2, "Major"),
                    CourseItem("ZOOL-303", "Environmental Biology", 2, "Interdisciplinary"),
                    CourseItem("ZOOL-301", "Entomology", 2, "Interdisciplinary"),
                    CourseItem("ZOOL-305", "Biochemistry-II", 2, "Major")
                )
            ),
            SemesterOutline(
                semesterNumber = 6,
                courses = listOf(
                    CourseItem("ZOOL-319", "Lab. Wildlife", 1, "Major"),
                    CourseItem("ZOOL-320", "Physiology-II", 2, "Major"),
                    CourseItem("ZOOL-313", "Research Methodology", 3, "Interdisciplinary"),
                    CourseItem("HQ-006", "Translation of Holy Quran", 1, ""),
                    CourseItem("ZOOL-318", "Wildlife", 2, "Major"),
                    CourseItem("ZOOL-321", "Lab. Physiology-II", 1, "Major"),
                    CourseItem("ZOOL-317", "Lab. Genetics-II", 1, "Major"),
                    CourseItem("ZOOL-312", "Lab. General Microbiology", 1, "Interdisciplinary"),
                    CourseItem("ZOOL-315", "Lab. Developmental Biology", 1, "Major"),
                    CourseItem("ZOOL-316", "Genetics-II", 2, "Major"),
                    CourseItem("ZOOL-311", "General Microbiology", 2, "Interdisciplinary"),
                    CourseItem("ZOOL-314", "Developmental Biology", 2, "Major")
                )
            ),
            SemesterOutline(
                semesterNumber = 7,
                courses = listOf(
                    CourseItem("ZOOL-405", "Lab. Principles of Systematics", 1, "Major"),
                    CourseItem("ZOOL-408", "Molecular Biology", 2, "Major"),
                    CourseItem("ZOOL-406", "Principles of Paleontology", 2, "Major"),
                    CourseItem("ZOOL-404", "Principles of Systematics", 2, "Major"),
                    CourseItem("HQ-007", "Translation of Holy Quran", 0, ""),
                    CourseItem("ZOOL-407", "Lab. Principles of Paleontology", 1, "Major"),
                    CourseItem("ZOOL-409", "Lab. Molecular Biology", 1, "Major"),
                    CourseItem("ZOOL-402", "Analysis of Development", 2, "Major"),
                    CourseItem("ZOOL-401", "Fieldwork / Internship", 3, "Major"),
                    CourseItem("ZOOL-410", "Industrial Biotechnology", 2, "Major"),
                    CourseItem("ZOOL-403", "Lab. Analysis of Development", 1, "Major"),
                    CourseItem("ZOOL-411", "Lab. Industrial Biotechnology", 1, "Major")
                )
            ),
            SemesterOutline(
                semesterNumber = 8,
                courses = listOf(
                    CourseItem("HQ-008", "Translation of Holy Quran", 1, ""),
                    CourseItem("ZOOL-420", "Lab. Zoogeography", 1, "Major"),
                    CourseItem("ZOOL-418", "Lab. Evolution", 1, "Major"),
                    CourseItem("ZOOL-414", "Lab. Bioinformatics", 2, "Major"),
                    CourseItem("ZOOL-417", "Evolution", 2, "Major"),
                    CourseItem("ZOOL-412", "Capstone Project", 3, "Major"),
                    CourseItem("ZOOL-413", "Bioinformatics", 1, "Major"),
                    CourseItem("ZOOL-415", "Applied Fisheries", 2, "Major"),
                    CourseItem("ZOOL-419", "Zoogeography", 2, "Major")
                )
            )
        )
    )

    // Intermediate Programs (Page 40)
    val intermediatePrograms = listOf(
        ProgramCourseOutline(
            id = "inter_ics",
            name = "Intermediate in Computer Science (ICS)",
            headOfDepartment = "Muhammad Faiyaz",
            duration = "2 Years",
            eligibility = "Matric with Science / Equivalent",
            isIntermediate = true,
            note = "Course-wise/semester-wise outlines for Intermediate programs are not yet published on the college website."
        ),
        ProgramCourseOutline(
            id = "inter_fsc_eng",
            name = "F.Sc Pre-Engineering",
            headOfDepartment = "Not listed",
            duration = "2 Years",
            eligibility = "Matric with Science / Equivalent",
            isIntermediate = true,
            note = "Course-wise/semester-wise outlines for Intermediate programs are not yet published on the college website."
        ),
        ProgramCourseOutline(
            id = "inter_fsc_med",
            name = "F.Sc Pre-Medical",
            headOfDepartment = "Not listed",
            duration = "2 Years",
            eligibility = "Matric with Science / Equivalent",
            isIntermediate = true,
            note = "Course-wise/semester-wise outlines for Intermediate programs are not yet published on the college website."
        ),
        ProgramCourseOutline(
            id = "inter_fa",
            name = "F.A (Faculty of Arts)",
            headOfDepartment = "Not listed",
            duration = "2 Years",
            eligibility = "Matric / Equivalent",
            isIntermediate = true,
            note = "Course-wise/semester-wise outlines for Intermediate programs are not yet published on the college website."
        ),
        ProgramCourseOutline(
            id = "inter_icom",
            name = "I.Com (Intermediate in Commerce)",
            headOfDepartment = "Not listed",
            duration = "2 Years",
            eligibility = "Matric / Equivalent",
            isIntermediate = true,
            note = "Course-wise/semester-wise outlines for Intermediate programs are not yet published on the college website."
        )
    )

    val bsPrograms: List<ProgramCourseOutline> = listOf(
        bsInformationTechnology,
        bsBusinessAdministration,
        bsEnglish,
        bsIslamicStudies,
        bsPhysics,
        bsMathematics,
        bsPoliticalScience,
        bsUrdu,
        bsChemistry,
        bsZoology
    )

    val allPrograms: List<ProgramCourseOutline> = bsPrograms + intermediatePrograms

    fun getProgramById(id: String): ProgramCourseOutline? =
        allPrograms.firstOrNull { it.id.equals(id, ignoreCase = true) }
}
