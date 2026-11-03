package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.AppConfig
import com.example.myapplication.core.today
import com.example.myapplication.core.userMessage
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.data.remote.ProofFile
import com.example.myapplication.domain.model.Permission
import com.example.myapplication.domain.model.PermissionStatus
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class PermissionsUiState(
    val isLoading: Boolean = true,
    val isProfessor: Boolean = false,
    val permissions: List<Permission> = emptyList(),
    val error: String? = null
)

/** Lista de solicitudes: las propias (alumno) o las de sus tutorados (docente). */
class PermissionsViewModel(
    private val repository: PermissionRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionsUiState())
    val uiState: StateFlow<PermissionsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        val professor = sessionManager.getUser()?.role == UserRole.PROFESSOR
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, isProfessor = professor)
        viewModelScope.launch {
            val result = if (professor) repository.tutorRequests() else repository.myRequests()
            _uiState.value = result.fold(
                onSuccess = { _uiState.value.copy(isLoading = false, permissions = it) },
                onFailure = { _uiState.value.copy(isLoading = false, error = it.userMessage()) }
            )
        }
    }
}

data class PermissionRequestUiState(
    val fullDays: Boolean = true,
    val startDate: LocalDate = today(),
    val endDate: LocalDate = today(),
    val startTime: String = "08:00",
    val endTime: String = "10:00",
    val reason: String = "",
    val proof: ProofFile? = null,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val created: Permission? = null
)

class PermissionRequestViewModel(
    private val repository: PermissionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionRequestUiState())
    val uiState: StateFlow<PermissionRequestUiState> = _uiState.asStateFlow()

    private fun update(change: PermissionRequestUiState.() -> PermissionRequestUiState) {
        _uiState.value = _uiState.value.change().copy(error = null)
    }

    fun setFullDays(value: Boolean) = update { copy(fullDays = value, endDate = if (value) endDate else startDate) }

    fun setStartDate(date: LocalDate) = update {
        copy(startDate = date, endDate = if (!fullDays || endDate < date) date else endDate)
    }

    fun setEndDate(date: LocalDate) = update { copy(endDate = date) }
    fun setStartTime(value: String) = update { copy(startTime = value) }
    fun setEndTime(value: String) = update { copy(endTime = value) }
    fun setReason(value: String) = update { copy(reason = value.take(MAX_REASON)) }

    fun setProof(file: ProofFile?) {
        if (file != null && file.bytes.size > AppConfig.MAX_PROOF_BYTES) {
            _uiState.value = _uiState.value.copy(error = "El PDF pesa más de 1 MB. Comprímelo o toma una foto más ligera.")
            return
        }
        update { copy(proof = file) }
    }

    fun submit() {
        val state = _uiState.value
        val problem = when {
            state.reason.trim().length < MIN_REASON -> "Describe el motivo (al menos $MIN_REASON caracteres)."
            state.endDate < state.startDate -> "La fecha final no puede ser antes de la inicial."
            !state.fullDays && state.endTime <= state.startTime -> "La hora final debe ser después de la inicial."
            else -> null
        }
        if (problem != null) {
            _uiState.value = state.copy(error = problem)
            return
        }
        _uiState.value = state.copy(isSubmitting = true, error = null)
        viewModelScope.launch {
            val result = repository.request(
                startDate = state.startDate,
                endDate = if (state.fullDays) state.endDate else state.startDate,
                startTime = if (state.fullDays) null else state.startTime,
                endTime = if (state.fullDays) null else state.endTime,
                reason = state.reason,
                proof = state.proof
            )
            _uiState.value = result.fold(
                onSuccess = { _uiState.value.copy(isSubmitting = false, created = it) },
                onFailure = { _uiState.value.copy(isSubmitting = false, error = it.userMessage()) }
            )
        }
    }

    companion object {
        const val MIN_REASON = 10
        const val MAX_REASON = 500
    }
}

data class PermissionDetailUiState(
    val isLoading: Boolean = true,
    val permission: Permission? = null,
    val canReview: Boolean = false,
    val isReviewing: Boolean = false,
    val error: String? = null,
    val reviewError: String? = null
)

class PermissionDetailViewModel(
    private val repository: PermissionRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionDetailUiState())
    val uiState: StateFlow<PermissionDetailUiState> = _uiState.asStateFlow()

    private var permissionId: Int? = null

    fun load(id: Int) {
        permissionId = id
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            _uiState.value = repository.get(id).fold(
                onSuccess = { applyPermission(it).copy(isLoading = false) },
                onFailure = { _uiState.value.copy(isLoading = false, error = it.userMessage()) }
            )
        }
    }

    fun retry() {
        permissionId?.let(::load)
    }

    /** Rechazar exige un motivo: es lo que verá el alumno. */
    fun review(approved: Boolean, comment: String) {
        val id = permissionId ?: return
        if (!approved && comment.isBlank()) {
            _uiState.value = _uiState.value.copy(reviewError = "Escribe el motivo del rechazo para el alumno.")
            return
        }
        _uiState.value = _uiState.value.copy(isReviewing = true, reviewError = null)
        viewModelScope.launch {
            _uiState.value = repository.reviewAsTutor(id, approved, comment).fold(
                onSuccess = { applyPermission(it).copy(isReviewing = false) },
                onFailure = { _uiState.value.copy(isReviewing = false, reviewError = it.userMessage()) }
            )
        }
    }

    private fun applyPermission(permission: Permission): PermissionDetailUiState {
        val professor = sessionManager.getUser()?.role == UserRole.PROFESSOR
        return _uiState.value.copy(
            permission = permission,
            canReview = professor && permission.status == PermissionStatus.PENDING_TUTOR
        )
    }
}
