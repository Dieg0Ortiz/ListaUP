package com.example.myapplication.presentation.ui.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.DAY_NAMES
import com.example.myapplication.core.initialsOf
import com.example.myapplication.core.navigation.Screen
import com.example.myapplication.domain.model.Group
import com.example.myapplication.domain.model.toSessions
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.HeroColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.AvatarCircle
import com.example.myapplication.presentation.ui.design.InfoLine
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.SecondaryButton
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.ui.design.WhiteCard

@Composable
fun SubjectDetailScreen(
    detail: Screen.SubjectDetail,
    onBack: () -> Unit,
    onRequestPermission: () -> Unit
) {
    val sessions = Group(
        grupoId = detail.grupoId,
        nombre = detail.grupo,
        materia = detail.materia,
        aula = detail.aula,
        horario = detail.horario,
        docenteNombre = detail.docente
    ).toSessions().filter { it.horaInicio.isNotBlank() }

    Column(Modifier.fillMaxSize().background(BackgroundColor)) {
        TopBar("Materia", onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(HeroColor)
                    .padding(20.dp)
            ) {
                if (detail.grupo.isNotBlank()) {
                    Pill(detail.grupo.uppercase(), Color.White.copy(alpha = 0.18f), Color.White)
                    Spacer(Modifier.height(10.dp))
                }
                Text(detail.materia, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }

            Spacer(Modifier.height(16.dp))
            WhiteCard {
                if (detail.aula.isNotBlank()) InfoLine("Aula", detail.aula)
                InfoLine("Grupo", detail.grupo.ifBlank { detail.grupoId })
            }

            Spacer(Modifier.height(16.dp))
            Text("Horario", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(8.dp))
            WhiteCard {
                if (sessions.isEmpty()) {
                    Text(detail.horario.ifBlank { "Sin horario registrado en el SIU." }, color = MutedColor, fontSize = 14.sp)
                } else {
                    sessions.forEach { session ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(AppIcons.Clock, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                session.dayIndex?.let { DAY_NAMES[it] } ?: session.dia.ifBlank { "Horario" },
                                color = InkColor,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                listOf(session.horaInicio, session.horaFin).filter { it.isNotBlank() }.joinToString(" – "),
                                color = InkColor
                            )
                        }
                    }
                }
            }

            if (detail.docente.isNotBlank()) {
                Spacer(Modifier.height(16.dp))
                Text("Docente", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(8.dp))
                WhiteCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AvatarCircle(initialsOf(detail.docente))
                        Spacer(Modifier.width(12.dp))
                        Text(detail.docente, fontWeight = FontWeight.Bold, color = InkColor)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(SoftPurple).padding(14.dp)
            ) {
                Icon(AppIcons.Info, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Tu historial de asistencia por materia aparecerá aquí cuando la universidad habilite su consulta.",
                    color = InkColor,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(20.dp))
            SecondaryButton("Solicitar permiso", onClick = onRequestPermission)
            Spacer(Modifier.height(28.dp))
        }
    }
}
