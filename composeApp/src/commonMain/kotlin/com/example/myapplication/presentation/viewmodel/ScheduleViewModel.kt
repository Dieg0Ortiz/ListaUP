package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.userMessage
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.model.toSessions
import com.example.myapplication.domain.usecase.GetGroupsUseCase
import com.example.myapplication.domain.usecase.GetScheduleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScheduleUiState(
    val isLoading: Boolean = true,
    val sessions: List<ClassSession> = emptyList(),
    val userName: String = "",
    val error: String? = null
)

class ScheduleViewModel(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        val user = sessionManager.getUser() ?: return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, userName = user.name)
        viewModelScope.launch {
            // El alumno tiene horario propio en el SIU; el docente, el de cada uno de sus grupos
            val result = if (user.role == UserRole.PROFESSOR) {
                getGroupsUseCase.invokeForProfessor(user.docenteId).map { groups -> groups.flatMap { it.toSessions() } }
            } else {
                getScheduleUseCase(user.matricula)
            }
            _uiState.value = result.fold(
                onSuccess = { _uiState.value.copy(isLoading = false, sessions = it) },
                onFailure = { _uiState.value.copy(isLoading = false, error = it.userMessage()) }
            )
        }
    }
}
