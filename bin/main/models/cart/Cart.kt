package models.cart

data class Cart(
    val userId: String,
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = items.sumOf { it.price * it.quantity }
)
