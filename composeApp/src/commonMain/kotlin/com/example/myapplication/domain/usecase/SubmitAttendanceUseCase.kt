package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.StudentAttendance
import com.example.myapplication.domain.repository.AttendanceRepository

class SubmitAttendanceUseCase(
    private val attendanceRepository: AttendanceRepository
) {
    suspend operator fun invoke(grupoId: String, materia: String, attendances: List<StudentAttendance>): Result<Boolean> {
        if (attendances.isEmpty()) {
            return Result.failure(Exception("No hay estudiantes para registrar asistencia."))
        }
        return attendanceRepository.submitAttendance(grupoId, materia, attendances)
    }
}
