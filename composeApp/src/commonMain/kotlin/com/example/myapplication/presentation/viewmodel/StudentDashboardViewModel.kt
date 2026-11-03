package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.userMessage
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.domain.model.Group
import com.example.myapplication.domain.model.Permission
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.NotificationRepository
import com.example.myapplication.domain.repository.PermissionRepository
import com.example.myapplication.domain.usecase.GetGroupsUseCase
import com.example.myapplication.domain.usecase.GetScheduleUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentDashboardUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val groups: List<Group> = emptyList(),
    val schedule: List<ClassSession> = emptyList(),
    val permissions: List<Permission> = emptyList(),
    val unreadNotifications: Int = 0,
    val error: String? = null
)

class StudentDashboardViewModel(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val permissionRepository: PermissionRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        val user = sessionManager.getUser() ?: return
        _uiState.value = _uiState.value.copy(user = user, isLoading = true, error = null)

        viewModelScope.launch {
            val groups = async { getGroupsUseCase.invokeForStudent(user.matricula) }
            val schedule = async { getScheduleUseCase(user.matricula) }
            // Permisos y avisos son secundarios: si fallan, el tablero se muestra igual
            val permissions = async { permissionRepository.myRequests() }
            val notifications = async { notificationRepository.list() }

            val groupsResult = groups.await()
            val scheduleResult = schedule.await()
            val failure = scheduleResult.exceptionOrNull() ?: groupsResult.exceptionOrNull()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                groups = groupsResult.getOrDefault(_uiState.value.groups),
                schedule = scheduleResult.getOrDefault(_uiState.value.schedule),
                permissions = permissions.await().getOrDefault(_uiState.value.permissions),
                unreadNotifications = notifications.await().getOrNull()?.count { !it.isRead }
                    ?: _uiState.value.unreadNotifications,
                error = failure?.userMessage()
            )
        }
    }
}
