package models.user

@kotlinx.serialization.Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
