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
}
