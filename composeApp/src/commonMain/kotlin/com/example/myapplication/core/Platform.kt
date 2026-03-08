package com.example.myapplication.core

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
