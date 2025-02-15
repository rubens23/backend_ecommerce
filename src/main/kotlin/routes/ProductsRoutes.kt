package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.product.toResponse
import repositories.ProductRepository

fun Route.getProducts(productRepository: ProductRepository){
    get("/getProducts"){
        try{
            val produtos = productRepository.listProducts()?.map {
                it.toResponse()
            }

            if(produtos != null){
                call.respond(HttpStatusCode.OK, produtos)
            }else{
                call.respond(HttpStatusCode.NoContent)

            }
        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

        }
    }
}