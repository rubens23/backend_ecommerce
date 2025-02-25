package plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import security.token.TokenConfig



//fun Application.configureSecurity(config: TokenConfig) {
//    authentication {
//        jwt {
//            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
//
//            // Configuração do Verifier para validar o token
//            verifier(
//                JWT
//                    .require(Algorithm.HMAC256(config.secret))
//                    .withAudience(config.audience)
//                    .withIssuer(config.issuer)
//                    .build()
//            )
//
//            // Pegando o token do Header ou do Cookie corretamente
//            authHeader { call ->
//                try {
//                    val tokenFromHeader = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
//                    val tokenFromCookie = call.request.cookies["JWT"]?.trim()
//
//                    // Logando os tokens recebidos
//                    println("Token recebido no Header: $tokenFromHeader")
//                    println("Token recebido no Cookie: $tokenFromCookie")
//
//                    // Determinando o token final a ser utilizado
//                    val token = tokenFromHeader ?: (tokenFromCookie ?: "")
//                    println("Token final para validação: $token")  // Logando o token final para depuração
//
//                    if (token.isNotEmpty()) {
//                        println("cabeçalho foi criado")  // Logando o token final para depuração
//                        return@authHeader HttpAuthHeader.Single("Bearer", token)  // Retorna o cabeçalho
//                    } else {
//                        return@authHeader null  // Retorna null se não houver token
//                    }
//                } catch (e: Exception) {
//                    // Logando erros
//                    println("Erro ao processar token: ${e.message}")
//                    return@authHeader null // Retorna null se houver erro
//                }
//            }
//
//            println("Vou chamar o validate")
//
//            // Validação do JWT
//            validate { credential ->
//                println("Payload do Token: ${credential.payload}")  // Logando o payload do token para depuração
//                    JWTPrincipal(credential.payload)
//
//            }
//        }
//    }
//}

fun Application.configureSecurity(config: TokenConfig) {
    authentication {
        jwt {
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )
            authHeader {
                call ->
                val token = call.request.cookies["JWT"] ?: return@authHeader null
                try {
                    // Retorna o token extraído do cookie
                    parseAuthorizationHeader("Bearer $token")
                } catch (cause: IllegalArgumentException) {
                    null
                }
            }
            validate { credential ->
                if (credential.payload.audience.contains(config.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}