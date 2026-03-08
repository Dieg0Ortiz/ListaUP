package com.example.myapplication.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    @SerialName("status") val status: String = "",
    @SerialName("mensaje") val mensaje: String = "",
    @SerialName("nombre") val nombre: String = "",
    @SerialName("matricula") val matricula: String = "",
    @SerialName("correo") val correo: String = "",
    @SerialName("id") val id: String = "",
    @SerialName("DocenteID") val docenteId: String = ""
)

@Serializable
data class GroupDto(
    @SerialName("GrupoID") val grupoId: String = "",
    @SerialName("Nombre") val nombre: String = "",
    @SerialName("Materia") val materia: String = "",
    @SerialName("Aula") val aula: String = "",
    @SerialName("Horario") val horario: String = "",
    @SerialName("PeriodoID") val periodoId: String = "",
    @SerialName("DocenteNombre") val docenteNombre: String = ""
)

@Serializable
data class StudentDto(
    @SerialName("AlumnoID") val alumnoId: String = "",
    @SerialName("Nombre") val nombre: String = "",
    @SerialName("Matricula") val matricula: String = "",
    @SerialName("GrupoID") val grupoId: String = ""
)

@Serializable
data class ScheduleDto(
    @SerialName("HorarioID") val horarioId: String = "",
    @SerialName("Materia") val materia: String = "",
    @SerialName("Aula") val aula: String = "",
    @SerialName("Grupo") val grupo: String = "",
    @SerialName("GrupoID") val grupoId: String = "",
    @SerialName("HoraInicio") val horaInicio: String = "",
    @SerialName("HoraFin") val horaFin: String = "",
    @SerialName("Dia") val dia: String = "",
    @SerialName("DocenteNombre") val docenteNombre: String = ""
)

@Serializable
data class ApiListResponse<T>(
    @SerialName("status") val status: String = "",
    @SerialName("data") val data: List<T> = emptyList()
)
