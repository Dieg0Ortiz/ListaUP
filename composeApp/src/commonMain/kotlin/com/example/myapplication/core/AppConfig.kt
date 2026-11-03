package com.example.myapplication.core

object AppConfig {
    // Periodo escolar activo en la API Asiste (ver seed.sql: periodo "20261")
    const val PERIODO_ID = "20261"

    // Correo al que se manda a los usuarios para soporte o recuperar contraseña.
    // TODO: confirmar con la universidad el buzón real antes de publicar.
    const val SUPPORT_EMAIL = "soporte@upchiapas.edu.mx"

    // Faltas con las que un alumno aparece en alertas si el docente no lo cambia
    const val DEFAULT_UMBRAL_FALTAS = 3

    // Tamaño máximo del comprobante PDF que acepta la API
    const val MAX_PROOF_BYTES = 1_048_576
}

// URL de la API Asiste. Cada plataforma define cómo llegar a ella.
expect val apiBaseUrl: String

// true solo en compilaciones de desarrollo: habilita el log HTTP y el acceso demo.
expect val isDebugBuild: Boolean
