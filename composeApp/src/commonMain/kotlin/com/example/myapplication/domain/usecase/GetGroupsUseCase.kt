package com.example.myapplication.domain.usecase

import com.example.myapplication.core.AppConfig
import com.example.myapplication.domain.model.Group
import com.example.myapplication.domain.repository.GroupRepository

class GetGroupsUseCase(
    private val groupRepository: GroupRepository
) {
    suspend fun invokeForStudent(matricula: String, periodoId: String = AppConfig.PERIODO_ID): Result<List<Group>> {
        return groupRepository.getStudentGroups(matricula, periodoId)
    }

    suspend fun invokeForProfessor(docenteId: String, periodoId: String = AppConfig.PERIODO_ID): Result<List<Group>> {
        return groupRepository.getProfessorGroups(docenteId, periodoId)
    }
}
