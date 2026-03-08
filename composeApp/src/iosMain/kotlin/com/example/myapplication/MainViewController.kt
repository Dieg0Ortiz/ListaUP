package com.example.myapplication

import androidx.compose.ui.window.ComposeUIViewController
import com.example.myapplication.di.appModule
import com.example.myapplication.presentation.ui.App
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    App()
}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}