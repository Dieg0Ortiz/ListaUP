package com.example.myapplication.domain.model

import kotlinx.datetime.LocalDate

enum class PermissionStatus(val label: String) {
    PENDING_TUTOR("Revisión del tutor"),
    PENDING_DIRECTOR("En Dirección"),
    APPROVED("Aprobado"),
    REJECTED("Rechazado"),
    UNKNOWN("Sin estado");

    val isFinal: Boolean get() = this == APPROVED || this == REJECTED

    companion object {
        fun from(value: String): PermissionStatus =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
    }
}

data class Permission(
    val id: Int,
    val studentId: String,
    val studentName: String,
    val matricula: String,
    /** true = días completos; false = solo un rango de horas. */
    val fullDays: Boolean,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val startTime: String?,
    val endTime: String?,
    val reason: String,
    val status: PermissionStatus,
    val tutorComment: String?,
    val directorComment: String?,
    val rejectionReason: String?,
    val proofFilename: String?,
    val proofUrl: String?,
    val createdAt: String,
    val disclaimer: String
) {
    /** Días naturales que cubre el permiso, contando inicio y fin. */
    val days: Int
        get() {
            val start = startDate ?: return 0
            val end = endDate ?: return 0
            return (end.toEpochDays() - start.toEpochDays()).toInt() + 1
        }
}

data class AppNotification(
    val id: Int,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String,
    val permissionId: Int?
)

/** Resumen de asistencia de un grupo a partir de las sesiones registradas. */
data class GroupAttendanceStats(
    val grupoId: String,
    val materia: String,
    val sessions: Int,
    val attendanceRate: Double?
)

/** Asistencia promedio de cada día de la semana actual (null = sin listas ese día). */
data class WeeklyAttendance(
    val average: Double?,
    val previousWeekAverage: Double?,
    val byDay: List<Double?>
)
