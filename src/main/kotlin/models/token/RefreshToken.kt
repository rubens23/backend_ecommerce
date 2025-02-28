package models.token

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class RefreshToken(
    @BsonId
    val id: ObjectId = ObjectId(),
    val userId: ObjectId,
    val token: String,
    val expiresAt: Long
)
