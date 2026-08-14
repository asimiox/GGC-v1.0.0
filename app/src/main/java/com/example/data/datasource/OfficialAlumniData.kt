package com.example.data.datasource

import com.example.data.model.AlumniMember

object OfficialAlumniData {
    val alumniList: List<AlumniMember> = listOf(
        AlumniMember(
            id = 1,
            name = "Muhammad Hasnain",
            position = "Lecturer (BPS-17)",
            organization = "Higher Education Department",
            education = "F.A (Faculty of Arts) (2012)",
            testimonial = null
        ),
        AlumniMember(
            id = 2,
            name = "Muhammad Umair",
            position = "Sales Executive",
            organization = "Green Solar Solutions",
            education = "BS Physics (2024)",
            testimonial = "I had amazing experience at Government Graduate College. At College, I had Studied core areas of Physics including Mechanics, Electromagnetism, Optics, Thermodynamics, and Modern Physics. Gained practical experience through laboratory apparatuses, Enjoying with friends in Bs and Intermediate Classes. Developed strong analytical, mathematical, and problem-solving skill"
        ),
        AlumniMember(
            id = 3,
            name = "Mujahid Husnain",
            position = "Software & Agentic AI Engineer",
            organization = "Systems Limited",
            education = "BS Information Technology (2025)",
            testimonial = "My journey at GGC has been one of the most valuable experiences of my life. I gained not only strong academic knowledge but also essential management and leadership skills. I am especially grateful to our HOD, Sir Faiyaz, and Prof. Sir Ubaid, whose kindness, guidance, and mentorship played a vital role in shaping my professional path and motivated me to join Pakistan's largest IT exporter. I am truly thankful to GGC for its supportive faculty, positive learning environment, and opportunities for growth."
        )
    )

    fun getAllAlumni(): List<AlumniMember> = alumniList
}
