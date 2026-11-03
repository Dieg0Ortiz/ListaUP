package com.example.myapplication.presentation.ui.design

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.core.AppConfig
import com.example.myapplication.data.remote.ProofFile

actual val isPdfPickerAvailable: Boolean = true

@Composable
actual fun rememberPdfPicker(onResult: (ProofFile?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) {
            onResult(null)
            return@rememberLauncherForActivityResult
        }
        val resolver = context.contentResolver
        val name = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        } ?: "comprobante.pdf"
        val bytes = runCatching {
            resolver.openInputStream(uri)?.use { input ->
                val buffer = ByteArray(AppConfig.MAX_PROOF_BYTES + 1)
                var read = 0
                while (read < buffer.size) {
                    val count = input.read(buffer, read, buffer.size - read)
                    if (count < 0) break
                    read += count
                }
                buffer.copyOf(read)
            }
        }.getOrNull()
        onResult(bytes?.let { ProofFile(name, it) })
    }
    return { launcher.launch("application/pdf") }
}
