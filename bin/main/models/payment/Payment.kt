package models.payment

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Payment(
    @BsonId
    val id: ObjectId = ObjectId(),
    val orderId: String,
    val userId: String,
    val amount: Double,
    val paymentMethod: PaymentMethod, // Ex.: "pix", "credit_card"
    val status: String, // Ex.: "pending", "completed", "failed"
    val transactionId: String?, // ID fornecido pelo gateway de pagamento
    val createdAt: Long = System.currentTimeMillis()
)
