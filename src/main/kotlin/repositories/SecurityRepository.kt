package repositories

import models.user.User

interface SecurityRepository {
    suspend fun autenticarComJWT(token: String): User?
    suspend fun verificarPermissoes(usuarioId: String, acao: String): Boolean
}