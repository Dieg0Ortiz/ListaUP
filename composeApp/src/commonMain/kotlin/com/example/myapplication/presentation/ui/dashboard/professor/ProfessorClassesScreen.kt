package com.example.myapplication.presentation.ui.dashboard.professor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.today
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.ErrorState
import com.example.myapplication.presentation.ui.design.InlineError
import com.example.myapplication.presentation.ui.design.LoadingState
import com.example.myapplication.presentation.ui.design.MessageState
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.RefreshBox
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.ProfessorDashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfessorClassesScreen(
    viewModel: ProfessorDashboardViewModel = koinViewModel(),
    onTakeAttendance: (String, String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val todayIndex = today().dayOfWeek.ordinal
    val todayGroups = state.sessions.filter { it.dayIndex == todayIndex }.map { it.grupoId }.toSet()
    // Primero los grupos con clase hoy
    val groups = state.groups.sortedByDescending { it.grupoId in todayGroups }
    val firstLoad = state.isLoading && state.groups.isEmpty()

    RefreshBox(
        refreshing = state.isLoading && !firstLoad,
        onRefresh = viewModel::loadDashboard,
        modifier = Modifier.fillMaxSize().background(BackgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Asistencia", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Text("Elige un grupo para pasar la lista de hoy.", color = MutedColor)
            }
            when {
                firstLoad -> item { LoadingState("Cargando tus grupos…") }
                state.error != null && state.groups.isEmpty() -> item { ErrorState(state.error ?: "", onRetry = viewModel::loadDashboard) }
                state.groups.isEmpty() -> item {
                    MessageState(
                        icon = AppIcons.List,
                        title = "No tienes grupos asignados",
                        body = "Cuando la universidad te asigne grupos para este periodo aparecerán aquí."
                    )
                }
            }
            if (state.error != null && state.groups.isNotEmpty()) {
                item { InlineError(state.error ?: "", onRetry = viewModel::loadDashboard) }
            }
            items(groups, key = { it.grupoId }) { group ->
                WhiteCard {
                    if (group.grupoId in todayGroups) {
                        Pill("CLASE HOY", GreenSoft, GreenText)
                        Spacer(Modifier.height(8.dp))
                    }
                    Text(group.materia, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    val detail = listOf(group.nombre, group.aula).filter { it.isNotBlank() }.joinToString(" • ")
                    if (detail.isNotBlank()) Text(detail, color = MutedColor, fontSize = 13.sp)
                    if (group.horario.isNotBlank()) Text(group.horario, color = MutedColor, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton("Pasar lista", onClick = { onTakeAttendance(group.grupoId, group.materia) })
                }
            }
        }
    }
}
