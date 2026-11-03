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
import com.example.myapplication.core.DAY_NAMES
import com.example.myapplication.core.MONTH_NAMES
import com.example.myapplication.core.initialsOf
import com.example.myapplication.core.parseTime
import com.example.myapplication.core.today
import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.domain.model.WeeklyAttendance
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
import com.example.myapplication.presentation.ui.design.ErrorState
import com.example.myapplication.presentation.ui.design.IconBubble
import com.example.myapplication.presentation.ui.design.InlineError
import com.example.myapplication.presentation.ui.design.LoadingState
import com.example.myapplication.presentation.ui.design.MessageState
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.RefreshBox
import com.example.myapplication.presentation.ui.design.SectionHeader
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.ProfessorDashboardViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

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
    val todayIndex = today.dayOfWeek.ordinal
    val classes = orderedForToday(state.sessions, todayIndex)
    val words = state.user?.name.orEmpty().split(" ").filter { it.isNotBlank() && !it.endsWith(".") }
    // "Carlos Méndez López" -> apellido paterno "Méndez"
    val surname = (if (words.size >= 3) words[words.size - 2] else words.lastOrNull()).orEmpty()
    val firstLoad = state.isLoading && state.groups.isEmpty()

    RefreshBox(
        refreshing = state.isLoading && !firstLoad,
        onRefresh = viewModel::loadDashboard,
        modifier = Modifier.fillMaxSize().background(BackgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarCircle(initialsOf(state.user?.name.orEmpty()))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "${DAY_NAMES[todayIndex]}, ${today.day} de ${MONTH_NAMES[today.month.ordinal]}",
                            color = MutedColor,
                            fontSize = 13.sp
                        )
                        Text(
                            if (surname.isBlank()) "¡Hola!" else "¡Hola, Prof. $surname!",
                            color = InkColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onOpenNotifications) {
                        BadgedBox(badge = {
                            if (state.unreadNotifications > 0) {
                                Badge(containerColor = PrimaryColor) { Text("${state.unreadNotifications}") }
                            }
                        }) {
                            val label = if (state.unreadNotifications > 0) {
                                "Avisos, ${state.unreadNotifications} sin leer"
                            } else {
                                "Avisos"
                            }
                            Icon(AppIcons.Bell, contentDescription = label, tint = InkColor)
                        }
                    }
                }
            }
            item { WeekCard(state.week, todayIndex) }
            item {
                Spacer(Modifier.height(20.dp))
                Box(Modifier.padding(start = 20.dp, end = 8.dp)) {
                    SectionHeader("Mis clases", "Ver horario", onOpenSchedule)
                }
                Spacer(Modifier.height(4.dp))
            }
            if (state.error != null) {
                item {
                    if (state.groups.isEmpty()) {
                        ErrorState(state.error ?: "", onRetry = viewModel::loadDashboard)
                    } else {
                        InlineError(state.error ?: "", onRetry = viewModel::loadDashboard, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
                    }
                }
            }
            when {
                firstLoad -> item { LoadingState("Cargando tus grupos…") }
                state.error == null && classes.isEmpty() -> item {
                    MessageState(
                        icon = AppIcons.List,
                        title = "No tienes grupos asignados",
                        body = "Cuando la universidad te asigne grupos para este periodo aparecerán aquí."
                    )
                }
            }
            items(classes, key = { it.first.grupoId + it.first.dia + it.first.horaInicio }) { (session, badge) ->
                ClassCard(session, badge) { onTakeAttendance(session.grupoId, session.materia) }
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
                            Text("Alumnos que llegaron al límite de faltas", color = MutedColor, fontSize = 13.sp)
                        }
                        Text("Ver", color = PrimaryColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Shortcut(
                        AppIcons.Document,
                        "Permisos",
                        when (state.pendingPermissions) {
                            0 -> "Sin pendientes"
                            1 -> "1 por revisar"
                            else -> "${state.pendingPermissions} por revisar"
                        },
                        onOpenPermissions,
                        Modifier.weight(1f)
                    )
                    Shortcut(AppIcons.Chart, "Reportes", "Por grupo", onOpenReports, Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Una tarjeta por grupo: si tiene clase hoy, la de hoy; si no, su próxima sesión de la
 * semana. Hoy primero por hora y luego el resto por cercanía.
 */
private fun orderedForToday(sessions: List<ClassSession>, todayIndex: Int): List<Pair<ClassSession, String>> {
    val distance = { s: ClassSession -> ((s.dayIndex ?: 7) - todayIndex + 7) % 7 }
    val order = compareBy<ClassSession>({ if (it.dayIndex == null) 8 else distance(it) }, { parseTime(it.horaInicio) })
    return sessions
        .groupBy { it.grupoId }
        .map { (_, blocks) -> blocks.minWith(order) }
        .sortedWith(order)
        .map { session ->
            val badge = when (session.dayIndex) {
                null -> "GRUPO"
                todayIndex -> "HOY"
                (todayIndex + 1) % 7 -> "MAÑANA"
                else -> DAY_NAMES[session.dayIndex!!].uppercase()
            }
            session to badge
        }
}

@Composable
private fun WeekCard(week: WeeklyAttendance?, todayIndex: Int) {
    val average = week?.average
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(HeroColor)
            .padding(20.dp)
    ) {
        Text("Asistencia de esta semana", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
        Spacer(Modifier.height(4.dp))
        if (average == null) {
            Text("Sin listas todavía", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text(
                "El promedio aparecerá cuando pases lista en tus grupos.",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${average.roundToInt()}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 40.sp)
                val previous = week.previousWeekAverage
                if (previous != null) {
                    val diff = (average - previous).roundToInt()
                    Spacer(Modifier.width(12.dp))
                    Pill(
                        when {
                            diff > 0 -> "+$diff% vs. semana pasada"
                            diff < 0 -> "$diff% vs. semana pasada"
                            else -> "Igual que la semana pasada"
                        },
                        Color.White.copy(alpha = 0.14f),
                        Color.White
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            WeekBars(week.byDay, todayIndex)
        }
    }
}

@Composable
private fun WeekBars(byDay: List<Double?>, todayIndex: Int) {
    val labels = listOf("L", "M", "M", "J", "V", "S", "D")
    val described = byDay.mapIndexedNotNull { index, value ->
        value?.let { "${DAY_NAMES[index]} ${it.roundToInt()}%" }
    }.joinToString(", ")
    Row(
        modifier = Modifier.fillMaxWidth().clearAndSetSemantics {
            contentDescription = "Asistencia por día: $described"
        },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEachIndexed { index, label ->
            val value = byDay.getOrNull(index)
            val isToday = index == todayIndex
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.BottomCenter) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Día sin listas: barra mínima tenue para que se note que no hay dato
                            .fillMaxHeight(((value ?: 4.0) / 100.0).toFloat().coerceIn(0.04f, 1f))
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when {
                                    value == null -> Color.White.copy(alpha = 0.10f)
                                    isToday -> PrimaryColor
                                    else -> Color.White.copy(alpha = 0.3f)
                                }
                            )
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    label,
                    color = if (isToday) Color.White else Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

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
private fun ClassCard(session: ClassSession, badge: String, onTake: () -> Unit) {
    val isToday = badge == "HOY"
    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Bloque de hora: deja la hora a primera vista
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
                    session.horaInicio.ifBlank { "--:--" },
                    color = if (isToday) Color.White else PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (session.horaFin.isNotBlank()) {
                    Text(
                        "a ${session.horaFin}",
                        color = if (isToday) Color.White.copy(alpha = 0.7f) else MutedColor,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Pill(badge, if (isToday) GreenSoft else SoftPurple, if (isToday) GreenText else PrimaryColor)
                Spacer(Modifier.height(6.dp))
                Text(
                    session.materia.ifBlank { "Clase" },
                    color = InkColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                val detail = listOf(session.aula, session.grupo).filter { it.isNotBlank() }.joinToString(" • ")
                if (detail.isNotBlank()) Text(detail, color = MutedColor, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
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
    }
}
