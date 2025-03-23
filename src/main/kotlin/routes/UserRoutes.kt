package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.user.RemoveAdminRequest
import models.user.UserResponse
import models.user.toUser
import models.user.toUserResponse
import repositories.UserRepository
import security.hashing.HashingService
import security.hashing.SaltedHash

fun Route.getAllAdmins(userRepository: UserRepository){
    authenticate {
        get("/getAllAdmins"){
            try {
                val admins = userRepository.getAllAdmins()

                if (admins != null) {
                    call.respond(HttpStatusCode.OK, admins)
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

            }
        }
    }
}

fun Route.getAdmin(userRepository: UserRepository){
    authenticate {
        get("/getAdminById/{id}"){
            try{
                val id = call.parameters["id"]

                if(id.isNullOrBlank()){
                    call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                }

                val admin = userRepository.getAdminById(id)

                if(admin != null){
                    call.respond(HttpStatusCode.OK, admin)
                }else{
                    call.respond(HttpStatusCode.NotFound)
                }

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
            }

        }
    }
}

fun Route.getUser(userRepository: UserRepository){
    authenticate {
        get("/getUserById/{id}"){
            try{
                val id = call.parameters["id"]

                if(id.isNullOrBlank()){
                    call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                }

                val user = userRepository.getUserById(id!!)

                if(user != null){
                    call.respond(HttpStatusCode.OK, user.toUserResponse())
                }else{
                    call.respond(HttpStatusCode.NotFound)
                }

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
            }

        }
    }
}

fun Route.updateUser(userRepository: UserRepository){
    authenticate {
        put("/updateUser/{id}"){
            try{

                val id = call.parameters["id"]
                val userReceived = call.receive<UserResponse>()

                if(id.isNullOrBlank()){
                    call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                }



                val user = userRepository.getUserById(id!!)


                if(user != null){
                    val updatedUser = userReceived.toUser(password = user.password, salt = user.salt, addresses = user.addresses.toMutableList())

                    val atualizouUser = userRepository.atualizarUser(updatedUser)
                    if (atualizouUser){
                        call.respond(HttpStatusCode.OK, "usuario atualizado")
                    }else{
                        call.respond(HttpStatusCode.BadRequest, "usuario não foi atualizado")
                    }

                }else{
                    call.respond(HttpStatusCode.NotFound)
                }






            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")


            }
        }
    }
}


fun Route.removeAdmin(
    userRepository: UserRepository,
    hashingService: HashingService
){
    authenticate {
        delete("/removeAdmin/{id}") {
            try{
                val principal = call.principal<JWTPrincipal>()
                val idUserLogado = principal?.payload?.getClaim("userId")?.asString()

                if(idUserLogado.isNullOrBlank()){
                    call.respond(HttpStatusCode.Unauthorized, "Usuário não autenticado")
                    return@delete
                }

                val idParaRemocao = call.parameters["id"]

                if(idParaRemocao.isNullOrBlank()){
                    call.respond(HttpStatusCode.BadRequest, "ID do administrador a ser removido não fornecido")
                    return@delete
                }

                // impede que um administrador remova a si mesmo
                if(idParaRemocao == idUserLogado){
                    call.respond(HttpStatusCode.Forbidden, "Você não pode excluir a si mesmo")
                    return@delete
                }

                val request = call.receive<RemoveAdminRequest>()

                if(request.senha.isBlank()){
                    call.respond(HttpStatusCode.BadRequest, "Senha obrigatória para confirmar a remoção")
                    return@delete
                }

                val adminLogado = userRepository.getUserById(idUserLogado)

                if (adminLogado == null){
                    call.respond(HttpStatusCode.NotFound, "Administrador autenticado não encontrado")
                    return@delete
                }

                // Verifica se a senha está correta
                val isPasswordValid = hashingService.verify(
                    value = request.senha,
                    saltedHash = SaltedHash(hash = adminLogado.password, salt = adminLogado.salt)
                )

                if(!isPasswordValid){
                    call.respond(HttpStatusCode.Unauthorized, "Senha incorreta")
                    return@delete
                }

                // Busca o admin que vai ser removido
                val adminParaRemover = userRepository.getUserById(idParaRemocao)

                if(adminParaRemover == null){
                    call.respond(HttpStatusCode.NotFound, "Administrador a ser removido não encontrado")
                    return@delete
                }

                // Remove o admin do banco de dados
                val sucesso = userRepository.removeUserById(idParaRemocao)

                if (sucesso) {
                    call.respond(HttpStatusCode.OK, "Administrador removido com sucesso")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Erro ao remover administrador")
                }

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Erro inesperado: ${e.message}")
            }

        }
    }
}