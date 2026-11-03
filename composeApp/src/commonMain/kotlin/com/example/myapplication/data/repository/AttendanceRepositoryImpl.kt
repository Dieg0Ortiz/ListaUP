package com.example.myapplication.data.repository

import com.example.myapplication.core.AppConfig
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.dto.AttendanceRecordDto
import com.example.myapplication.data.remote.dto.AttendanceSessionDto
import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.AttendanceStatus
import com.example.myapplication.domain.model.StudentAttendance
import com.example.myapplication.domain.repository.AttendanceRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AttendanceRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : AttendanceRepository {

    private val attendanceRecords = mutableListOf<Attendance>()

    @OptIn(ExperimentalTime::class)
    override suspend fun submitAttendance(
        grupoId: String,
        materia: String,
        attendances: List<StudentAttendance>
    ): Result<Boolean> {
        return try {
            val docenteId = sessionManager.getUser()?.docenteId
                ?.takeIf { it.isNotBlank() }
                ?: return Result.failure(Exception("Inicia sesión como docente para enviar la lista."))
            val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

            apiService.registerAttendance(
                AttendanceSessionDto(
                    grupoId = grupoId,
                    materia = materia,
                    docenteId = docenteId,
                    periodoId = AppConfig.PERIODO_ID,
                    sessionDate = currentDate,
                    records = attendances.map { student ->
                        AttendanceRecordDto(
                            alumnoId = student.studentId,
                            matricula = student.matricula,
                            // La API solo acepta presente|retardo|ausente; el permiso viaja aparte
                            status = when (student.status) {
                                AttendanceStatus.PRESENTE -> "presente"
                                AttendanceStatus.RETARDO -> "retardo"
                                AttendanceStatus.AUSENTE, AttendanceStatus.PERMISO -> "ausente"
                            },
                            hasPermission = student.hasPermission || student.status == AttendanceStatus.PERMISO
                        )
                    }
                )
            )

            attendances.forEach { student ->
                attendanceRecords.add(
                    Attendance(
                        id = "${student.studentId}_${grupoId}_$currentDate",
                        studentId = student.studentId,
                        studentName = student.studentName,
                        grupoId = grupoId,
                        materia = materia,
                        fecha = currentDate,
                        status = student.status
                    )
                )
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAttendanceHistory(
        studentId: String,
        grupoId: String
    ): Result<List<Attendance>> {
        return try {
            val filtered = attendanceRecords.filter {
                it.studentId == studentId && it.grupoId == grupoId
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAttendanceSummary(studentId: String): Result<Map<String, Double>> {
        return try {
            val studentRecords = attendanceRecords.filter { it.studentId == studentId }
            val total = studentRecords.size.toDouble()
            if (total == 0.0) {
                return Result.success(mapOf("presente" to 0.0, "retardo" to 0.0, "ausente" to 0.0))
            }
            val presentes = studentRecords.count {
                it.status == com.example.myapplication.domain.model.AttendanceStatus.PRESENTE
            }
            val retardos = studentRecords.count {
                it.status == com.example.myapplication.domain.model.AttendanceStatus.RETARDO
            }
            val ausentes = studentRecords.count {
                it.status == com.example.myapplication.domain.model.AttendanceStatus.AUSENTE
            }
            Result.success(
                mapOf(
                    "presente" to (presentes / total) * 100,
                    "retardo" to (retardos / total) * 100,
                    "ausente" to (ausentes / total) * 100
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
