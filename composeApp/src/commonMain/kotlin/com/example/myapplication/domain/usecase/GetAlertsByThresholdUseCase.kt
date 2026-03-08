package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Alert
import com.example.myapplication.domain.repository.AlertRepository

class GetAlertsByThresholdUseCase(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(grupoId: String, threshold: Double = 20.0): Result<List<Alert>> {
        return alertRepository.getAlertsByThreshold(grupoId, threshold)
    }
}
