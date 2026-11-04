package com.example.myapplication.domain.usecase

import com.example.myapplication.core.UserFacingException
import com.example.myapplication.core.userMessage
import com.example.myapplication.data.remote.ApiException
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.AuthRepository

/**
 * Un solo login para alumnos y docentes. La API de alumnos no rechaza credenciales
 * de docente, así que el rol se deduce del formato: las matrículas son solo dígitos
 * y los usuarios docentes llevan letras. Si el servicio elegido rechaza las
 * credenciales, se intenta el otro (por si algún docente usa un número de empleado).
 */
class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(credential: String, password: String): Result<User> {
        val id = normalizeCredential(credential)
        val looksLikeMatricula = id.all { it.isDigit() }

        val first = attempt(asProfessor = !looksLikeMatricula, id, password)
        val result = if (first.isFailure && first.exceptionOrNull().isRejection()) {
            attempt(asProfessor = looksLikeMatricula, id, password).let { second ->
                // Si el segundo intento también rechaza, el error útil es el del primero
                if (second.isFailure && second.exceptionOrNull().isRejection()) first else second
            }
        } else {
            first
        }

        return result.recoverCatching { e ->
            throw UserFacingException(
                if (e.isRejection()) "Matrícula, usuario o contraseña incorrectos." else e.userMessage()
            )
        }
    }

    private suspend fun attempt(asProfessor: Boolean, id: String, password: String): Result<User> =
        if (asProfessor) authRepository.loginProfessor(id, password) else authRepository.loginStudent(id, password)

    /** 401/403 de la API, o una respuesta 200 que dice "no" (status distinto de 1 y sin nombre). */
    private fun Throwable?.isRejection(): Boolean = when (this) {
        null -> false
        is ApiException -> code == 401 || code == 403
        is UserFacingException -> false
        else -> this::class == Exception::class
    }

    companion object {
        /** Quita espacios y el dominio si escribieron el correo institucional completo. */
        fun normalizeCredential(raw: String): String = raw.trim().substringBefore('@').trim()
    }
}
