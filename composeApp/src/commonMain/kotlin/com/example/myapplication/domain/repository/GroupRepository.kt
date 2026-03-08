package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.domain.model.Group
import com.example.myapplication.domain.model.StudentAttendance

interface GroupRepository {
    suspend fun getStudentGroups(matricula: String, periodoId: String = "62"): Result<List<Group>>
    suspend fun getProfessorGroups(docenteId: String, periodoId: String = "62"): Result<List<Group>>
    suspend fun getStudentsOfGroup(grupoId: String): Result<List<StudentAttendance>>
    suspend fun getStudentSchedule(matricula: String, periodoId: String = "62"): Result<List<ClassSession>>
}
