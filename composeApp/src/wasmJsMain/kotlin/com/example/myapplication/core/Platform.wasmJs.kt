package com.example.myapplication.core

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual val apiBaseUrl: String = "http://localhost:8000"

// La versión web es para desarrollo: sin log HTTP ni acceso demo, sesión en memoria
actual val isDebugBuild: Boolean = false

actual fun createKeyValueStore(): KeyValueStore = InMemoryKeyValueStore()
