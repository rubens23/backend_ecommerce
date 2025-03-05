package repositories

import models.product.book.Book

interface BookStockRepository {
    suspend fun atualizarEstoque(bookId: String, quantidade: Int): Boolean

    suspend fun getStock(productId: String): Int


    suspend fun getLowStockBooks(): List<Book>?
}