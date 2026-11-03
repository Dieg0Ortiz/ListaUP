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
        val response = postForm("/siu/servicio/logingappalumnos") {
            append("matricula", matricula)
            append("password", password)
        }
        val dto = json.decodeFromString<LoginResponseDto>(response.bodyAsText())
        return Pair(dto, response.headers[HttpHeaders.SetCookie])
    }

    suspend fun loginProfessor(usuario: String, password: String): Pair<LoginResponseDto, String?> {
        val response = postForm("/siu/servicio/logingapp") {
            append("usuario", usuario)
            append("password", password)
        }
        val dto = json.decodeFromString<LoginResponseDto>(response.bodyAsText())
        return Pair(dto, response.headers[HttpHeaders.SetCookie])
    }

    suspend fun getStudentGroups(matricula: String, periodoId: String): List<GroupDto> {
        val response = postForm("/siu/servicio/lista_grupos_v_alumno") {
            append("matricula", matricula)
            append("PeriodoID", periodoId)
        }
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getProfessorGroups(periodoId: String, docenteId: String): List<GroupDto> {
        val response = postForm("/siu/servicio/lista_grupo_docente") {
            append("PeriodoID", periodoId)
            append("DocenteID", docenteId)
        }
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getStudentsOfGroup(grupoId: String): List<StudentDto> {
        val response = postForm("/siu/servicio/lista_alumnos_grupo") {
            append("GrupoID", grupoId)
        }
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getStudentSchedule(matricula: String, periodoId: String): List<ScheduleDto> {
        val response = postForm("/siu/servicio/lista_horario_alumno") {
            append("matricula", matricula)
            append("PeriodoID", periodoId)
        }
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun registerAttendance(payload: AttendanceSessionDto): AttendanceSessionResponseDto {
        val response = client.post("${ApiClient.BASE_URL}/api/v1/asistencia/registrar") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(AttendanceSessionDto.serializer(), payload))
        }
        ensureSuccess(response)
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getAlerts(grupoId: String, umbralFaltas: Int): AlertsResponseDto {
        val response = client.get("${ApiClient.BASE_URL}/api/v1/asistencia/alertas") {
            parameter("grupo_id", grupoId)
            parameter("umbral_faltas", umbralFaltas)
        }
        ensureSuccess(response)
        return json.decodeFromString(response.bodyAsText())
    }

    private suspend fun postForm(path: String, params: ParametersBuilder.() -> Unit): HttpResponse {
        val response = client.submitForm(
            url = "${ApiClient.BASE_URL}$path",
            formParameters = parameters(params)
        )
        ensureSuccess(response)
        return response
    }

    private suspend fun ensureSuccess(response: HttpResponse) {
        if (!response.status.isSuccess()) {
            throw ApiException(response.status.value, response.bodyAsText())
        }
    }
}

class ApiException(val code: Int, body: String) :
    Exception("Error del servidor ($code): ${body.take(200)}")
