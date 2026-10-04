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
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AvatarCircle
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.ProgressRing
import com.example.myapplication.presentation.ui.design.RecentClass
import com.example.myapplication.presentation.ui.design.SampleData
import com.example.myapplication.presentation.ui.design.SectionHeader
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.StudentDashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentDashboardScreen(
    viewModel: StudentDashboardViewModel = koinViewModel(),
    onOpenNotifications: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenSubjects: () -> Unit,
    onOpenSchedule: () -> Unit,
    onOpenSubject: (RecentClass) -> Unit,
    onOpenPermission: (String, String, Boolean) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val name = state.user?.name ?: "Alex Johnson"
    val percent = if (state.schedule.isEmpty() && state.groups.isEmpty()) 85 else state.attendancePercentage.toInt()
    val recent = if (state.schedule.isEmpty()) {
        SampleData.studentRecent
    } else {
        state.schedule.take(3).map {
            RecentClass(it.materia, "${it.horaInicio} • ${it.aula}", "📘", true, percent, it.docenteNombre)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCircle(name.take(1).uppercase())
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Buenos días,", color = MutedColor, fontSize = 13.sp)
                    Text(name, color = InkColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Box(Modifier.clickable(onClick = onOpenNotifications).padding(8.dp)) {
                    Text("🔔", fontSize = 22.sp)
                    Box(
                        Modifier.align(Alignment.TopEnd).size(8.dp).clip(CircleShape).background(RoseText)
                    )
                }
            }
        }
        item {
            WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressRing(percent, size = 92.dp)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Asistencia Total", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Semestre Otoño 2023", color = MutedColor, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Pill("✓  Vas por buen camino", GreenSoft, GreenText)
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickTile("＋", "Solicitar\nPermiso", onRequestPermission, Modifier.weight(1f))
                QuickTile("📅", "Reporte\nCompleto", onOpenSubjects, Modifier.weight(1f))
                QuickTile("🏫", "Horario", onOpenSchedule, Modifier.weight(1f))
            }
        }
        item {
            Box(Modifier.padding(horizontal = 20.dp)) {
                SectionHeader("Clases Recientes", "Ver Todo", onOpenSubjects)
            }
        }
        items(recent.size) { index ->
            val item = recent[index]
            WhiteCard(modifier = Modifier.padding(horizontal = 20.dp), onClick = { onOpenSubject(item) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(SoftPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item.icon, color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.materia, fontWeight = FontWeight.Bold, color = InkColor)
                        Text(item.detail, color = MutedColor, fontSize = 12.sp)
                    }
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(if (item.present) GreenSoft else RoseSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (item.present) "✓" else "✕", color = if (item.present) GreenText else RoseText, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Text(
                "Estado de Permisos",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                color = InkColor,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        item {
            PermissionStrip(
                accent = Color(0xFFF59E0B),
                icon = "✚",
                title = "Permiso Médico",
                subtitle = "Solicitado el 12 Oct",
                chip = "En Directorio",
                chipBg = Color(0xFFFFF6E8),
                chipFg = Color(0xFFB45309),
                id = "ID: #4029",
                onClick = { onOpenPermission("Alex Johnson", "Permiso Médico", false) }
            )
        }
        item {
            PermissionStrip(
                accent = GreenText,
                icon = "✦",
                title = "Evento Familiar",
                subtitle = "Solicitado el 20 Sep",
                chip = "Aprobado",
                chipBg = GreenSoft,
                chipFg = GreenText,
                id = "ID: #3901",
                onClick = { onOpenPermission("Alex Johnson", "Evento Familiar", false) }
            )
        }
    }
}

@Composable
private fun QuickTile(icon: String, label: String, onClick: () -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(42.dp).clip(CircleShape).background(SoftPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = PrimaryColor, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = InkColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 15.sp)
    }
}

@Composable
private fun PermissionStrip(
    accent: Color,
    icon: String,
    title: String,
    subtitle: String,
    chip: String,
    chipBg: Color,
    chipFg: Color,
    id: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        Box(Modifier.width(6.dp).height(92.dp).background(accent))
        Column(Modifier.padding(14.dp).weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, color = accent, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, color = InkColor, modifier = Modifier.weight(1f))
                Pill(chip, chipBg, chipFg)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(subtitle, color = MutedColor, fontSize = 12.sp)
                Text(id, color = MutedColor, fontSize = 12.sp)
            }
        }
    }
}
