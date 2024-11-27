package repositories

import models.cart.CartItem

interface CartRepository {
    suspend fun adicionarAoCarrinho(usuarioId: String, productId: String, quantidade: Int): CartItem
    suspend fun listarItensCarrinho(usuarioId: String): List<CartItem>
    suspend fun atualizarCarrinho(usuarioId: String, productId: String, quantidade: Int): CartItem
    suspend fun esvaziarCarrinho(usuarioId: String): Boolean
}