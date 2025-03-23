package models.payment

import kotlinx.serialization.Serializable

@Serializable
data class PixPaymentRequest(
    val userId: String,
    val nome: String,
    val email: String,
    val cpf: String,
    val valor: Double
)
