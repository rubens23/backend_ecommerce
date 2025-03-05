package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.product.Product
import models.product.ProductResponse
import models.product.book.toResponse
import models.product.copyManual
import models.product.toResponse
import models.stock.UpdateStockRequest
import repositories.BookRepository
import repositories.BookStockRepository
import repositories.ProductRepository
import repositories.StockRepository

fun Route.getProducts(productRepository: ProductRepository){
    authenticate {
        get("/getProducts") {
            try {
                val produtos = productRepository.listProducts()?.map {
                    it.toResponse()
                }

                if (produtos != null) {
                    call.respond(HttpStatusCode.OK, produtos)
                } else {
                    call.respond(HttpStatusCode.NoContent)

                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

            }
        }
    }
}


fun Route.saveNewProduct(productRepository: ProductRepository){
    authenticate {
        post("/saveNewProduct") {
            try {

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
                    minimumStock = product.minimumStock
                )

                productRepository.addProduct(productToBeSaved)

                call.respond(HttpStatusCode.Created, productToBeSaved.toResponse())

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

            }
        }
    }
}

fun Route.getProductById(productRepository: ProductRepository){
    authenticate {
        get("/getProductById/{id}") {
            try {
                val id = call.parameters["id"]

                if (id.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                    return@get
                }

                val product = productRepository.getProductById(id)

                if (product != null) {
                    call.respond(HttpStatusCode.OK, product.toResponse())
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
            }


        }
    }
}

fun Route.updateProduct(productRepository: ProductRepository) {
    authenticate {
        put("/updateProduct/{id}") {
            try {
                val id = call.parameters["id"]

                if (id.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                    return@put
                }

                val productUpdateRequest = call.receive<ProductResponse>()

                val existingProduct = productRepository.getProductById(id)
                if (existingProduct == null) {
                    call.respond(HttpStatusCode.NotFound, "Produto não encontrado")
                    return@put
                }

                val updatedProduct = existingProduct.copyManual(
                    name = productUpdateRequest.name,
                    description = productUpdateRequest.description,
                    price = productUpdateRequest.price,
                    stock = productUpdateRequest.stock,
                    category = productUpdateRequest.category
                )

                val updateSuccess = productRepository.updateProduct(updatedProduct)

                if (updateSuccess) {
                    call.respond(HttpStatusCode.OK, updatedProduct.toResponse())
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Erro ao atualizar o produto")
                }

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
            }
        }
    }
}

fun Route.deleteProduct(productRepository: ProductRepository){
    authenticate {
        delete("/deleteProduct/{id}") {
            try {
                val id = call.parameters["id"]

                if (id.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                    return@delete
                }

                val wasDeleted = productRepository.removeProduct(id)

                if (wasDeleted) {
                    call.respond(HttpStatusCode.OK, "Produto deletado com sucesso")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Produto não deletado")
                }

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
            }

        }
    }
}

fun Route.updateProductStock(productRepository: ProductRepository, stockRepository: StockRepository){
    authenticate {
        put("/updateProductStock/{id}"){
            val id = call.parameters["id"]?: return@put call.respond(HttpStatusCode.BadRequest, "ID do produto é obrigatório")

            try{
                val request = call.receive<UpdateStockRequest>()

                val produto = productRepository.getProductById(id)
                if (produto == null) {
                    call.respond(HttpStatusCode.NotFound, "Produto não encontrado")
                    return@put
                }

                // Atualiza o estoque conforme a movimentação
                val novoEstoque = when(request.tipo){
                    "entrada" -> {produto.stock + request.quantidade}
                    "saida" -> {
                        if(produto.stock < request.quantidade){
                            return@put call.respond(HttpStatusCode.BadRequest, "Estoque insuficiente")

                        }
                        produto.stock - request.quantidade
                    }
                    else -> return@put call.respond(HttpStatusCode.BadRequest, "Tipo de movimentação inválido")


                }
                //Atualizando o produto no banco
                stockRepository.atualizarEstoque(id, novoEstoque)

                call.respond(HttpStatusCode.OK, "Estoque atualizado com sucesso")

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Erro ao atualizar estoque: ${e.message}")
            }

        }
    }
}