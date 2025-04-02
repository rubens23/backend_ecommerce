package models.payment.pix

@kotlinx.serialization.Serializable
data class CreatePixPaymentRequest(
    val orderId: String,
    val pixPaymentResponse: PixPaymentResponse
)
