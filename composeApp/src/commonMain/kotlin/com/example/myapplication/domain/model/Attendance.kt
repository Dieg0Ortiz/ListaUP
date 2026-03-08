package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

enum class AttendanceStatus {
    PRESENTE,
    RETARDO,
    AUSENTE,
    PERMISO
}

@Serializable
data class Attendance(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val grupoId: String = "",
    val materia: String = "",
    val fecha: String = "",
    val status: AttendanceStatus = AttendanceStatus.AUSENTE
)
