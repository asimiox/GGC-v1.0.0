package com.example.data.model

data class FacultyMember(
    val id: Int,
    val name: String,
    val designation: String,
    val qualification: String,
    val department: String
) {
    val isPrincipal: Boolean
        get() = designation.equals("Principal", ignoreCase = true) || designation.startsWith("Principal", ignoreCase = true)

    val isVicePrincipal: Boolean
        get() = designation.contains("Vice Principal", ignoreCase = true)

    val isHod: Boolean
        get() = designation.contains("HOD", ignoreCase = true)

    val isLeadership: Boolean
        get() = isPrincipal || isVicePrincipal || isHod
}
