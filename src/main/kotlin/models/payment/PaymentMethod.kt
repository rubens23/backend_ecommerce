package models.payment

data class PaymentMethod(
    val id: String,               // ID único do método de pagamento
    val nome: String,             // Nome do método de pagamento (ex: "Cartão de Crédito", "Pix")
    val descricao: String,        // Descrição do método de pagamento
    val taxa: Double? = null      // Taxa adicional, se houver (por exemplo, para cartão de crédito)
)
