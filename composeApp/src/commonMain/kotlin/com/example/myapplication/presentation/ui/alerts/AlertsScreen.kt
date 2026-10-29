package com.example.myapplication.presentation.ui.alerts

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.Alert
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.FieldColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.IconBubble
import com.example.myapplication.presentation.ui.design.PermissionRequestCard
import com.example.myapplication.presentation.ui.design.SampleData
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.AlertsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel = koinViewModel(),
    onBack: (() -> Unit)? = null,
    onReview: (PermissionRequestCard) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var tab by remember { mutableStateOf("Todas") }

    LaunchedEffect(Unit) {
        viewModel.loadAlerts()
    }

    val alerts = state.alerts.ifEmpty {
        listOf(
            Alert(id = "a1", studentName = "Maria Garcia", materia = "Matemáticas", absencePercentage = 20.0),
            Alert(id = "a2", studentName = "Juan Pérez", materia = "Física", absencePercentage = 15.0)
        )
    }

    Column(Modifier.fillMaxSize().background(BackgroundColor)) {
        TopBar("Centro de notificaciones", onBack = onBack)
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(4.dp)
        ) {
            listOf("Todas", "Alertas", "Solicitudes").forEach { label ->
                val selected = tab == label
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) SoftPurple else Color.Transparent)
                        .clickable { tab = label }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = if (selected) PrimaryColor else MutedColor, fontWeight = FontWeight.Bold)
                }
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (tab != "Solicitudes") {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("ALERTAS DE ASISTENCIA", color = MutedColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            "2 NUEVAS",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clip(RoundedCornerShape(50)).background(RoseText).padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                items(alerts) { alert ->
                    AlertCard(alert)
                }
            }
            if (tab != "Alertas") {
                item {
                    Text("SOLICITUDES DE PERMISO", color = MutedColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                items(SampleData.permissionRequests.take(2)) { request ->
                    RequestCard(request) { onReview(request) }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: Alert) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
    ) {
        Box(Modifier.width(6.dp).height(150.dp).background(RoseText))
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBubble("!", RoseSoft, size = 36.dp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(alert.studentName, fontWeight = FontWeight.Bold, color = InkColor)
                    Text(
                        "Ha alcanzado el ${alert.absencePercentage.toInt()}% de inasistencias en ${alert.materia}.",
                        color = InkColor,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(FieldColor)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Ver Reporte de Asistencia", color = InkColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun RequestCard(request: PermissionRequestCard, onReview: () -> Unit) {
    WhiteCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(request.icon, SoftPurple, size = 40.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(request.name, fontWeight = FontWeight.Bold, color = InkColor)
                Text(request.detail, color = MutedColor, fontSize = 13.sp)
            }
            Text(request.time, color = MutedColor, fontSize = 12.sp)
        }
        Spacer(Modifier.height(12.dp))
        Row {
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PrimaryColor)
                    .clickable(onClick = onReview)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Revisar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
