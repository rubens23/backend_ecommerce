package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.user.Role
import models.user.User
import models.user.UserDto
import models.user.toAddress
import repositories.UserRepository
import security.hashing.HashingService
import security.token.TokenService

fun Route.registerNewUser(userRepository: UserRepository,
                          hashingService: HashingService,
){
    post("/registerNewUser"){
        try {
            val newUser = call.receive<UserDto>()

            val saltedHash = hashingService.generateSaltedHash(
                value = newUser.password
            )


            val userToSave = User(
                name = newUser.name,
                email = newUser.email,
                password = saltedHash.hash,
                salt = saltedHash.salt,
                role = Role.ADMIN,
                addresses = newUser.addresses.map { it.toAddress() },
                createdAt = System.currentTimeMillis()
            )

            userRepository.registrarUsuario(userToSave)

            call.respond(HttpStatusCode.OK)

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

        }
    }
}