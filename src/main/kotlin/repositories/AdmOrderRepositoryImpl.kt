package repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import models.logs.ActivityLog
import models.order.Order
import org.litote.kmongo.coroutine.CoroutineDatabase

class AdmOrderRepositoryImpl(db: CoroutineDatabase,
                             private val logRepository: LogRepository): AdmOrderRepository {

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