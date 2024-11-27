package repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import models.logs.ActivityLog
import models.order.Order
import org.litote.kmongo.coroutine.CoroutineDatabase

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AdmOrderRepositoryImpl(): AdmOrderRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()

    private val ordersDb = db.getCollection<Order>()



    override suspend fun listarPedidos(): List<Order> {
        try {
            val orders = ordersDb.find()
            return orders.toList()
        }catch (e: Exception){
            logRepository.registrarLog(e, "listar pedidos", "Order", null)
            throw e

        }

    }

    override suspend fun alterarStatusPedido(pedidoId: String, status: String): Boolean {
        try{
            val query = Filters.eq(Order::id.toString(), pedidoId)
            val updates = Updates.set(Order::orderStatus.toString(), status)
            return ordersDb.updateOne(query, updates).wasAcknowledged()

        }catch (e: Exception){
            logRepository.registrarLog(e, "alterar status do pedido", "Order", null)
            throw e



        }

    }
}