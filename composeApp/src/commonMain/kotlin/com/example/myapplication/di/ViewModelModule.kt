package com.example.myapplication.di

import com.example.myapplication.presentation.viewmodel.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { ProfessorDashboardViewModel(get(), get()) }
    viewModel { StudentDashboardViewModel(get(), get(), get()) }
    viewModel { AttendanceViewModel(get(), get()) }
    viewModel { AlertsViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
}
