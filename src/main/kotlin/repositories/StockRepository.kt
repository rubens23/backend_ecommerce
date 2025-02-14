package repositories

import models.product.Product

interface StockRepository {
    suspend fun atualizarEstoque(productId: String, quantidade: Int): Boolean

    suspend fun getStock(productId: String): Int

    suspend fun getStockCurrentQuantity(): Int

}