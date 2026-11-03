package com.example.myapplication.di

import com.example.myapplication.data.repository.AlertRepositoryImpl
import com.example.myapplication.data.repository.AttendanceRepositoryImpl
import com.example.myapplication.data.repository.AuthRepositoryImpl
import com.example.myapplication.data.repository.GroupRepositoryImpl
import com.example.myapplication.data.repository.NotificationRepositoryImpl
import com.example.myapplication.data.repository.PermissionRepositoryImpl
import com.example.myapplication.data.repository.StatsRepositoryImpl
import com.example.myapplication.domain.repository.AlertRepository
import com.example.myapplication.domain.repository.AttendanceRepository
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.GroupRepository
import com.example.myapplication.domain.repository.NotificationRepository
import com.example.myapplication.domain.repository.PermissionRepository
import com.example.myapplication.domain.repository.StatsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<GroupRepository> { GroupRepositoryImpl(get()) }
    single { AttendanceRepositoryImpl(get(), get()) }
    single<AttendanceRepository> { get<AttendanceRepositoryImpl>() }
    single<AlertRepository> { AlertRepositoryImpl(get(), get()) }
    single<PermissionRepository> { PermissionRepositoryImpl(get(), get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get(), get()) }
    single<StatsRepository> { StatsRepositoryImpl(get(), get()) }
}
