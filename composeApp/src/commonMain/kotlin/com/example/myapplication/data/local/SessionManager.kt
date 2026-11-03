package com.example.myapplication.data.local

import com.example.myapplication.core.AppConfig
import com.example.myapplication.core.KeyValueStore
import com.example.myapplication.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

/**
 * Sesión del usuario. Se guarda en disco para que cerrar la app no obligue a
 * volver a iniciar sesión; cerrar sesión la borra.
 */
class SessionManager(private val store: KeyValueStore) {
    private val json = Json { ignoreUnknownKeys = true }

    private var sessionCookie: String? = store.get(KEY_COOKIE)
    private val _currentUser = MutableStateFlow(
        store.get(KEY_USER)?.let { runCatching { json.decodeFromString<User>(it) }.getOrNull() }
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun saveSession(cookie: String) {
        sessionCookie = cookie
        store.put(KEY_COOKIE, cookie)
    }

    fun getSession(): String? = sessionCookie

    fun saveUser(user: User) {
        _currentUser.value = user
        store.put(KEY_USER, json.encodeToString(User.serializer(), user))
    }

    fun getUser(): User? = _currentUser.value

    fun clearSession() {
        sessionCookie = null
        _currentUser.value = null
        store.put(KEY_COOKIE, null)
        store.put(KEY_USER, null)
    }

    fun isLoggedIn(): Boolean = _currentUser.value != null

    /** Faltas a partir de las cuales un alumno aparece en alertas (ajuste del docente). */
    var umbralFaltas: Int
        get() = store.get(KEY_UMBRAL)?.toIntOrNull() ?: AppConfig.DEFAULT_UMBRAL_FALTAS
        set(value) = store.put(KEY_UMBRAL, value.toString())

    private companion object {
        const val KEY_COOKIE = "session_cookie"
        const val KEY_USER = "session_user"
        const val KEY_UMBRAL = "umbral_faltas"
    }
}
