package repositories

import models.cart.CartItem
import models.payment.*
import models.user.Address

interface PaymentRepository {

    suspend fun adicionarNovoPagamento(payment: Payment): Boolean
    suspend fun processarPagamento(userId: String, carrinho: List<CartItem>, endereco: Address?, metodoPagamento: PaymentMethod): ProcessarPagamentoResult
    suspend fun verificarStatusPagamento(paymentId: String): PaymentStatus?

    suspend fun cancelarPagamento(paymentId: String): Boolean

    suspend fun atualizarStatusPagamento(paymentId: String, newStatus: PaymentStatus): PaymentStatusUpdateResult

    suspend fun obterPagamentoPorId(paymentId: String): Payment?







}