package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ClassSession(
    val id: String = "",
    val materia: String = "",
    val aula: String = "",
    val grupo: String = "",
    val grupoId: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val dia: String = "",
    val docenteNombre: String = ""
)
