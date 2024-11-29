package repositories

import models.cart.CartItem
import models.order.Order
import models.order.OrderResponse
import models.order.Payment
import models.user.Address

interface OrderRepository {
    suspend fun gerarPedido(usuarioId: String, carrinho: List<CartItem>, endereco: Address, pagamento: Payment): OrderResponse
    suspend fun atualizarStatusPedido(pedidoId: String, status: String): Boolean
    suspend fun listarPedidos(usuarioId: String): List<Order>?
}