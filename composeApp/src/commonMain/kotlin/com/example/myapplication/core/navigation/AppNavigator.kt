package com.example.myapplication.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Login : Screen()

    @Serializable
    data object ProfessorDashboard : Screen()

    @Serializable
    data object StudentDashboard : Screen()

    @Serializable
    data class Attendance(val grupoId: String = "", val materia: String = "") : Screen()

    @Serializable
    data object Alerts : Screen()

    @Serializable
    data object Permissions : Screen()

    @Serializable
    data object Reports : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object Subjects : Screen()

    @Serializable
    data class SubjectDetail(
        val materia: String = "Estructuras de Datos",
        val docente: String = "Dr. Roberto Hernández",
        val percent: Int = 82,
        val aula: String = "Edificio C, Cubículo 402"
    ) : Screen()

    @Serializable
    data object Schedule : Screen()

    @Serializable
    data object Notifications : Screen()

    @Serializable
    data object ProfessorClasses : Screen()

    @Serializable
    data class PermissionDetail(
        val studentName: String = "Julian Rivera",
        val studentId: String = "20230541",
        val career: String = "Ing. de Sistemas",
        val motivo: String = "Permiso Médico",
        val start: String = "24 Oct, 2023",
        val end: String = "26 Oct, 2023",
        val days: String = "3 Días",
        val status: String = "Pendiente de Revisión",
        val canReview: Boolean = true
    ) : Screen()

    @Serializable
    data object DocumentPreview : Screen()

    @Serializable
    data object AttendanceSettings : Screen()
}
