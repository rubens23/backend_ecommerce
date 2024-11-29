package services

import models.cart.CartItem
import models.order.Order
import models.order.OrderResponse
import models.order.Payment
import models.user.Address
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.OrderRepository

class OrderService: KoinComponent {

    private val orderRepository: OrderRepository by inject()

    suspend fun gerarPedido(usuarioId: String, carrinho: List<CartItem>, endereco: Address, pagamento: Payment): OrderResponse{
        return orderRepository.gerarPedido(usuarioId, carrinho, endereco, pagamento)

    }
    suspend fun atualizarStatusPedido(pedidoId: String, status: String): Boolean{
        return orderRepository.atualizarStatusPedido(pedidoId, status)

    }
    suspend fun listarPedidos(usuarioId: String): List<Order>?{
        return orderRepository.listarPedidos(usuarioId)

    }
}