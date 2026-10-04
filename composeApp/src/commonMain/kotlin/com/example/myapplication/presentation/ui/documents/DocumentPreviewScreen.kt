package com.example.myapplication.presentation.ui.documents

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.PrimaryLight
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.TopBar

@Composable
fun DocumentPreviewScreen(onBack: () -> Unit) {
    var downloaded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(BackgroundColor)) {
    TopBar("Vista Previa del Documento", onBack = onBack, action = "↑", onAction = { downloaded = true })
    LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(PrimaryColor, PrimaryLight)))
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(42.dp).clip(RoundedCornerShape(10.dp)).background(Color.White),
                            contentAlignment = Alignment.Center
                        ) { Text("U", color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 20.sp) }
                        Column(Modifier.padding(start = 10.dp)) {
                            Text("UNIVERSIDAD TÉCNICA DEL ESTADO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Departamento de Ingeniería", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                        }
                    }
                }
                Column(Modifier.padding(18.dp)) {
                    Text(
                        "PERMISO OFICIAL DE AUSENCIA",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = InkColor
                    )
                    Text(
                        "REF: PERMISO-2023-8942",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MutedColor,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            Text("NOMBRE DEL ESTUDIANTE", color = MutedColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Jonathan Doe", fontWeight = FontWeight.Bold, color = InkColor)
                        }
                        Column(Modifier.weight(1f)) {
                            Text("ID ESTUDIANTE", color = MutedColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("U12345678", fontWeight = FontWeight.Bold, color = InkColor)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            Text("COHORTE", color = MutedColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Ingeniería 2024", fontWeight = FontWeight.Bold, color = InkColor)
                        }
                        Column(Modifier.weight(1f)) {
                            Text("FECHA DE EMISIÓN", color = MutedColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("24 oct. 2023", fontWeight = FontWeight.Bold, color = InkColor)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SoftPurple)
                            .padding(14.dp)
                    ) {
                        Text("Asunto: Cita Médica", fontWeight = FontWeight.Bold, color = InkColor)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "El presente documento certifica que el estudiante ha recibido autorización para ausentarse en la fecha especificada. El motivo fue verificado como una cita médica válida. El estudiante debe ponerse al día en un plazo de 7 días tras su reincorporación.",
                            color = InkColor,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                if (downloaded) "PDF listo para compartir" else "↓   Descargar PDF",
                onClick = { downloaded = true },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
    }
}
