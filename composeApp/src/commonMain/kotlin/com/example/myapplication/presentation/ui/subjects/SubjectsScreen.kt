package com.example.myapplication.presentation.ui.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.DangerColor
import com.example.myapplication.presentation.theme.FieldColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.ProgressRing
import com.example.myapplication.presentation.ui.design.SampleData
import com.example.myapplication.presentation.ui.design.SubjectCard
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.StudentDashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SubjectsScreen(
    viewModel: StudentDashboardViewModel = koinViewModel(),
    onOpenNotifications: () -> Unit,
    onOpenSubject: (SubjectCard) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("Todas") }
    val fromApi = state.groups.mapIndexed { index, group ->
        val sample = SampleData.subjects[index % SampleData.subjects.size]
        SubjectCard(group.materia, group.docenteNombre.ifBlank { sample.docente }, sample.percent, group.horario.ifBlank { sample.schedule }, sample.badge, sample.risk)
    }
    val source = fromApi.ifEmpty { SampleData.subjects }
    val visible = source.filter { card ->
        val text = query.trim().lowercase()
        val matches = text.isBlank() || card.materia.lowercase().contains(text) || card.docente.lowercase().contains(text)
        val area = when (filter) {
            "Ingeniería" -> !card.materia.contains("Ética")
            "Humanidades" -> card.materia.contains("Ética") || card.materia.contains("Álgebra")
            else -> true
        }
        matches && area
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.clip(RoundedCornerShape(16.dp)).background(PrimaryColor).padding(10.dp)
                ) { Text("🎓", color = Color.White) }
                Spacer(Modifier.width(10.dp))
                Text("Mis Materias", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = InkColor)
                Text("🔔", modifier = Modifier.clickable(onClick = onOpenNotifications), fontSize = 20.sp)
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar materias o profesores...", color = MutedColor) },
                leadingIcon = { Text("🔍") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Todas", "Ingeniería", "Humanidades", "Cierre").forEach { label ->
                    val selected = filter == label
                    Text(
                        label,
                        color = if (selected) Color.White else InkColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) PrimaryColor else Color.White)
                            .border(1.dp, if (selected) PrimaryColor else FieldColor, RoundedCornerShape(50))
                            .clickable { filter = label }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
        items(visible) { card ->
            SubjectRow(card) { onOpenSubject(card) }
        }
    }
}

@Composable
private fun SubjectRow(card: SubjectCard, onClick: () -> Unit) {
    val accent = if (card.risk) RoseText else PrimaryColor
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        Box(Modifier.width(6.dp).height(168.dp).background(accent))
        Column(Modifier.padding(16.dp).weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(card.materia, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
                Pill(
                    card.badge,
                    if (card.risk) RoseSoft else GreenSoft,
                    if (card.risk) RoseText else GreenText
                )
            }
            Text(card.docente, color = MutedColor, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressRing(
                    card.percent,
                    size = 64.dp,
                    color = if (card.risk) DangerColor else PrimaryColor,
                    track = if (card.risk) RoseSoft else SoftPurple
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("ASISTENCIA", color = MutedColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("⏱  ${card.schedule}", color = InkColor, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Ver Detalles", color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
