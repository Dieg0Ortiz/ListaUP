package com.example.myapplication.presentation.ui.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.AttendanceStatus
import com.example.myapplication.presentation.theme.AusenteColor
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.FieldColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PresenteColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RetardoColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.RollRow
import com.example.myapplication.presentation.ui.design.SampleData
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
    var fallback by remember(grupoId) { mutableStateOf(SampleData.roll) }
    var localSubmitted by remember(grupoId) { mutableStateOf(false) }
    var groupFilter by remember { mutableStateOf("Todos") }
    LaunchedEffect(grupoId) {
        viewModel.loadStudents(grupoId)
    }

    val usingApi = state.students.isNotEmpty()
    val rows = if (usingApi) {
        state.students.map {
            RollRow(
                id = it.studentId,
                initials = it.studentName.split(" ").take(2).mapNotNull { part -> part.firstOrNull()?.uppercase() }.joinToString(""),
                name = it.studentName,
                matricula = it.matricula,
                group = "Grupo",
                status = it.status,
                hasPermission = it.hasPermission
            )
        }
    } else {
        fallback
    }
    val query = state.searchQuery.trim().lowercase()
    val visible = rows.filter { row ->
        val matchesQuery = query.isBlank() || row.name.lowercase().contains(query) || row.matricula.lowercase().contains(query)
        val matchesGroup = groupFilter == "Todos" || row.group.contains(groupFilter.removePrefix("Grupo ").trim(), ignoreCase = true) || row.group == "Grupo"
        matchesQuery && matchesGroup
    }
    val registered = rows.count { it.status != null && it.status != AttendanceStatus.AUSENTE || it.hasPermission }
    val submitted = state.isSubmitted || localSubmitted

    if (submitted) {
        Column(
            modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Listo", color = PrimaryColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("Lista enviada", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text("La asistencia de $materia quedó registrada.", color = MutedColor)
            Spacer(Modifier.height(20.dp))
            PrimaryButton("Volver", onClick = onBack)
        }
        return
    }

    Column(Modifier.fillMaxSize().background(BackgroundColor)) {
        TopBar(
            title = materia.ifBlank { "Pase de lista" },
            onBack = onBack,
            action = "Enviar",
            onAction = {
                if (usingApi) viewModel.submitAttendance() else localSubmitted = true
            }
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Sesión de hoy", color = MutedColor, fontSize = 13.sp)
                Text("Aula 402  •  10:00 AM - 12:00 PM", color = InkColor, fontWeight = FontWeight.SemiBold)
            }
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar nombre o matrícula", color = MutedColor, fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Todos los Grupos", "Grupo A", "Grupo B").forEach { label ->
                        val key = if (label.startsWith("Todos")) "Todos" else label
                        val selected = groupFilter == key
                        Text(
                            label,
                            color = if (selected) Color.White else InkColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) PrimaryColor else Color.White)
                                .border(1.dp, if (selected) PrimaryColor else FieldColor, RoundedCornerShape(50))
                                .clickable { groupFilter = key }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Lista de Estudiantes\n(${rows.size})", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        "Marcar todos\npresentes",
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable {
                            if (usingApi) {
                                viewModel.markAllPresent()
                            } else {
                                fallback = fallback.map { row ->
                                    if (row.hasPermission) row else row.copy(status = AttendanceStatus.PRESENTE)
                                }
                            }
                        }
                    )
                }
            }
            items(visible, key = { it.id }) { row ->
                StudentCard(row) { status ->
                    if (usingApi) {
                        viewModel.updateStudentStatus(row.id, status)
                    } else {
                        fallback = fallback.map { if (it.id == row.id) it.copy(status = status, hasPermission = false) else it }
                    }
                }
            }
            item {
                WhiteCard {
                    Text("Progreso de Lista", color = MutedColor, fontSize = 12.sp)
                    Text("$registered / ${rows.size} Estudiantes", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { if (rows.isEmpty()) 0f else registered / rows.size.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp)),
                        color = PrimaryColor,
                        trackColor = SoftPurple
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StudentCard(row: RollRow, onStatus: (AttendanceStatus) -> Unit) {
    WhiteCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(SoftPurple)
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Text(row.initials, color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(row.name, color = InkColor, fontWeight = FontWeight.Bold)
                Text("ID: ${row.matricula} • ${row.group}", color = MutedColor, fontSize = 12.sp)
            }
            if (row.hasPermission) {
                Text(
                    "PERMISO\nAUTORIZADO",
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SoftPurple)
                        .padding(8.dp)
                )
            }
        }
        if (!row.hasPermission) {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip("Presente", row.status == AttendanceStatus.PRESENTE, PresenteColor, GreenSoft, Modifier.weight(1f)) {
                    onStatus(AttendanceStatus.PRESENTE)
                }
                StatusChip("Retardo", row.status == AttendanceStatus.RETARDO, Color.White, RetardoColor, Modifier.weight(1f), filled = true) {
                    onStatus(AttendanceStatus.RETARDO)
                }
                StatusChip("Ausente", row.status == AttendanceStatus.AUSENTE, Color.White, AusenteColor, Modifier.weight(1f), filled = true) {
                    onStatus(AttendanceStatus.AUSENTE)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    selected: Boolean,
    content: Color,
    fill: Color,
    modifier: Modifier,
    filled: Boolean = false,
    onClick: () -> Unit
) {
    val bg = if (!selected) FieldColor else if (filled) fill else GreenSoft
    val fg = if (!selected) MutedColor else if (filled) Color.White else content
    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = fg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
