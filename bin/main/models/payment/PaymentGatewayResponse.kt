package models.payment

data class PaymentGatewayResponse(
    val transactionId: String,      // ID da transação gerado pelo gateway
    val status: PaymentStatus,      // Status do pagamento (PENDENTE, APROVADO, etc.)
    val amount: Double,             // Valor do pagamento
    val paymentMethod: String,      // Método de pagamento utilizado (pix, cartão, etc.)
    val message: String? = null     // Mensagem adicional, como erro ou sucesso
)
