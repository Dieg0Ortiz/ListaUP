package com.example.myapplication.presentation.ui.design

import androidx.compose.runtime.Composable

// Sin botón atrás del sistema: la pantalla depende de su propio botón Regresar
@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit
