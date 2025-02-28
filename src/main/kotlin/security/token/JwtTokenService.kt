package security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

class JwtTokenService: TokenService {
    override fun generate(config: TokenConfig, vararg claims: TokenClaim): String {
        var token =  JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn))

        claims.forEach {
                claim->
            token = token.withClaim(claim.name, claim.value)
        }

        return token.sign(Algorithm.HMAC256(config.secret))

    }

    override fun generateRefreshToken(userId: String): String {
        return JWT.create()
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) //7 dias
            .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET")))
    }

    override fun decodeRefreshToken(token: String): DecodedJWT? {
        return try{
            val verifier = JWT.require(Algorithm.HMAC256(System.getenv("JWT_SECRET"))).build()
            verifier.verify(token)
        }catch (e: Exception){
            null
        }
    }
}

