package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Alert

interface AlertRepository {
    suspend fun getAlertsByThreshold(grupoId: String, threshold: Double = 20.0): Result<List<Alert>>
}
