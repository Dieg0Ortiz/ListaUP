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

@Serializable
data class AttendanceRecordDto(
    @SerialName("alumno_id") val alumnoId: String,
    @SerialName("matricula") val matricula: String,
    @SerialName("enrollment_status_id") val enrollmentStatusId: String = "inscrito",
    @SerialName("status") val status: String,
    @SerialName("has_permission") val hasPermission: Boolean = false
)

@Serializable
data class AttendanceSessionDto(
    @SerialName("grupo_id") val grupoId: String,
    @SerialName("materia") val materia: String,
    @SerialName("docente_id") val docenteId: String,
    @SerialName("periodo_id") val periodoId: String,
    @SerialName("session_date") val sessionDate: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("records") val records: List<AttendanceRecordDto> = emptyList()
)

@Serializable
data class AttendanceSessionResponseDto(
    @SerialName("status") val status: String = "",
    @SerialName("message") val message: String = "",
    @SerialName("session_id") val sessionId: String = "",
    @SerialName("total_records") val totalRecords: Int = 0
)

@Serializable
data class AlertStudentDto(
    @SerialName("alumno_id") val alumnoId: String = "",
    @SerialName("matricula") val matricula: String = "",
    @SerialName("nombre") val nombre: String = "",
    @SerialName("total_ausencias") val totalAusencias: Int = 0,
    @SerialName("total_retardos") val totalRetardos: Int = 0,
    @SerialName("porcentaje_asistencia") val porcentajeAsistencia: Double = 0.0,
    @SerialName("estatus_riesgo") val estatusRiesgo: String = ""
)

@Serializable
data class AlertsResponseDto(
    @SerialName("grupo_id") val grupoId: String = "",
    @SerialName("umbral_faltas") val umbralFaltas: Int = 0,
    @SerialName("alumnos_en_riesgo") val alumnosEnRiesgo: List<AlertStudentDto> = emptyList()
)
