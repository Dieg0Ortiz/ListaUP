package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.domain.model.Group
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.usecase.GetGroupsUseCase
import com.example.myapplication.domain.usecase.GetScheduleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentDashboardUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val groups: List<Group> = emptyList(),
    val schedule: List<ClassSession> = emptyList(),
    val attendanceHistory: List<Attendance> = emptyList(),
    val attendancePercentage: Double = 0.0,
    val error: String? = null
)

class StudentDashboardViewModel(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        val user = sessionManager.getUser() ?: return
        _uiState.value = _uiState.value.copy(user = user, isLoading = true)

        viewModelScope.launch {
            val groupsResult = getGroupsUseCase.invokeForStudent(user.matricula)
            groupsResult.fold(
                onSuccess = { groups ->
                    _uiState.value = _uiState.value.copy(groups = groups)
                },
                onFailure = {}
            )

            val scheduleResult = getScheduleUseCase(user.matricula)
            scheduleResult.fold(
                onSuccess = { schedule ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        schedule = schedule,
                        attendancePercentage = 92.0
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
