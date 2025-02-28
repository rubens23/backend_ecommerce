package repositories

import models.token.RefreshToken
import org.bson.types.ObjectId

interface RefreshTokenRepository {
    suspend fun saveRefreshToken(refreshToken: RefreshToken)
    suspend fun getRefreshToken(userId: ObjectId): RefreshToken?
    suspend fun deleteRefreshToken(userId: ObjectId)
}