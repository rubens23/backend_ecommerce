package models.payment.pix

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class PixPayment(
    @BsonId
    val id: ObjectId = ObjectId(),
    val orderId: String,
    val status: String,
    val statusDetail: String,
    val qrCode: String?,
    val qrCodeBase64: String?,
    val ticketUrl: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)

fun PixPayment.toDto(): PixPaymentDto{
    return PixPaymentDto(
        id = id.toHexString(),
        orderId = orderId,
        status = status,
        statusDetail = statusDetail,
        qrCode = qrCode,
        qrCodeBase64 = qrCodeBase64,
        ticketUrl = ticketUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}