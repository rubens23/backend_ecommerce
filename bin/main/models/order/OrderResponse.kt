package models.order

data class OrderResponse(
    val success: Boolean,
    val message: String? = null,
    val order: Order? = null
)
