package com.example.myapplication.core

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()

actual val apiBaseUrl: String = "http://localhost:8000"
