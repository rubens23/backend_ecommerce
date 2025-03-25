package repositories

import com.mongodb.client.model.Filters
import models.cart.CartItem
import models.order.Order
import models.order.OrderItem
import models.order.OrderResponse
import models.payment.Payment
import models.sale.Sale
import models.user.Address
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class OrderRepositoryImpl: OrderRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()
    private val stockRepository: StockRepository by inject()

    private val orderDb = db.getCollection<Order>()


    override suspend fun gerarPedido(
        order: Order
    ): OrderResponse {
        return try{

            // Validar carrinho do user
            for(item in order.items){
                val availableStock = stockRepository.getStock(item.productId)
                if(availableStock < item.quantity){
                    //não tem estoque suficiente para esse item
                    return OrderResponse(
                        success = false,
                        message = "not enough stock! Check your shopping cart and try again!",
                        order = null
                    )
                }
            }

            // se pagamento não for pix, pedido só deve ser gerado
            // depois do pagamento
            if(order.paymentMethod != "Pix"){
                // Processar pagamento
                if(order.orderStatus != "completed"){
                    return OrderResponse(
                        success = false,
                        message = "payment not confirmed! Check your payment and try again!",
                        order = null
                    )
                }
            }


            // Calcular preço total do pedido
            val totalAmount = order.items.sumOf { it.price * it.quantity}



            // Salvar o pedido no banco de dados
            orderDb.insertOne(order)

            // Atualizar o estoque
            for (item in order.items){
                stockRepository.atualizarEstoque(item.productId, item.quantity)
            }

            OrderResponse(
                success = true,
                message = "Your order was successfully registered!",
                order = order
            )



        }catch (e: Exception){
            logRepository.registrarLog(e, "gerar pedido", "Order", order.userId)
            OrderResponse(
                success = false,
                message = "There was an error registering your order! Try again!",
                order = null
            )
        }

    }

    override suspend fun atualizarStatusPedido(pedidoId: String, status: String): Boolean {
        return try {
            // Buscar o pedido no banco de dados
            val order = orderDb.findOne(Filters.eq("_id", ObjectId(pedidoId))) ?: return false


            // Verificar se o status fornecido é válido
            val validStatuses = listOf("processing", "shipped", "delivered", "cancelled")
            if(status !in validStatuses){
                return false
            }

            // Atualizar o status do pedido
            val updatedOrder = order.copy(
                orderStatus = status, //Atualiza o status do pedido
                updatedAt = System.currentTimeMillis()
            )

            // Salvar as alterações no banco de dados
            orderDb.replaceOne(Filters.eq("_id", ObjectId(pedidoId)), updatedOrder).modifiedCount > 0





        } catch (e: Exception) {
            logRepository.registrarLog(e, "atualizar status pedido", "Order", null)
            false
        }
    }

    override suspend fun listarPedidos(usuarioId: String): List<Order>? {
        return try {
            val filter = Filters.eq(Order::userId.name, usuarioId)
            orderDb.find(filter).toList()


        } catch (e: Exception) {
            logRepository.registrarLog(e, "listar pedidos", "Order", usuarioId)
            null
        }
    }

    override suspend fun listarPedidosPorStatus(status: String): List<Order>? {
        return try{
            val filtro = Filters.and(
                Filters.eq(Order::orderStatus.name, status)
            )

            orderDb.find(filtro).toList()
        }catch (e: Exception){
            logRepository.registrarLog(e, "listar pedidos por status", "Order", null)
            null
        }
    }

    override suspend fun pegarQuantidadeTotalDePedidosPorPeriodo(dataInicio: Long, dataFim: Long): Int? {
        return try {
            val filtroPeriodo = Filters.and(
                Filters.gte(Order::createdAt.name, dataInicio),
                Filters.lte(Order::createdAt.name, dataFim)
            )
            orderDb.find(filtroPeriodo).toList().size




        } catch (e: Exception) {
            logRepository.registrarLog(e, " pegar quantidade total de pedidos", "Order", null)
            null
        }
    }

    override suspend fun listarPedidosPorPeriodo(dataInicio: Long, dataFim: Long): List<Order>? {
        return try {
            val filtroPeriodo = Filters.and(
                Filters.gte(Order::createdAt.name, dataInicio),
                Filters.lte(Order::createdAt.name, dataFim)
            )
            orderDb.find(filtroPeriodo).toList()




        } catch (e: Exception) {
            logRepository.registrarLog(e, "listar pedidos por periodo", "Order", null)
            null
        }
    }

    override suspend fun getAllOrders(): List<Order>? {
        return try {

            orderDb.find().toList()




        } catch (e: Exception) {
            logRepository.registrarLog(e, "get all orders", "Order", null)
            null
        }
    }

    override suspend fun getOrderById(id: String): Order? {
        return try {

            val idToBeSearched = ObjectId(id)
            orderDb.findOneById(idToBeSearched)




        } catch (e: Exception) {
            logRepository.registrarLog(e, "get order by id", "Order", null)
            null
        }
    }

    override suspend fun removerPedido(id: String): Boolean {
        return try{
            val idToBeSearched = ObjectId(id)
            val filter = Filters.eq("_id", idToBeSearched)
            orderDb.deleteOne(filter).wasAcknowledged()

        }catch (e: Exception){
            logRepository.registrarLog(e, "remover pedido", "Order", null)
            false
        }
    }
}