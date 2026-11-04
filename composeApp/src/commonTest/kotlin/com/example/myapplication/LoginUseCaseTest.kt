package com.example.myapplication

import com.example.myapplication.data.remote.ApiException
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.usecase.LoginUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginUseCaseTest {

    private class FakeAuth(
        val acceptStudent: Boolean = true,
        val acceptProfessor: Boolean = true
    ) : AuthRepository {
        val calls = mutableListOf<String>()

        override suspend fun loginStudent(matricula: String, password: String): Result<User> {
            calls += "alumno:$matricula"
            return if (acceptStudent) Result.success(User(id = matricula, name = "A", email = "", role = UserRole.STUDENT))
            else Result.failure(Exception("Login de alumno fallido"))
        }

        override suspend fun loginProfessor(usuario: String, password: String): Result<User> {
            calls += "docente:$usuario"
            return if (acceptProfessor) Result.success(User(id = usuario, name = "D", email = "", role = UserRole.PROFESSOR))
            else Result.failure(Exception("Login de docente fallido"))
        }

        override suspend fun getCurrentUser(): User? = null
        override suspend fun logout() {}
    }

    @Test
    fun numericIdGoesToStudentLogin() = runTest {
        val auth = FakeAuth()
        val user = LoginUseCase(auth)(" 221045 ", "x").getOrThrow()
        assertEquals(UserRole.STUDENT, user.role)
        assertEquals(listOf("alumno:221045"), auth.calls)
    }

    @Test
    fun alphanumericIdGoesToProfessorLogin() = runTest {
        val auth = FakeAuth()
        val user = LoginUseCase(auth)("profesor01", "x").getOrThrow()
        assertEquals(UserRole.PROFESSOR, user.role)
        assertEquals(listOf("docente:profesor01"), auth.calls)
    }

    @Test
    fun institutionalEmailIsStrippedToId() = runTest {
        val auth = FakeAuth()
        LoginUseCase(auth)("221045@upchiapas.edu.mx", "x").getOrThrow()
        assertEquals(listOf("alumno:221045"), auth.calls)
    }

    @Test
    fun rejectedStudentFallsBackToProfessor() = runTest {
        val auth = FakeAuth(acceptStudent = false)
        val user = LoginUseCase(auth)("90012", "x").getOrThrow()
        assertEquals(UserRole.PROFESSOR, user.role)
        assertEquals(listOf("alumno:90012", "docente:90012"), auth.calls)
    }

    @Test
    fun bothRejectedGivesSingleFriendlyMessage() = runTest {
        val auth = FakeAuth(acceptStudent = false, acceptProfessor = false)
        val error = LoginUseCase(auth)("221045", "x").exceptionOrNull()
        assertEquals("Matrícula, usuario o contraseña incorrectos.", error?.message)
    }

    @Test
    fun networkErrorDoesNotTryTheOtherLogin() = runTest {
        val auth = object : AuthRepository by FakeAuth() {
            var calls = 0
            override suspend fun loginStudent(matricula: String, password: String): Result<User> {
                calls++
                return Result.failure(ApiException(500, "boom"))
            }
            override suspend fun loginProfessor(usuario: String, password: String): Result<User> {
                calls++
                return Result.failure(IllegalStateException("no debería llamarse"))
            }
        }
        val result = LoginUseCase(auth)("221045", "x")
        assertTrue(result.isFailure)
        assertEquals(1, auth.calls)
    }
}
