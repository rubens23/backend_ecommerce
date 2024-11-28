package models.logs

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class ActivityLog(
    @BsonId
    val id: ObjectId = ObjectId(),
    val userId: String?,
    val action: String, // Ex.: "login", "checkout", "update_profile"
    val details: String?,
    val createdAt: Long = System.currentTimeMillis()
)
