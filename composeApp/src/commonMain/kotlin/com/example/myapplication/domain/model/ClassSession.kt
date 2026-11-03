package com.example.myapplication.domain.model

import com.example.myapplication.core.dayIndexOf
import kotlinx.serialization.Serializable

@Serializable
data class ClassSession(
    val id: String = "",
    val materia: String = "",
    val aula: String = "",
    val grupo: String = "",
    val grupoId: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val dia: String = "",
    val docenteNombre: String = ""
) {
    /** 0 = lunes … 6 = domingo; null si el SIU no mandó un día reconocible. */
    val dayIndex: Int? get() = dayIndexOf(dia)
}

/**
 * Convierte el horario de un grupo del SIU ("Lunes 08:00-10:00", a veces varios
 * separados por coma o punto y coma) en sesiones, una por bloque.
 */
fun Group.toSessions(): List<ClassSession> {
    val blocks = horario.split(",", ";", "\n").map { it.trim() }.filter { it.isNotBlank() }
    if (blocks.isEmpty()) {
        return listOf(base())
    }
    return blocks.map { block ->
        val firstToken = block.substringBefore(" ")
        val hasDay = dayIndexOf(firstToken) != null
        val dia = if (hasDay) firstToken else ""
        val horas = if (hasDay) block.substringAfter(" ", "").trim() else block
        base().copy(
            dia = dia,
            horaInicio = horas.substringBefore("-").trim(),
            horaFin = horas.substringAfter("-", "").trim()
        )
    }
}

private fun Group.base() = ClassSession(
    id = grupoId,
    materia = materia,
    aula = aula,
    grupo = nombre,
    grupoId = grupoId,
    docenteNombre = docenteNombre
)
