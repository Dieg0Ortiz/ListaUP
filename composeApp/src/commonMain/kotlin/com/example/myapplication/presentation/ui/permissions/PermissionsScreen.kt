package com.example.myapplication.presentation.ui.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.myapplication.core.relativeLabel
import com.example.myapplication.core.shortLabel
import com.example.myapplication.domain.model.Permission
import com.example.myapplication.domain.model.PermissionStatus
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.AmberText
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.ErrorState
import com.example.myapplication.presentation.ui.design.InlineError
import com.example.myapplication.presentation.ui.design.LoadingState
import com.example.myapplication.presentation.ui.design.MessageState
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.RefreshBox
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.PermissionsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PermissionsScreen(
    viewModel: PermissionsViewModel = koinViewModel(),
    onOpen: (Int) -> Unit,
    onNewRequest: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    // Docente: 0 = por revisar, 1 = revisadas. Alumno: todas.
    var tab by remember { mutableIntStateOf(0) }
    val visible = if (state.isProfessor) {
        state.permissions.filter { (it.status == PermissionStatus.PENDING_TUTOR) == (tab == 0) }
    } else {
        state.permissions
    }
    val pendingCount = state.permissions.count { it.status == PermissionStatus.PENDING_TUTOR }
    val firstLoad = state.isLoading && state.permissions.isEmpty()

    Box(Modifier.fillMaxSize().background(BackgroundColor)) {
        RefreshBox(refreshing = state.isLoading && !firstLoad, onRefresh = viewModel::load, modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Permisos", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = InkColor)
                    Text(
                        if (state.isProfessor) "Solicitudes de tus tutorados" else "Tus solicitudes y su estado",
                        color = MutedColor
                    )
                }
                if (state.isProfessor) {
                    item {
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(4.dp)
                        ) {
                            listOf("Por revisar ($pendingCount)", "Revisadas").forEachIndexed { index, label ->
                                val selected = tab == index
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) SoftPurple else Color.Transparent)
                                        .selectable(selected = selected, role = Role.Tab) { tab = index }
                                        .heightIn(min = 44.dp)
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(label, color = if (selected) PrimaryColor else MutedColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                when {
                    firstLoad -> item { LoadingState("Cargando solicitudes…") }
                    state.error != null && state.permissions.isEmpty() -> item {
                        ErrorState(state.error ?: "", onRetry = viewModel::load)
                    }
                    visible.isEmpty() -> item {
                        MessageState(
                            icon = AppIcons.Document,
                            title = when {
                                !state.isProfessor -> "Aún no tienes solicitudes"
                                tab == 0 -> "No hay solicitudes por revisar"
                                else -> "Todavía no has revisado solicitudes"
                            },
                            body = if (state.isProfessor) {
                                "Aquí aparecen los permisos que piden los alumnos de los que eres tutor."
                            } else {
                                "Si vas a faltar por un motivo justificado, crea una solicitud y tu tutor la revisará."
                            }
                        )
                    }
                }
                if (state.error != null && state.permissions.isNotEmpty()) {
                    item { InlineError(state.error ?: "", onRetry = viewModel::load) }
                }
                items(visible, key = { it.id }) { permission ->
                    PermissionRow(permission, showStudent = state.isProfessor) { onOpen(permission.id) }
                }
            }
        }
        if (!state.isProfessor) {
            ExtendedFloatingActionButton(
                onClick = onNewRequest,
                containerColor = PrimaryColor,
                contentColor = Color.White,
                modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
                icon = { Icon(AppIcons.Document, contentDescription = null) },
                text = { Text("Nueva solicitud", fontWeight = FontWeight.Bold) }
            )
        }
    }
}

/** Colores de fondo y texto para el estado de un permiso. */
fun statusColors(status: PermissionStatus): Pair<Color, Color> = when (status) {
    PermissionStatus.APPROVED -> GreenSoft to GreenText
    PermissionStatus.REJECTED -> RoseSoft to RoseText
    PermissionStatus.PENDING_DIRECTOR -> SoftPurple to PrimaryColor
    else -> AmberSoft to AmberText
}

/** "3 nov 2026" o "3 nov 2026 – 5 nov 2026", con horas si es un permiso por horas. */
fun Permission.rangeLabel(): String {
    val start = startDate?.shortLabel().orEmpty()
    val end = endDate?.shortLabel().orEmpty()
    val dates = if (end.isBlank() || end == start) start else "$start – $end"
    return if (!fullDays && startTime != null && endTime != null) "$dates, $startTime – $endTime" else dates
}

@Composable
fun PermissionRow(
    permission: Permission,
    showStudent: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (bg, fg) = statusColors(permission.status)
    WhiteCard(modifier = modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    if (showStudent) permission.studentName else permission.reason,
                    fontWeight = FontWeight.Bold,
                    color = InkColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (showStudent) {
                    Text(permission.reason, color = MutedColor, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.width(8.dp))
            Pill(permission.status.label, bg, fg)
        }
        Spacer(Modifier.padding(top = 6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(permission.rangeLabel(), color = MutedColor, fontSize = 12.sp)
            Text(relativeLabel(permission.createdAt), color = MutedColor, fontSize = 12.sp)
        }
    }
}
