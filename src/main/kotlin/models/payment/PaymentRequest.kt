package models.payment


/**
 * ver se a coexistencia de PaymentResponse e PaymentRequest
 * é realmente necessaria
 */
@kotlinx.serialization.Serializable
data class PaymentRequest(
    val status: String,
    val paymentId: String,
    val paymentMethod: String

)
