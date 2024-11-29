package models.payment

data class PaymentStatusUpdateResult(
    val success: Boolean,
    val currentStatus: PaymentStatus?,
    val message: String? = null
)
