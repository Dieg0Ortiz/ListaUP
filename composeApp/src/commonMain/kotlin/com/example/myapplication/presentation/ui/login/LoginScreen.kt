package com.example.myapplication.presentation.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.AppConfig
import com.example.myapplication.presentation.theme.CardStroke
import com.example.myapplication.presentation.theme.FieldColor
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.PrimaryLight
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppDialog
import com.example.myapplication.presentation.ui.design.AppIcons
import com.example.myapplication.presentation.ui.design.PrimaryButton
import com.example.myapplication.presentation.viewmodel.LoginViewModel
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.logo_upchiapas
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var dialog by remember { mutableStateOf<String?>(null) }
    val scroll = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val passwordFocus = remember { FocusRequester() }
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

    val submit = {
        focusManager.clearFocus()
        viewModel.login()
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryColor,
        unfocusedBorderColor = Color.Transparent,
        errorBorderColor = RoseText.copy(alpha = 0.6f),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = FieldColor,
        errorContainerColor = FieldColor,
        focusedLabelColor = PrimaryColor,
        unfocusedLabelColor = MutedColor,
        errorLabelColor = RoseText,
        focusedLeadingIconColor = PrimaryColor,
        unfocusedLeadingIconColor = MutedColor,
        errorLeadingIconColor = RoseText,
        cursorColor = PrimaryColor,
        focusedTextColor = InkColor,
        unfocusedTextColor = InkColor,
        errorTextColor = InkColor
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(0f to SoftPurple, 0.55f to Color.White))
    ) {
        BackgroundDecor()

        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
        // Centrado vertical que sigue funcionando con scroll cuando el teclado reduce el espacio
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scroll)
                .heightIn(min = maxHeight)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(Res.drawable.logo_upchiapas),
                contentDescription = "Universidad Politécnica de Chiapas",
                modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth(0.78f)
            )
            Spacer(Modifier.height(36.dp))

            // Tarjeta del formulario; el ancho máximo evita campos larguísimos en tablet o web
            Column(
                Modifier
                    .widthIn(max = 460.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = PrimaryColor.copy(alpha = 0.10f),
                        spotColor = PrimaryColor.copy(alpha = 0.14f)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .border(1.dp, CardStroke, RoundedCornerShape(28.dp))
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    "Inicia sesión",
                    color = InkColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.semantics { heading() }
                )
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = state.credential,
                    onValueChange = viewModel::onCredentialChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Matrícula o usuario") },
                    leadingIcon = { Icon(AppIcons.User, contentDescription = null) },
                    isError = state.error != null,
                    enabled = !state.isLoading,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() })
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    modifier = Modifier.fillMaxWidth().focusRequester(passwordFocus),
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(AppIcons.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = viewModel::togglePasswordVisibility) {
                            Icon(
                                if (state.passwordVisible) AppIcons.VisibilityOff else AppIcons.Visibility,
                                contentDescription = if (state.passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = MutedColor
                            )
                        }
                    },
                    isError = state.error != null,
                    enabled = !state.isLoading,
                    singleLine = true,
                    visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { submit() })
                )

                TextButton(
                    onClick = {
                        dialog = "Para restablecer tu contraseña, escribe a ${AppConfig.SUPPORT_EMAIL} desde tu correo institucional."
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("¿Olvidaste tu contraseña?", color = PrimaryColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }

                AnimatedVisibility(
                    visible = state.error != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ErrorBanner(state.error.orEmpty())
                }

                Spacer(Modifier.height(8.dp))
                PrimaryButton(
                    if (state.isLoading) "Entrando…" else "Iniciar sesión",
                    onClick = submit,
                    loading = state.isLoading
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("¿Problemas para entrar?", color = MutedColor, fontSize = 14.sp)
                TextButton(onClick = {
                    dialog = "Escribe a ${AppConfig.SUPPORT_EMAIL} desde tu correo institucional e indica tu matrícula o usuario."
                }) {
                    Text("Contactar soporte", color = PrimaryColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
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

/** Círculos suaves de fondo para que la parte superior no se vea vacía. */
@Composable
private fun BackgroundDecor() {
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(
            color = PrimaryColor.copy(alpha = 0.07f),
            radius = size.width * 0.55f,
            center = Offset(size.width * 1.05f, -size.width * 0.05f)
        )
        drawCircle(
            color = PrimaryLight.copy(alpha = 0.06f),
            radius = size.width * 0.38f,
            center = Offset(-size.width * 0.12f, size.width * 0.42f)
        )
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(RoseSoft)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(AppIcons.Warning, contentDescription = null, tint = RoseText, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(message, color = RoseText, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 19.sp)
    }
}
