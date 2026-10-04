package com.example.myapplication.presentation.ui.reports

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.FieldColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AvatarCircle
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.ui.design.WhiteCard

@Composable
fun ReportsScreen(onBack: (() -> Unit)? = null) {
    val cohorts = listOf("2024", "2023", "2022")
    var cohort by remember { mutableIntStateOf(0) }
    var group by remember { mutableStateOf("Todos") }
    var openTutor by remember { mutableStateOf("AT") }
    var exported by remember { mutableStateOf(false) }
    val rate = when (cohorts[cohort]) {
        "2024" -> "84.2%"
        "2023" -> "88.0%"
        else -> "91.4%"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            TopBar("Reportes por Cohorte y Grupo", onBack = onBack, action = "↗", onAction = { exported = true })
        }
        item {
            Row(
                Modifier.padding(horizontal = 20.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Cohorte: ${cohorts[cohort]}  ▾",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(PrimaryColor)
                        .clickable { cohort = (cohort + 1) % cohorts.size }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                )
                listOf("Todos", "A", "B", "C").forEach { label ->
                    val selected = group == label
                    Text(
                        if (label == "Todos") "Grupo: Todos" else "Grupo $label",
                        color = if (selected) PrimaryColor else InkColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) SoftPurple else Color.White)
                            .border(1.dp, if (selected) PrimaryColor else FieldColor, RoundedCornerShape(50))
                            .clickable { group = label }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            Text("Comparativa de Asistencia", modifier = Modifier.padding(horizontal = 20.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = InkColor)
            Spacer(Modifier.height(8.dp))
            WhiteCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tasa de Asistencia Promedio", modifier = Modifier.weight(1f), color = MutedColor, fontSize = 13.sp)
                    Pill("↘ -2.4%", RoseSoft, RoseText)
                }
                Text(rate, color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 36.sp)
                Spacer(Modifier.height(28.dp))
                Text("META 85%", modifier = Modifier.fillMaxWidth(), color = MutedColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("Gp A", "Gp B", "Gp C", "Gp D", "Gp E").forEach {
                        Text(it, color = MutedColor, fontSize = 12.sp)
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.Bottom) {
                Text("Estudiantes en\nRiesgo", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = InkColor)
                Text("POR TUTOR\nACADÉMICO", color = MutedColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            TutorCard(
                initials = "AT",
                name = "Dr. Aris Thorne",
                summary = "5 estudiantes en riesgo",
                summaryColor = RoseText,
                expanded = openTutor == "AT",
                onToggle = { openTutor = if (openTutor == "AT") "" else "AT" }
            ) {
                RiskStudent("Julianna Vane", "ID: #2100492", "64%", "CRÍTICO", RoseSoft, RoseText)
                RiskStudent("Marcus Wright", "ID: #2100511", "72%", "ADVERTENCIA", AmberSoft, Color(0xFFB45309))
            }
            TutorCard(
                initials = "SL",
                name = "Prof. Sarah Loomis",
                summary = "2 estudiantes en riesgo",
                summaryColor = Color(0xFFB45309),
                expanded = openTutor == "SL",
                onToggle = { openTutor = if (openTutor == "SL") "" else "SL" }
            ) {
                RiskStudent("Elena Cruz", "ID: #2100601", "70%", "ADVERTENCIA", AmberSoft, Color(0xFFB45309))
            }
            TutorCard(
                initials = "JK",
                name = "Dr. James Kade",
                summary = "Sin estudiantes en riesgo",
                summaryColor = MutedColor,
                expanded = openTutor == "JK",
                onToggle = { openTutor = if (openTutor == "JK") "" else "JK" }
            ) {
                Text("Este tutor no tiene alumnos bajo el umbral.", color = MutedColor, fontSize = 13.sp)
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            if (exported) {
                Text(
                    "Reporte de cohorte ${cohorts[cohort]} listo. Revisa tu correo institucional.",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    color = PrimaryColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            PrimaryButton(
                "↓   Exportar Reporte Completo",
                onClick = { exported = true },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun TutorCard(
    initials: String,
    name: String,
    summary: String,
    summaryColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp), onClick = onToggle) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarCircle(initials)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, color = InkColor)
                Text(summary, color = summaryColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            Text(if (expanded) "˄" else "˅", color = MutedColor, fontSize = 18.sp)
        }
        if (expanded) {
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun RiskStudent(name: String, id: String, percent: String, label: String, bg: Color, fg: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold, color = InkColor)
            Text(id, color = MutedColor, fontSize = 12.sp)
        }
        Text(percent, color = fg, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Pill(label, bg, fg)
    }
}
