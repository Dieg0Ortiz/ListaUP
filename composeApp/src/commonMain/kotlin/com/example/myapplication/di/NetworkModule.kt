package com.example.myapplication.di

import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.data.remote.ApiClient
import com.example.myapplication.data.remote.ApiService
import org.koin.dsl.module

val networkModule = module {
    single { SessionManager() }
    single { ApiClient.create(get()) }
    single { ApiService(get()) }
}
