package com.example.myapplication.presentation.ui.design

import com.example.myapplication.domain.model.AttendanceStatus

data class DashClass(
    val materia: String,
    val aula: String,
    val grupo: String,
    val inicio: String,
    val fin: String,
    val grupoId: String,
    val badge: String,
    val live: Boolean,
    val dia: String = ""
)

data class RecentClass(
    val materia: String,
    val detail: String,
    val icon: String,
    val present: Boolean,
    val percent: Int,
    val docente: String
)

data class SubjectCard(
    val materia: String,
    val docente: String,
    val percent: Int,
    val schedule: String,
    val badge: String,
    val risk: Boolean
)

data class WeekClass(
    val start: String,
    val end: String,
    val materia: String,
    val docente: String,
    val place: String,
    val grupo: String,
    val badge: String
)

data class RollRow(
    val id: String,
    val initials: String,
    val name: String,
    val matricula: String,
    val group: String,
    val status: AttendanceStatus?,
    val hasPermission: Boolean = false
)

data class StudentNotice(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val section: String,
    val icon: String,
    val tint: NoticeTint,
    val unread: Boolean
)

enum class NoticeTint { Purple, Green, Amber, Rose }

data class PermissionRequestCard(
    val name: String,
    val studentId: String,
    val career: String,
    val motivo: String,
    val detail: String,
    val time: String,
    val icon: String,
    val start: String,
    val end: String,
    val days: String
)

object SampleData {
    val professorClasses = listOf(
        DashClass("Cálculo II", "Aula 302", "Grupo 4A", "09:00", "10:30 AM", "calc-302", "EN CURSO", true),
        DashClass("Álgebra Lineal", "Aula 105", "Grupo 2B", "11:30", "01:00 PM", "alg-105", "PRÓXIMAMENTE", false)
    )

    val studentRecent = listOf(
        RecentClass("Estructuras de Datos", "10:00 AM • Aula A", "⟨⟩", true, 82, "Dr. Roberto Hernández"),
        RecentClass("Álgebra Lineal", "08:30 AM • Salón 302", "Σ", false, 80, "Dr. Carlos Fuentes"),
        RecentClass("Desarrollo Web", "Ayer • Lab 2", "HTML", true, 91, "Mtra. Elena Gómez")
    )

    val subjects = listOf(
        SubjectCard("Cálculo II", "Dr. Roberto Méndez", 90, "Lun - Mie 10:00 AM", "ACTIVA", false),
        SubjectCard("Estructuras de Datos", "Mtra. Elena Garrido", 65, "Mar - Jue 08:30 AM", "EN RIESGO", true),
        SubjectCard("Álgebra Lineal", "Dr. Carlos Fuentes", 80, "Lun 14:00 PM", "ACTIVA", false),
        SubjectCard("Redes de Computadoras", "Ing. Samuel Ruiz", 100, "Mie - Jue 16:00 PM", "PRÓXIMA", false)
    )

    val weekClasses = listOf(
        WeekClass("08:00 AM", "09:30", "Cálculo II", "Ing. Roberto Méndez", "Aula 302", "Grupo 4A", "FINALIZADA"),
        WeekClass("10:00 AM", "11:30", "Estructuras de Datos", "Lic. Ana Victoria", "Lab 2", "Grupo 4A", "EN CURSO"),
        WeekClass("12:00 PM", "01:30", "Sistemas Operativos", "Dr. Carlos Ruiz", "Aula 105", "Grupo 4A", "PRÓXIMA"),
        WeekClass("02:00 PM", "03:30", "Ética Profesional", "Mtra. Elena Gómez", "Aula Virtual", "Grupo 4A", "PROGRAMADA")
    )

    val roll = listOf(
        RollRow("1", "JD", "Jane Doe", "2024001", "Grupo A", AttendanceStatus.PRESENTE),
        RollRow("2", "JS", "John Smith", "2024042", "Grupo B", null, hasPermission = true),
        RollRow("3", "AM", "Alice Miller", "2024015", "Grupo A", null),
        RollRow("4", "BT", "Bob Taylor", "2024088", "Grupo B", AttendanceStatus.RETARDO),
        RollRow("5", "CH", "Charlie Huang", "2024102", "Grupo A", AttendanceStatus.AUSENTE),
        RollRow("6", "LR", "Lucía Ramos", "2024118", "Grupo A", AttendanceStatus.PRESENTE),
        RollRow("7", "MP", "Mateo Pérez", "2024120", "Grupo B", null),
        RollRow("8", "SG", "Sofía Gómez", "2024133", "Grupo A", AttendanceStatus.PRESENTE)
    )

    val studentNotices = listOf(
        StudentNotice("n1", "Asistencia registrada", "Matemáticas Avanzadas. Quedaste como presente.", "hace 2m", "Hoy", "AS", NoticeTint.Purple, true),
        StudentNotice("n2", "Permiso actualizado", "Tu permiso médico fue aprobado por dirección.", "hace 1h", "Hoy", "OK", NoticeTint.Green, true),
        StudentNotice("n3", "Aviso de Baja Asistencia", "Aviso: La asistencia en Estructuras de Datos es del 76%.", "hace 1d", "Ayer", "!", NoticeTint.Amber, false),
        StudentNotice("n4", "Asistencia registrada", "Bases de Datos, laboratorio. Quedaste como presente.", "hace 1d", "Ayer", "AS", NoticeTint.Purple, false),
        StudentNotice("n5", "Permiso denegado", "La visita industrial se rechazó por falta de documento.", "hace 3d", "Esta semana", "NO", NoticeTint.Rose, false)
    )

    val permissionRequests = listOf(
        PermissionRequestCard(
            "David Smith", "20230112", "Ing. de Sistemas", "Licencia Médica",
            "Licencia Médica: 24 Oct - 26 Oct. Adjunta certificado clínico.",
            "Hace 3h", "MD", "24 Oct, 2023", "26 Oct, 2023", "3 Días"
        ),
        PermissionRequestCard(
            "Sarah Wilson", "20230880", "Ing. Industrial", "Evento Deportivo",
            "Participación en Evento Deportivo (Selección Nacional).",
            "Ayer", "DP", "18 Oct, 2023", "19 Oct, 2023", "2 Días"
        ),
        PermissionRequestCard(
            "Julian Rivera", "20230541", "Ing. de Sistemas", "Permiso Médico",
            "Procedimiento quirúrgico menor. Adjunta certificado.",
            "Hace 5h", "MD", "24 Oct, 2023", "26 Oct, 2023", "3 Días"
        )
    )
}
