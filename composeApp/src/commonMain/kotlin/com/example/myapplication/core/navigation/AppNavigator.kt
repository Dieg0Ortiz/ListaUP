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
    data object PermissionRequest : Screen()

    @Serializable
    data class PermissionDetail(val permissionId: Int) : Screen()

    @Serializable
    data object Reports : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object Subjects : Screen()

    @Serializable
    data class SubjectDetail(
        val grupoId: String,
        val materia: String,
        val docente: String = "",
        val aula: String = "",
        val horario: String = "",
        val grupo: String = ""
    ) : Screen()

    @Serializable
    data object Schedule : Screen()

    @Serializable
    data object Notifications : Screen()

    @Serializable
    data object ProfessorClasses : Screen()

    @Serializable
    data object AttendanceSettings : Screen()
}
