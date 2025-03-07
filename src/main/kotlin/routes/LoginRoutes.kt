package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.token.RefreshToken
import models.user.LoginRequest
import models.user.Role
import org.bson.types.ObjectId
import repositories.RefreshTokenRepository
import repositories.UserRepository
import security.hashing.HashingService
import security.hashing.SaltedHash
import security.token.JwtTokenService
import security.token.TokenClaim
import security.token.TokenConfig

fun Route.loginUser(userRepository: UserRepository,
                    hashingService: HashingService,
                    jwtTokenService: JwtTokenService,
tokenConfig: TokenConfig,
refreshTokenRepository: RefreshTokenRepository){
    post("/login"){
        try{
            val loginRequest = call.receive<LoginRequest>()
            loginRequest


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



            val token = jwtTokenService.generate(
                config = tokenConfig,
                TokenClaim("userId", user.id.toHexString()),
                TokenClaim("role", user.role.toString()))

            val refreshToken = createNewRefreshToken(user.id.toHexString(), jwtTokenService)
            val expiresAt = createTokenExpirationTime()
            refreshTokenRepository.deleteRefreshToken(ObjectId(user.id.toHexString()))
            saveNewRefreshToken(user.id.toHexString(), refreshToken, expiresAt, refreshTokenRepository)

            // Armazenando o token no cookie http only
            call.response.cookies.append(
                Cookie(
                    name = "JWT",
                    value = token,
                    httpOnly = true,
                    secure = false, // em produção mude para true para usar https
                    path = "/",
                    maxAge = 3600,

                )
            )

            // salvar o refresh token num cookie
            call.response.cookies.append(
                Cookie(
                    name = "RefreshToken",
                    value = refreshToken,
                    httpOnly = true,
                    secure = false,
                    path = "/",
                    maxAge = 7 * 24 * 60 * 60
                )
            )


            call.respond(HttpStatusCode.OK, mapOf("message" to "Login bem-sucedido"))

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }
    }
}

fun Route.refreshToken(refreshTokenRepository: RefreshTokenRepository,
userRepository: UserRepository,
jwtTokenService: JwtTokenService,
tokenConfig: TokenConfig){
    post("/refresh"){
        try{
            val refreshToken = call.request.cookies["RefreshToken"]
            if(refreshToken == null){
                call.respond(HttpStatusCode.Unauthorized, "Refresh token não fornecido")
                return@post
            }

            // Verificar e validar o refresh token
            val decodedRefreshToken = jwtTokenService.decodeRefreshToken(refreshToken)

            if(decodedRefreshToken == null){
                call.respond(HttpStatusCode.Unauthorized, "Refresh token inválido ou expirado")
                return@post
            }

            // Recuperar o usuário relacionado ao refresh token
            val userId = decodedRefreshToken.getClaim("userId").asString()
            val user = userRepository.getUserById(userId)
            if(user == null){
                call.respond(HttpStatusCode.Unauthorized, "Usuário não encontrado")
                return@post
            }

            // Verificar se o refresh token ainda é válido no banco
            val storedToken = refreshTokenRepository.getRefreshToken(ObjectId(userId))
            if(storedToken == null || storedToken.token != refreshToken){
                call.respond(HttpStatusCode.Unauthorized, "Refresh token inválido")
                return@post
            }

            // Gerar um novo Access Token (JWT)
            val accessToken = jwtTokenService.generate(
                config = tokenConfig,
                TokenClaim("userId", user.id.toHexString()),
                TokenClaim("role", user.role.toString())
            )

            val newRefreshToken = jwtTokenService.generateRefreshToken(user.id.toHexString())
            val expiresAt = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000) //7 dias

            // Remover o token antigo e salvar o novo
            refreshTokenRepository.deleteRefreshToken(ObjectId(user.id.toHexString()))
            saveNewRefreshToken(user.id.toHexString(), newRefreshToken, expiresAt, refreshTokenRepository)


            call.response.cookies.append(
                Cookie(
                    name = "JWT",
                    value = accessToken,
                    httpOnly = true,
                    secure = false, // em produção mude para true para usar https
                    path = "/",
                    maxAge = 3600,

                    )
            )

            call.response.cookies.append(
                Cookie(
                    name = "RefreshToken",
                    value = newRefreshToken,
                    httpOnly = true,
                    secure = false,
                    path = "/",
                    maxAge = 7 * 24 * 60 * 60
                )
            )

            call.respond(HttpStatusCode.OK, mapOf("accessToken" to accessToken))



        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Erro ao tentar renovar o token: ${e.message}")
        }
    }
}

private suspend fun saveNewRefreshToken(userId: String, refreshToken: String, expiresAt: Long,
                                       refreshTokenRepository: RefreshTokenRepository){

    // Armazenar o Refresh Token no banco de dados
    refreshTokenRepository.saveRefreshToken(
        RefreshToken(
            userId = ObjectId(userId),
            token = refreshToken,
            expiresAt = expiresAt
        )
    )
}

private suspend fun createTokenExpirationTime(): Long{
    return  System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000) // 7 dias


}

private suspend fun createNewRefreshToken(userId: String, jwtTokenService: JwtTokenService,
                                       ): String {
    // Gerar o Refresh Token
    return jwtTokenService.generateRefreshToken(userId)


}