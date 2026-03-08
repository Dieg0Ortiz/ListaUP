package com.example.myapplication.di

import org.koin.core.module.Module

val appModule: List<Module> = listOf(
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
