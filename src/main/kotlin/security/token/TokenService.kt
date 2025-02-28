package security.token

import com.auth0.jwt.interfaces.DecodedJWT

interface TokenService {

    fun generate(config: TokenConfig, vararg claims: TokenClaim): String
    fun generateRefreshToken(userId: String): String

    fun decodeRefreshToken(token: String): DecodedJWT?
}