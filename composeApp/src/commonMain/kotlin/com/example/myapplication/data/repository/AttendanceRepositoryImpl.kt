package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.StudentAttendance
import com.example.myapplication.domain.repository.AttendanceRepository

class AttendanceRepositoryImpl : AttendanceRepository {

    private val attendanceRecords = mutableListOf<Attendance>()

    override suspend fun submitAttendance(
        grupoId: String,
        attendances: List<StudentAttendance>
    ): Result<Boolean> {
        return try {
            val currentDate = "2026-03-08"
            attendances.forEach { student ->
                attendanceRecords.add(
                    Attendance(
                        id = "${student.studentId}_${grupoId}_$currentDate",
                        studentId = student.studentId,
                        studentName = student.studentName,
                        grupoId = grupoId,
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
