package com.example.myapplication.core

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

// El emulador de Android ve la máquina host en 10.0.2.2
actual val apiBaseUrl: String = "http://10.0.2.2:8000"
