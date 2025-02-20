package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.product.book.Book
import models.product.book.BookResponse
import models.product.book.copyManual
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

fun Route.getBookById(bookRepository: BookRepository){
    get("/getBookById/{id}"){
        try {
            val id = call.parameters["id"]

            if(id.isNullOrBlank()){
                call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                return@get
            }

            val book = bookRepository.buscarLivroPorId(id)

            if(book != null){
                call.respond(HttpStatusCode.OK, book.toResponse())
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

fun Route.updateBook(bookRepository: BookRepository){
    put("/updateBook/{id}"){
        try{
            val id = call.parameters["id"]

            if(id.isNullOrBlank()){
                call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                return@put
            }

            val bookUpdateRequest = call.receive<BookResponse>()

            val existingBook = bookRepository.buscarLivroPorId(id)
            if(existingBook == null){
                call.respond(HttpStatusCode.NotFound, "Livro não encontrado")
                return@put
            }

            val updatedBook = existingBook.copyManual(
                name = bookUpdateRequest.name,
                description = bookUpdateRequest.description,
                price = bookUpdateRequest.price,
                stock = bookUpdateRequest.stock,
                category = bookUpdateRequest.category,
                author = bookUpdateRequest.author,
                publisher = bookUpdateRequest.publisher,
                pages = bookUpdateRequest.pages,
                bookCover = bookUpdateRequest.bookCover
            )

            val updateSuccess = bookRepository.atualizarLivro(updatedBook)

            if(updateSuccess){
                call.respond(HttpStatusCode.OK, updatedBook.toResponse())
            }else{
                call.respond(HttpStatusCode.InternalServerError, "Erro ao atualizar o livro")
            }

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