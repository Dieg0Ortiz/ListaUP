package com.example.myapplication.data.repository

import com.example.myapplication.core.UserFacingException
import com.example.myapplication.core.parseIsoDate
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.ProofFile
import com.example.myapplication.data.remote.dto.PermissionDto
import com.example.myapplication.data.remote.dto.ReviewRequestDto
import com.example.myapplication.domain.model.Permission
import com.example.myapplication.domain.model.PermissionStatus
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.PermissionRepository
import kotlinx.datetime.LocalDate

class PermissionRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : PermissionRepository {

    // Nombre y matrícula por alumno_id: la API no siempre los manda en target_students.
    // Se recuerda de qué docente son para no mezclarlos si otro inicia sesión en el teléfono.
    private var tutoradoNames: Map<String, Pair<String, String>> = emptyMap()
    private var tutoradosOf: String? = null

    override suspend fun myRequests(): Result<List<Permission>> = runCatching {
        val user = sessionManager.getUser() ?: throw notLoggedIn()
        apiService.listPermissions(studentId = user.id).map { it.toDomain(user.name, user.matricula) }
    }

    override suspend fun tutorRequests(): Result<List<Permission>> = runCatching {
        val docenteId = docenteId()
        loadTutorados(docenteId)
        apiService.listPermissions(tutorId = docenteId).map { it.toDomain() }
    }

    override suspend fun get(id: Int): Result<Permission> = runCatching {
        val user = sessionManager.getUser() ?: throw notLoggedIn()
        val dto = apiService.getPermission(id)
        if (user.role == UserRole.STUDENT) {
            dto.toDomain(user.name, user.matricula)
        } else {
            loadTutorados(user.docenteId)
            dto.toDomain()
        }
    }

    override suspend fun request(
        startDate: LocalDate,
        endDate: LocalDate,
        startTime: String?,
        endTime: String?,
        reason: String,
        proof: ProofFile?
    ): Result<Permission> = runCatching {
        val user = sessionManager.getUser() ?: throw notLoggedIn()
        val hours = startTime != null && endTime != null
        apiService.requestPermission(
            studentId = user.id,
            scope = if (hours) "SPECIFIC_HOURS" else "DATE_RANGE",
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            startTime = if (hours) "$startTime:00" else null,
            endTime = if (hours) "$endTime:00" else null,
            reason = reason.trim(),
            tutorId = user.tutorId,
            proof = proof
        ).toDomain(user.name, user.matricula)
    }

    override suspend fun reviewAsTutor(id: Int, approved: Boolean, comment: String?): Result<Permission> = runCatching {
        val text = comment?.trim()?.takeIf { it.isNotBlank() }
        apiService.reviewAsTutor(
            id,
            docenteId(),
            ReviewRequestDto(approved = approved, comment = text, rejectionReason = if (approved) null else text)
        ).toDomain()
    }

    private suspend fun loadTutorados(docenteId: String) {
        if (docenteId.isBlank() || (tutoradosOf == docenteId && tutoradoNames.isNotEmpty())) return
        tutoradosOf = docenteId
        tutoradoNames = runCatching {
            apiService.getTutorados(docenteId).associate { it.alumnoId to (it.nombre to it.matricula) }
        }.getOrDefault(emptyMap())
    }

    private fun docenteId(): String =
        sessionManager.getUser()?.docenteId?.takeIf { it.isNotBlank() } ?: throw notLoggedIn()

    private fun notLoggedIn() = UserFacingException("Tu sesión terminó. Vuelve a iniciar sesión.")

    private fun PermissionDto.toDomain(knownName: String? = null, knownMatricula: String? = null): Permission {
        val target = targetStudents.firstOrNull()
        val studentId = target?.studentId ?: createdByStudentId.orEmpty()
        val fromTutor = if (tutoradosOf == sessionManager.getUser()?.docenteId) tutoradoNames[studentId] else null
        return Permission(
            id = id,
            studentId = studentId,
            studentName = target?.nombre ?: knownName ?: fromTutor?.first ?: "Alumno $studentId",
            matricula = target?.matricula ?: knownMatricula ?: fromTutor?.second.orEmpty(),
            fullDays = scope != "SPECIFIC_HOURS",
            startDate = parseIsoDate(startDate),
            endDate = parseIsoDate(endDate),
            startTime = startTime?.take(5),
            endTime = endTime?.take(5),
            reason = reason,
            status = PermissionStatus.from(status),
            tutorComment = tutorComment,
            directorComment = directorComment,
            rejectionReason = rejectionReason,
            proofFilename = proofFilename,
            proofUrl = proofFilename?.let { apiService.proofUrl(id) },
            createdAt = createdAt,
            disclaimer = disclaimerLegend
        )
    }
}
