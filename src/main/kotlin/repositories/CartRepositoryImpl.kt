package repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Updates.combine
import models.cart.Cart
import models.cart.CartItem
import models.product.ItemType
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class CartRepositoryImpl: CartRepository, KoinComponent {
    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()
    private val stockRepository: StockRepository by inject()
    private val bookStockRepository: BookStockRepository by inject()

    private val cartDb = db.getCollection<Cart>()

    override suspend fun adicionarCarrinho(usuarioId: String): Boolean {
        return try{
            cartDb.insertOne(
                Cart(
                    userId = usuarioId,
                    items = emptyList(),
                )
            ).wasAcknowledged()

        }catch (e: Exception){
            logRepository.registrarLog(e, "adicionar carrinho", "Cart", usuarioId)
            false
        }
    }

    override suspend fun adicionarAoCarrinho(usuarioId: String, productId: String, quantidade: Int, itemType: ItemType, price: Double): Boolean {

        return try{
            // Verificar se o user já tem um carrinho
            val existingCart = cartDb.findOne(Filters.eq(Cart::userId.name, usuarioId))


            // Se não existir, cria um carrinho vazio
            if(existingCart == null){
                adicionarCarrinho(usuarioId)
            }

            // Buscar estoque do produto
            val stockQnt = when (itemType.name ){
                "BOOK"-> bookStockRepository.getStock(productId)
                "PRODUCT" -> stockRepository.getStock(productId)
                else -> return false


            }



            // Verificar se o item já está dentro do carrinho
            val cart = cartDb.findOne(Filters.eq(Cart::userId.name, usuarioId))?:return false
            val existingItem = cart.items.find { it.productId == productId }


            //Verificar se há estoque suficiente
            val newQuantity = (existingItem?.quantity ?: 0)+ quantidade
            if(newQuantity > stockQnt){
                return false
            }


            val updatedItems = if (existingItem != null) {
                cart.items.map {
                    if (it.productId == productId) it.copy(quantity = newQuantity) else it
                }
            } else {
                cart.items + CartItem(
                    userId = usuarioId,
                    productId = productId,
                    quantity = quantidade,
                    price = price,
                    itemType = itemType
                )
            }

            //atualizar total amount do carrinho
            val updatedCart = cart.copy(
                items = updatedItems,
                totalAmount = updatedItems.sumOf{it.price * it.quantity}
            )

            // Atualizar no banco apenas os campos modificados
            val updateResult = cartDb.updateOne(
                Filters.eq(Cart::userId.name, usuarioId),
                combine(
                    Updates.set("items", updatedCart.items),
                    Updates.set("totalAmount", updatedCart.totalAmount)
                )
            )

            return updateResult.modifiedCount > 0





        }catch (e: Exception){
            logRepository.registrarLog(e, "adicionar ao carrinho", "Cart", usuarioId)
            false
        }
    }

    override suspend fun removerItemDoCarrinho(usuarioId: String, productId: String): Boolean {
        return try {
            // Verificar se o carrinho existe
            val cart = cartDb.findOne(Filters.eq(Cart::userId.name, usuarioId)) ?: return false

            // Filtrar os itens, removendo aqueles com o productId especificado
            val updatedCart = cart.copy(
                items = cart.items.filter { it.productId != productId },
                totalAmount = cart.items
                    .filter { it.productId != productId }
                    .sumOf { it.price * it.quantity } // Recalcular o totalAmount
            )

            // Atualizar o carrinho no banco
            cartDb.updateOne(Filters.eq(Cart::userId.name, usuarioId), updatedCart).modifiedCount > 0
        } catch (e: Exception) {
            logRepository.registrarLog(e, "remover item do carrinho", "Cart", usuarioId)
            false
        }

    }


    override suspend fun listarItensCarrinho(usuarioId: String): List<CartItem>? {
        return try {
            val filter = Filters.eq(Cart::userId.name, usuarioId)
            cartDb.findOne(filter)?.items


        } catch (e: Exception) {
            logRepository.registrarLog(e, "listar itens carrinho", "Cart", usuarioId)
            null
        }
    }



    override suspend fun esvaziarCarrinho(usuarioId: String): Boolean {
        return try {
            val cart = cartDb.findOne(Filters.eq(Cart::userId.name, usuarioId))?: return false

            val updatedCart = cart.copy(
                items = emptyList(),
                totalAmount = 0.0

            )

            cartDb.updateOne(Filters.eq(Cart::userId.name, usuarioId), updatedCart).modifiedCount > 0






        } catch (e: Exception) {
            logRepository.registrarLog(e, "esvaziar carrinho", "Cart", usuarioId)
            false
        }
    }

    override suspend fun pegarCarrinhoPorId(cartId: String): Cart? {
        return try{
            cartDb.findOne(Filters.eq("_id", ObjectId(cartId)))

        }catch(e: Exception){
            logRepository.registrarLog(e, "pegar carrinho por id", "Cart", null)
            null
        }
    }

    override suspend fun pegarCarrinhoPorUserId(userId: String): Cart? {
        return try{
            cartDb.findOne(Filters.eq(Cart::userId.name, userId))

        }catch(e: Exception){
            logRepository.registrarLog(e, "pegar carrinho por user id", "Cart", null)
            null
        }
    }

    override suspend fun atualizarProdutoNoCarrinho(userId: String, productId: String, quantity: Int): Boolean {
        return try{
            val cart = cartDb.findOne(Filters.eq(Cart::userId.name, userId))?: return false

            val existingItem = cart.items.find { it.productId == productId } ?: return false

            val updatedItem = existingItem.copy(quantity = quantity)

            val updatedCart = cart.copy(
                items = cart.items.map {
                    if(it.productId == productId) updatedItem else it
                },
                totalAmount = cart.items.map {
                    if(it.productId == productId) updatedItem else it
                }.sumOf{it.price * it.quantity}
            )

            cartDb.updateOne(Filters.eq(Cart::userId.name, userId), updatedCart).modifiedCount > 0


        }catch (e: Exception){
            logRepository.registrarLog(e, "atualizar produto no carrinho", "Cart", userId)
            false

        }
    }


}