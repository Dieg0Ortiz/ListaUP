package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.userMessage
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.domain.model.Alert
import com.example.myapplication.domain.model.GroupAttendanceStats
import com.example.myapplication.domain.model.Permission
import com.example.myapplication.domain.model.PermissionStatus
import com.example.myapplication.domain.repository.PermissionRepository
import com.example.myapplication.domain.repository.StatsRepository
import com.example.myapplication.domain.usecase.GetAlertsByThresholdUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AlertsUiState(
    val isLoading: Boolean = false,
    val alerts: List<Alert> = emptyList(),
    val pendingRequests: List<Permission> = emptyList(),
    val umbralFaltas: Int = 0,
    val error: String? = null,
    val requestsError: String? = null
)

class AlertsViewModel(
    private val getAlertsByThresholdUseCase: GetAlertsByThresholdUseCase,
    private val permissionRepository: PermissionRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    fun loadAlerts(grupoId: String = "") {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, requestsError = null)
        viewModelScope.launch {
            val alerts = async { getAlertsByThresholdUseCase(grupoId) }
            val requests = async { permissionRepository.tutorRequests() }
            val alertsResult = alerts.await()
            val requestsResult = requests.await()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                alerts = alertsResult.getOrDefault(_uiState.value.alerts),
                pendingRequests = requestsResult.getOrNull()
                    ?.filter { it.status == PermissionStatus.PENDING_TUTOR }
                    ?: _uiState.value.pendingRequests,
                umbralFaltas = sessionManager.umbralFaltas,
                error = alertsResult.exceptionOrNull()?.userMessage(),
                requestsError = requestsResult.exceptionOrNull()?.userMessage()
            )
        }
    }
}

data class ReportsUiState(
    val isLoading: Boolean = true,
    val groups: List<GroupAttendanceStats> = emptyList(),
    val atRisk: List<Alert> = emptyList(),
    val error: String? = null
) {
    /** Promedio ponderado por sesiones de los grupos con datos. */
    val overallRate: Double?
        get() {
            val withData = groups.filter { it.attendanceRate != null && it.sessions > 0 }
            val sessions = withData.sumOf { it.sessions }
            if (sessions == 0) return null
            return withData.sumOf { it.attendanceRate!! * it.sessions } / sessions
        }
}

class ReportsViewModel(
    private val statsRepository: StatsRepository,
    private val getAlertsByThresholdUseCase: GetAlertsByThresholdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val groups = async { statsRepository.professorGroups() }
            val alerts = async { getAlertsByThresholdUseCase("") }
            val groupsResult = groups.await()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                groups = groupsResult.getOrDefault(_uiState.value.groups),
                atRisk = alerts.await().getOrDefault(_uiState.value.atRisk),
                error = groupsResult.exceptionOrNull()?.userMessage()
            )
        }
    }
}

class SettingsViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _umbral = MutableStateFlow(sessionManager.umbralFaltas)
    val umbral: StateFlow<Int> = _umbral.asStateFlow()

    fun save(value: Int) {
        val clamped = value.coerceIn(MIN_UMBRAL, MAX_UMBRAL)
        sessionManager.umbralFaltas = clamped
        _umbral.value = clamped
    }

    companion object {
        const val MIN_UMBRAL = 1
        const val MAX_UMBRAL = 10
    }
}
