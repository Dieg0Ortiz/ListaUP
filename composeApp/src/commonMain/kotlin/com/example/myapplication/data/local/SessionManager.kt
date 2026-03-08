package com.example.myapplication.data.local

import com.example.myapplication.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager {
    private var ciSessionCookie: String? = null
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun saveSession(cookie: String) {
        ciSessionCookie = cookie
    }

    fun getSession(): String? {
        return ciSessionCookie
    }

    fun saveUser(user: User) {
        _currentUser.value = user
    }

    fun getUser(): User? {
        return _currentUser.value
    }

    fun clearSession() {
        ciSessionCookie = null
        _currentUser.value = null
    }

    fun isLoggedIn(): Boolean {
        return ciSessionCookie != null && _currentUser.value != null
    }
}
