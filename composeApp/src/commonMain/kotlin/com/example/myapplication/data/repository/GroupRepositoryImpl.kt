package com.example.myapplication.data.repository

import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.domain.model.Group
import com.example.myapplication.domain.model.StudentAttendance
import com.example.myapplication.domain.model.AttendanceStatus
import com.example.myapplication.domain.repository.GroupRepository

class GroupRepositoryImpl(
    private val apiService: ApiService
) : GroupRepository {

    override suspend fun getStudentGroups(matricula: String, periodoId: String): Result<List<Group>> {
        return try {
            val dtos = apiService.getStudentGroups(matricula, periodoId)
            val groups = dtos.map { dto ->
                Group(
                    grupoId = dto.grupoId,
                    nombre = dto.nombre,
                    materia = dto.materia,
                    aula = dto.aula,
                    horario = dto.horario,
                    periodoId = dto.periodoId,
                    docenteNombre = dto.docenteNombre
                )
            }
            Result.success(groups)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfessorGroups(docenteId: String, periodoId: String): Result<List<Group>> {
        return try {
            val dtos = apiService.getProfessorGroups(periodoId, docenteId)
            val groups = dtos.map { dto ->
                Group(
                    grupoId = dto.grupoId,
                    nombre = dto.nombre,
                    materia = dto.materia,
                    aula = dto.aula,
                    horario = dto.horario,
                    periodoId = dto.periodoId,
                    docenteNombre = dto.docenteNombre
                )
            }
            Result.success(groups)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStudentsOfGroup(grupoId: String): Result<List<StudentAttendance>> {
        return try {
            val dtos = apiService.getStudentsOfGroup(grupoId)
            val students = dtos.map { dto ->
                StudentAttendance(
                    studentId = dto.alumnoId,
                    studentName = dto.nombre,
                    matricula = dto.matricula,
                    grupoId = dto.grupoId,
                    status = AttendanceStatus.AUSENTE,
                    hasPermission = false
                )
            }
            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStudentSchedule(matricula: String, periodoId: String): Result<List<ClassSession>> {
        return try {
            val dtos = apiService.getStudentSchedule(matricula, periodoId)
            val schedule = dtos.map { dto ->
                ClassSession(
                    id = dto.horarioId,
                    materia = dto.materia,
                    aula = dto.aula,
                    grupo = dto.grupo,
                    grupoId = dto.grupoId,
                    horaInicio = dto.horaInicio,
                    horaFin = dto.horaFin,
                    dia = dto.dia,
                    docenteNombre = dto.docenteNombre
                )
            }
            Result.success(schedule)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
