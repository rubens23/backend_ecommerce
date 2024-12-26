package models.payment

import kotlinx.serialization.Serializable

@Serializable
data class PixPaymentRequest(
    val userId: String,
    val nome: String,
    val sobrenome: String,
    val email: String,
    val cidade: String,
    val bairro: String,
    val numero: String,
    val rua: String,
    val cep: String,
    val estado: String,
    val ddd: String,
    val celular: String,
    val cpf: String,
    val valor: Double
)
