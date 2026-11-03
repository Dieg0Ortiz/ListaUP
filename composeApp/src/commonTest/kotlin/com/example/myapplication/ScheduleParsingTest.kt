package com.example.myapplication

import com.example.myapplication.core.dayIndexOf
import com.example.myapplication.core.initialsOf
import com.example.myapplication.core.parseIsoDate
import com.example.myapplication.core.parseTime
import com.example.myapplication.core.weekStart
import com.example.myapplication.domain.model.Group
import com.example.myapplication.domain.model.PermissionStatus
import com.example.myapplication.domain.model.toSessions
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScheduleParsingTest {

    @Test
    fun dayNamesFromTheSiuMatchWithOrWithoutAccents() {
        assertEquals(0, dayIndexOf("Lunes"))
        assertEquals(2, dayIndexOf("Miércoles"))
        assertEquals(2, dayIndexOf("MIERCOLES"))
        assertEquals(5, dayIndexOf("Sab"))
        assertNull(dayIndexOf("08:00-10:00"))
        assertNull(dayIndexOf(""))
    }

    @Test
    fun timesAcceptSiuAndTwelveHourFormats() {
        assertEquals(LocalTime(8, 0), parseTime("08:00"))
        assertEquals(LocalTime(8, 30), parseTime("8:30:00"))
        assertEquals(LocalTime(14, 0), parseTime("2:00 PM"))
        assertEquals(LocalTime(0, 15), parseTime("12:15 AM"))
        assertNull(parseTime("25:00"))
        assertNull(parseTime(""))
    }

    @Test
    fun groupScheduleSplitsIntoOneSessionPerBlock() {
        val group = Group(grupoId = "3401", materia = "Programación", horario = "Lunes 08:00-10:00, Miércoles 10:00-12:00")
        val sessions = group.toSessions()
        assertEquals(2, sessions.size)
        assertEquals(0, sessions[0].dayIndex)
        assertEquals("08:00", sessions[0].horaInicio)
        assertEquals("10:00", sessions[0].horaFin)
        assertEquals(2, sessions[1].dayIndex)
        assertEquals("12:00", sessions[1].horaFin)
    }

    @Test
    fun groupWithoutScheduleStillProducesOneSession() {
        val sessions = Group(grupoId = "1", materia = "Ética", horario = "").toSessions()
        assertEquals(1, sessions.size)
        assertNull(sessions[0].dayIndex)
    }

    @Test
    fun scheduleWithoutDayKeepsTheHours() {
        val session = Group(grupoId = "1", horario = "08:00-10:00").toSessions().single()
        assertNull(session.dayIndex)
        assertEquals("08:00", session.horaInicio)
        assertEquals("10:00", session.horaFin)
    }

    @Test
    fun weekStartsOnMonday() {
        // 3 nov 2026 es martes
        assertEquals(LocalDate(2026, 11, 2), weekStart(LocalDate(2026, 11, 3)))
        assertEquals(LocalDate(2026, 11, 2), weekStart(LocalDate(2026, 11, 8)))
    }

    @Test
    fun isoDatesFromTheServerParse() {
        assertEquals(LocalDate(2026, 9, 7), parseIsoDate("2026-09-07T18:30:00"))
        assertNull(parseIsoDate("sin fecha"))
        assertNull(parseIsoDate(null))
    }

    @Test
    fun permissionStatusIgnoresCaseAndUnknownValues() {
        assertEquals(PermissionStatus.PENDING_TUTOR, PermissionStatus.from("pending_tutor"))
        assertEquals(PermissionStatus.APPROVED, PermissionStatus.from("APPROVED"))
        assertEquals(PermissionStatus.UNKNOWN, PermissionStatus.from("CANCELLED"))
    }

    @Test
    fun initialsSkipTitles() {
        assertEquals("CM", initialsOf("Prof. Carlos Méndez López"))
        assertEquals("?", initialsOf(""))
    }
}
