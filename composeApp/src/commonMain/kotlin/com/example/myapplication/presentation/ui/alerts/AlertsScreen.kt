package com.example.myapplication.presentation.ui.alerts

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.Alert
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.IconBubble
import com.example.myapplication.presentation.ui.design.InlineError
import com.example.myapplication.presentation.ui.design.LoadingState
import com.example.myapplication.presentation.ui.design.MessageState
import com.example.myapplication.presentation.ui.design.RefreshBox
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.ui.permissions.PermissionRow
import com.example.myapplication.presentation.viewmodel.AlertsViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel = koinViewModel(),
    onBack: (() -> Unit)? = null,
    onReview: (Int) -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    // 0 = alertas de asistencia, 1 = solicitudes por revisar
    var tab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadAlerts()
    }

    Column(Modifier.fillMaxSize().background(BackgroundColor)) {
        TopBar("Alertas y solicitudes", onBack = onBack)
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(4.dp)
        ) {
            listOf("Asistencia (${state.alerts.size})", "Solicitudes (${state.pendingRequests.size})").forEachIndexed { index, label ->
                val selected = tab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) SoftPurple else Color.Transparent)
                        .selectable(selected = selected, role = Role.Tab) { tab = index }
                        .heightIn(min = 44.dp)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = if (selected) PrimaryColor else MutedColor, fontWeight = FontWeight.Bold)
                }
            }
        }
        RefreshBox(refreshing = state.isLoading, onRefresh = { viewModel.loadAlerts() }, modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (tab == 0) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Alumnos con ${state.umbralFaltas} faltas o más",
                                color = MutedColor,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = onOpenSettings) {
                                Text("Cambiar", color = PrimaryColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (state.error != null) {
                        item { InlineError(state.error ?: "", onRetry = { viewModel.loadAlerts() }) }
                    }
                    when {
                        state.isLoading && state.alerts.isEmpty() -> item { LoadingState("Revisando la asistencia de tus grupos…") }
                        state.alerts.isEmpty() && state.error == null -> item {
                            MessageState(
                                icon = AppIcons.CheckCircle,
                                title = "Sin alumnos en riesgo",
                                body = "Ningún alumno de tus grupos llega al límite de faltas."
                            )
                        }
                    }
                    items(state.alerts, key = { it.id }) { alert -> AlertCard(alert) }
                } else {
                    if (state.requestsError != null) {
                        item { InlineError(state.requestsError ?: "", onRetry = { viewModel.loadAlerts() }) }
                    }
                    when {
                        state.isLoading && state.pendingRequests.isEmpty() -> item { LoadingState("Cargando solicitudes…") }
                        state.pendingRequests.isEmpty() && state.requestsError == null -> item {
                            MessageState(
                                icon = AppIcons.Document,
                                title = "Nada por revisar",
                                body = "Las solicitudes de permiso de tus tutorados aparecerán aquí."
                            )
                        }
                    }
                    items(state.pendingRequests, key = { it.id }) { permission ->
                        PermissionRow(permission, showStudent = true) { onReview(permission.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: Alert) {
    val percent = alert.absencePercentage.roundToInt().coerceIn(0, 100)
    WhiteCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(AppIcons.Warning, RoseSoft, size = 40.dp, tint = RoseText)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(alert.studentName, fontWeight = FontWeight.Bold, color = InkColor)
                val detail = listOf(alert.matricula, alert.materia).filter { it.isNotBlank() }.joinToString(" • ")
                if (detail.isNotBlank()) Text(detail, color = MutedColor, fontSize = 13.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${alert.absences}", color = RoseText, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(if (alert.absences == 1) "falta" else "faltas", color = MutedColor, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(6.dp)),
            color = RoseText,
            trackColor = RoseSoft,
            drawStopIndicator = {}
        )
        Spacer(Modifier.height(6.dp))
        Text(
            buildString {
                append("$percent% de inasistencia")
                if (alert.lates > 0) append(" • ${alert.lates} ${if (alert.lates == 1) "retardo" else "retardos"}")
            },
            color = MutedColor,
            fontSize = 12.sp
        )
    }
}
