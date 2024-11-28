package services

import models.product.book.Book
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.BookStockRepository

class BookStockService: KoinComponent {

    private val bookStockRepository: BookStockRepository by inject()

    suspend fun atualizarEstoque(bookId: String, quantidade: Int): Boolean{
        return bookStockRepository.atualizarEstoque(bookId, quantidade)
    }

}