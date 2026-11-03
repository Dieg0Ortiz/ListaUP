package com.example.myapplication.domain.usecase

import com.example.myapplication.core.UserFacingException
import com.example.myapplication.core.userMessage
import com.example.myapplication.data.remote.ApiException
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    // El rol lo elige el usuario en el login: la API de alumnos no rechaza
    // credenciales de docente, así que no se puede probar uno y luego el otro.
    suspend operator fun invoke(credential: String, password: String, asProfessor: Boolean): Result<User> {
        val result = if (asProfessor) {
            authRepository.loginProfessor(credential.trim(), password)
        } else {
            authRepository.loginStudent(credential.trim(), password)
        }
        return result.recoverCatching { e ->
            throw UserFacingException(
                if (e is ApiException && (e.code == 401 || e.code == 403)) {
                    if (asProfessor) {
                        "Usuario o contraseña incorrectos."
                    } else {
                        "Matrícula o contraseña incorrectas."
                    }
                } else {
                    e.userMessage()
                }
            )
        }
    }
}
