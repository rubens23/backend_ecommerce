package repositories

import models.order.Payment
import models.order.PaymentMethod
import models.order.PaymentStatus

interface PaymentRepository {
    suspend fun processarPagamento(pedidoId: String, metodoPagamento: PaymentMethod): Payment
    suspend fun verificarStatusPagamento(pedidoId: String): PaymentStatus
}