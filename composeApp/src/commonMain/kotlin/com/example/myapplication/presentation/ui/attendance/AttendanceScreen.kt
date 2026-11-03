package com.example.myapplication.presentation.ui.attendance

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.AttendanceStatus
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.AmberText
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.FieldColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.LineColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppDialog
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.AvatarCircle
import com.example.myapplication.presentation.ui.design.IconBubble
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.RollRow
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.AttendanceViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AttendanceScreen(
    grupoId: String,
    materia: String,
    viewModel: AttendanceViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var confirmSend by remember { mutableStateOf(false) }
    LaunchedEffect(grupoId) {
        viewModel.loadStudents(grupoId, materia)
    }

    val rows = state.students.map {
        RollRow(
            id = it.studentId,
            initials = it.studentName.split(" ").take(2).mapNotNull { part -> part.firstOrNull()?.uppercase() }.joinToString(""),
            name = it.studentName,
            matricula = it.matricula,
            group = "Grupo $grupoId",
            // Sin marcar se muestra neutro aunque internamente cuente como ausente
            status = if (it.studentId in state.marked) it.status else null,
            hasPermission = it.hasPermission
        )
    }
    val query = state.searchQuery.trim().lowercase()
    val visible = rows.filter { row ->
        query.isBlank() || row.name.lowercase().contains(query) || row.matricula.lowercase().contains(query)
    }
    val marked = rows.count { it.status != null || it.hasPermission }
    val pending = rows.size - marked

    if (state.isSubmitted) {
        SubmittedView(materia, rows, onBack)
        return
    }

    Box(Modifier.fillMaxSize().background(BackgroundColor)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = materia.ifBlank { "Pase de lista" }, onBack = onBack)
            if (rows.isEmpty()) {
                EmptyState(
                    loading = state.isLoading,
                    error = state.error,
                    onRetry = { viewModel.loadStudents(grupoId, materia) }
                )
                return@Column
            }
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Sesión de hoy • Grupo $grupoId", color = MutedColor, fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    SummaryRow(rows)
                }
                item {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar nombre o matrícula", color = MutedColor, fontSize = 14.sp) },
                        leadingIcon = { Icon(AppIcons.Search, contentDescription = null, tint = MutedColor) },
                        trailingIcon = if (state.searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(AppIcons.Close, contentDescription = "Borrar búsqueda", tint = MutedColor)
                                }
                            }
                        } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = LineColor,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Estudiantes (${rows.size})",
                            color = InkColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = viewModel::markAllPresent) {
                            Icon(AppIcons.Check, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Todos presentes", color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
                if (visible.isEmpty()) {
                    item {
                        Text(
                            "Ningún alumno coincide con \"${state.searchQuery.trim()}\".",
                            color = MutedColor,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                items(visible, key = { it.id }) { row ->
                    StudentCard(row) { status ->
                        viewModel.updateStudentStatus(row.id, status)
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
            SubmitBar(
                marked = marked,
                total = rows.size,
                submitting = state.isSubmitting,
                error = state.error,
                onSubmit = {
                    if (pending > 0) confirmSend = true else viewModel.submitAttendance()
                }
            )
        }
        if (confirmSend) {
            AppDialog(
                title = "¿Enviar la lista?",
                body = if (pending == 1) {
                    "Falta 1 alumno por marcar. Se registrará como ausente."
                } else {
                    "Faltan $pending alumnos por marcar. Se registrarán como ausentes."
                },
                confirm = "Enviar",
                dismiss = "Seguir marcando",
                onConfirm = {
                    confirmSend = false
                    viewModel.submitAttendance()
                },
                onDismiss = { confirmSend = false }
            )
        }
    }
}

@Composable
private fun EmptyState(loading: Boolean, error: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            loading -> {
                CircularProgressIndicator(color = PrimaryColor)
                Spacer(Modifier.height(16.dp))
                Text("Cargando alumnos…", color = MutedColor)
            }
            error != null -> {
                IconBubble(AppIcons.Warning, RoseSoft, size = 56.dp, tint = RoseText)
                Spacer(Modifier.height(16.dp))
                Text("No se pudo cargar la lista", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(4.dp))
                Text(error, color = MutedColor, fontSize = 13.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                PrimaryButton("Reintentar", onClick = onRetry)
            }
            else -> {
                IconBubble(AppIcons.User, SoftPurple, size = 56.dp)
                Spacer(Modifier.height(16.dp))
                Text("Este grupo no tiene alumnos inscritos.", color = MutedColor, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun SummaryRow(rows: List<RollRow>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        SummaryTile("Presentes", rows.count { it.status == AttendanceStatus.PRESENTE }, GreenSoft, GreenText, Modifier.weight(1f))
        SummaryTile("Retardos", rows.count { it.status == AttendanceStatus.RETARDO }, AmberSoft, AmberText, Modifier.weight(1f))
        SummaryTile("Ausentes", rows.count { it.status == AttendanceStatus.AUSENTE }, RoseSoft, RoseText, Modifier.weight(1f))
        SummaryTile("Permisos", rows.count { it.hasPermission }, SoftPurple, PrimaryColor, Modifier.weight(1f))
    }
}

@Composable
private fun SummaryTile(label: String, count: Int, background: Color, content: Color, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .padding(vertical = 10.dp)
            .semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("$count", color = content, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(label, color = content, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun SubmitBar(marked: Int, total: Int, submitting: Boolean, error: String?, onSubmit: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(width = 1.dp, color = LineColor)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "$marked de $total marcados",
                color = InkColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            if (marked < total) {
                Text("Faltan ${total - marked}", color = MutedColor, fontSize = 13.sp)
            } else {
                Icon(AppIcons.CheckCircle, contentDescription = null, tint = GreenText, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Lista completa", color = GreenText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { if (total == 0) 0f else marked / total.toFloat() },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(6.dp)),
            color = PrimaryColor,
            trackColor = SoftPurple,
            drawStopIndicator = {}
        )
        if (error != null) {
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(AppIcons.Warning, contentDescription = null, tint = RoseText, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("No se pudo enviar: $error", color = RoseText, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        PrimaryButton(
            if (submitting) "Enviando…" else "Enviar lista",
            onClick = onSubmit,
            loading = submitting
        )
    }
}

@Composable
private fun StudentCard(row: RollRow, onStatus: (AttendanceStatus) -> Unit) {
    WhiteCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarCircle(row.initials, size = 44.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(row.name, color = InkColor, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Matrícula ${row.matricula}", color = MutedColor, fontSize = 13.sp)
            }
            if (row.hasPermission) {
                Text(
                    "CON PERMISO",
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SoftPurple)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
        if (!row.hasPermission) {
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.selectableGroup()
            ) {
                StatusChip("Presente", AppIcons.Check, row.status == AttendanceStatus.PRESENTE, GreenText, Modifier.weight(1f)) {
                    onStatus(AttendanceStatus.PRESENTE)
                }
                StatusChip("Retardo", AppIcons.Clock, row.status == AttendanceStatus.RETARDO, AmberText, Modifier.weight(1f)) {
                    onStatus(AttendanceStatus.RETARDO)
                }
                StatusChip("Ausente", AppIcons.Close, row.status == AttendanceStatus.AUSENTE, RoseText, Modifier.weight(1f)) {
                    onStatus(AttendanceStatus.AUSENTE)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(if (selected) color else FieldColor)
    val fg by animateColorAsState(if (selected) Color.White else MutedColor)
    Row(
        modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, color = fg, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
    }
}

@Composable
private fun SubmittedView(materia: String, rows: List<RollRow>, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconBubble(AppIcons.CheckCircle, GreenSoft, size = 72.dp, tint = GreenText)
        Spacer(Modifier.height(20.dp))
        Text("Lista enviada", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            "La asistencia de $materia quedó registrada.",
            color = MutedColor,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        SummaryRow(rows.map { if (it.status == null && !it.hasPermission) it.copy(status = AttendanceStatus.AUSENTE) else it })
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Volver al inicio", onClick = onBack)
    }
}
