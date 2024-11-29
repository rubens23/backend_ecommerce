package repositories

import models.payment.PaymentGatewayResponse
import models.payment.PaymentMethod

interface PaymentGateway {
    suspend fun iniciarGatewayPagamento(metodoPagamento: PaymentMethod, valorTotal: Double): PaymentGatewayResponse
    suspend fun cancelarPagamento(paymentId: String): Boolean
}