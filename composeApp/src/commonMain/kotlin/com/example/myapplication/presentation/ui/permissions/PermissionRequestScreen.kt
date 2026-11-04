package com.example.myapplication.presentation.ui.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.shortLabel
import com.example.myapplication.core.parseTime
import com.example.myapplication.core.label
import com.example.myapplication.core.today
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.LineColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.InlineError
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.ui.design.isPdfPickerAvailable
import com.example.myapplication.presentation.ui.design.rememberPdfPicker
import com.example.myapplication.presentation.viewmodel.PermissionRequestViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.koin.compose.viewmodel.koinViewModel

private const val DAY_MS = 86_400_000L

// Se permite justificar faltas de hasta 30 días atrás y pedir permisos hasta 6 meses adelante
private const val DAYS_BACK = 30
private const val DAYS_AHEAD = 180

private enum class Picking { START_DATE, END_DATE, START_TIME, END_TIME }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionRequestScreen(
    viewModel: PermissionRequestViewModel = koinViewModel(),
    onBack: () -> Unit,
    onCreated: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var picking by remember { mutableStateOf<Picking?>(null) }
    val pickPdf = rememberPdfPicker { file -> if (file != null) viewModel.setProof(file) }

    LaunchedEffect(state.created) {
        state.created?.let { onCreated(it.id) }
    }

    Column(Modifier.fillMaxSize().background(BackgroundColor)) {
        TopBar("Solicitar permiso", onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            FieldLabel("¿Cuánto tiempo vas a faltar?")
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(4.dp)
            ) {
                listOf(true to "Días completos", false to "Unas horas").forEach { (value, label) ->
                    val selected = state.fullDays == value
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) SoftPurple else Color.Transparent)
                            .selectable(selected = selected, role = Role.RadioButton) { viewModel.setFullDays(value) }
                            .heightIn(min = 44.dp)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = if (selected) PrimaryColor else MutedColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            if (state.fullDays) {
                FieldLabel("Fechas")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PickerBox("Desde", state.startDate.shortLabel(), Modifier.weight(1f)) { picking = Picking.START_DATE }
                    PickerBox("Hasta", state.endDate.shortLabel(), Modifier.weight(1f)) { picking = Picking.END_DATE }
                }
            } else {
                FieldLabel("Día y horario")
                PickerBox("Día", state.startDate.shortLabel(), Modifier.fillMaxWidth()) { picking = Picking.START_DATE }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PickerBox("De", state.startTime, Modifier.weight(1f)) { picking = Picking.START_TIME }
                    PickerBox("A", state.endTime, Modifier.weight(1f)) { picking = Picking.END_TIME }
                }
            }

            Spacer(Modifier.height(16.dp))
            FieldLabel("Motivo")
            OutlinedTextField(
                value = state.reason,
                onValueChange = viewModel::setReason,
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                placeholder = { Text("Ej. Cita médica en el IMSS, adjunto constancia.", color = MutedColor) },
                supportingText = {
                    Text(
                        "${state.reason.length}/${PermissionRequestViewModel.MAX_REASON}",
                        color = MutedColor,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = LineColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            if (isPdfPickerAvailable) {
                Spacer(Modifier.height(8.dp))
                FieldLabel("Comprobante (opcional)")
                WhiteCard(onClick = if (state.proof == null) pickPdf else null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(AppIcons.Document, contentDescription = null, tint = PrimaryColor)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                state.proof?.name ?: "Adjuntar PDF",
                                color = InkColor,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "PDF · máx. 1 MB",
                                color = MutedColor,
                                fontSize = 12.sp
                            )
                        }
                        if (state.proof != null) {
                            IconButton(onClick = { viewModel.setProof(null) }) {
                                Icon(AppIcons.Close, contentDescription = "Quitar comprobante", tint = MutedColor)
                            }
                        }
                    }
                }
            }

            if (state.error != null) {
                Spacer(Modifier.height(12.dp))
                InlineError(state.error ?: "")
            }
            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                if (state.isSubmitting) "Enviando…" else "Enviar solicitud",
                onClick = viewModel::submit,
                loading = state.isSubmitting
            )
            Spacer(Modifier.height(24.dp))
        }
    }

    when (picking) {
        Picking.START_DATE, Picking.END_DATE -> {
            val isStart = picking == Picking.START_DATE
            val initial = if (isStart) state.startDate else state.endDate
            val minDay = if (isStart) today().toEpochDays() - DAYS_BACK else state.startDate.toEpochDays()
            val maxDay = today().toEpochDays() + DAYS_AHEAD
            val pickerState = rememberDatePickerState(
                initialSelectedDateMillis = initial.toEpochDays() * DAY_MS,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                        (utcTimeMillis / DAY_MS) in minDay..maxDay
                }
            )
            DatePickerDialog(
                onDismissRequest = { picking = null },
                confirmButton = {
                    TextButton(onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            val date = LocalDate.fromEpochDays(millis / DAY_MS)
                            if (isStart) viewModel.setStartDate(date) else viewModel.setEndDate(date)
                        }
                        picking = null
                    }) { Text("Aceptar", color = PrimaryColor, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { picking = null }) { Text("Cancelar", color = MutedColor) }
                }
            ) {
                DatePicker(state = pickerState, showModeToggle = false)
            }
        }
        Picking.START_TIME, Picking.END_TIME -> {
            val isStart = picking == Picking.START_TIME
            val initial = parseTime(if (isStart) state.startTime else state.endTime) ?: LocalTime(8, 0)
            val timeState = rememberTimePickerState(initialHour = initial.hour, initialMinute = initial.minute, is24Hour = true)
            AlertDialog(
                onDismissRequest = { picking = null },
                confirmButton = {
                    TextButton(onClick = {
                        val value = LocalTime(timeState.hour, timeState.minute).label()
                        if (isStart) viewModel.setStartTime(value) else viewModel.setEndTime(value)
                        picking = null
                    }) { Text("Aceptar", color = PrimaryColor, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { picking = null }) { Text("Cancelar", color = MutedColor) }
                },
                text = { TimePicker(state = timeState) }
            )
        }
        null -> Unit
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, color = InkColor, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun PickerBox(label: String, value: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, LineColor, RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClickLabel = "Cambiar $label", onClick = onClick)
            .heightIn(min = 56.dp)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(label, color = MutedColor, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = InkColor, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Icon(AppIcons.Calendar, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
        }
    }
}
