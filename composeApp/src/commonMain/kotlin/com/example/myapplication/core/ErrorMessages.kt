package com.example.myapplication.core

import com.example.myapplication.data.remote.ApiException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/** Mensaje que se le puede mostrar al usuario, sin detalles técnicos. */
fun Throwable.userMessage(): String = when (this) {
    is ApiException -> when (code) {
        401, 403 -> "Tu sesión no es válida o no tienes permiso. Vuelve a iniciar sesión."
        404 -> detail ?: "No se encontró la información solicitada."
        in 400..499 -> detail ?: "La solicitud no es válida. Revisa los datos."
        else -> "El servidor tuvo un problema. Intenta de nuevo en unos minutos."
    }
    is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException ->
        "El servidor tardó demasiado en responder. Revisa tu conexión e intenta de nuevo."
    is IOException -> "No hay conexión con el servidor. Revisa tu internet e intenta de nuevo."
    is SerializationException -> "El servidor respondió con datos inesperados. Intenta más tarde."
    is UserFacingException -> message ?: "Ocurrió un error."
    else -> "Ocurrió un error inesperado. Intenta de nuevo."
}

/** Error cuyo mensaje ya está escrito para el usuario. */
class UserFacingException(message: String) : Exception(message)
