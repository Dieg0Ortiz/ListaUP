package com.example.myapplication.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.ui.design.WhiteCard

@Composable
fun AttendanceSettingsScreen(onBack: () -> Unit) {
    var warning by remember { mutableFloatStateOf(10f) }
    var critical by remember { mutableFloatStateOf(25f) }
    var roles by remember {
        mutableStateOf(
            linkedMapOf(
                "Director Académico" to true,
                "Tutor Académico" to true,
                "Tutor Familiar" to false,
                "Profesor" to true,
                "Estudiante" to true
            )
        )
    }
    var period by remember { mutableStateOf("Mensual") }
    var reminders by remember { mutableStateOf(true) }
    var saved by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(BackgroundColor)) {
    TopBar("Ajustes de Asistencia", onBack = onBack)
    LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            Text("Porcentajes de Umbral", modifier = Modifier.padding(horizontal = 20.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
            Text(
                "Establece las tasas de inasistencia que activan las alertas automáticas.",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                color = MutedColor,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(8.dp))
            ThresholdCard("Advertencia de Falta", "${warning.toInt()}%", warning, 5f..30f, PrimaryColor) {
                warning = it
                saved = false
            }
            ThresholdCard("Inasistencia Crítica", "${critical.toInt()}%", critical, 15f..40f, DangerColor) {
                critical = it
                saved = false
            }
        }
        item {
            Text("Destinatarios de Alertas", modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
            Text(
                "Selecciona los roles que recibirán notificaciones.",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MutedColor,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(8.dp))
            roles.keys.forEach { role ->
                val checked = roles[role] == true
                WhiteCard(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    onClick = {
                        roles = LinkedHashMap(roles).also { it[role] = !checked }
                        saved = false
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(role, modifier = Modifier.weight(1f), color = InkColor, fontWeight = FontWeight.SemiBold)
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .background(if (checked) PrimaryColor else Color.Transparent)
                                .border(2.dp, if (checked) PrimaryColor else FieldColor, CircleShape)
                                .padding(4.dp)
                        ) {
                            Text(if (checked) "✓" else " ", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        item {
            Text("Período de Reporte", modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
            Row(Modifier.padding(horizontal = 20.dp)) {
                listOf("Mensual", "Trimestral", "Semestral").forEach { label ->
                    val selected = period == label
                    Text(
                        label,
                        color = if (selected) PrimaryColor else MutedColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) SoftPurple else Color.White)
                            .clickable { period = label; saved = false }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }
            }
        }
        item {
            Spacer(Modifier.height(12.dp))
            WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Recordatorios Inteligentes", fontWeight = FontWeight.Bold, color = InkColor)
                        Text("Notificar al estudiante al alcanzar el 8%", color = MutedColor, fontSize = 12.sp)
                    }
                    Switch(
                        checked = reminders,
                        onCheckedChange = { reminders = it; saved = false },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = PrimaryColor,
                            checkedThumbColor = Color.White,
                            uncheckedTrackColor = FieldColor,
                            uncheckedThumbColor = Color.White
                        )
                    )
                }
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            if (saved) {
                Text(
                    "Configuración guardada: advertencia ${warning.toInt()}%, crítica ${critical.toInt()}%, periodo $period.",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    color = PrimaryColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            PrimaryButton(
                "Guardar Configuración",
                onClick = { saved = true },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
    }
}

@Composable
private fun ThresholdCard(
    title: String,
    value: String,
    current: Float,
    range: ClosedFloatingPointRange<Float>,
    color: Color,
    onChange: (Float) -> Unit
) {
    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
        Row {
            Text(title, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = InkColor)
            Text(value, color = color, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = current,
            onValueChange = onChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = FieldColor
            )
        )
    }
}
