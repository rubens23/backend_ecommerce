package services

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.AdmOrderRepository

class AdmOrderService: KoinComponent {
    private val orderRepository: AdmOrderRepository by inject()

    suspend fun listarPedidos() {
        val pedidos = orderRepository.listarPedidos()
    }

    suspend fun alterarStatusPedido(pedidoId: String, status: String) {
        val sucesso = orderRepository.alterarStatusPedido(pedidoId, status)
    }
}