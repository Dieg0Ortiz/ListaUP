package com.example.myapplication.presentation.ui.dashboard.professor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.AmberText
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.HeroColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.AvatarCircle
import com.example.myapplication.presentation.ui.design.DashClass
import com.example.myapplication.presentation.ui.design.IconBubble
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.SampleData
import com.example.myapplication.presentation.ui.design.SectionHeader
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.ProfessorDashboardViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.koin.compose.viewmodel.koinViewModel

private val DAYS = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val MONTHS = listOf(
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
)

@Composable
fun ProfessorDashboardScreen(
    viewModel: ProfessorDashboardViewModel = koinViewModel(),
    onTakeAttendance: (String, String) -> Unit,
    onOpenSchedule: () -> Unit,
    onOpenAlerts: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenPermissions: () -> Unit = {},
    onOpenReports: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val today = today()
    val todayName = DAYS[today.dayOfWeek.ordinal]
    val classes = state.todayClasses.map {
        val isToday = it.dia.equals(todayName, ignoreCase = true)
        DashClass(
            materia = it.materia.ifBlank { "Clase" },
            aula = it.aula.ifBlank { "Aula" },
            grupo = it.grupo.ifBlank { "Grupo" },
            inicio = it.horaInicio.ifBlank { "--:--" },
            fin = it.horaFin,
            grupoId = it.grupoId,
            badge = if (isToday || it.dia.isBlank()) "HOY" else it.dia.uppercase(),
            // Los grupos reales siempre permiten pasar lista; la API aún no da el horario del día
            live = true,
            dia = it.dia
        )
    }.sortedByDescending { it.badge == "HOY" }.ifEmpty { SampleData.professorClasses }
    val percent = if (state.todayClasses.isEmpty()) 92 else state.weeklyAttendance.toInt()
    val words = state.user?.name.orEmpty().split(" ").filter { it.isNotBlank() && !it.endsWith(".") }
    // "Carlos Méndez López" -> apellido paterno "Méndez"
    val surname = (if (words.size >= 3) words[words.size - 2] else words.lastOrNull()).orEmpty().ifBlank { "García" }
    val initials = words.take(2).joinToString("") { it.first().uppercase() }.ifBlank { "PG" }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCircle(initials)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("$todayName, ${today.day} de ${MONTHS[today.month.ordinal]}", color = MutedColor, fontSize = 13.sp)
                    Text(
                        "¡Hola, Prof. $surname!",
                        color = InkColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onOpenNotifications) {
                    BadgedBox(badge = { Badge(containerColor = PrimaryColor) }) {
                        Icon(AppIcons.Bell, contentDescription = "Avisos", tint = InkColor)
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(HeroColor)
                    .padding(20.dp)
            ) {
                Text("Asistencia promedio semanal", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$percent%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 40.sp)
                    Spacer(Modifier.width(12.dp))
                    Pill("+3% vs. semana pasada", Color.White.copy(alpha = 0.14f), Color.White)
                }
                Spacer(Modifier.height(16.dp))
                WeekBars(todayIndex = today.dayOfWeek.ordinal)
            }
        }
        item {
            Spacer(Modifier.height(20.dp))
            Box(Modifier.padding(start = 20.dp, end = 8.dp)) {
                SectionHeader("Mis clases", "Ver horario", onOpenSchedule)
            }
            Spacer(Modifier.height(4.dp))
        }
        items(classes, key = { it.grupoId }) { session ->
            ClassCard(session) { onTakeAttendance(session.grupoId, session.materia) }
            Spacer(Modifier.height(12.dp))
        }
        item {
            Spacer(Modifier.height(8.dp))
            Text(
                "Accesos rápidos",
                color = InkColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))
            WhiteCard(modifier = Modifier.padding(horizontal = 20.dp), onClick = onOpenAlerts) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBubble(AppIcons.Warning, AmberSoft, tint = AmberText)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Alertas de asistencia", fontWeight = FontWeight.Bold, color = InkColor)
                        Text("Alumnos cerca del límite de faltas", color = MutedColor, fontSize = 13.sp)
                    }
                    Text("Ver", color = PrimaryColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Shortcut(AppIcons.Document, "Permisos", "Pendientes", onOpenPermissions, Modifier.weight(1f))
                Shortcut(AppIcons.Chart, "Reportes", "Mensuales", onOpenReports, Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

@Composable
private fun Shortcut(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit, modifier: Modifier) {
    WhiteCard(modifier = modifier, onClick = onClick) {
        IconBubble(icon, SoftPurple, size = 40.dp)
        Spacer(Modifier.height(10.dp))
        Text(title, color = InkColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(subtitle, color = MutedColor, fontSize = 13.sp)
    }
}

@Composable
private fun WeekBars(todayIndex: Int) {
    val heights = listOf(0.45f, 0.62f, 0.7f, 0.55f, 0.95f, 0.5f, 0.58f)
    val labels = listOf("L", "M", "M", "J", "V", "S", "D")
    Row(
        modifier = Modifier.fillMaxWidth().clearAndSetSemantics {
            contentDescription = "Gráfica de asistencia de la semana"
        },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        heights.forEachIndexed { index, fraction ->
            val isToday = index == todayIndex
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.BottomCenter) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isToday) PrimaryColor else Color.White.copy(alpha = 0.22f))
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    labels[index],
                    color = if (isToday) Color.White else Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ClassCard(session: DashClass, onTake: () -> Unit) {
    val isToday = session.badge == "HOY"
    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Bloque de hora: reemplaza el cuadro decorativo y deja la hora a primera vista
            Column(
                modifier = Modifier
                    .size(width = 72.dp, height = 80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isToday) HeroColor else SoftPurple)
                    .semantics(mergeDescendants = true) {},
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    session.inicio,
                    color = if (isToday) Color.White else PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (session.fin.isNotBlank()) {
                    Text(
                        "a ${session.fin}",
                        color = if (isToday) Color.White.copy(alpha = 0.7f) else MutedColor,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Pill(
                    session.badge,
                    if (isToday) GreenSoft else SoftPurple,
                    if (isToday) GreenText else PrimaryColor
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    session.materia,
                    color = InkColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text("${session.aula} • ${session.grupo}", color = MutedColor, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
        if (session.live) {
            Button(
                onClick = onTake,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = if (isToday) {
                    ButtonDefaults.buttonColors(containerColor = PrimaryColor, contentColor = Color.White)
                } else {
                    ButtonDefaults.buttonColors(containerColor = SoftPurple, contentColor = PrimaryColor)
                }
            ) {
                Icon(AppIcons.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Pasar lista", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        } else {
            Text("Próximamente", color = MutedColor, fontWeight = FontWeight.Medium, fontSize = 13.sp)
        }
    }
}
