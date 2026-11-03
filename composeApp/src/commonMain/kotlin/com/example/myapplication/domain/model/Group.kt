package com.example.myapplication.domain.model

import com.example.myapplication.core.AppConfig
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val grupoId: String = "",
    val nombre: String = "",
    val materia: String = "",
    val aula: String = "",
    val horario: String = "",
    val periodoId: String = AppConfig.PERIODO_ID,
    val docenteNombre: String = ""
)
