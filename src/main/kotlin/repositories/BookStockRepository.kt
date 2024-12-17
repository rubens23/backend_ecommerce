package repositories

import models.product.book.Book

interface BookStockRepository {
    suspend fun atualizarEstoque(bookId: String, quantidade: Int): Boolean


}