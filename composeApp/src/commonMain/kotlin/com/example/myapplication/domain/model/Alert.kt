package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Alert(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val matricula: String = "",
    val materia: String = "",
    val absencePercentage: Double = 0.0,
    val threshold: Double = 20.0,
    val grupoId: String = "",
    val absences: Int = 0,
    val lates: Int = 0,
    /** "ALTO", "MEDIO"… tal como lo manda la API. */
    val risk: String = ""
)
