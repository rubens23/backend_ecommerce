package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.user.LoginRequest
import models.user.Role
import repositories.UserRepository
import security.hashing.HashingService
import security.hashing.SaltedHash
import security.token.JwtTokenService
import security.token.TokenClaim
import security.token.TokenConfig

fun Route.loginUser(userRepository: UserRepository, hashingService: HashingService, jwtTokenService: JwtTokenService){
    post("/login"){
        try{
            val loginRequest = call.receive<LoginRequest>()


            if(loginRequest.email.isBlank() || loginRequest.password.isBlank()){
                call.respond(HttpStatusCode.BadRequest, "Email e senha são obrigatórios")
                return@post
            }

            val user = userRepository.getUserByEmail(loginRequest.email)

            if(user == null){
                call.respond(HttpStatusCode.Unauthorized, "Email ou senha inválidos")
                return@post
            }

            val isPasswordValid = hashingService.verify(
                value = loginRequest.password,
                saltedHash = SaltedHash(hash = user.password, salt = user.salt)
            )

            if(!isPasswordValid){
                call.respond(HttpStatusCode.Unauthorized, "Email ou senha inválidos")
                return@post
            }

            // Configuração do token
            val tokenConfig = TokenConfig(
                issuer = "http://localhost:8099", //aqui vai o dominio do backend
                audience = if (user.role == Role.ADMIN) "minhaloja.admin" else "minhaloja.mobile",
                expiresIn = 3600000,
                secret = System.getenv("JWT_SECRET")
            )

            val token = jwtTokenService.generate(
                config = tokenConfig,
                TokenClaim("userId", user.id.toHexString()),
                TokenClaim("role", user.role.toString()))

            // Armazenando o token no cookie http only
            call.response.cookies.append(
                Cookie(
                    name = "JWT",
                    value = token,
                    httpOnly = true,
                    secure = false, // em produção mude para true para usar https
                    path = "/",
                    maxAge = 3600
                )
            )

            call.respond(HttpStatusCode.OK, mapOf("message" to "Login bem-sucedido"))

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }
    }
}