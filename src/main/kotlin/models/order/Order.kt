package models.order

import models.user.Address

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val address: Address,
    val paymentStatus: String = "pending", // "pending", "approved", "cancelled"
    val orderStatus: String = "processing", // "processing", "shipped", "delivered"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)
