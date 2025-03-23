package models.payment

@kotlinx.serialization.Serializable
data class PaymentResponse(
    val id: String,
    val orderId: String,
    val userId: String,
    val userName: String,
    val amount: Double,
    val paymentMethod: String,
    val status: String,
    val transactionId: String?,
    val createdAt: String
)
