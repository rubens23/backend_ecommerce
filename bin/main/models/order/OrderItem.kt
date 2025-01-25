package models.order

import org.bson.types.ObjectId

data class OrderItem(
    val id: ObjectId = ObjectId(),
    val productId: String,
    val quantity: Int,
    val price: Double
)
