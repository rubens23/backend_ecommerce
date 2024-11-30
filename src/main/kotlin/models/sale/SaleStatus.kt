package models.sale

enum class SaleStatus {
    PENDENTE,      // Quando a venda ainda não foi processada
    CONFIRMADA,    // Quando a venda foi confirmada
    ENVIADA,       // Quando a venda foi enviada para o cliente
    CANCELADA,     // Quando a venda foi cancelada
    COMPLETADA     // Quando a venda foi finalizada com sucesso
}