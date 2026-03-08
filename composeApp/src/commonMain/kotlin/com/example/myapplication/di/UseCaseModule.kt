package com.example.myapplication.di

import com.example.myapplication.domain.usecase.*
import org.koin.dsl.module

val useCaseModule = module {
    factory { LoginUseCase(get()) }
    factory { GetGroupsUseCase(get()) }
    factory { GetStudentsOfGroupUseCase(get()) }
    factory { SubmitAttendanceUseCase(get()) }
    factory { GetAlertsByThresholdUseCase(get()) }
    factory { GetScheduleUseCase(get()) }
    factory { GetAttendanceHistoryUseCase(get()) }
}
