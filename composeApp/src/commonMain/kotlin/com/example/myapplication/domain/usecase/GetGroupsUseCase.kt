package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Group
import com.example.myapplication.domain.repository.GroupRepository

class GetGroupsUseCase(
    private val groupRepository: GroupRepository
) {
    suspend fun invokeForStudent(matricula: String, periodoId: String = "62"): Result<List<Group>> {
        return groupRepository.getStudentGroups(matricula, periodoId)
    }

    suspend fun invokeForProfessor(docenteId: String, periodoId: String = "62"): Result<List<Group>> {
        return groupRepository.getProfessorGroups(docenteId, periodoId)
    }
}
