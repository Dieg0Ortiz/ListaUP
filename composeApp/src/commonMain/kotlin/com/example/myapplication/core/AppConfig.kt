package com.example.myapplication.core

object AppConfig {
    // Periodo escolar activo en la API Asiste (ver seed.sql: periodo "20261")
    const val PERIODO_ID = "20261"
}

// URL de la API Asiste. Cada plataforma define cómo llegar a localhost:8000.
expect val apiBaseUrl: String
