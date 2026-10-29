package com.example.myapplication.presentation.ui.schedule

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.HeroColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppDialog
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.SampleData
import com.example.myapplication.presentation.ui.design.WeekClass
import com.example.myapplication.presentation.ui.design.WhiteCard

@Composable
fun ScheduleScreen(isProfessor: Boolean) {
    val days = listOf("LUN" to "16", "MAR" to "17", "MIE" to "18", "JUE" to "19", "VIE" to "20")
    var selected by remember { mutableIntStateOf(1) }
    var showAdd by remember { mutableIntStateOf(0) }
    val classes = SampleData.weekClasses

    Box(Modifier.fillMaxSize()) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(HeroColor)
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.2f)).padding(8.dp)) {
                        Text(if (isProfessor) "PG" else "AJ", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(if (isProfessor) "Horario docente" else "Horario Semanal", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("OCTUBRE 2023", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    days.forEachIndexed { index, day ->
                        val active = index == selected
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (active) Color.White else Color.Transparent)
                                .clickable { selected = index }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(day.first, color = if (active) PrimaryColor else Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(day.second, color = if (active) PrimaryColor else Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
        items(classes) { item ->
            ScheduleCard(item)
        }
        item {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .clickable { showAdd = 1 }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("▮▮▮  Ver Semestre Completo", color = InkColor, fontWeight = FontWeight.Bold)
            }
        }
    }
    if (showAdd == 1) {
        AppDialog(
            title = "Semestre completo",
            body = "El horario mostrado corresponde a la semana del 16 al 20 de octubre. El resto del semestre conserva estos mismos bloques.",
            confirm = "Cerrar",
            onConfirm = { showAdd = 0 },
            onDismiss = { showAdd = 0 }
        )
    }
    }
}

@Composable
private fun ScheduleCard(item: WeekClass) {
    val live = item.badge == "EN CURSO"
    val (bg, fg) = when (item.badge) {
        "FINALIZADA" -> FieldSoft to MutedColor
        "EN CURSO" -> SoftPurple to PrimaryColor
        "PRÓXIMA" -> AmberSoft to Color(0xFFB45309)
        else -> GreenSoft to GreenText
    }
    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
        Row {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(item.start, color = InkColor, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                Box(Modifier.padding(vertical = 4.dp).width(2.dp).height(28.dp).background(if (live) PrimaryColor else SoftPurple))
                Text(item.end, color = MutedColor, fontSize = 12.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.materia, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = InkColor)
                    Pill(item.badge, bg, fg)
                }
                Text(item.docente, color = MutedColor, fontSize = 13.sp)
                Text("${item.place}  ·  ${item.grupo}", color = MutedColor, fontSize = 12.sp)
            }
        }
    }
}

private val FieldSoft = Color(0xFFF3F0F7)
