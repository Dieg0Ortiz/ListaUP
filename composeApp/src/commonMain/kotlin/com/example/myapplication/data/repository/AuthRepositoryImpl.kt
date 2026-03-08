package com.example.myapplication.data.repository

import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun loginStudent(matricula: String, password: String): Result<User> {
        return try {
            val (response, cookie) = apiService.loginStudent(matricula, password)
            if (response.status == "1" || response.nombre.isNotEmpty()) {
                if (cookie != null) {
                    val ciSession = extractCiSession(cookie)
                    sessionManager.saveSession(ciSession)
                }
                val user = User(
                    id = response.id.ifEmpty { response.matricula },
                    name = response.nombre,
                    email = response.correo,
                    matricula = response.matricula.ifEmpty { matricula },
                    role = UserRole.STUDENT
                )
                sessionManager.saveUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception(response.mensaje.ifEmpty { "Login de alumno fallido" }))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginProfessor(usuario: String, password: String): Result<User> {
        return try {
            val (response, cookie) = apiService.loginProfessor(usuario, password)
            if (response.status == "1" || response.nombre.isNotEmpty()) {
                if (cookie != null) {
                    val ciSession = extractCiSession(cookie)
                    sessionManager.saveSession(ciSession)
                }
                val user = User(
                    id = response.id.ifEmpty { response.docenteId },
                    name = response.nombre,
                    email = response.correo,
                    role = UserRole.PROFESSOR,
                    docenteId = response.docenteId.ifEmpty { response.id }
                )
                sessionManager.saveUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception(response.mensaje.ifEmpty { "Login de docente fallido" }))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return sessionManager.getUser()
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    private fun extractCiSession(setCookieHeader: String): String {
        return setCookieHeader
            .split(";")
            .firstOrNull { it.trim().startsWith("ci_session") }
            ?.trim()
            ?: setCookieHeader.split(";").first().trim()
    }
}
