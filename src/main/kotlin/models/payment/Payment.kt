package models.payment

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonRepresentation
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Payment(
    @BsonId
    val id: ObjectId = ObjectId(),
    val orderId: String,
    val userId: String,
    val amount: Double,
    val paymentMethod: String, // Ex.: "pix", "credit_card"
    val status: String, // Ex.: "pending", "completed", "failed"
    val transactionId: String?, // ID fornecido pelo gateway de pagamento
    val createdAt: Long = System.currentTimeMillis()
)

fun Payment.toResponse(userName: String): PaymentResponse{
    return PaymentResponse(
        id = this.id.toHexString(),
        orderId = this.orderId,
        userId = this.userId,
        userName = userName,
        amount = this.amount,
        paymentMethod = this.paymentMethod.lowercase(),
        status = this.status.lowercase(),
        transactionId = this.transactionId,
        createdAt = Instant.ofEpochMilli(this.createdAt)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
    )
}



