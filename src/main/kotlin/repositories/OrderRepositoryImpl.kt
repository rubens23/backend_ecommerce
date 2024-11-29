package repositories

import com.mongodb.client.model.Filters
import models.cart.CartItem
import models.order.Order
import models.order.OrderItem
import models.order.OrderResponse
import models.payment.Payment
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
        usuarioId: String,
        carrinho: List<CartItem>,
        endereco: Address,
        pagamento: Payment
    ): OrderResponse {
        return try{

            // Validar carrinho do user
            for(item in carrinho){
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

            // Processar pagamento
            if(pagamento.status != "completed"){
                return OrderResponse(
                    success = false,
                    message = "payment not confirmed! Check your payment and try again!",
                    order = null
                )
            }

            // Calcular preço total do pedido
            val totalAmount = carrinho.sumOf { it.price * it.quantity}

            // Criar o pedido
            val order = Order(
                userId = usuarioId,
                items = carrinho.map { OrderItem(productId = it.productId, quantity = it.quantity, price = it.price) },
                totalAmount = totalAmount,
                address = endereco,
                orderStatus = "processing" //Status inicial do pedido
            )

            // Salvar o pedido no banco de dados
            orderDb.insertOne(order)

            // Atualizar o estoque
            for (item in carrinho){
                stockRepository.decrementStock(item.productId, item.quantity)
            }

            OrderResponse(
                success = true,
                message = "Your order was successfully registered!",
                order = order
            )



        }catch (e: Exception){
            logRepository.registrarLog(e, "gerar pedido", "Order", usuarioId)
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
}