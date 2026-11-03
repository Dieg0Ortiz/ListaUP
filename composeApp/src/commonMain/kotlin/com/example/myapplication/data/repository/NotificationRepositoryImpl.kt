package com.example.myapplication.data.repository

import com.example.myapplication.core.UserFacingException
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.domain.model.AppNotification
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : NotificationRepository {

    override suspend fun list(): Result<List<AppNotification>> = runCatching {
        val (ownerId, forStudent) = owner()
        apiService.getNotifications(ownerId, forStudent).map {
            AppNotification(
                id = it.id,
                title = it.title.humanizeStatus(),
                message = it.message.humanizeStatus(),
                type = it.type,
                isRead = it.isRead != 0,
                createdAt = it.createdAt,
                permissionId = it.permissionId
            )
        }
    }

    override suspend fun markAllRead(): Result<Unit> = runCatching {
        val (ownerId, forStudent) = owner()
        apiService.markNotificationsRead(ownerId, forStudent)
    }

    // La API arma los avisos con el estado en inglés ("ha sido REJECTED")
    private fun String.humanizeStatus(): String = this
        .replace("APPROVED", "aprobada")
        .replace("REJECTED", "rechazada")
        .replace("PENDING_DIRECTOR", "en revisión de Dirección")
        .replace("PENDING_TUTOR", "en revisión del tutor")

    private fun owner(): Pair<String, Boolean> {
        val user = sessionManager.getUser() ?: throw UserFacingException("Tu sesión terminó. Vuelve a iniciar sesión.")
        return if (user.role == UserRole.PROFESSOR) user.docenteId to false else user.id to true
    }
}
