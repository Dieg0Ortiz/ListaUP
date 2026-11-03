package com.example.myapplication.data.remote

import com.example.myapplication.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

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

    /** Sesiones registradas (vista v_attendance_session_summary). */
    suspend fun getSessionSummary(periodoId: String?, grupoId: String? = null): List<SessionSummaryDto> {
        val response = client.get("${ApiClient.BASE_URL}/api/v1/asistencia/resumen") {
            if (periodoId != null) parameter("periodo_id", periodoId)
            if (grupoId != null) parameter("grupo_id", grupoId)
        }
        ensureSuccess(response)
        val root = json.parseToJsonElement(response.bodyAsText()).jsonObject
        // Los conteos llegan como número o null, y is_cancelled como 0/1 o bool según el motor de BD
        return root["sesiones"]?.jsonArray.orEmpty().map { element ->
            val row = element.jsonObject
            SessionSummaryDto(
                sessionId = row.string("session_id"),
                grupoId = row.string("grupo_id"),
                materia = row.string("materia"),
                docenteId = row.string("docente_id"),
                sessionDate = row.string("session_date").take(10),
                isCancelled = row.flag("is_cancelled"),
                totalAlumnos = row.int("total_alumnos"),
                presentes = row.int("presentes"),
                retardos = row.int("retardos"),
                ausentes = row.int("ausentes"),
                justificados = row.int("justificados_permiso"),
                inactivos = row.int("inactivos")
            )
        }
    }

    suspend fun listPermissions(studentId: String? = null, tutorId: String? = null): List<PermissionDto> {
        val response = client.get("${ApiClient.BASE_URL}/api/v1/permisos") {
            if (studentId != null) parameter("student_id", studentId)
            if (tutorId != null) parameter("tutor_id", tutorId)
        }
        ensureSuccess(response)
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getPermission(id: Int): PermissionDto {
        val response = client.get("${ApiClient.BASE_URL}/api/v1/permisos/$id")
        ensureSuccess(response)
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun requestPermission(
        studentId: String,
        scope: String,
        startDate: String,
        endDate: String,
        startTime: String?,
        endTime: String?,
        reason: String,
        tutorId: String?,
        proof: ProofFile?
    ): PermissionDto {
        val response = client.submitFormWithBinaryData(
            url = "${ApiClient.BASE_URL}/api/v1/permisos/solicitar",
            formData = formData {
                append("student_id", studentId)
                append("created_by_user_id", studentId)
                append("permission_scope", scope)
                append("start_date", startDate)
                append("end_date", endDate)
                if (startTime != null) append("start_time", startTime)
                if (endTime != null) append("end_time", endTime)
                append("reason", reason)
                if (!tutorId.isNullOrBlank()) append("tutor_id", tutorId)
                if (proof != null) {
                    val safeName = proof.name.replace("\"", "")
                    append("file", proof.bytes, Headers.build {
                        append(HttpHeaders.ContentType, "application/pdf")
                        append(HttpHeaders.ContentDisposition, "filename=\"$safeName\"")
                    })
                }
            }
        )
        ensureSuccess(response)
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun reviewAsTutor(permissionId: Int, tutorId: String, review: ReviewRequestDto): PermissionDto {
        val response = client.put("${ApiClient.BASE_URL}/api/v1/permisos/$permissionId/revision-tutor") {
            parameter("tutor_id", tutorId)
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ReviewRequestDto.serializer(), review))
        }
        ensureSuccess(response)
        return json.decodeFromString(response.bodyAsText())
    }

    fun proofUrl(permissionId: Int): String = "${ApiClient.BASE_URL}/api/v1/permisos/$permissionId/comprobante"

    suspend fun getTutorados(docenteId: String): List<TutoradoDto> {
        val response = client.get("${ApiClient.BASE_URL}/api/v1/docente/$docenteId/tutorados")
        ensureSuccess(response)
        return json.decodeFromString<TutoradosResponseDto>(response.bodyAsText()).tutorados
    }

    /** [forStudent] = true usa /alumno/{id}; false, /docente/{id}. */
    suspend fun getNotifications(ownerId: String, forStudent: Boolean): List<NotificationDto> {
        val response = client.get("${ApiClient.BASE_URL}/api/v1/notificaciones/${owner(forStudent)}/$ownerId")
        ensureSuccess(response)
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun markNotificationsRead(ownerId: String, forStudent: Boolean) {
        val response = client.put("${ApiClient.BASE_URL}/api/v1/notificaciones/${owner(forStudent)}/$ownerId/marcar-leidas")
        ensureSuccess(response)
    }

    private fun owner(forStudent: Boolean) = if (forStudent) "alumno" else "docente"

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
    Exception("Error del servidor ($code): ${body.take(200)}") {
    /** El "detail" que manda FastAPI, si viene en la respuesta. */
    val detail: String? = runCatching {
        (Json.parseToJsonElement(body).jsonObject["detail"] as? JsonPrimitive)?.contentOrNull
    }.getOrNull()?.takeIf { it.isNotBlank() }
}

/** Comprobante PDF que el alumno adjunta a una solicitud de permiso. */
class ProofFile(val name: String, val bytes: ByteArray)

private fun JsonObject.string(key: String): String =
    (this[key] as? JsonPrimitive)?.contentOrNull.orEmpty()

private fun JsonObject.int(key: String): Int {
    val value = this[key] as? JsonPrimitive ?: return 0
    return value.intOrNull ?: value.contentOrNull?.toDoubleOrNull()?.toInt() ?: 0
}

private fun JsonObject.flag(key: String): Boolean {
    val value = this[key] as? JsonPrimitive ?: return false
    return value.booleanOrNull ?: (value.intOrNull?.let { it != 0 } ?: false)
}
