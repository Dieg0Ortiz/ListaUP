package com.example.myapplication.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.FieldColor
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun AttendanceSettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val saved by viewModel.umbral.collectAsState()
    var value by remember { mutableFloatStateOf(saved.toFloat()) }
    var justSaved by remember { mutableStateOf(false) }
    val faltas = value.roundToInt()
    val label = if (faltas == 1) "1 falta" else "$faltas faltas"

    Column(Modifier.fillMaxSize().background(BackgroundColor)) {
        TopBar("Ajustes de asistencia", onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Text("Límite para alertas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
            Text(
                "Un alumno aparece en Alertas y Reportes cuando acumula este número de faltas en uno de tus grupos.",
                color = MutedColor,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(Modifier.height(8.dp))
            WhiteCard {
                Row {
                    Text("Faltas", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = InkColor)
                    Text(label, color = PrimaryColor, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = value,
                    onValueChange = { value = it; justSaved = false },
                    valueRange = SettingsViewModel.MIN_UMBRAL.toFloat()..SettingsViewModel.MAX_UMBRAL.toFloat(),
                    steps = SettingsViewModel.MAX_UMBRAL - SettingsViewModel.MIN_UMBRAL - 1,
                    modifier = Modifier.semantics { stateDescription = label },
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryColor,
                        activeTrackColor = PrimaryColor,
                        inactiveTrackColor = FieldColor
                    )
                )
            }
            Text(
                "Este ajuste se guarda en este teléfono.",
                color = MutedColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(12.dp))
            if (justSaved) {
                Text(
                    "Guardado: alertas a partir de $label.",
                    color = GreenText,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            PrimaryButton(
                "Guardar",
                onClick = {
                    viewModel.save(faltas)
                    justSaved = true
                },
                enabled = faltas != saved || !justSaved
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
