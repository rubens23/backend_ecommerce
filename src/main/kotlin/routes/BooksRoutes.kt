package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.product.book.Book
import models.product.book.BookResponse
import models.product.book.toResponse
import repositories.BookRepository

fun Route.getBooks(bookRepository: BookRepository){
    get("/getBooks"){
        try{
            val books = bookRepository.listarLivros()?.map {
                it.toResponse()
            }

            if(books != null){
                call.respond(HttpStatusCode.OK, books)
            }else{
                call.respond(HttpStatusCode.NoContent)

            }
        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

        }
    }
}

fun Route.saveNewBook(bookRepository: BookRepository){
    post("/saveNewBook"){
        try{

            val book = call.receive<BookResponse>()

            // Validação simples
            if (book.name.isBlank() || book.author.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Nome e autor são obrigatórios.")
                return@post
            }

            val bookToBeSaved = Book(
                name = book.name,
                author = book.author,
                description = book.description,
                price = book.price,
                category = book.category,
                publisher = book.publisher,
                pages = book.pages,
                bookCover = book.bookCover,
                stock = book.stock,
            )

            bookRepository.adicionarLivro(bookToBeSaved)

            call.respond(HttpStatusCode.Created, bookToBeSaved.toResponse())

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

        }
    }
}

fun Route.deleteBook(bookRepository: BookRepository){
    delete("/deleteBook/{id}"){
        try{
            val id = call.parameters["id"]

            if(id.isNullOrBlank()){
                call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                return@delete
            }

            val wasDeleted = bookRepository.removerLivro(id)

            if(wasDeleted){
                call.respond(HttpStatusCode.OK, "Produto deletado com sucesso")
            }else{
                call.respond(HttpStatusCode.NotFound, "Produto não deletado")
            }

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }

    }
}