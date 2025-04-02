package models.payment

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class CreatePaymentRequest(
    val id: String,
    val orderId: String,
    val userId: String,
    val amount: Double,
    val paymentMethod: String,
    val status: String,
    val transactionId: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val details: Map<String, String?>,
    val pixPaymentId: String? = null,
    val creditCardPaymentId: String? = null
)
