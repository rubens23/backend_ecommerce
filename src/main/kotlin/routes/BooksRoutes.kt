package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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