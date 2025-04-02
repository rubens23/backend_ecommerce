package models.payment

data class CreatePaymentResult (
    val success: Boolean,
    val paymentId: String?
)