package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    // El rol lo elige el usuario en el login: la API de alumnos no rechaza
    // credenciales de docente, así que no se puede probar uno y luego el otro.
    suspend operator fun invoke(credential: String, password: String, asProfessor: Boolean): Result<User> {
        val result = if (asProfessor) {
            authRepository.loginProfessor(credential, password)
        } else {
            authRepository.loginStudent(credential, password)
        }
        return result.recoverCatching { e ->
            throw Exception(
                if (e.message?.contains("(401)") == true) {
                    "Credenciales inválidas. Verifica tu usuario/matrícula y contraseña."
                } else {
                    "No se pudo conectar con el servidor: ${e.message}"
                }
            )
        }
    }
}
