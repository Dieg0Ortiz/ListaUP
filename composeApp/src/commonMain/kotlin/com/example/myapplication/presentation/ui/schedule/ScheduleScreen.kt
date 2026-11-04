package com.example.myapplication.presentation.ui.schedule

import androidx.compose.foundation.background
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.DAY_NAMES
import com.example.myapplication.core.DAY_SHORT
import com.example.myapplication.core.MONTH_NAMES
import com.example.myapplication.core.now
import com.example.myapplication.core.parseTime
import com.example.myapplication.core.plusDays
import com.example.myapplication.core.today
import com.example.myapplication.core.weekStart
import com.example.myapplication.domain.model.ClassSession
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.AmberText
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.FieldColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.HeroColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.ErrorState
import com.example.myapplication.presentation.ui.design.InlineError
import com.example.myapplication.presentation.ui.design.LoadingState
import com.example.myapplication.presentation.ui.design.MessageState
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.RefreshBox
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.ScheduleViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScheduleScreen(
    isProfessor: Boolean,
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val today = today()
    val monday = weekStart(today)
    val todayIndex = today.dayOfWeek.ordinal
    // Domingo no hay clases: se abre en lunes. El sábado solo aparece si hay clases ese día
    val showSaturday = state.sessions.any { it.dayIndex == 5 }
    val dayCount = if (showSaturday) 6 else 5
    var selected by remember { mutableIntStateOf(todayIndex.coerceAtMost(dayCount - 1)) }
    val selectedDate = monday.plusDays(selected)
    val dayClasses = state.sessions
        .filter { it.dayIndex == selected }
        .sortedBy { parseTime(it.horaInicio) }
    val noDayInfo = state.sessions.isNotEmpty() && state.sessions.all { it.dayIndex == null }
    val firstLoad = state.isLoading && state.sessions.isEmpty()

    RefreshBox(
        refreshing = state.isLoading && !firstLoad,
        onRefresh = viewModel::load,
        modifier = Modifier.fillMaxSize().background(BackgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(HeroColor)
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (isProfessor) "Horario docente" else "Horario semanal",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${MONTH_NAMES[selectedDate.month.ordinal].uppercase()} ${selectedDate.year}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        (0 until dayCount).forEach { index ->
                            val date = monday.plusDays(index)
                            val active = index == selected
                            val isToday = index == todayIndex
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (active) Color.White else Color.Transparent)
                                    .selectable(selected = active, role = Role.Tab) { selected = index }
                                    .semantics { contentDescription = "${DAY_NAMES[index]} ${date.day}" + if (isToday) ", hoy" else "" }
                                    .heightIn(min = 48.dp)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(DAY_SHORT[index], color = if (active) PrimaryColor else Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("${date.day}", color = if (active) PrimaryColor else Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Box(
                                    Modifier
                                        .padding(top = 3.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(if (isToday) (if (active) PrimaryColor else Color.White) else Color.Transparent)
                                )
                            }
                        }
                    }
                }
            }
            when {
                firstLoad -> item { LoadingState("Cargando horario…") }
                state.error != null && state.sessions.isEmpty() -> item { ErrorState(state.error ?: "", onRetry = viewModel::load) }
                state.sessions.isEmpty() -> item {
                    MessageState(
                        icon = AppIcons.Calendar,
                        title = "Sin horario registrado"
                    )
                }
                noDayInfo -> items(state.sessions, key = { it.id + it.horaInicio }) { session ->
                    // El SIU no mandó días: se listan todas las clases sin agrupar
                    ScheduleCard(session, status = null)
                }
                dayClasses.isEmpty() -> item {
                    MessageState(
                        icon = AppIcons.Calendar,
                        title = "Sin clases el ${DAY_NAMES[selected].lowercase()}",
                        body = ""
                    )
                }
                else -> items(dayClasses, key = { it.id + it.dia + it.horaInicio }) { session ->
                    ScheduleCard(session, status = if (selected == todayIndex) statusOf(session) else null)
                }
            }
            if (state.error != null && state.sessions.isNotEmpty()) {
                item { InlineError(state.error ?: "", onRetry = viewModel::load, modifier = Modifier.padding(horizontal = 20.dp)) }
            }
        }
    }
}

private enum class LiveStatus(val label: String) { DONE("FINALIZADA"), LIVE("EN CURSO"), NEXT("PRÓXIMA") }

private fun statusOf(session: ClassSession): LiveStatus? {
    val start = parseTime(session.horaInicio) ?: return null
    val end = parseTime(session.horaFin) ?: return null
    val current = now().time
    return when {
        current >= end -> LiveStatus.DONE
        current >= start -> LiveStatus.LIVE
        else -> LiveStatus.NEXT
    }
}

@Composable
private fun ScheduleCard(item: ClassSession, status: LiveStatus?) {
    val live = status == LiveStatus.LIVE
    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(52.dp)) {
                Text(item.horaInicio.ifBlank { "--:--" }, color = InkColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Box(Modifier.padding(vertical = 4.dp).width(2.dp).height(24.dp).background(if (live) PrimaryColor else SoftPurple))
                Text(item.horaFin, color = MutedColor, fontSize = 12.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.materia,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        color = InkColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (status != null) {
                        val (bg, fg) = when (status) {
                            LiveStatus.DONE -> FieldColor to MutedColor
                            LiveStatus.LIVE -> GreenSoft to GreenText
                            LiveStatus.NEXT -> AmberSoft to AmberText
                        }
                        Spacer(Modifier.width(8.dp))
                        Pill(status.label, bg, fg)
                    }
                }
                if (item.docenteNombre.isNotBlank()) Text(item.docenteNombre, color = MutedColor, fontSize = 13.sp)
                val place = listOf(item.aula, item.grupo).filter { it.isNotBlank() }.joinToString("  ·  ")
                if (place.isNotBlank()) Text(place, color = MutedColor, fontSize = 12.sp)
            }
        }
    }
}
