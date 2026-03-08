package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(credential: String, password: String): Result<User> {
        val studentResult = authRepository.loginStudent(credential, password)
        if (studentResult.isSuccess) {
            return studentResult
        }

        val professorResult = authRepository.loginProfessor(credential, password)
        if (professorResult.isSuccess) {
            return professorResult
        }

        return Result.failure(
            Exception("Credenciales inválidas. Verifica tu correo/matrícula y contraseña.")
        )
    }
}
