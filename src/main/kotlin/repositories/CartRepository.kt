package repositories

import models.cart.Cart
import models.cart.CartItem

interface CartRepository {

    suspend fun adicionarCarrinho(usuarioId: String): Boolean
    suspend fun adicionarAoCarrinho(usuarioId: String, productId: String, quantidade: Int): Boolean

    suspend fun removerItemDoCarrinho(usuarioId: String, productId: String): Boolean

    suspend fun listarItensCarrinho(usuarioId: String): List<CartItem>?
    suspend fun esvaziarCarrinho(usuarioId: String): Boolean

    suspend fun pegarCarrinhoPorId(cartId: String): Cart?

    suspend fun pegarCarrinhoPorUserId(userId: String): Cart?

    suspend fun atualizarProdutoNoCarrinho(userId: String, productId: String, quantity: Int): Boolean
}