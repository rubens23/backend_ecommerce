package models.user

import io.ktor.server.auth.*

data class UserSession(
    val userId: String,
    val role: String
): Principal