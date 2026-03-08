package com.example.myapplication.data.remote

import com.example.myapplication.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class ApiService(private val client: HttpClient) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun loginStudent(matricula: String, password: String): Pair<LoginResponseDto, String?> {
        val response: HttpResponse = client.submitForm(
            url = "${ApiClient.BASE_URL}/siu/servicio/logingappalumnos",
            formParameters = parameters {
                append("matricula", matricula)
                append("password", password)
            }
        )
        val body = response.bodyAsText()
        val dto = json.decodeFromString<LoginResponseDto>(body)
        val cookie = response.headers[HttpHeaders.SetCookie]
        return Pair(dto, cookie)
    }

    suspend fun loginProfessor(usuario: String, password: String): Pair<LoginResponseDto, String?> {
        val response: HttpResponse = client.submitForm(
            url = "${ApiClient.BASE_URL}/siu/servicio/logingapp",
            formParameters = parameters {
                append("usuario", usuario)
                append("password", password)
            }
        )
        val body = response.bodyAsText()
        val dto = json.decodeFromString<LoginResponseDto>(body)
        val cookie = response.headers[HttpHeaders.SetCookie]
        return Pair(dto, cookie)
    }

    suspend fun getStudentGroups(matricula: String, periodoId: String): List<GroupDto> {
        val response: HttpResponse = client.submitForm(
            url = "${ApiClient.BASE_URL}/siu/servicio/lista_grupos_v_alumno",
            formParameters = parameters {
                append("matricula", matricula)
                append("PeriodoID", periodoId)
            }
        )
        val body = response.bodyAsText()
        return try {
            json.decodeFromString<List<GroupDto>>(body)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getProfessorGroups(periodoId: String, docenteId: String): List<GroupDto> {
        val response: HttpResponse = client.submitForm(
            url = "${ApiClient.BASE_URL}/siu/servicio/lista_grupo_docente",
            formParameters = parameters {
                append("PeriodoID", periodoId)
                append("DocenteID", docenteId)
            }
        )
        val body = response.bodyAsText()
        return try {
            json.decodeFromString<List<GroupDto>>(body)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getStudentsOfGroup(grupoId: String): List<StudentDto> {
        val response: HttpResponse = client.submitForm(
            url = "${ApiClient.BASE_URL}/siu/servicio/lista_alumnos_grupo",
            formParameters = parameters {
                append("GrupoID", grupoId)
            }
        )
        val body = response.bodyAsText()
        return try {
            json.decodeFromString<List<StudentDto>>(body)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getStudentSchedule(matricula: String, periodoId: String): List<ScheduleDto> {
        val response: HttpResponse = client.submitForm(
            url = "${ApiClient.BASE_URL}/siu/servicio/lista_horario_alumno",
            formParameters = parameters {
                append("matricula", matricula)
                append("PeriodoID", periodoId)
            }
        )
        val body = response.bodyAsText()
        return try {
            json.decodeFromString<List<ScheduleDto>>(body)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
