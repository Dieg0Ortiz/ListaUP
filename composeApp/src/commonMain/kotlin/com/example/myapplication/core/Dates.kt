package com.example.myapplication.core

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

val DAY_NAMES = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
val DAY_SHORT = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
val MONTH_NAMES = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
)

@OptIn(ExperimentalTime::class)
fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

@OptIn(ExperimentalTime::class)
fun now(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

/** Lunes de la semana de [date]. */
fun weekStart(date: LocalDate): LocalDate = date.minus(DatePeriod(days = date.dayOfWeek.ordinal))

fun LocalDate.plusDays(days: Int): LocalDate = plus(DatePeriod(days = days))

/** "3 de noviembre de 2026" */
fun LocalDate.longLabel(): String = "$day de ${MONTH_NAMES[month.ordinal]} de $year"

/** "3 nov 2026" */
fun LocalDate.shortLabel(): String = "$day ${MONTH_NAMES[month.ordinal].take(3)} $year"

/** Índice 0 = lunes … 6 = domingo para un nombre de día del SIU ("Miércoles", "MIERCOLES"…). */
fun dayIndexOf(name: String): Int? {
    val key = name.normalizeKey()
    if (key.isBlank()) return null
    val exact = DAY_NAMES.indexOfFirst { it.normalizeKey() == key }
    if (exact >= 0) return exact
    // Abreviaturas: "Mie", "Sab"…
    return DAY_NAMES.indexOfFirst { key.length >= 3 && it.normalizeKey().startsWith(key.take(3)) }
        .takeIf { it >= 0 }
}

val DayOfWeek.index: Int get() = ordinal

fun String.normalizeKey(): String = lowercase()
    .replace('á', 'a').replace('é', 'e').replace('í', 'i')
    .replace('ó', 'o').replace('ú', 'u').replace('ü', 'u')
    .trim()

fun parseIsoDate(value: String?): LocalDate? =
    value?.take(10)?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

/** "08:00", "8:00", "08:00:00" o "8:00 AM" → hora del día. */
fun parseTime(value: String?): LocalTime? {
    val text = value?.trim()?.uppercase() ?: return null
    val pm = text.endsWith("PM")
    val am = text.endsWith("AM")
    val parts = text.removeSuffix("PM").removeSuffix("AM").trim().split(":")
    var hour = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: return null
    val minute = parts.getOrNull(1)?.trim()?.take(2)?.toIntOrNull() ?: 0
    if (pm && hour < 12) hour += 12
    if (am && hour == 12) hour = 0
    if (hour !in 0..23 || minute !in 0..59) return null
    return LocalTime(hour, minute)
}

fun LocalTime.label(): String = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

/** "hace 5 min", "hace 3 h", "ayer", "12 oct 2026" a partir de un timestamp ISO del servidor. */
fun relativeLabel(iso: String): String {
    val date = parseIsoDate(iso) ?: return ""
    val days = (today().toEpochDays() - date.toEpochDays()).toInt()
    val time = parseTime(iso.drop(11).take(5))
    return when {
        days == 0 && time != null -> {
            val minutes = (now().time.toSecondOfDay() - time.toSecondOfDay()) / 60
            when {
                // El servidor puede guardar en UTC: si la hora sale "en el futuro", solo se dice "hoy"
                minutes < 0 -> "hoy"
                minutes < 1 -> "ahora"
                minutes < 60 -> "hace $minutes min"
                else -> "hace ${minutes / 60} h"
            }
        }
        days == 0 -> "hoy"
        days == 1 -> "ayer"
        days in 2..6 -> "hace $days días"
        else -> date.shortLabel()
    }
}

fun greeting(): String = when (now().hour) {
    in 5..11 -> "Buenos días"
    in 12..18 -> "Buenas tardes"
    else -> "Buenas noches"
}

/** Iniciales para el avatar: "Carlos Méndez López" → "CM". */
fun initialsOf(name: String): String = name.split(" ")
    .filter { it.isNotBlank() && !it.endsWith(".") }
    .take(2)
    .joinToString("") { it.first().uppercase() }
    .ifBlank { "?" }
