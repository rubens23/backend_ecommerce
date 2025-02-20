package models.order

import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class OrderItemDto(
    val id: String,
    val productId: String,
    val quantity: Int,
    val price: Double

)

fun OrderItem.toDto(): OrderItemDto{
    return OrderItemDto(
        id = this.id.toHexString(),
        productId = this.productId,
        quantity = this.quantity,
        price = this.price
    )
}
