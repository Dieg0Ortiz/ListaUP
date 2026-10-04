package com.example.myapplication.presentation.ui.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.FieldColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AvatarCircle
import com.example.myapplication.presentation.ui.design.InfoLine
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.TopBar

@Composable
fun PermissionDetailScreen(
    studentName: String,
    studentId: String,
    career: String,
    motivo: String,
    start: String,
    end: String,
    days: String,
    status: String,
    canReview: Boolean,
    onBack: () -> Unit,
    onOpenDocument: () -> Unit
) {
    var decision by remember(studentName) { mutableStateOf(status) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item { TopBar("Detalles de la Solicitud", onBack = onBack) }
        item {
            Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle(studentName.take(1).ifBlank { "E" })
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(studentName, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = InkColor)
                    Text("ID: $studentId • $career", color = MutedColor, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    val (bg, fg) = when {
                        decision.contains("Aprob", true) -> GreenSoft to GreenText
                        decision.contains("Rechaz", true) -> RoseSoft to RoseText
                        else -> AmberSoft to Color(0xFFB45309)
                    }
                    Pill(decision, bg, fg)
                }
            }
        }
        item {
            Text("Información de la Solicitud", modifier = Modifier.padding(20.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
            Column(Modifier.padding(horizontal = 20.dp).clip(RoundedCornerShape(20.dp)).background(Color.White).padding(16.dp)) {
                InfoLine("Motivo", motivo)
                InfoLine("Fecha Inicio", start)
                InfoLine("Fecha Fin", end)
                InfoLine("Días Totales", days)
            }
        }
        item {
            Text("JUSTIFICACIÓN DEL ESTUDIANTE", modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp), color = MutedColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                "Solicito permiso para ausentarme de mis sesiones de laboratorio debido a un procedimiento programado. Adjunto el certificado y la nota de referencia.",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = InkColor,
                fontSize = 14.sp
            )
        }
        item {
            Text("Evidencia Adjunta", modifier = Modifier.padding(20.dp), fontWeight = FontWeight.Bold, color = InkColor)
            Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Evidence("Cert_Médico.pdf", onOpenDocument, Modifier.weight(1f))
                Evidence("Referencia.jpg", onOpenDocument, Modifier.weight(1f))
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, PrimaryColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .background(SoftPurple)
                    .padding(16.dp)
            ) {
                Text("TU SELLO DE APROBACIÓN DIGITAL", color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (decision.contains("Aprob", true)) "Sello aplicado" else "Dra. Elena Vance • Directora Académica",
                    color = InkColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Al aprobar, esta insignia queda registrada en la autorización.",
                    color = MutedColor,
                    fontSize = 12.sp
                )
            }
        }
        if (canReview && !decision.contains("Aprob", true) && !decision.contains("Rechaz", true)) {
            item {
                Spacer(Modifier.height(16.dp))
                Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, FieldColor, RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .clickable { decision = "Rechazado" }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("Rechazar", fontWeight = FontWeight.Bold, color = InkColor) }
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(PrimaryColor)
                            .clickable { decision = "Aprobado" }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("✓  Aprobar", fontWeight = FontWeight.Bold, color = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun Evidence(name: String, onClick: () -> Unit, modifier: Modifier) {
    Column(modifier.clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SoftPurple),
            contentAlignment = Alignment.Center
        ) { Text("📄", fontSize = 28.sp) }
        Spacer(Modifier.height(6.dp))
        Text(name, color = MutedColor, fontSize = 12.sp)
    }
}
