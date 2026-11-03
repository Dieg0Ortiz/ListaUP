package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.userMessage
import com.example.myapplication.domain.model.AppNotification
import com.example.myapplication.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val notifications: List<AppNotification> = emptyList(),
    val error: String? = null,
    val actionError: String? = null
)

class NotificationsViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            _uiState.value = repository.list().fold(
                onSuccess = { _uiState.value.copy(isLoading = false, notifications = it) },
                onFailure = { _uiState.value.copy(isLoading = false, error = it.userMessage()) }
            )
        }
    }

    fun markAllRead() {
        val before = _uiState.value.notifications
        if (before.none { !it.isRead }) return
        // Se marcan en pantalla de inmediato y se revierte si la API falla
        _uiState.value = _uiState.value.copy(notifications = before.map { it.copy(isRead = true) }, actionError = null)
        viewModelScope.launch {
            repository.markAllRead().onFailure { e ->
                _uiState.value = _uiState.value.copy(notifications = before, actionError = e.userMessage())
            }
        }
    }
}
