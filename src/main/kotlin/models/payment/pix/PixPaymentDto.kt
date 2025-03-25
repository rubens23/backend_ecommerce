package models.payment.pix

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class PixPaymentDto(
    val id: String,
    val orderId: String,
    val status: String,
    val statusDetail: String,
    val qrCode: String?,
    val qrCodeBase64: String?,
    val ticketUrl: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)
