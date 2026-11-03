package com.example.myapplication.core

import android.content.Context
import android.os.Build
import com.example.myapplication.BuildConfig

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

// Debug apunta al emulador (10.0.2.2); release a la URL de producción (ver build.gradle.kts)
actual val apiBaseUrl: String get() = BuildConfig.API_BASE_URL

actual val isDebugBuild: Boolean get() = BuildConfig.DEBUG

private lateinit var appContext: Context

/** Se llama desde MyApp antes de iniciar Koin. */
fun initPlatform(context: Context) {
    appContext = context.applicationContext
}

actual fun createKeyValueStore(): KeyValueStore {
    val prefs = appContext.getSharedPreferences("listaup", Context.MODE_PRIVATE)
    return object : KeyValueStore {
        override fun get(key: String): String? = prefs.getString(key, null)
        override fun put(key: String, value: String?) {
            prefs.edit().apply { if (value == null) remove(key) else putString(key, value) }.apply()
        }
    }
}
