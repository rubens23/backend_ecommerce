package repositories

import models.cart.CartItem
import models.order.Order
import models.order.Payment
import models.user.Address

interface OrderRepository {
    suspend fun gerarPedido(usuarioId: String, carrinho: List<CartItem>, endereco: Address, pagamento: Payment): Order
    suspend fun atualizarStatusPedido(pedidoId: String, status: String): Order
    suspend fun listarPedidos(usuarioId: String): List<Order>
}