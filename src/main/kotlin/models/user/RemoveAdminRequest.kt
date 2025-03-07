package models.user

@kotlinx.serialization.Serializable
data class RemoveAdminRequest(
    val senha: String
)
