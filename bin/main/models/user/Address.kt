package models.user

data class Address(
    val id: String,
    val userId: String,
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String
)
