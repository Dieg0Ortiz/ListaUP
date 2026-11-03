package com.example.myapplication.data.repository

import com.example.myapplication.core.AppConfig
import com.example.myapplication.core.UserFacingException
import com.example.myapplication.core.parseIsoDate
import com.example.myapplication.core.plusDays
import com.example.myapplication.core.today
import com.example.myapplication.core.weekStart
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.dto.SessionSummaryDto
import com.example.myapplication.domain.model.GroupAttendanceStats
import com.example.myapplication.domain.model.WeeklyAttendance
import com.example.myapplication.domain.repository.StatsRepository

class StatsRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : StatsRepository {

    override suspend fun professorWeek(): Result<WeeklyAttendance> = runCatching {
        val sessions = mySessions()
        val monday = weekStart(today())
        val previousMonday = monday.plusDays(-7)
        val thisWeek = sessions.filter { s -> parseIsoDate(s.sessionDate)?.let { it >= monday } == true }
        val lastWeek = sessions.filter { s ->
            parseIsoDate(s.sessionDate)?.let { it >= previousMonday && it < monday } == true
        }
        WeeklyAttendance(
            average = thisWeek.rate(),
            previousWeekAverage = lastWeek.rate(),
            byDay = (0..6).map { offset ->
                val day = monday.plusDays(offset).toString()
                thisWeek.filter { it.sessionDate == day }.rate()
            }
        )
    }

    override suspend fun professorGroups(): Result<List<GroupAttendanceStats>> = runCatching {
        mySessions().groupBy { it.grupoId }.map { (grupoId, rows) ->
            GroupAttendanceStats(
                grupoId = grupoId,
                materia = rows.first().materia,
                sessions = rows.size,
                attendanceRate = rows.rate()
            )
        }.sortedBy { it.attendanceRate ?: 101.0 }
    }

    private suspend fun mySessions(): List<SessionSummaryDto> {
        val docenteId = sessionManager.getUser()?.docenteId?.takeIf { it.isNotBlank() }
            ?: throw UserFacingException("Tu sesión terminó. Vuelve a iniciar sesión.")
        return apiService.getSessionSummary(AppConfig.PERIODO_ID)
            .filter { it.docenteId == docenteId && !it.isCancelled }
    }

    /** Porcentaje que asistió (presente, retardo o con permiso) sobre los alumnos activos. */
    private fun List<SessionSummaryDto>.rate(): Double? {
        val attended = sumOf { it.presentes + it.retardos + it.justificados }
        val expected = sumOf { (it.totalAlumnos - it.inactivos).coerceAtLeast(0) }
        return if (expected == 0) null else attended * 100.0 / expected
    }
}
