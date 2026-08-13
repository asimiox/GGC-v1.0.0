package com.example.data.model

data class CollegeEvent(
    val id: String,
    val title: String,
    val date: String,
    val time: String? = null,
    val venue: String? = null,
    val category: String,
    val description: String,
    val isUpcoming: Boolean = true,
    val attachmentName: String? = null
)
