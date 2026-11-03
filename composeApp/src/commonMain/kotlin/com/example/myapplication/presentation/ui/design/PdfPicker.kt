package com.example.myapplication.presentation.ui.design

import androidx.compose.runtime.Composable
import com.example.myapplication.data.remote.ProofFile

/** false donde todavía no hay selector de archivos (iOS, web): la UI oculta el botón. */
expect val isPdfPickerAvailable: Boolean

/**
 * Devuelve una función que abre el selector de PDF del sistema. [onResult] recibe
 * null si el usuario cancela. Se leen como máximo MAX_PROOF_BYTES + 1 bytes, así
 * un archivo demasiado grande se detecta sin cargarlo completo.
 */
@Composable
expect fun rememberPdfPicker(onResult: (ProofFile?) -> Unit): () -> Unit
