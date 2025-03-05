package repositories

import models.product.Product
import models.stock.StockMovement

interface StockRepository {
    suspend fun atualizarEstoque(productId: String, quantidade: Int): Boolean

    suspend fun getStock(productId: String): Int

    suspend fun getStockCurrentQuantity(): Int

    suspend fun saveStockMovement(movimentacao: StockMovement): Boolean

    suspend fun getAllStockMovements(): List<StockMovement>?
    suspend fun getLowStockProducts(): List<Product>?
}