package com.example.myapplication.presentation.ui.dashboard.professor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.DashClass
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.SampleData
import com.example.myapplication.presentation.viewmodel.ProfessorDashboardViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.example.myapplication.presentation.ui.design.WhiteCard

@Composable
fun ProfessorClassesScreen(
    viewModel: ProfessorDashboardViewModel = koinViewModel(),
    onTakeAttendance: (String, String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val classes = state.todayClasses.map {
        DashClass(it.materia, it.aula, it.grupo, it.horaInicio, it.horaFin, it.grupoId, "HOY", true)
    }.ifEmpty { SampleData.professorClasses }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Asistencia", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 26.sp)
            Text("Elige una clase para pasar lista.", color = MutedColor)
        }
        items(classes) { session ->
            WhiteCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Pill(if (session.live) "LISTA ABIERTA" else session.badge, if (session.live) GreenSoft else SoftPurple, if (session.live) GreenText else Color(0xFF520096))
                        Spacer(Modifier.height(8.dp))
                        Text(session.materia, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
                        Text("${session.inicio} - ${session.fin} • ${session.aula}", color = MutedColor, fontSize = 13.sp)
                        Text(session.grupo, color = MutedColor, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                PrimaryButton("Pasar lista", onClick = { onTakeAttendance(session.grupoId, session.materia) })
            }
        }
    }
}
