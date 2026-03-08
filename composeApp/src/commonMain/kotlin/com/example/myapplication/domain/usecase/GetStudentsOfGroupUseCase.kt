package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.StudentAttendance
import com.example.myapplication.domain.repository.GroupRepository

class GetStudentsOfGroupUseCase(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(grupoId: String): Result<List<StudentAttendance>> {
        return groupRepository.getStudentsOfGroup(grupoId)
    }
}
