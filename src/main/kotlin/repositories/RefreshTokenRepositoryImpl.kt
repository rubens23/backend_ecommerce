package repositories

import models.token.RefreshToken
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class RefreshTokenRepositoryImpl: RefreshTokenRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()

    private val refreshTokenDb = db.getCollection<RefreshToken>()

    override suspend fun saveRefreshToken(refreshToken: RefreshToken) {
        try {
            refreshTokenDb.insertOne(refreshToken)

        }catch (e: Exception){
            logRepository.registrarLog(e, "save refresh token", "Refresh Token", refreshToken.userId.toHexString())
        }
    }

    override suspend fun getRefreshToken(userId: ObjectId): RefreshToken? {
        return try {
            refreshTokenDb.findOne(RefreshToken::userId eq userId)

        }catch (e: Exception){
            logRepository.registrarLog(e, "get refresh token", "Refresh Token", null)
            null
        }
    }

    override suspend fun deleteRefreshToken(userId: ObjectId) {
        try {
            refreshTokenDb.deleteMany(RefreshToken::userId eq userId)

        }catch (e: Exception){
            logRepository.registrarLog(e, "delete refresh token", "Refresh Token", null)
        }
    }


}