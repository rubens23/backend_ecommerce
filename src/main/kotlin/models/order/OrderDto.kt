package models.order

import models.user.Address
import models.user.AddressDto
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class OrderDto(
    val id: String,
    val userId: String,
    val items: List<OrderItemDto>,
    val totalAmount: Double,
    val address: AddressDto,
    val orderStatus: String,
    val createdAt: Long,
    val updatedAt: Long,
    val paymentIds: List<String>,
    val paymentMethod: String
)
