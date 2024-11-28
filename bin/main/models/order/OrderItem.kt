package models.order

data class OrderItem(
    val productId: String,
    val title: String,
    val quantity: Int,
    val price: Double
)
