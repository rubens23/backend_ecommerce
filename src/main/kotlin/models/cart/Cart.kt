package models.cart

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Cart(
    @BsonId
    val id: ObjectId = ObjectId(),
    val userId: String,
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = items.sumOf { it.price * it.quantity }
)
