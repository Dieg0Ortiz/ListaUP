package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.domain.repository.GroupRepository

class GetScheduleUseCase(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(matricula: String, periodoId: String = "62"): Result<List<ClassSession>> {
        return groupRepository.getStudentSchedule(matricula, periodoId)
    }
}
