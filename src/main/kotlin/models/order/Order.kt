package models.order

import models.payment.Payment
import models.user.Address
import models.user.toDto
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Order(
    @BsonId
    val id: ObjectId = ObjectId(),
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val address: Address,
    val orderStatus: String = "pending_payment", //pending_payment, payment_failed
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null,
    val paymentIds: List<String>,
    val paymentMethod: String
)

fun Order.toDto(): OrderDto{
    return OrderDto(
        id = this.id.toHexString(),
        userId = this.userId,
        items = this.items.map{it.toDto()},
        totalAmount = this.totalAmount,
        address = this.address.toDto(),
        orderStatus = this.orderStatus,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt?:0,
        paymentIds = this.paymentIds,
        paymentMethod = this.paymentMethod

    )
}
