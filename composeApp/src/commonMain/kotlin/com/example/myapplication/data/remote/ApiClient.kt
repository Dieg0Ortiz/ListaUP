package com.example.myapplication.data.remote

import com.example.myapplication.core.apiBaseUrl
import com.example.myapplication.core.isDebugBuild
import com.example.myapplication.data.local.SessionManager
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {
    val BASE_URL: String get() = apiBaseUrl

    fun create(sessionManager: SessionManager): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                    encodeDefaults = true
                })
            }

            // Solo en desarrollo, y sin cuerpos: el login viaja con la contraseña
            if (isDebugBuild) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.INFO
                    sanitizeHeader { it == HttpHeaders.Cookie || it == HttpHeaders.SetCookie }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 15_000
            }

            defaultRequest {
                url(BASE_URL)
                val cookie = sessionManager.getSession()
                if (cookie != null) {
                    header(HttpHeaders.Cookie, cookie)
                }
            }
        }
    }
}
