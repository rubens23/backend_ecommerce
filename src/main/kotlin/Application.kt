import `dependency-injection`.appModule
import org.koin.core.context.GlobalContext.startKoin
import services.*
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.user.Address
import models.user.User
import org.koin.core.context.GlobalContext.get
import repositories.UserRepository
import org.koin.java.KoinJavaComponent
import security.hashing.HashingService


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(){


    startKoin{
        modules(appModule)


    }

    // Estou usando esse escopo de corrotina para testar meus repositorios
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val job = coroutineScope.launch {
        val userRepository: UserRepository = KoinJavaComponent.get(UserRepository::class.java)
        val hashingService: HashingService = KoinJavaComponent.get(HashingService::class.java)

        // Teste de registro de usuário
       // println("Iniciando teste: Registrar Usuário")
        val email = "johnny.depp@example.com"
        var user: User? = null
        val senha = "novaSenha123"  //senha antiga: "senha123"
//        val saltedHash = hashingService.generateSaltedHash(senha)
//
//        val newUser = User(
//            name = "Johnny Depp",
//            email = email,
//            password = saltedHash.hash,
//            salt = saltedHash.salt
//        )
//        try {
//            val registeredUser = userRepository.registrarUsuario(newUser)
//            println("Usuário registrado com sucesso! ID: ${registeredUser?.id}")
//        } catch (e: Exception) {
//            println("Erro ao registrar usuário: ${e.message}")
//            return@launch
//        }
//
//        // Teste de busca por email
//        println("Iniciando teste: Buscar Usuário por Email")
//        val user = try {
//            userRepository.getUserByEmail(email).also {
//                if (it != null) println("Usuário encontrado: ${it.name}")
//                else println("Nenhum usuário encontrado com o email: $email")
//            }
//        } catch (e: Exception) {
//            println("Erro ao buscar usuário: ${e.message}")
//            null
//        }
//
        // Teste de autenticação
        println("Iniciando teste: Autenticar Usuário")
        try {
            user = userRepository.autenticarUsuario(email, senha)
            if (user != null) {
                println("Usuário autenticado com sucesso: ${user.name}")
            } else {
                println("Falha na autenticação")
            }
        } catch (e: Exception) {
            println("Erro ao autenticar usuário: ${e.message}")
        }

//        // Teste de alteração de senha
//        println("Iniciando teste: Alterar Senha")
//        val novaSenha = "novaSenha123"
//        try {
//            val alterouSenha = user?.id?.let { userId ->
//                userRepository.alterarSenha(userId.toHexString(), senha, novaSenha)
//            } ?: false
//            if (alterouSenha) println("Senha alterada com sucesso!")
//            else println("Falha ao alterar senha!")
//        } catch (e: Exception) {
//            println("Erro ao alterar senha: ${e.message}")
//        }

//        // Teste de adicionar endereço
//        println("Iniciando teste: Adicionar Endereço")
//        try {
//            val novoEndereco = Address(
//                id = "1",
//                userId = user?.id?.toHexString() ?: "",
//                street = "Rua das Laranjeiras",
//                city = "São Paulo",
//                state = "SP",
//                postalCode = "12345-678",
//                country = "Brasil"
//            )
//            val enderecoAdicionado = user?.id?.let { userId ->
//                userRepository.adicionarEndereco(userId.toHexString(), novoEndereco)
//            }
//            if (enderecoAdicionado != null) {
//                println("Endereço adicionado com sucesso!")
//            } else {
//                println("Falha ao adicionar endereço!")
//            }
//        } catch (e: Exception) {
//            println("Erro ao adicionar endereço: ${e.message}")
//        }

//        // Teste de remoção de endereço
//        println("Iniciando teste: Remover Endereço")
//        try {
//            val removeuEndereco = user?.id?.let { userId ->
//                userRepository.removerEndereco(userId.toHexString(), "1")
//            } ?: false
//            if (removeuEndereco) println("Endereço removido com sucesso!")
//            else println("Falha ao remover endereço!")
//        } catch (e: Exception) {
//            println("Erro ao remover endereço: ${e.message}")
//        }

        // Teste de exclusão de usuário
        println("Iniciando teste: Deletar Usuário")
        try {
            val excluiuUsuario = user?.id?.let { userId ->
                userRepository.deletarUsuario(userId.toHexString())
            } ?: false
            if (excluiuUsuario) println("Usuário deletado com sucesso!")
            else println("Falha ao deletar usuário!")
        } catch (e: Exception) {
            println("Erro ao deletar usuário: ${e.message}")
        }


    }

    runBlocking {
        job.join()
    }












}