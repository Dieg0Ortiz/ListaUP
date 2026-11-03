package com.example.myapplication.di

import com.example.myapplication.presentation.viewmodel.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { ProfessorDashboardViewModel(get(), get(), get(), get(), get()) }
    viewModel { StudentDashboardViewModel(get(), get(), get(), get(), get()) }
    viewModel { AttendanceViewModel(get(), get()) }
    viewModel { AlertsViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { ScheduleViewModel(get(), get(), get()) }
    viewModel { NotificationsViewModel(get()) }
    viewModel { PermissionsViewModel(get(), get()) }
    viewModel { PermissionRequestViewModel(get()) }
    viewModel { PermissionDetailViewModel(get(), get()) }
    viewModel { ReportsViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
}
