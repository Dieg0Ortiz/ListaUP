package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.User

interface AuthRepository {
    suspend fun loginStudent(matricula: String, password: String): Result<User>
    suspend fun loginProfessor(usuario: String, password: String): Result<User>
    suspend fun getCurrentUser(): User?
    suspend fun logout()
}
