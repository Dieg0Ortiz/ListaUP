package com.example.myapplication.presentation.ui.design

import androidx.compose.runtime.Composable

/** Intercepta el botón "atrás" del sistema mientras [enabled] sea true. */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
