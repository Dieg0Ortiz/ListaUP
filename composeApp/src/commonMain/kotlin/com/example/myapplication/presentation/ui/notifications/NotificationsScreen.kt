package com.example.myapplication.presentation.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.parseIsoDate
import com.example.myapplication.core.relativeLabel
import com.example.myapplication.core.today
import com.example.myapplication.domain.model.AppNotification
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.AmberText
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
import com.example.myapplication.presentation.ui.design.IconBubble
import com.example.myapplication.presentation.ui.design.InlineError
import com.example.myapplication.presentation.ui.design.LoadingState
import com.example.myapplication.presentation.ui.design.MessageState
import com.example.myapplication.presentation.ui.design.RefreshBox
import com.example.myapplication.presentation.ui.design.TopBar
import com.example.myapplication.presentation.viewmodel.NotificationsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = koinViewModel(),
    onBack: () -> Unit,
    onOpenPermission: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var unreadOnly by remember { mutableStateOf(false) }
    val visible = if (unreadOnly) state.notifications.filter { !it.isRead } else state.notifications
    val unread = state.notifications.count { !it.isRead }
    val firstLoad = state.isLoading && state.notifications.isEmpty()
    val sections = visible.groupBy { sectionOf(it.createdAt) }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        TopBar(
            title = "Notificaciones",
            onBack = onBack,
            action = if (unread > 0) "Marcar leídas" else null,
            onAction = viewModel::markAllRead
        )
        Row(Modifier.padding(horizontal = 20.dp)) {
            TabLabel("Todas", !unreadOnly) { unreadOnly = false }
            Spacer(Modifier.width(18.dp))
            TabLabel(if (unread > 0) "No leídas ($unread)" else "No leídas", unreadOnly) { unreadOnly = true }
        }
        if (state.actionError != null) {
            InlineError(state.actionError ?: "", modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
        }
        RefreshBox(refreshing = state.isLoading && !firstLoad, onRefresh = viewModel::load, modifier = Modifier.fillMaxSize()) {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
                when {
                    firstLoad -> item { LoadingState("Cargando notificaciones…") }
                    state.error != null && state.notifications.isEmpty() -> item { ErrorState(state.error ?: "", onRetry = viewModel::load) }
                    visible.isEmpty() -> item {
                        MessageState(
                            icon = AppIcons.Bell,
                            title = if (unreadOnly) "Estás al día" else "Sin notificaciones",
                            body = if (unreadOnly) "No tienes notificaciones sin leer." else "Aquí verás los avisos sobre tus permisos y tu asistencia."
                        )
                    }
                }
                SECTION_ORDER.forEach { section ->
                    val rows = sections[section].orEmpty()
                    if (rows.isNotEmpty()) {
                        item(key = "header_$section") {
                            Text(
                                section,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                fontWeight = FontWeight.Bold,
                                color = InkColor
                            )
                        }
                        items(rows, key = { it.id }) { notice ->
                            NoticeRow(notice) { notice.permissionId?.let(onOpenPermission) }
                        }
                    }
                }
            }
        }
    }
}

private val SECTION_ORDER = listOf("Hoy", "Ayer", "Esta semana", "Anteriores")

private fun sectionOf(createdAt: String): String {
    val date = parseIsoDate(createdAt) ?: return "Anteriores"
    val days = (today().toEpochDays() - date.toEpochDays()).toInt()
    return when {
        days <= 0 -> "Hoy"
        days == 1 -> "Ayer"
        days < 7 -> "Esta semana"
        else -> "Anteriores"
    }
}

@Composable
private fun TabLabel(text: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .heightIn(min = 44.dp)
            .padding(vertical = 8.dp)
    ) {
        Text(text, color = if (selected) PrimaryColor else MutedColor, fontWeight = FontWeight.Bold)
        if (selected) {
            Box(Modifier.padding(top = 6.dp).size(width = 36.dp, height = 3.dp).clip(RoundedCornerShape(2.dp)).background(PrimaryColor))
        }
    }
}

@Composable
private fun NoticeRow(notice: AppNotification, onClick: () -> Unit) {
    val text = (notice.title + " " + notice.type).lowercase()
    val (bg, fg, icon) = when {
        "rechaz" in text || "reject" in text -> Triple(RoseSoft, RoseText, AppIcons.Close)
        "aprob" in text || "approv" in text -> Triple(GreenSoft, GreenText, AppIcons.CheckCircle)
        "asistencia" in text || "falta" in text -> Triple(AmberSoft, AmberText, AppIcons.Warning)
        else -> Triple(SoftPurple, PrimaryColor, AppIcons.Bell)
    }
    val clickable = notice.permissionId != null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (clickable) Modifier.clickable(onClick = onClick) else Modifier)
            .semantics(mergeDescendants = true) {}
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        IconBubble(icon, bg, size = 44.dp, tint = fg)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(notice.title, fontWeight = FontWeight.Bold, color = InkColor, modifier = Modifier.weight(1f))
                if (!notice.isRead) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(PrimaryColor))
                }
            }
            Text(notice.message, color = MutedColor, fontSize = 13.sp)
            Text(relativeLabel(notice.createdAt), color = MutedColor, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
