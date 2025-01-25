package models.user

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: ObjectId = ObjectId(),
    val name: String,
    val email: String,
    val password: String, // Deve ser armazenada como hash
    val salt: String = "", // Salt armazenado em formato Base64
    val role: Role = Role.USER, // "user" ou "admin"
    val addresses: List<Address> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)
