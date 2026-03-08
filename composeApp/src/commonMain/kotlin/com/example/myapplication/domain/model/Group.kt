package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val grupoId: String = "",
    val nombre: String = "",
    val materia: String = "",
    val aula: String = "",
    val horario: String = "",
    val periodoId: String = "62",
    val docenteNombre: String = ""
)
