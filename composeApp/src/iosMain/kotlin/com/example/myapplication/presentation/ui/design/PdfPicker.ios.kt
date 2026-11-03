package com.example.myapplication.presentation.ui.design

import androidx.compose.runtime.Composable
import com.example.myapplication.data.remote.ProofFile

// Sin selector de archivos todavía: la UI oculta la opción de adjuntar comprobante
actual val isPdfPickerAvailable: Boolean = false

@Composable
actual fun rememberPdfPicker(onResult: (ProofFile?) -> Unit): () -> Unit = { onResult(null) }
