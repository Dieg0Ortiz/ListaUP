package com.example.myapplication.di

import com.example.myapplication.data.repository.AlertRepositoryImpl
import com.example.myapplication.data.repository.AttendanceRepositoryImpl
import com.example.myapplication.data.repository.AuthRepositoryImpl
import com.example.myapplication.data.repository.GroupRepositoryImpl
import com.example.myapplication.domain.repository.AlertRepository
import com.example.myapplication.domain.repository.AttendanceRepository
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.GroupRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<GroupRepository> { GroupRepositoryImpl(get()) }
    single { AttendanceRepositoryImpl(get(), get()) }
    single<AttendanceRepository> { get<AttendanceRepositoryImpl>() }
    single<AlertRepository> { AlertRepositoryImpl(get(), get()) }
}
