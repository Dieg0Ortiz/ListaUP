package com.example.myapplication.presentation.ui.dashboard.professor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.HeroColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AvatarCircle
import com.example.myapplication.presentation.ui.design.DashClass
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.SampleData
import com.example.myapplication.presentation.ui.design.SectionHeader
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.ProfessorDashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

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
    val classes = state.todayClasses.map {
        DashClass(
            materia = it.materia.ifBlank { "Clase" },
            aula = it.aula.ifBlank { "Aula" },
            grupo = it.grupo.ifBlank { "Grupo" },
            inicio = it.horaInicio.ifBlank { "--:--" },
            fin = it.horaFin.ifBlank { "" },
            grupoId = it.grupoId,
            badge = "HOY",
            live = false
        )
    }.ifEmpty { SampleData.professorClasses }
    val percent = if (state.todayClasses.isEmpty()) 92 else state.weeklyAttendance.toInt()
    val name = state.user?.name?.substringAfterLast(" ")?.ifBlank { state.user?.name } ?: "García"

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCircle("PG")
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Domingo, 4 de Octubre", color = MutedColor, fontSize = 12.sp)
                    Text("¡Hola, Prof. $name!", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Box(Modifier.clickable(onClick = onOpenNotifications).padding(8.dp)) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor)
                            .align(Alignment.TopEnd)
                    )
                    Text("Avisos", color = PrimaryColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(HeroColor)
                    .padding(20.dp)
            ) {
                Column {
                    Text("Asistencia Promedio Semanal", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$percent%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 40.sp)
                        Spacer(Modifier.width(12.dp))
                        Pill("+3%", PrimaryColor, Color.White)
                    }
                    Spacer(Modifier.height(16.dp))
                    WeekBars()
                }
            }
        }
        item {
            Spacer(Modifier.height(22.dp))
            Box(Modifier.padding(horizontal = 20.dp)) {
                SectionHeader("Clases de Hoy", "Ver Horario", onOpenSchedule)
            }
            Spacer(Modifier.height(12.dp))
        }
        items(classes) { session ->
            ClassCard(session) { onTakeAttendance(session.grupoId, session.materia) }
            Spacer(Modifier.height(12.dp))
        }
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Alertas críticas", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(12.dp))
            WhiteCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = onOpenAlerts
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarCircle("CM", background = RoseSoft, content = RoseText)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Carlos Mendoza", fontWeight = FontWeight.Bold, color = InkColor)
                        Text("23% Inasistencias (Límite 20%)", color = RoseText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text("Contactar", color = PrimaryColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Shortcut("Permisos", "Pendientes", onOpenPermissions, Modifier.weight(1f))
                Shortcut("Reportes", "Mensuales", onOpenReports, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Shortcut(title: String, subtitle: String, onClick: () -> Unit, modifier: Modifier) {
    WhiteCard(modifier = modifier, onClick = onClick) {
        Text(title, color = InkColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(subtitle, color = MutedColor, fontSize = 12.sp)
    }
}

@Composable
private fun WeekBars() {
    val heights = listOf(0.45f, 0.62f, 0.7f, 0.55f, 0.95f, 0.5f, 0.58f)
    Row(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        heights.forEachIndexed { index, fraction ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(fraction)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (index == 4) PrimaryColor else Color.White.copy(alpha = 0.22f))
            )
        }
    }
}

@Composable
private fun ClassCard(session: DashClass, onTake: () -> Unit) {
    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row {
            Column(Modifier.weight(1f)) {
                Pill(
                    session.badge,
                    if (session.live) GreenText.copy(alpha = 0.12f) else SoftPurple,
                    if (session.live) GreenText else PrimaryColor
                )
                Spacer(Modifier.height(8.dp))
                Text("${session.inicio} - ${session.fin}", color = MutedColor, fontSize = 12.sp)
                Text(session.materia, color = InkColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("${session.aula} • ${session.grupo}", color = MutedColor, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                if (session.live) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryColor)
                            .clickable(onClick = onTake)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text("Pasar lista", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                } else {
                    Text("Próximamente", color = MutedColor, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (session.live) HeroColor else Color(0xFFE7E4EC))
            ) {
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .size(width = 36.dp, height = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (session.live) PrimaryColor else Color(0xFFC8C4D0))
                )
            }
        }
    }
}
