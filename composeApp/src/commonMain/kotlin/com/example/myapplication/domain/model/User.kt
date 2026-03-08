package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

enum class UserRole {
    STUDENT,
    PROFESSOR
}

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val matricula: String = "",
    val role: UserRole = UserRole.STUDENT,
    val photoUrl: String = "",
    val docenteId: String = ""
)
