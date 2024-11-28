package repositories

import models.order.Order

interface AdmOrderRepository{
    suspend fun listarPedidos(): List<Order>
    suspend fun alterarStatusPedido(pedidoId: String, status: String): Boolean
}


