package models.order

data class Payment(
    val id: String,
    val orderId: String,
    val userId: String,
    val amount: Double,
    val paymentMethod: String, // Ex.: "pix", "credit_card"
    val status: String, // Ex.: "pending", "completed", "failed"
    val transactionId: String?, // ID fornecido pelo gateway de pagamento
    val createdAt: Long = System.currentTimeMillis()
)
