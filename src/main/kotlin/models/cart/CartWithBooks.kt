package models.cart

@kotlinx.serialization.Serializable
data class CartWithBooks(
    val id: String,
    val userId: String,
    val items: List<CartItemWithBook>,
    val totalAmount: Double
)
