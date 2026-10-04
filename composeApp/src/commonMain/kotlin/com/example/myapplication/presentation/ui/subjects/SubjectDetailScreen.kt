package com.example.myapplication.presentation.ui.subjects

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.PrimaryLight
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppDialog
import com.example.myapplication.presentation.ui.design.AvatarCircle
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.ProgressRing
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.ui.design.WhiteCard

@Composable
fun SubjectDetailScreen(
    materia: String,
    docente: String,
    percent: Int,
    aula: String,
    onBack: () -> Unit
) {
    var messageOpen by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }
    val sesiones = 17
    val asistencias = (percent * sesiones / 100).coerceAtMost(sesiones)
    val retardos = if (percent < 90) 2 else 0

    Box(Modifier.fillMaxSize().background(BackgroundColor)) {
    Column(Modifier.fillMaxSize()) {
    TopBar("Detalle de Materia", onBack = onBack)
    LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(listOf(PrimaryColor, PrimaryLight)))
                    .padding(20.dp)
            ) {
                Pill("INGENIERÍA DE SOFTWARE", Color.White.copy(alpha = 0.18f), Color.White)
                Spacer(Modifier.height(10.dp))
                Text(materia, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Text(docente.ifBlank { "Docente" }, color = Color.White.copy(alpha = 0.85f))
            }
        }
        item {
            Spacer(Modifier.height(14.dp))
            WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Asistencia Total", fontWeight = FontWeight.Bold, color = InkColor)
                        Text("Ciclo Escolar 2024-1", color = MutedColor, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Stat("$percent%", "PROMEDIO")
                            Spacer(Modifier.width(16.dp))
                            Stat("$asistencias/$sesiones", "SESIONES")
                        }
                    }
                    ProgressRing(percent, color = PrimaryColor, track = SoftPurple)
                }
            }
        }
        item {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniStat("✓", "ASISTENCIAS", asistencias.toString(), Modifier.weight(1f))
                MiniStat("⏱", "RETARDOS", retardos.toString(), Modifier.weight(1f))
            }
        }
        if (percent < 80) {
            item {
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(RoseSoft)
                        .padding(16.dp)
                ) {
                    Text("⚠  Alerta de Inasistencias", color = RoseText, fontWeight = FontWeight.Bold)
                    Text(
                        "Estás a 1 falta de perder el derecho a examen final. Se recomienda precaución.",
                        color = InkColor,
                        fontSize = 13.sp
                    )
                }
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            Text("Historial de Clases", modifier = Modifier.padding(horizontal = 20.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
            Spacer(Modifier.height(8.dp))
            HistoryRow("Octubre 24, 2023", "TEORÍA DE GRAFOS", "PRESENTE", GreenSoft, GreenText)
            HistoryRow("Octubre 22, 2023", "ÁRBOLES BINARIOS", "RETARDO", AmberSoft, Color(0xFFB45309))
            HistoryRow("Octubre 17, 2023", "RECURSIVIDAD", "FALTA", RoseSoft, RoseText)
        }
        item {
            Spacer(Modifier.height(8.dp))
            Text("Información del Docente", modifier = Modifier.padding(horizontal = 20.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
            Spacer(Modifier.height(8.dp))
            WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarCircle(docente.take(1).ifBlank { "D" })
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(docente.ifBlank { "Docente" }, fontWeight = FontWeight.Bold, color = InkColor)
                        Text("INVESTIGADOR NIVEL II", color = PrimaryColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("✉  contacto@institucional.edu.mx", color = MutedColor, fontSize = 13.sp)
                Text("📍  $aula", color = MutedColor, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                PrimaryButton(if (sent) "Mensaje enviado" else "ENVIAR MENSAJE", onClick = { messageOpen = true }, enabled = !sent)
            }
        }
    }
    }
    if (messageOpen) {
        AppDialog(
            title = "Mensaje para $docente",
            body = "",
            confirm = "Enviar",
            onConfirm = {
                if (message.isNotBlank()) {
                    sent = true
                    messageOpen = false
                }
            },
            onDismiss = { messageOpen = false }
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Escribe tu mensaje") }
            )
        }
    }
    }
}

@Composable
private fun Stat(value: String, label: String) {
    Column {
        Text(value, color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(label, color = MutedColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MiniStat(icon: String, label: String, value: String, modifier: Modifier) {
    WhiteCard(modifier) {
        Text(icon, color = PrimaryColor)
        Text(label, color = MutedColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(value, color = InkColor, fontWeight = FontWeight.Bold, fontSize = 22.sp)
    }
}

@Composable
private fun HistoryRow(date: String, topic: String, status: String, bg: Color, fg: Color) {
    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(CircleShape).background(SoftPurple), contentAlignment = Alignment.Center) {
                Text("📅", fontSize = 14.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(date, fontWeight = FontWeight.Bold, color = InkColor, fontSize = 14.sp)
                Text(topic, color = MutedColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Pill(status, bg, fg)
        }
    }
}
