package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.domain.model.Group
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.usecase.GetGroupsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfessorDashboardUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val groups: List<Group> = emptyList(),
    val todayClasses: List<ClassSession> = emptyList(),
    val weeklyAttendance: Double = 0.0,
    val error: String? = null
)

class ProfessorDashboardViewModel(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfessorDashboardUiState())
    val uiState: StateFlow<ProfessorDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        val user = sessionManager.getUser() ?: return
        _uiState.value = _uiState.value.copy(user = user, isLoading = true)

        viewModelScope.launch {
            val result = getGroupsUseCase.invokeForProfessor(user.docenteId)
            result.fold(
                onSuccess = { groups ->
                    val todayClasses = groups.map { group ->
                        ClassSession(
                            id = group.grupoId,
                            materia = group.materia,
                            aula = group.aula,
                            grupo = group.nombre,
                            grupoId = group.grupoId,
                            horaInicio = group.horario.substringBefore("-").trim(),
                            horaFin = group.horario.substringAfter("-").trim(),
                            dia = "Hoy"
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        groups = groups,
                        todayClasses = todayClasses,
                        weeklyAttendance = 85.5
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }
}
