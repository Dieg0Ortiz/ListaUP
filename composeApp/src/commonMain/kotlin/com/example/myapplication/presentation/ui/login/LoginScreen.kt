package com.example.myapplication.presentation.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.ime
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.ui.semantics.Role
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.ui.design.AppIcons
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.snapshotFlow
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
    val scroll = rememberScrollState()
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    // Con el teclado abierto, deja visible el botón de iniciar sesión
    LaunchedEffect(imeVisible) {
        // maxValue crece mientras el teclado termina de abrir
        if (imeVisible) snapshotFlow { scroll.maxValue }.collect { scroll.scrollTo(it) }
    }

    LaunchedEffect(state.isLoggedIn, state.user) {
        if (state.isLoggedIn && state.user != null) {
            onLoginSuccess()
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedLabelColor = PrimaryColor,
        unfocusedLabelColor = MutedColor,
        errorBorderColor = RoseText,
        errorContainerColor = FieldColor,
        errorLabelColor = RoseText,
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
            .imePadding()
            .verticalScroll(scroll)
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
                .selectableGroup()
        ) {
            RoleChip(
                label = "Alumno",
                selected = !state.asProfessor,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setProfessorMode(false) }
            )
            RoleChip(
                label = "Docente",
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
            label = { Text(if (state.asProfessor) "Usuario docente" else "Matrícula") },
            leadingIcon = { Icon(AppIcons.User, contentDescription = null, tint = MutedColor) },
            isError = state.error != null,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            leadingIcon = { Icon(AppIcons.Lock, contentDescription = null, tint = MutedColor) },
            trailingIcon = {
                TextButton(onClick = viewModel::togglePasswordVisibility) {
                    Text(
                        if (state.passwordVisible) "Ocultar" else "Mostrar",
                        color = PrimaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            isError = state.error != null,
            singleLine = true,
            visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { viewModel.login() })
        )

        if (state.error != null) {
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(AppIcons.Warning, contentDescription = null, tint = RoseText, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(state.error ?: "", color = RoseText, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(
            onClick = { dialog = "Para restablecer tu contraseña, escribe a soporte.it@institucional.edu.mx desde tu correo institucional." },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("¿Olvidaste tu contraseña?", color = PrimaryColor, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(12.dp))
        PrimaryButton(
            if (state.isLoading) "Entrando…" else "Iniciar sesión",
            onClick = { viewModel.login() },
            loading = state.isLoading
        )
        Spacer(Modifier.height(28.dp))
        TextButton(onClick = {
            dialog = "Soporte IT está disponible de lunes a viernes, 8:00 a 16:00. Correo: soporte.it@institucional.edu.mx"
        }) {
            Text("Contactar soporte", color = MutedColor, fontWeight = FontWeight.Medium)
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
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) PrimaryColor else Color.Transparent)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (selected) Color.White else MutedColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
