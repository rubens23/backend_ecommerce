package repositories

import models.cart.CartItem
import models.order.Order
import models.payment.*
import models.payment.pix.PixPayment
import models.payment.pix.PixPaymentResponse
import models.user.Address

interface PaymentRepository {

    suspend fun adicionarNovoPagamento(payment: Payment): CreatePaymentResult
    suspend fun processarPagamento(userId: String, carrinho: List<CartItem>, endereco: Address?, metodoPagamento: PaymentMethod): ProcessarPagamentoResult
    suspend fun verificarStatusPagamento(paymentId: String): PaymentStatus?

    suspend fun cancelarPagamento(paymentId: String): Boolean

    suspend fun atualizarStatusPagamento(paymentId: String, newStatus: PaymentStatus): PaymentStatusUpdateResult

    suspend fun obterPagamentoPorId(paymentId: String): Payment?
    suspend fun pegarPagamentos(): List<Payment>?

    suspend  fun savePixPayment(pixPayment: PixPayment): Boolean


    suspend fun getPixPayment(pixPaymentId: String): PixPayment?
    suspend fun updateOrderWithPaymentId(orderId: String, paymentId: String): Order?
    suspend fun adicionarPixPayment(novoPixPayment: PixPayment): String?
}