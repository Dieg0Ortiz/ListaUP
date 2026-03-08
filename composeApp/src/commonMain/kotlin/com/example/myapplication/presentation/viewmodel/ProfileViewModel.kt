package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val user: User? = null,
    val isLoggedOut: Boolean = false
)

class ProfileViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = ProfileUiState(user = sessionManager.getUser())
    }

    fun logout() {
        sessionManager.clearSession()
        _uiState.value = ProfileUiState(isLoggedOut = true)
    }
}
