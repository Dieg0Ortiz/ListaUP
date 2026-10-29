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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.FieldColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.IconBubble
import com.example.myapplication.presentation.ui.design.PermissionRequestCard
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.SampleData
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.ui.design.WhiteCard

@Composable
fun PermissionsScreen(
    isProfessor: Boolean,
    onReview: (PermissionRequestCard) -> Unit
) {
    if (isProfessor) {
        ProfessorPermissions(onReview)
    } else {
        StudentPermissionForm()
    }
}

@Composable
private fun ProfessorPermissions(onReview: (PermissionRequestCard) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Permisos", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = InkColor)
            Text("Solicitudes pendientes de revisión", color = MutedColor)
        }
        items(SampleData.permissionRequests) { request ->
            WhiteCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBubble(request.icon, SoftPurple)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(request.name, fontWeight = FontWeight.Bold, color = InkColor)
                        Text(request.detail, color = MutedColor, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                PrimaryButton("Revisar", onClick = { onReview(request) })
            }
        }
    }
}

@Composable
private fun StudentPermissionForm() {
    var day by remember { mutableIntStateOf(5) }
    var start by remember { mutableStateOf("09:00 AM") }
    var end by remember { mutableStateOf("05:00 PM") }
    var motivo by remember { mutableStateOf("") }
    var step by remember { mutableIntStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    val starts = listOf("08:00 AM", "09:00 AM", "10:00 AM", "12:00 PM")
    val ends = listOf("12:00 PM", "02:00 PM", "05:00 PM", "06:00 PM")

    Column(Modifier.fillMaxSize().background(BackgroundColor)) {
    TopBar("Solicitar Permiso")
    LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            Column(Modifier.padding(horizontal = 24.dp)) {
                StepItem("Solicitado", "Etapa actual", step >= 0, step == 0)
                StepItem("Revisión del Tutor", "", step >= 1, step == 1)
                StepItem("Aprobación del Director", "", step >= 2, step == 2)
            }
        }
        item {
            Text("Fecha del Permiso", modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
            WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("‹", color = PrimaryColor, fontSize = 22.sp, modifier = Modifier.clickable { })
                    Text("Octubre 2023", fontWeight = FontWeight.Bold, color = InkColor)
                    Text("›", color = PrimaryColor, fontSize = 22.sp, modifier = Modifier.clickable { })
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("D", "L", "M", "M", "J", "V", "S").forEach {
                        Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = MutedColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(6.dp))
                val cells = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31)
                cells.chunked(7).forEach { week ->
                    Row(Modifier.fillMaxWidth()) {
                        week.forEach { value ->
                            val selected = value == day
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) PrimaryColor else Color.Transparent)
                                    .clickable { day = value }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    value.toString(),
                                    color = if (selected) Color.White else InkColor,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
        item {
            Text("Rango de Horario", modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp), fontWeight = FontWeight.Bold, color = InkColor)
            Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TimeBox(start, Modifier.weight(1f)) {
                    start = starts[(starts.indexOf(start) + 1) % starts.size]
                }
                TimeBox(end, Modifier.weight(1f)) {
                    end = ends[(ends.indexOf(end) + 1) % ends.size]
                }
            }
        }
        item {
            Text("Motivo del Permiso", modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp), fontWeight = FontWeight.Bold, color = InkColor)
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it; error = null },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(120.dp),
                placeholder = { Text("Explica el motivo de tu ausencia...", color = MutedColor) },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            if (error != null) {
                Text(error ?: "", color = Rose, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp), fontSize = 13.sp)
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            PrimaryButton(
                if (step == 0) "Enviar Solicitud" else "Solicitud enviada",
                onClick = {
                    if (motivo.isBlank()) {
                        error = "Escribe el motivo para enviar la solicitud."
                    } else {
                        step = 1
                        error = null
                    }
                },
                enabled = step == 0,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                "Obligatorio para motivos médicos u oficiales (Máx 5MB)",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center,
                color = MutedColor,
                fontSize = 12.sp
            )
        }
    }
    }
}

@Composable
private fun StepItem(title: String, subtitle: String, done: Boolean, current: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
        Box(
            Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (done && current) PrimaryColor else Color.Transparent)
                .border(2.dp, if (done) PrimaryColor else FieldColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (done && !current) Text("✓", color = PrimaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = if (current) PrimaryColor else InkColor, fontWeight = FontWeight.Bold)
            if (subtitle.isNotBlank()) Text(subtitle, color = MutedColor, fontSize = 12.sp)
        }
    }
}

@Composable
private fun TimeBox(value: String, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, FieldColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(value, fontWeight = FontWeight.Bold, color = InkColor, modifier = Modifier.weight(1f))
    }
}

private val Rose = Color(0xFFB00020)
