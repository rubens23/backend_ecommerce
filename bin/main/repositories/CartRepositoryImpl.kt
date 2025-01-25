package repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import models.cart.Cart
import models.cart.CartItem
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class CartRepositoryImpl: CartRepository, KoinComponent {
    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()
    private val productRepository: ProductRepository by inject()

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

    override suspend fun adicionarAoCarrinho(usuarioId: String, productId: String, quantidade: Int): Boolean {

        return try{
            // Verificar se o user já tem um carrinho
            val existingCart = cartDb.findOne(Filters.eq(Cart::userId.name, usuarioId))

            // Se não existir, cria um carrinho vazio
            if(existingCart == null){
                adicionarCarrinho(usuarioId)
            }

            // Verificar se o item já está dentro do carrinho
            val cart = cartDb.findOne(Filters.eq(Cart::userId.name, usuarioId))?:return false

            val existingItem = cart.items.find { it.productId == productId }


            // Se o item já existir, atualizar a quantidade
            if(existingItem != null){
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantidade)

                val updatedCart = cart.copy(
                    items = cart.items.map { if(it.productId == productId) updatedItem else it }
                )

                //atualiza o totalAmount considerando o item que acabou de ser adicionado
                val updateCartTotalAmount = updatedCart.copy(
                    totalAmount = updatedCart.items.sumOf { it.price * it.quantity }
                )

                cartDb.updateOne(Filters.eq(Cart::userId.name, usuarioId), updateCartTotalAmount).modifiedCount > 0

            }else{
                val price = productRepository.getProductPrice(productId) ?: return false


                // Caso o item não exista, adicionar um novo item
                val newItem = CartItem(
                    userId = usuarioId,
                    productId = productId,
                    quantity = quantidade,
                    price = price
                )
                val updatedCart = cart.copy(
                    items = cart.items + newItem
                )

                //atualiza o totalAmount considerando o item que acabou de ser adicionado
                val updateCartTotalAmount = updatedCart.copy(
                    totalAmount = updatedCart.items.sumOf { it.price * it.quantity }
                )

                cartDb.updateOne(Filters.eq(Cart::userId.name, usuarioId), updateCartTotalAmount).modifiedCount > 0
            }




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


}