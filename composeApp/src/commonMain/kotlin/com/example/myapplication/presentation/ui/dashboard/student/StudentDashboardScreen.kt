package com.example.myapplication.presentation.ui.dashboard.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.DAY_NAMES
import com.example.myapplication.core.greeting
import com.example.myapplication.core.initialsOf
import com.example.myapplication.core.longLabel
import com.example.myapplication.core.now
import com.example.myapplication.core.parseTime
import com.example.myapplication.core.today
import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.HeroColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.AvatarCircle
import com.example.myapplication.presentation.ui.design.InlineError
import com.example.myapplication.presentation.ui.design.LoadingState
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.RefreshBox
import com.example.myapplication.presentation.ui.design.SectionHeader
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.ui.permissions.PermissionRow
import com.example.myapplication.presentation.viewmodel.StudentDashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentDashboardScreen(
    viewModel: StudentDashboardViewModel = koinViewModel(),
    onOpenNotifications: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenPermissions: () -> Unit,
    onOpenSubjects: () -> Unit,
    onOpenSchedule: () -> Unit,
    onOpenPermission: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val name = state.user?.name.orEmpty()
    val today = today()
    val todayIndex = today.dayOfWeek.ordinal
    val todayClasses = state.schedule
        .filter { it.dayIndex == todayIndex }
        .sortedBy { parseTime(it.horaInicio) }
    val nextClass = upcoming(state.schedule)
    val firstLoad = state.isLoading && state.schedule.isEmpty() && state.groups.isEmpty()

    RefreshBox(
        refreshing = state.isLoading && !firstLoad,
        onRefresh = viewModel::loadDashboard,
        modifier = Modifier.fillMaxSize().background(BackgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarCircle(initialsOf(name))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${greeting()},", color = MutedColor, fontSize = 13.sp)
                        Text(
                            name.ifBlank { "Alumno" },
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
            if (state.error != null) {
                item {
                    InlineError(state.error ?: "", onRetry = viewModel::loadDashboard, modifier = Modifier.padding(horizontal = 20.dp))
                }
            }
            item {
                NextClassCard(nextClass, firstLoad, onOpenSchedule)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickTile(AppIcons.Document, "Pedir permiso", onRequestPermission, Modifier.weight(1f))
                    QuickTile(AppIcons.List, "Materias", onOpenSubjects, Modifier.weight(1f))
                    QuickTile(AppIcons.Calendar, "Horario", onOpenSchedule, Modifier.weight(1f))
                }
            }
            item {
                Box(Modifier.padding(start = 20.dp, end = 8.dp)) {
                    SectionHeader("Hoy, ${DAY_NAMES[todayIndex].lowercase()}", "Ver semana", onOpenSchedule)
                }
            }
            when {
                firstLoad -> item { LoadingState("Cargando tu horario…") }
                todayClasses.isEmpty() -> item {
                    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text("No tienes clases hoy", color = InkColor, fontWeight = FontWeight.Bold)
                        Text(today.longLabel(), color = MutedColor, fontSize = 13.sp)
                    }
                }
                else -> items(todayClasses, key = { it.id + it.dia + it.horaInicio }) { session ->
                    TodayClassRow(session)
                }
            }
            item {
                Box(Modifier.padding(start = 20.dp, end = 8.dp)) {
                    SectionHeader("Mis permisos", if (state.permissions.isNotEmpty()) "Ver todos" else null, onOpenPermissions)
                }
            }
            if (state.permissions.isEmpty() && !firstLoad) {
                item {
                    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp), onClick = onRequestPermission) {
                        Text("Aún no has pedido permisos", color = InkColor, fontWeight = FontWeight.Bold)
                        Text(
                            "Si vas a faltar por un motivo justificado, solicítalo aquí y tu tutor lo revisará.",
                            color = MutedColor,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            items(state.permissions.take(3), key = { it.id }) { permission ->
                PermissionRow(permission, showStudent = false, modifier = Modifier.padding(horizontal = 20.dp)) {
                    onOpenPermission(permission.id)
                }
            }
        }
    }
}

/** La clase en curso o la siguiente de hoy; si ya no hay, la primera del próximo día con clases. */
private fun upcoming(schedule: List<ClassSession>): Pair<ClassSession, String>? {
    if (schedule.isEmpty()) return null
    val nowTime = now().time
    val todayIndex = today().dayOfWeek.ordinal
    for (offset in 0..6) {
        val day = (todayIndex + offset) % 7
        val candidates = schedule.filter { it.dayIndex == day }.sortedBy { parseTime(it.horaInicio) }
        val pick = if (offset == 0) {
            candidates.firstOrNull { (parseTime(it.horaFin) ?: parseTime(it.horaInicio))?.let { end -> end > nowTime } == true }
        } else {
            candidates.firstOrNull()
        }
        if (pick != null) {
            val start = parseTime(pick.horaInicio)
            val label = when {
                offset == 0 && start != null && start <= nowTime -> "EN CURSO"
                offset == 0 -> "HOY"
                offset == 1 -> "MAÑANA"
                else -> DAY_NAMES[day].uppercase()
            }
            return pick to label
        }
    }
    return null
}

@Composable
private fun NextClassCard(next: Pair<ClassSession, String>?, loading: Boolean, onOpenSchedule: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(HeroColor)
            .clickable(onClick = onOpenSchedule)
            .padding(20.dp)
    ) {
        Text("Tu próxima clase", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
        Spacer(Modifier.height(6.dp))
        when {
            next != null -> {
                val (session, label) = next
                Pill(label, Color.White.copy(alpha = 0.16f), Color.White)
                Spacer(Modifier.height(8.dp))
                Text(
                    session.materia,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                val time = listOf(session.horaInicio, session.horaFin).filter { it.isNotBlank() }.joinToString(" – ")
                val place = listOf(time, session.aula).filter { it.isNotBlank() }.joinToString(" • ")
                if (place.isNotBlank()) Text(place, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                if (session.docenteNombre.isNotBlank()) {
                    Text(session.docenteNombre, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                }
            }
            loading -> Text("Cargando…", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            else -> {
                Text("Sin clases registradas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Tu horario aparecerá aquí cuando esté cargado en el SIU.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun TodayClassRow(session: ClassSession) {
    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier
                    .size(width = 64.dp, height = 56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftPurple),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(session.horaInicio.ifBlank { "--:--" }, color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                if (session.horaFin.isNotBlank()) Text(session.horaFin, color = MutedColor, fontSize = 12.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(session.materia, fontWeight = FontWeight.Bold, color = InkColor, maxLines = 2, overflow = TextOverflow.Ellipsis)
                val detail = listOf(session.aula, session.docenteNombre).filter { it.isNotBlank() }.joinToString(" • ")
                if (detail.isNotBlank()) Text(detail, color = MutedColor, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun QuickTile(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) { }
            .heightIn(min = 88.dp)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(8.dp))
        Text(label, color = InkColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}
