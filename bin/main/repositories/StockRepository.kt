package repositories

import models.product.Product

interface StockRepository {
    suspend fun atualizarEstoque(productId: String, quantidade: Int): Product

}