package models.user

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class UserDto(
    val id: String = "",
    val name: String,
    val email: String,
    val password: String,
    val salt: String = "",
    val role: Role = Role.USER, // "user" ou "admin"
    val addresses: List<AddressDto> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)
