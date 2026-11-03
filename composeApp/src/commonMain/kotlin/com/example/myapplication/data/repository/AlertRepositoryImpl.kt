package com.example.myapplication.data.repository

import com.example.myapplication.core.AppConfig
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.domain.model.Alert
import com.example.myapplication.domain.repository.AlertRepository

class AlertRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : AlertRepository {

    override suspend fun getAlertsByThreshold(grupoId: String, threshold: Double): Result<List<Alert>> {
        return try {
            // Sin grupo específico se consultan todos los grupos del docente en sesión
            val groups = if (grupoId.isNotBlank()) {
                listOf(grupoId to "")
            } else {
                val docenteId = sessionManager.getUser()?.docenteId.orEmpty()
                if (docenteId.isBlank()) return Result.success(emptyList())
                apiService.getProfessorGroups(AppConfig.PERIODO_ID, docenteId).map { it.grupoId to it.materia }
            }

            val alerts = groups.flatMap { (id, materia) ->
                apiService.getAlerts(id, UMBRAL_FALTAS).alumnosEnRiesgo.map { dto ->
                    Alert(
                        id = "${id}_${dto.alumnoId}",
                        studentId = dto.alumnoId,
                        studentName = dto.nombre,
                        matricula = dto.matricula,
                        materia = materia,
                        absencePercentage = 100.0 - dto.porcentajeAsistencia,
                        threshold = threshold,
                        grupoId = id
                    )
                }
            }.filter { it.absencePercentage >= threshold }
            Result.success(alerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private companion object {
        const val UMBRAL_FALTAS = 3
    }
}
