package com.example.myapplication.presentation.ui.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.initialsOf
import com.example.myapplication.core.relativeLabel
import com.example.myapplication.domain.model.Permission
import com.example.myapplication.domain.model.PermissionStatus
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.AmberText
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.LineColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.ui.design.AppDialog
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.AvatarCircle
import com.example.myapplication.presentation.ui.design.ErrorState
import com.example.myapplication.presentation.ui.design.InfoLine
import com.example.myapplication.presentation.ui.design.InlineError
import com.example.myapplication.presentation.ui.design.LoadingState
import com.example.myapplication.presentation.ui.design.Pill
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.SecondaryButton
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.PermissionDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PermissionDetailScreen(
    permissionId: Int,
    viewModel: PermissionDetailViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    // null = sin diálogo; true = aprobar; false = rechazar
    var deciding by remember { mutableStateOf<Boolean?>(null) }
    var comment by remember { mutableStateOf("") }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(permissionId) { viewModel.load(permissionId) }
    // Al terminar la revisión se cierra el diálogo
    LaunchedEffect(state.permission?.status) {
        if (state.permission?.status?.isFinal == true || state.permission?.status == PermissionStatus.PENDING_DIRECTOR) {
            deciding = null
        }
    }

    Box(Modifier.fillMaxSize().background(BackgroundColor)) {
        Column(Modifier.fillMaxSize()) {
            TopBar("Solicitud #$permissionId", onBack = onBack)
            val permission = state.permission
            when {
                permission == null && state.isLoading -> LoadingState("Cargando solicitud…")
                permission == null -> ErrorState(state.error ?: "No se encontró la solicitud.", onRetry = viewModel::retry)
                else -> Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Header(permission)
                    Spacer(Modifier.height(16.dp))
                    Timeline(permission)
                    Spacer(Modifier.height(16.dp))
                    WhiteCard {
                        InfoLine("Tipo", if (permission.fullDays) "Días completos" else "Por horas")
                        InfoLine("Periodo", permission.rangeLabel())
                        if (permission.fullDays && permission.days > 0) {
                            InfoLine("Días", if (permission.days == 1) "1 día" else "${permission.days} días")
                        }
                        InfoLine("Solicitado", relativeLabel(permission.createdAt))
                    }
                    Spacer(Modifier.height(16.dp))
                    SectionTitle("Motivo")
                    Text(permission.reason, color = InkColor, fontSize = 15.sp)
                    if (permission.proofUrl != null) {
                        Spacer(Modifier.height(16.dp))
                        SectionTitle("Comprobante")
                        WhiteCard(onClick = { runCatching { uriHandler.openUri(permission.proofUrl) } }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(AppIcons.Document, contentDescription = null, tint = PrimaryColor)
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(permission.proofFilename ?: "Comprobante.pdf", color = InkColor, fontWeight = FontWeight.SemiBold)
                                    Text("Toca para abrir el PDF", color = MutedColor, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    Comments(permission)
                    if (permission.disclaimer.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(AmberSoft).padding(14.dp)
                        ) {
                            Icon(AppIcons.Info, contentDescription = null, tint = AmberText, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(permission.disclaimer, color = AmberText, fontSize = 13.sp)
                        }
                    }
                    if (state.canReview) {
                        Spacer(Modifier.height(20.dp))
                        if (state.reviewError != null && deciding == null) {
                            InlineError(state.reviewError ?: "")
                            Spacer(Modifier.height(12.dp))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SecondaryButton("Rechazar", onClick = { comment = ""; deciding = false }, modifier = Modifier.weight(1f))
                            PrimaryButton("Aprobar", onClick = { comment = ""; deciding = true }, modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(28.dp))
                }
            }
        }

        val approving = deciding
        if (approving != null) {
            AppDialog(
                title = if (approving) "¿Aprobar la solicitud?" else "Rechazar la solicitud",
                body = if (approving) {
                    "Pasará a Dirección para su autorización final."
                } else {
                    "El alumno verá el motivo que escribas."
                },
                confirm = when {
                    state.isReviewing -> "Enviando…"
                    approving -> "Aprobar"
                    else -> "Rechazar"
                },
                dismiss = "Cancelar",
                onConfirm = { if (!state.isReviewing) viewModel.review(approving, comment) },
                onDismiss = { if (!state.isReviewing) deciding = null }
            ) {
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it.take(300) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(if (approving) "Comentario (opcional)" else "Motivo del rechazo") },
                    minLines = 2
                )
                if (state.reviewError != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.reviewError ?: "", color = RoseText, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun Header(permission: Permission) {
    val (bg, fg) = statusColors(permission.status)
    Row(verticalAlignment = Alignment.CenterVertically) {
        AvatarCircle(initialsOf(permission.studentName))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(permission.studentName, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = InkColor)
            if (permission.matricula.isNotBlank()) {
                Text("Matrícula ${permission.matricula}", color = MutedColor, fontSize = 13.sp)
            }
            Spacer(Modifier.height(4.dp))
            Pill(permission.status.label, bg, fg)
        }
    }
}

/** Solicitado → Tutor → Dirección → resultado. Si el tutor rechaza, Dirección no participa. */
@Composable
private fun Timeline(permission: Permission) {
    val status = permission.status
    // El tutor deja su motivo en tutor_comment; si Dirección no comentó, el rechazo fue del tutor
    val rejectedByTutor = status == PermissionStatus.REJECTED && permission.directorComment.isNullOrBlank()
    val reached = when (status) {
        PermissionStatus.PENDING_TUTOR -> 1
        PermissionStatus.PENDING_DIRECTOR -> 2
        PermissionStatus.APPROVED, PermissionStatus.REJECTED -> 3
        PermissionStatus.UNKNOWN -> 0
    }
    val steps = listOf(
        "Solicitado",
        "Revisión del tutor",
        if (rejectedByTutor) "Autorización de Dirección (no aplica)" else "Autorización de Dirección",
        when (status) {
            PermissionStatus.APPROVED -> "Aprobado"
            PermissionStatus.REJECTED -> if (rejectedByTutor) "Rechazado por el tutor" else "Rechazado por Dirección"
            else -> "Resultado"
        }
    )
    WhiteCard {
        steps.forEachIndexed { index, title ->
            val skipped = rejectedByTutor && index == 2
            val done = !skipped && (index < reached || (index == 3 && status.isFinal))
            val current = index == reached && !status.isFinal
            val color = when {
                index == 3 && status == PermissionStatus.REJECTED -> RoseText
                index == 3 && status == PermissionStatus.APPROVED -> GreenText
                done || current -> PrimaryColor
                else -> LineColor
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 6.dp).semantics(mergeDescendants = true) {}
            ) {
                Box(
                    Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (done) color else Color.Transparent)
                        .border(2.dp, color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (done) Icon(AppIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    title + if (current) " (en curso)" else "",
                    color = if (done || current) InkColor else MutedColor,
                    fontWeight = if (current) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun Comments(permission: Permission) {
    val notes = listOfNotNull(
        permission.tutorComment?.takeIf { it.isNotBlank() }?.let { "Tutor" to it },
        permission.directorComment?.takeIf { it.isNotBlank() }?.let { "Dirección" to it },
        permission.rejectionReason?.takeIf {
            it.isNotBlank() && it != permission.tutorComment && it != permission.directorComment
        }?.let { "Motivo del rechazo" to it }
    )
    if (notes.isEmpty()) return
    Spacer(Modifier.height(16.dp))
    SectionTitle("Comentarios")
    notes.forEach { (who, text) ->
        WhiteCard(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(who, color = MutedColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text, color = InkColor, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        color = InkColor,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        modifier = Modifier.padding(bottom = 8.dp).heightIn(min = 24.dp)
    )
}
