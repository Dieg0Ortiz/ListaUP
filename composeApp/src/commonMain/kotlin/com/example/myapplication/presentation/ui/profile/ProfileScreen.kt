package com.example.myapplication.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.HeroColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.ui.design.AvatarCircle
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.ui.design.WhiteCard
import com.example.myapplication.presentation.viewmodel.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val user = state.user
    val professor = user?.role == UserRole.PROFESSOR

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) onLogout()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(HeroColor)
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvatarCircle(
                    user?.name?.take(1)?.uppercase() ?: "?",
                    modifier = Modifier,
                    background = Color.White,
                    content = PrimaryColor
                )
                Spacer(Modifier.height(12.dp))
                Text(user?.name ?: "Usuario", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text(if (professor) "Docente" else "Alumno", color = Color.White.copy(alpha = 0.85f))
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            InfoRow("Correo", user?.email ?: "No disponible")
            if (!professor) InfoRow("Matrícula", user?.matricula ?: "No disponible")
            if (professor) InfoRow("ID Docente", user.docenteId.ifBlank { "No disponible" })
            InfoRow("Rol", if (professor) "Profesor" else "Estudiante")
        }
        if (professor) {
            item {
                Spacer(Modifier.height(8.dp))
                WhiteCard(modifier = Modifier.padding(horizontal = 20.dp), onClick = onOpenSettings) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Ajustes de Asistencia", color = InkColor, fontWeight = FontWeight.Bold)
                            Text("Umbrales, alertas y recordatorios", color = MutedColor, fontSize = 12.sp)
                        }
                        Text("›", color = PrimaryColor, fontSize = 24.sp)
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                "Cerrar Sesión",
                onClick = { viewModel.logout() },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    WhiteCard(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
        Text(label, color = MutedColor, fontSize = 12.sp)
        Text(value, color = InkColor, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}
