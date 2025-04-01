package models.payment.pix

import kotlinx.serialization.Serializable

@Serializable
data class PixPaymentResponse(
    val id: Long,
    val status: String,
    val statusDetail: String,
    val qrCode: String?,
    val qrCodeBase64: String?,
    val ticketUrl: String?,
    val vencimento: Long
)
