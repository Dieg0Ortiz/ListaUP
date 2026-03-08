package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StudentAttendance(
    val studentId: String = "",
    val studentName: String = "",
    val matricula: String = "",
    val grupoId: String = "",
    val status: AttendanceStatus = AttendanceStatus.AUSENTE,
    val hasPermission: Boolean = false
)
