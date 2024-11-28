package services

import models.product.book.Book
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.BookRepository

class BookService: KoinComponent {

    private val bookRepository: BookRepository by inject()

    suspend fun adicionarLivro(livro: Book): Boolean{
        return bookRepository.adicionarLivro(livro)

    }
    suspend fun listarLivros(): List<Book>?{
        return bookRepository.listarLivros()
    }
    suspend fun buscarLivroPorId(id: String): Book?{
        return bookRepository.buscarLivroPorId(id)

    }
    suspend fun buscarLivrosPorCriterios(criterios: Map<String, String>): List<Book>?{
        return bookRepository.buscarLivrosPorCriterios(criterios)

    }
    suspend fun atualizarLivro(livro: Book): Boolean{
        return bookRepository.atualizarLivro(livro)

    }
    suspend fun removerLivro(id: String): Boolean{
        return bookRepository.removerLivro(id)

    }
}