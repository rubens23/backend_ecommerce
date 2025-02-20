package repositories

import models.cart.CartItem
import models.order.Order
import models.order.OrderResponse
import models.payment.Payment
import models.user.Address

interface OrderRepository {
    suspend fun gerarPedido(usuarioId: String, carrinho: List<CartItem>, endereco: Address, pagamento: Payment): OrderResponse
    suspend fun atualizarStatusPedido(pedidoId: String, status: String): Boolean
    suspend fun listarPedidos(usuarioId: String): List<Order>?

    suspend fun listarPedidosPorStatus(status: String): List<Order>?

    suspend fun pegarQuantidadeTotalDePedidosPorPeriodo(dataInicio: Long, dataFim: Long): Int?
    suspend fun listarPedidosPorPeriodo(dataInicio: Long, dataFim: Long): List<Order>?
    suspend fun getAllOrders(): List<Order>?

    suspend fun getOrderById(id: String): Order?
    suspend fun removerPedido(id: String): Boolean
}