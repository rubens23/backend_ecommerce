package models.user

@kotlinx.serialization.Serializable
data class AddressDto(
    val id: String,
    val userId: String,
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String
)

fun Address.toDto(): AddressDto {
    return AddressDto(
        id = this.id,
        userId = this.userId,
        street = this.street,
        city = this.city,
        state = this.state,
        postalCode = this.postalCode,
        country = this.country
    )
}
