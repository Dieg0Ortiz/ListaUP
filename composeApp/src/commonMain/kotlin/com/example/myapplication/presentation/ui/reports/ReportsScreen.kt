package com.example.myapplication.presentation.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.GroupAttendanceStats
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.AmberText
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.HeroColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.ErrorState
import com.example.myapplication.presentation.ui.design.InlineError
import com.example.myapplication.presentation.ui.design.LoadingState
import com.example.myapplication.presentation.ui.design.MessageState
import com.example.myapplication.presentation.ui.design.RefreshBox
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.ReportsViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

// Por debajo de esto un grupo se marca en ámbar, y por debajo de RISK_RATE en rojo
private const val WARNING_RATE = 85.0
private const val RISK_RATE = 75.0

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = koinViewModel(),
    onOpenSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val firstLoad = state.isLoading && state.groups.isEmpty()
    val overall = state.overallRate

    RefreshBox(
        refreshing = state.isLoading && !firstLoad,
        onRefresh = viewModel::load,
        modifier = Modifier.fillMaxSize().background(BackgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Reportes", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = InkColor)
            }
            when {
                firstLoad -> item { LoadingState("Calculando asistencia…") }
                state.error != null && state.groups.isEmpty() -> item { ErrorState(state.error ?: "", onRetry = viewModel::load) }
                state.groups.isEmpty() -> item {
                    MessageState(
                        icon = AppIcons.Chart,
                        title = "Aún no hay listas registradas"
                    )
                }
                else -> {
                    if (state.error != null) item { InlineError(state.error ?: "", onRetry = viewModel::load) }
                    item {
                        Column(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(HeroColor).padding(20.dp)
                        ) {
                            Text("Asistencia promedio", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                            Text(
                                overall?.let { "${it.roundToInt()}%" } ?: "—",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp
                            )
                            Text(
                                "${state.groups.sumOf { it.sessions }} listas en ${state.groups.size} ${if (state.groups.size == 1) "grupo" else "grupos"}",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 13.sp
                            )
                        }
                    }
                    item {
                        Text("Por grupo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor, modifier = Modifier.padding(top = 8.dp))
                    }
                    items(state.groups, key = { it.grupoId }) { group -> GroupRow(group) }
                }
            }
            if (!firstLoad) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Alumnos en riesgo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor, modifier = Modifier.weight(1f))
                        TextButton(onClick = onOpenSettings) { Text("Umbral", color = PrimaryColor, fontWeight = FontWeight.Bold) }
                    }
                }
                if (state.atRisk.isEmpty()) {
                    item {
                        WhiteCard {
                            Text("Ningún alumno llega al límite de faltas.", color = MutedColor, fontSize = 14.sp)
                        }
                    }
                }
                items(state.atRisk, key = { it.id }) { alert ->
                    WhiteCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(alert.studentName, fontWeight = FontWeight.SemiBold, color = InkColor)
                                val detail = listOf(alert.matricula, alert.materia).filter { it.isNotBlank() }.joinToString(" • ")
                                if (detail.isNotBlank()) Text(detail, color = MutedColor, fontSize = 12.sp)
                            }
                            Text(
                                "${alert.absences} ${if (alert.absences == 1) "falta" else "faltas"}",
                                color = RoseText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupRow(group: GroupAttendanceStats) {
    val rate = group.attendanceRate
    val (bg, fg) = when {
        rate == null -> AmberSoft to MutedColor
        rate < RISK_RATE -> RoseSoft to RoseText
        rate < WARNING_RATE -> AmberSoft to AmberText
        else -> GreenSoft to GreenText
    }
    WhiteCard(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    group.materia.ifBlank { "Grupo ${group.grupoId}" },
                    fontWeight = FontWeight.Bold,
                    color = InkColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${group.sessions} ${if (group.sessions == 1) "lista" else "listas"} • Grupo ${group.grupoId}",
                    color = MutedColor,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(rate?.let { "${it.roundToInt()}%" } ?: "—", color = fg, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { ((rate ?: 0.0) / 100.0).toFloat() },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(6.dp)),
            color = fg,
            trackColor = bg,
            drawStopIndicator = {}
        )
    }
}
