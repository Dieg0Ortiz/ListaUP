package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.StudentAttendance

interface AttendanceRepository {
    suspend fun submitAttendance(grupoId: String, materia: String, attendances: List<StudentAttendance>): Result<Boolean>
    suspend fun getAttendanceHistory(studentId: String, grupoId: String): Result<List<Attendance>>
    suspend fun getAttendanceSummary(studentId: String): Result<Map<String, Double>>
}
