package models.user

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String, // Deve ser armazenada como hash
    val role: String = "user", // "user" ou "admin"
    val addresses: List<Address> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)
