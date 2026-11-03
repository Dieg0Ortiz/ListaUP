package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.ProofFile
import com.example.myapplication.domain.model.AppNotification
import com.example.myapplication.domain.model.GroupAttendanceStats
import com.example.myapplication.domain.model.Permission
import com.example.myapplication.domain.model.WeeklyAttendance
import kotlinx.datetime.LocalDate

interface PermissionRepository {
    /** Solicitudes del alumno en sesión. */
    suspend fun myRequests(): Result<List<Permission>>

    /** Solicitudes dirigidas al docente en sesión como tutor. */
    suspend fun tutorRequests(): Result<List<Permission>>

    suspend fun get(id: Int): Result<Permission>

    /** [startTime]/[endTime] en "HH:mm"; ambos null = días completos. */
    suspend fun request(
        startDate: LocalDate,
        endDate: LocalDate,
        startTime: String?,
        endTime: String?,
        reason: String,
        proof: ProofFile?
    ): Result<Permission>

    suspend fun reviewAsTutor(id: Int, approved: Boolean, comment: String?): Result<Permission>
}

interface NotificationRepository {
    suspend fun list(): Result<List<AppNotification>>
    suspend fun markAllRead(): Result<Unit>
}

interface StatsRepository {
    /** Asistencia de la semana actual en las listas que tomó el docente en sesión. */
    suspend fun professorWeek(): Result<WeeklyAttendance>

    /** Asistencia acumulada del periodo por cada grupo del docente en sesión. */
    suspend fun professorGroups(): Result<List<GroupAttendanceStats>>
}
