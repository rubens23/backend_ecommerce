package repositories

import models.product.book.Book

interface BookRepository {
    suspend fun adicionarLivro(livro: Book): Boolean
    suspend fun listarLivros(): List<Book>?
    suspend fun buscarLivroPorId(id: String): Book?
    suspend fun buscarLivrosPorCriterios(criterios: Map<String, String>): List<Book>?
    suspend fun atualizarLivro(book: Book): Boolean
    suspend fun removerLivro(id: String): Boolean
}