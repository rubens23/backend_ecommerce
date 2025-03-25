package models.user

import kotlinx.serialization.SerialName
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class UserResponse(
    val id: String = "",
    val name: String,
    val email: String,
    val role: String, // "user" ou "admin"
    val addresses: AddressDto?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null

)

fun UserResponse.toUser(password: String, salt: String, addresses: MutableList<Address>): User{
    return User(
        id = ObjectId(id),
        name = name,
        email = email,
        password = password,
        salt = salt,
        role = if(role == "USER") Role.USER else if(role == "ADMIN") Role.ADMIN else Role.INVALIDO,
        addresses = (this.addresses?.let{ listOf(it.toAddress()) } ?: emptyList()) + addresses,
        createdAt = createdAt,
        updatedAt = updatedAt

    )
}
