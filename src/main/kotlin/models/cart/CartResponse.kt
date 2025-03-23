package models.cart

@kotlinx.serialization.Serializable
data class CartResponse(
    val id: String,
    val userId: String,
    val items: List<CartItemResponse>,
    val totalAmount: Double
)


