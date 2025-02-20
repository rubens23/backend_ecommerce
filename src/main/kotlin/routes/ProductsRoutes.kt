package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.product.Product
import models.product.ProductResponse
import models.product.book.toResponse
import models.product.toResponse
import repositories.BookRepository
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


fun Route.saveNewProduct(productRepository: ProductRepository){
    post("/saveNewProduct"){
        try{

            val product = call.receive<ProductResponse>()

            // Validação simples
            if (product.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Nome é obrigatório.")
                return@post
            }

            val productToBeSaved = Product(
                name = product.name,
                description = product.description,
                price = product.price,
                category = product.category,
                stock = product.stock,
            )

            productRepository.addProduct(productToBeSaved)

            call.respond(HttpStatusCode.Created, productToBeSaved.toResponse())

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

        }
    }
}

fun Route.getProductById(productRepository: ProductRepository){
    get("/getProductById/{id}"){
        try {
            val id = call.parameters["id"]

            if(id.isNullOrBlank()){
                call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                return@get
            }

            val product = productRepository.getProductById(id)

            if(product != null){
                call.respond(HttpStatusCode.OK, product.toResponse())
            }else{
                call.respond(HttpStatusCode.NoContent)
            }
        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }


    }
}

fun Route.deleteProduct(productRepository: ProductRepository){
    delete("/deleteProduct/{id}"){
        try{
            val id = call.parameters["id"]

            if(id.isNullOrBlank()){
                call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                return@delete
            }

            val wasDeleted = productRepository.removeProduct(id)

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