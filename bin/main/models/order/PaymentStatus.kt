package models.order

enum class PaymentStatus {
        PENDENTE,     // Quando o pagamento ainda não foi confirmado
        APROVADO,     // Quando o pagamento foi aprovado com sucesso
        CANCELADO,    // Quando o pagamento foi cancelado (ex: pedido rejeitado)
        FALHOU,       // Quando houve falha na transação
        EM_PROCESSAMENTO // Quando o pagamento está em andamento (ex: aguardando confirmação de um boleto)

}