package repositories

import models.user.Address
import models.user.User

interface UserRepository {
    suspend fun registrarUsuario(usuario: User): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserById(id: String): User?

    suspend fun alterarSenha(usuarioId: String, senhaAntiga: String, novaSenha: String): Boolean

    suspend fun deletarUsuario(usuarioId: String): Boolean

    suspend fun verificarEmailExistente(email: String): Boolean


    suspend fun autenticarUsuario(email: String, senha: String): User?
    suspend fun atualizarPerfil(usuario: User): User?
    suspend fun adicionarEndereco(usuarioId: String, endereco: Address): Address?
    suspend fun removerEndereco(usuarioId: String, enderecoId: String): Boolean
    suspend fun getUsersById(responsaveisIDs: Set<String>): List<User>?
}