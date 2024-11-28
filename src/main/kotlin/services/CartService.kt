package services

import models.cart.CartItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.CartRepository

class CartService: KoinComponent {

    private val cartRepository: CartRepository by inject()

    suspend fun adicionarCarrinho(usuarioId: String): Boolean{
        return cartRepository.adicionarCarrinho(usuarioId)
    }


    suspend fun adicionarAoCarrinho(usuarioId: String, productId: String, quantidade: Int): Boolean{
        return cartRepository.adicionarAoCarrinho(usuarioId, productId, quantidade)
    }

    suspend fun removerItemDoCarrinho(usuarioId: String, productId: String): Boolean{
        return cartRepository.removerItemDoCarrinho(usuarioId, productId)
    }


    suspend fun listarItensCarrinho(usuarioId: String): List<CartItem>?{
        return cartRepository.listarItensCarrinho(usuarioId)

    }

    suspend fun esvaziarCarrinho(usuarioId: String): Boolean{
        return cartRepository.esvaziarCarrinho(usuarioId)

    }
}