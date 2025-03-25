package models.user

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.text.SimpleDateFormat
import java.util.*

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
    val updatedAt: Long? = null,
    val isActive: Boolean? = true
)

fun User.toAdminDto(): AdminDto{
    return AdminDto(
        id=id.toHexString(),
        email = email,
        status = isActive?:true,
        name = name,
        creationDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Date(this.createdAt))

    )
}

fun User.toUserResponse(): UserResponse{
    return UserResponse(
        id = id.toHexString(),
        name = name,
        email = email,
        role = if(role.name == "USER") "USER" else if(role.name == "ADMIN") "ADMIN" else "INVALIDO",
        addresses = if(addresses.isNullOrEmpty()) null else addresses[0].toDto(),
        createdAt= createdAt,
        updatedAt = updatedAt

    )
}


