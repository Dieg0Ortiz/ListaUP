package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.userMessage
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.domain.model.Group
import com.example.myapplication.domain.model.PermissionStatus
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.WeeklyAttendance
import com.example.myapplication.domain.model.toSessions
import com.example.myapplication.domain.repository.NotificationRepository
import com.example.myapplication.domain.repository.PermissionRepository
import com.example.myapplication.domain.repository.StatsRepository
import com.example.myapplication.domain.usecase.GetGroupsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfessorDashboardUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val groups: List<Group> = emptyList(),
    /** Un bloque por día y hora de cada grupo, según el horario del SIU. */
    val sessions: List<ClassSession> = emptyList(),
    val week: WeeklyAttendance? = null,
    val pendingPermissions: Int = 0,
    val unreadNotifications: Int = 0,
    val error: String? = null
)

class ProfessorDashboardViewModel(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val statsRepository: StatsRepository,
    private val permissionRepository: PermissionRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfessorDashboardUiState())
    val uiState: StateFlow<ProfessorDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        val user = sessionManager.getUser() ?: return
        _uiState.value = _uiState.value.copy(user = user, isLoading = true, error = null)

        viewModelScope.launch {
            val groups = async { getGroupsUseCase.invokeForProfessor(user.docenteId) }
            // Estadísticas, permisos y avisos son secundarios: si fallan, el tablero se muestra igual
            val week = async { statsRepository.professorWeek() }
            val permissions = async { permissionRepository.tutorRequests() }
            val notifications = async { notificationRepository.list() }

            val groupsResult = groups.await()
            val current = _uiState.value
            val loadedGroups = groupsResult.getOrNull()
            _uiState.value = current.copy(
                isLoading = false,
                groups = loadedGroups ?: current.groups,
                sessions = loadedGroups?.flatMap { it.toSessions() } ?: current.sessions,
                week = week.await().getOrNull() ?: current.week,
                pendingPermissions = permissions.await().getOrNull()
                    ?.count { it.status == PermissionStatus.PENDING_TUTOR } ?: current.pendingPermissions,
                unreadNotifications = notifications.await().getOrNull()?.count { !it.isRead }
                    ?: current.unreadNotifications,
                error = groupsResult.exceptionOrNull()?.userMessage()
            )
        }
    }
}
