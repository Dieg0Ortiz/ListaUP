package com.example.myapplication.presentation.ui.design

import com.example.myapplication.domain.model.AttendanceStatus

/** Fila del pase de lista. [status] null = el docente todavía no lo marca. */
data class RollRow(
    val id: String,
    val initials: String,
    val name: String,
    val matricula: String,
    val group: String,
    val status: AttendanceStatus?,
    val hasPermission: Boolean = false
)
