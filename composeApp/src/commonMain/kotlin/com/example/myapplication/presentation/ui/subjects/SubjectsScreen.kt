package com.example.myapplication.presentation.ui.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.normalizeKey
import com.example.myapplication.domain.model.Group
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.LineColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.ErrorState
import com.example.myapplication.presentation.ui.design.IconBubble
import com.example.myapplication.presentation.ui.design.InlineError
import com.example.myapplication.presentation.ui.design.LoadingState
import com.example.myapplication.presentation.ui.design.MessageState
import com.example.myapplication.presentation.ui.design.RefreshBox
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.StudentDashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SubjectsScreen(
    viewModel: StudentDashboardViewModel = koinViewModel(),
    onOpenNotifications: () -> Unit,
    onOpenSubject: (Group) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    val key = query.normalizeKey()
    val visible = state.groups.filter { group ->
        key.isBlank() || group.materia.normalizeKey().contains(key) || group.docenteNombre.normalizeKey().contains(key)
    }
    val firstLoad = state.isLoading && state.groups.isEmpty()

    RefreshBox(
        refreshing = state.isLoading && !firstLoad,
        onRefresh = viewModel::loadDashboard,
        modifier = Modifier.fillMaxSize().background(BackgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Mis materias", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = InkColor)
                        if (state.groups.isNotEmpty()) {
                            Text("${state.groups.size} inscritas este periodo", color = MutedColor, fontSize = 13.sp)
                        }
                    }
                    IconButton(onClick = onOpenNotifications) {
                        BadgedBox(badge = {
                            if (state.unreadNotifications > 0) Badge(containerColor = PrimaryColor) { Text("${state.unreadNotifications}") }
                        }) {
                            Icon(AppIcons.Bell, contentDescription = "Avisos", tint = InkColor)
                        }
                    }
                }
            }
            if (state.groups.size > 4) {
                item {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar materia o profesor", color = MutedColor) },
                        leadingIcon = { Icon(AppIcons.Search, contentDescription = null, tint = MutedColor) },
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
            }
            when {
                firstLoad -> item { LoadingState("Cargando tus materias…") }
                state.error != null && state.groups.isEmpty() -> item {
                    ErrorState(state.error ?: "", onRetry = viewModel::loadDashboard)
                }
                state.groups.isEmpty() -> item {
                    MessageState(
                        icon = AppIcons.List,
                        title = "No tienes materias en este periodo",
                        body = "Si crees que es un error, revisa tu inscripción con Servicios Escolares."
                    )
                }
                visible.isEmpty() -> item {
                    Text("Ninguna materia coincide con \"${query.trim()}\".", color = MutedColor, modifier = Modifier.padding(vertical = 24.dp))
                }
            }
            if (state.error != null && state.groups.isNotEmpty()) {
                item { InlineError(state.error ?: "", onRetry = viewModel::loadDashboard) }
            }
            items(visible, key = { it.grupoId }) { group ->
                SubjectRow(group) { onOpenSubject(group) }
            }
        }
    }
}

@Composable
private fun SubjectRow(group: Group, onClick: () -> Unit) {
    WhiteCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(AppIcons.List, SoftPurple, size = 44.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(group.materia, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = InkColor, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (group.docenteNombre.isNotBlank()) {
                    Text(group.docenteNombre, color = MutedColor, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        val detail = listOf(group.horario, group.aula, group.nombre).filter { it.isNotBlank() }.joinToString(" • ")
        if (detail.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(AppIcons.Clock, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(detail, color = InkColor, fontSize = 13.sp)
            }
        }
    }
}
