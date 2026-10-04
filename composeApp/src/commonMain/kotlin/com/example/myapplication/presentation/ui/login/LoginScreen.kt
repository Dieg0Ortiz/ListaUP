package com.example.myapplication.presentation.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.FieldColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.PrimaryLight
import com.example.myapplication.presentation.ui.design.AppDialog
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.viewmodel.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var dialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.isLoggedIn, state.user) {
        if (state.isLoggedIn && state.user != null) {
            onLoginSuccess()
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryColor,
        unfocusedBorderColor = Color.Transparent,
        focusedContainerColor = FieldColor,
        unfocusedContainerColor = FieldColor,
        cursorColor = PrimaryColor,
        focusedTextColor = InkColor,
        unfocusedTextColor = InkColor
    )

    Box(Modifier.fillMaxSize().background(Color.White)) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(72.dp))
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text("UP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp)
        }
        Spacer(Modifier.height(28.dp))
        Text("Bienvenido", color = InkColor, fontWeight = FontWeight.Bold, fontSize = 32.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "Por favor, inicia sesión para continuar.",
            color = MutedColor,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(FieldColor)
                .padding(4.dp)
        ) {
            RoleChip(
                label = "Alumno",
                icon = "🎓",
                selected = !state.asProfessor,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setProfessorMode(false) }
            )
            RoleChip(
                label = "Docente/Personal",
                icon = "💼",
                selected = state.asProfessor,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setProfessorMode(true) }
            )
        }

        Spacer(Modifier.height(18.dp))
        OutlinedTextField(
            value = state.credential,
            onValueChange = viewModel::onCredentialChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Correo Institucional", color = MutedColor) },
            leadingIcon = { Text("✉", color = MutedColor) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Contraseña", color = MutedColor) },
            leadingIcon = { Text("🔒", color = MutedColor) },
            trailingIcon = {
                Text(
                    if (state.passwordVisible) "🙈" else "👁",
                    modifier = Modifier.clickable { viewModel.togglePasswordVisibility() }.padding(8.dp)
                )
            },
            singleLine = true,
            visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { viewModel.login() })
        )

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error ?: "", color = Color(0xFFB00020), fontSize = 13.sp, modifier = Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "¿Olvidaste tu contraseña?",
            color = PrimaryColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { dialog = "Para restablecer tu contraseña, escribe a soporte.it@institucional.edu.mx desde tu correo institucional." }
                .padding(vertical = 8.dp)
        )
        Spacer(Modifier.height(12.dp))
        if (state.isLoading) {
            CircularProgressIndicator(color = PrimaryColor)
        } else {
            PrimaryButton("Iniciar Sesión  →", onClick = { viewModel.login() })
        }
        Spacer(Modifier.height(36.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                dialog = "Soporte IT está disponible de lunes a viernes, 8:00 a 16:00. Correo: soporte.it@institucional.edu.mx"
            }
        ) {
            Text("🎧", fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Text("Contactar Soporte IT", color = MutedColor, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(32.dp))
    }
    if (dialog != null) {
        AppDialog(
            title = "ListaUP",
            body = dialog ?: "",
            confirm = "Entendido",
            onConfirm = { dialog = null },
            onDismiss = { dialog = null }
        )
    }
    }
}

@Composable
private fun RoleChip(
    label: String,
    icon: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 14.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            color = if (selected) PrimaryLight else MutedColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
