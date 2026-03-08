package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.repository.AttendanceRepository

class GetAttendanceHistoryUseCase(
    private val attendanceRepository: AttendanceRepository
) {
    suspend operator fun invoke(studentId: String, grupoId: String): Result<List<Attendance>> {
        return attendanceRepository.getAttendanceHistory(studentId, grupoId)
    }
}
