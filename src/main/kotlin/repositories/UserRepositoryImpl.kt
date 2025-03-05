package repositories

import com.mongodb.client.model.Updates.*
import models.reports.SalesReport
import models.user.Address
import models.user.User
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.setValue
import security.hashing.HashingService
import security.hashing.SaltedHash


class UserRepositoryImpl: UserRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()
    private val hashingService: HashingService by inject()


    private val usersDb = db.getCollection<User>()

    override suspend fun registrarUsuario(usuario: User): User? {
        return try {
            usersDb.insertOne(usuario)
            usuario
        } catch (e: Exception) {
            logRepository.registrarLog(e, "registrar usuario", "User", null)
            null

        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return try {
            usersDb.findOne(User::email eq email)
        } catch (e: Exception) {
            logRepository.registrarLog(e, "pegar usuario por email", "User", null)
            null
        }
    }

    override suspend fun getUserById(id: String): User? {
        return try {
            usersDb.findOneById(ObjectId(id))
        } catch (e: Exception) {
            logRepository.registrarLog(e, "pegar usuario por id", "User", null)
            null
        }
    }

    override suspend fun alterarSenha(usuarioId: String, senhaAntiga: String, novaSenha: String): Boolean {
        return try {
            val usuario = getUserById(usuarioId)
            if (usuario != null) {
                val saltedHash = SaltedHash(
                    hash = usuario.password, // Hash armazenado
                    salt = usuario.salt
                )

                // Verificar se a senha antiga fornecida é válida
                if (hashingService.verify(senhaAntiga, saltedHash)) {
                    // Gerar um novo salted hash para a nova senha
                    val novoSaltedHash = hashingService.generateSaltedHash(novaSenha)

                    // Atualizar o hash e o salt no banco de dados
                    val updateResult = usersDb.updateOneById(
                        ObjectId(usuarioId),
                        combine(
                            setValue(User::password, novoSaltedHash.hash),
                            setValue(User::salt, novoSaltedHash.salt)
                        )
                    )
                    return updateResult.modifiedCount > 0
                }
            }
            false
        } catch (e: Exception) {
            logRepository.registrarLog(e, "alterar senha", "User", usuarioId)
            false
        }
    }

    override suspend fun deletarUsuario(usuarioId: String): Boolean {
        return try {
            val deleteResult = usersDb.deleteOneById(ObjectId(usuarioId))
            deleteResult.deletedCount > 0
        } catch (e: Exception) {
            logRepository.registrarLog(e, "deletar usuario", "User", usuarioId)
            false
        }
    }

    override suspend fun verificarEmailExistente(email: String): Boolean {
        return try {
            usersDb.findOne(User::email eq email) != null
        } catch (e: Exception) {
            logRepository.registrarLog(e, "verificar email existente", "User", null)
            false
        }
    }

    override suspend fun autenticarUsuario(email: String, senha: String): User? {
        return try {
            val usuario = getUserByEmail(email)
            if (usuario != null) {
                val saltedHash = SaltedHash(hash = usuario.password, salt = usuario.salt)
                if (hashingService.verify(senha, saltedHash)) usuario else null
            } else null
        } catch (e: Exception) {
            logRepository.registrarLog(e, "autenticar usuario", "User", null)
            null
        }
    }

    override suspend fun atualizarPerfil(usuario: User): User? {
        return try {
            val updateResult = usersDb.updateOneById(
                usuario.id,
                combine(
                    setValue(User::name, usuario.name),
                    setValue(User::updatedAt, System.currentTimeMillis())
                )
            )
            if (updateResult.modifiedCount > 0) usuario else null
        } catch (e: Exception) {
            logRepository.registrarLog(e, "atualizar perfil", "User", usuario.id.toHexString())
            null
        }
    }

    override suspend fun adicionarEndereco(usuarioId: String, endereco: Address): Address? {
        return try {
            val updateResult = usersDb.updateOneById(
                ObjectId(usuarioId),
                addToSet(User::addresses.name, endereco)
            )
            if (updateResult.modifiedCount > 0) endereco else null
        } catch (e: Exception) {
            logRepository.registrarLog(e, "adicionar endereço", "User", usuarioId)
            null
        }
    }

    override suspend fun removerEndereco(usuarioId: String, enderecoId: String): Boolean {
        return try {
            val usuario = getUserById(usuarioId)
            if (usuario != null) {
                val endereco = usuario.addresses.find { it.id == enderecoId }
                if (endereco != null) {
                    val updateResult = usersDb.updateOneById(
                        ObjectId(usuarioId),
                        pull(User::addresses.name, endereco)
                    )
                    updateResult.modifiedCount > 0
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            logRepository.registrarLog(e, "registrar usuario", "User", usuarioId)
            false
        }
    }

    override suspend fun getUsersById(responsaveisIDs: Set<String>): List<User>? {
        return try {
            // Convertendo os IDs de String para ObjectId
            val objectIds = responsaveisIDs.map { ObjectId(it) }

            // Buscando os usuários com os IDs fornecidos
            usersDb.find(User::id `in` objectIds).toList() // Retorna a lista de usuários encontrados
        } catch (e: Exception) {
            logRepository.registrarLog(e, "buscar usuários por IDs", "User", null)
            null
        }
    }


}