package services

import models.cart.CartItem
import models.payment.*
import models.user.Address
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.PaymentRepository

class PaymentService: KoinComponent {
    private val paymentRepository: PaymentRepository by inject()

    suspend fun processarPagamento(userId: String, carrinho: List<CartItem>, endereco: Address?, metodoPagamento: PaymentMethod): ProcessarPagamentoResult{
        return paymentRepository.processarPagamento(userId, carrinho, endereco, metodoPagamento)
    }
    suspend fun verificarStatusPagamento(paymentId: String): PaymentStatus?{
        return paymentRepository.verificarStatusPagamento(paymentId)

    }

    suspend fun cancelarPagamento(paymentId: String): Boolean{
        return paymentRepository.cancelarPagamento(paymentId)

    }

    suspend fun atualizarStatusPagamento(paymentId: String, newStatus: PaymentStatus): PaymentStatusUpdateResult{
        return paymentRepository.atualizarStatusPagamento(paymentId, newStatus)

    }

    suspend fun obterPagamentoPorId(paymentId: String): Payment?{
        return paymentRepository.obterPagamentoPorId(paymentId)

    }
}