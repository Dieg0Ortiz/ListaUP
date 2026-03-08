package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Alert
import com.example.myapplication.domain.repository.AlertRepository

class AlertRepositoryImpl(
    private val attendanceRepository: AttendanceRepositoryImpl
) : AlertRepository {

    override suspend fun getAlertsByThreshold(grupoId: String, threshold: Double): Result<List<Alert>> {
        return try {
            val alerts = listOf(
                Alert(
                    id = "alert_sample_1",
                    studentId = "1001",
                    studentName = "Juan Pérez García",
                    matricula = "221001",
                    materia = "Programación Avanzada",
                    absencePercentage = 22.0,
                    threshold = threshold,
                    grupoId = grupoId
                ),
                Alert(
                    id = "alert_sample_2",
                    studentId = "1002",
                    studentName = "María López Hernández",
                    matricula = "221002",
                    materia = "Bases de Datos",
                    absencePercentage = 25.0,
                    threshold = threshold,
                    grupoId = grupoId
                )
            ).filter { it.absencePercentage >= threshold }
            Result.success(alerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
