package com.example.data.model

data class AlumniMember(
    val id: Int,
    val name: String,
    val position: String,
    val organization: String,
    val education: String,
    val testimonial: String? = null
)
