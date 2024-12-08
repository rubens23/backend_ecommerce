package repositories

import models.product.Product

interface StockRepository {
    suspend fun atualizarEstoque(productId: String, quantidade: Int): Product?

    suspend fun getStock(productId: String): Int
    suspend fun decrementStock(productId: String, quantity: Int)

}