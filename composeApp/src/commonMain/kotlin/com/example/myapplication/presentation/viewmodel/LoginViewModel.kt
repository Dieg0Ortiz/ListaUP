package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val credential: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val asProfessor: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isLoggedIn: Boolean = false
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        val currentUser = sessionManager.getUser()
        if (currentUser != null) {
            _uiState.value = _uiState.value.copy(user = currentUser, isLoggedIn = true)
        }
    }

    fun onCredentialChange(value: String) {
        _uiState.value = _uiState.value.copy(credential = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(passwordVisible = !_uiState.value.passwordVisible)
    }

    fun setProfessorMode(asProfessor: Boolean) {
        _uiState.value = _uiState.value.copy(asProfessor = asProfessor, error = null)
    }

    fun resetAfterLogout() {
        _uiState.value = LoginUiState()
    }

    fun login() {
        val state = _uiState.value
        if (state.credential.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Por favor, completa todos los campos.")
            return
        }

        if (state.credential.equals("demo", ignoreCase = true) && state.password == "demo") {
            val user = if (state.asProfessor) {
                User(
                    id = "docente-demo",
                    name = "Roberto García",
                    email = "rgarcia@institucional.edu.mx",
                    role = UserRole.PROFESSOR,
                    docenteId = "demo"
                )
            } else {
                User(
                    id = "alumno-demo",
                    name = "Alex Johnson",
                    email = "ajohnson@institucional.edu.mx",
                    matricula = "20230541",
                    role = UserRole.STUDENT
                )
            }
            sessionManager.saveSession("demo")
            sessionManager.saveUser(user)
            _uiState.value = state.copy(
                isLoading = false,
                user = user,
                isLoggedIn = true,
                error = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = loginUseCase(state.credential, state.password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = user,
                        isLoggedIn = true,
                        error = null
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error de autenticación"
                    )
                }
            )
        }
    }
}
