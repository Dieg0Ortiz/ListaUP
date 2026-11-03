package com.example.myapplication.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TargetStudentDto(
    @SerialName("id") val id: Int = 0,
    @SerialName("student_id") val studentId: String = "",
    @SerialName("nombre") val nombre: String? = null,
    @SerialName("matricula") val matricula: String? = null
)

@Serializable
data class PermissionDto(
    @SerialName("id") val id: Int,
    @SerialName("permission_scope") val scope: String = "DATE_RANGE",
    @SerialName("start_date") val startDate: String = "",
    @SerialName("end_date") val endDate: String = "",
    @SerialName("start_time") val startTime: String? = null,
    @SerialName("end_time") val endTime: String? = null,
    @SerialName("reason") val reason: String = "",
    @SerialName("created_by_student_id") val createdByStudentId: String? = null,
    @SerialName("proof_filename") val proofFilename: String? = null,
    @SerialName("status") val status: String = "",
    @SerialName("tutor_id") val tutorId: String? = null,
    @SerialName("tutor_comment") val tutorComment: String? = null,
    @SerialName("director_comment") val directorComment: String? = null,
    @SerialName("rejection_reason") val rejectionReason: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("disclaimer_legend") val disclaimerLegend: String = "",
    @SerialName("target_students") val targetStudents: List<TargetStudentDto> = emptyList()
)

@Serializable
data class ReviewRequestDto(
    @SerialName("approved") val approved: Boolean,
    @SerialName("comment") val comment: String? = null,
    @SerialName("rejection_reason") val rejectionReason: String? = null
)

@Serializable
data class NotificationDto(
    @SerialName("id") val id: Int,
    @SerialName("permission_id") val permissionId: Int? = null,
    @SerialName("title") val title: String = "",
    @SerialName("message") val message: String = "",
    @SerialName("notification_type") val type: String = "",
    @SerialName("is_read") val isRead: Int = 0,
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class TutoradoDto(
    @SerialName("alumno_id") val alumnoId: String = "",
    @SerialName("matricula") val matricula: String = "",
    @SerialName("nombre") val nombre: String = ""
)

@Serializable
data class TutoradosResponseDto(
    @SerialName("tutorados") val tutorados: List<TutoradoDto> = emptyList()
)

/** Una fila de la vista v_attendance_session_summary. */
data class SessionSummaryDto(
    val sessionId: String,
    val grupoId: String,
    val materia: String,
    val docenteId: String,
    val sessionDate: String,
    val isCancelled: Boolean,
    val totalAlumnos: Int,
    val presentes: Int,
    val retardos: Int,
    val ausentes: Int,
    val justificados: Int,
    val inactivos: Int
)
