package repositories

import models.user.Address
import models.user.User

interface UserRepository {
    suspend fun registrarUsuario(usuario: User): User
    suspend fun autenticarUsuario(email: String, senha: String): User?
    suspend fun atualizarPerfil(usuario: User): User
    suspend fun adicionarEndereco(usuarioId: String, endereco: Address): Address
    suspend fun removerEndereco(usuarioId: String, enderecoId: String): Boolean
}