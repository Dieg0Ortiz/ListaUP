package com.example.myapplication.domain.usecase

import com.example.myapplication.core.AppConfig
import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.domain.repository.GroupRepository

class GetScheduleUseCase(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(matricula: String, periodoId: String = AppConfig.PERIODO_ID): Result<List<ClassSession>> {
        return groupRepository.getStudentSchedule(matricula, periodoId)
    }
}
