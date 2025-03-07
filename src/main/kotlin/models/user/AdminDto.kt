package models.user

@kotlinx.serialization.Serializable
data class AdminDto(
    val id: String,
    val email: String,
    val status: Boolean,
    val name: String,
    val creationDate: String,

)
