package models.payment.pix

data class CreatePixPaymentRequest(
    val orderId: String,
    val pixPaymentResponse: PixPaymentResponse
)
