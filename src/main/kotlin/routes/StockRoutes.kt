package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.product.toResponse
import models.stock.StockMovementDTO
import models.stock.toDTO
import models.stock.toStockMovement
import models.user.UserSession
import repositories.*

fun Route.getQuantidadeProdutosEmEstoque(stockRepository: StockRepository){
    authenticate {
        get("/getQuantidadeProdutosEmEstoque") {
            try {
                val qntStock = stockRepository.getStockCurrentQuantity()

                call.respond(HttpStatusCode.OK, qntStock)


            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

            }


        }
    }
}

fun Route.saveStockMovement(stockRepository: StockRepository){
    authenticate {
        post("/saveStockMovement"){
            try{
                // Acessando o userId do JWT
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Usuário não autenticado")
                    return@post
                }

                val movimentacaoDTO = call.receive<StockMovementDTO>()

                // Validando se os dados da movimentação são válidos
                if (movimentacaoDTO.productId.isBlank() || movimentacaoDTO.tipo.isBlank() || movimentacaoDTO.quantidade <= 0) {
                    call.respond(HttpStatusCode.BadRequest, "Dados inválidos para movimentação")
                    return@post
                }

                val movimentacao = movimentacaoDTO.toStockMovement(responsavelId = userId)

                stockRepository.saveStockMovement(movimentacao)

                call.respond(HttpStatusCode.Created, "Movimentação de estoque registrada com sucesso!")


            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Erro ao salvar movimentação: ${e.message}")
            }
        }
    }
}

fun Route.getAllStockMovements(stockRepository: StockRepository, productRepository: ProductRepository, userRepository: UserRepository) {
    authenticate {
        get("/getAllStockMovements") {
            try {
                // Buscando todas as movimentações de estoque
                val movimentacoes = stockRepository.getAllStockMovements()

                if(movimentacoes != null){
                    if (movimentacoes.isEmpty()) {
                        call.respond(HttpStatusCode.NoContent, "Nenhuma movimentação encontrada.")
                        return@get
                    }
                    val productIds = movimentacoes.map { it.productId }.toSet() // pegando todos os IDs de produtos
                    val responsaveisIDs = movimentacoes.map { it.responsavelId }.toSet()

                    //buscando os produtos e responsaveis
                    val produtos = productRepository.getProductsById(productIds)
                    val responsaveis = userRepository.getUsersById(responsaveisIDs)

                    // convertendo as movimentações para DTO com os dados adicionais (nome do produto e nome do responsável)
                    if(produtos != null && responsaveis != null){
                        val movimentacoesDTO = movimentacoes.map {
                                movimentacao ->
                            val produto = produtos.firstOrNull{it.id.toHexString() == movimentacao.productId}
                            val responsavel = responsaveis.firstOrNull{it.id.toHexString() == movimentacao.responsavelId}

                            // convertendo para DTO e adicionando os nomes
                            movimentacao.toDTO().copy(
                                nomeProduto = produto?.name?:"",
                                nomeResponsavel = responsavel?.name?:""
                            )
                        }
                        call.respond(HttpStatusCode.OK, movimentacoesDTO)
                    }else{
                        call.respond(HttpStatusCode.NoContent, "Erro ao encontrar as movimentações")

                    }



                }else{
                    call.respond(HttpStatusCode.NoContent, "Nenhuma movimentação encontrada.")

                }




            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar movimentações: ${e.message}")
            }
        }
    }
}

fun Route.getAllBookStockMovements(stockRepository: StockRepository, bookRepository: BookRepository, userRepository: UserRepository) {
    authenticate {
        get("/getAllBookStockMovements") {
            try {
                // Buscando todas as movimentações de estoque
                val movimentacoes = stockRepository.getAllStockMovements()

                if(movimentacoes != null){
                    if (movimentacoes.isEmpty()) {
                        call.respond(HttpStatusCode.NoContent, "Nenhuma movimentação encontrada.")
                        return@get
                    }
                    val productIds = movimentacoes.map { it.productId }.toSet() // pegando todos os IDs de produtos
                    val responsaveisIDs = movimentacoes.map { it.responsavelId }.toSet()

                    //buscando os produtos e responsaveis
                    val produtos = bookRepository.getBooksById(productIds)
                    val responsaveis = userRepository.getUsersById(responsaveisIDs)

                    // convertendo as movimentações para DTO com os dados adicionais (nome do produto e nome do responsável)
                    if(produtos != null && responsaveis != null){
                        val movimentacoesDTO = movimentacoes.map {
                                movimentacao ->
                            val produto = produtos.firstOrNull{it.id.toHexString() == movimentacao.productId}
                            val responsavel = responsaveis.firstOrNull{it.id.toHexString() == movimentacao.responsavelId}

                            // convertendo para DTO e adicionando os nomes
                            movimentacao.toDTO().copy(
                                nomeProduto = produto?.name?:"",
                                nomeResponsavel = responsavel?.name?:""
                            )
                        }
                        call.respond(HttpStatusCode.OK, movimentacoesDTO)
                    }else{
                        call.respond(HttpStatusCode.NoContent, "Erro ao encontrar as movimentações")

                    }



                }else{
                    call.respond(HttpStatusCode.NoContent, "Nenhuma movimentação encontrada.")

                }




            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar movimentações: ${e.message}")
            }
        }
    }
}

fun Route.getProductsWithStockLowerThanMinimum(stockRepository: StockRepository){
    authenticate {
        get("/productsWithStockBelowMinimum"){
            try {
                val products = stockRepository.getLowStockProducts()?.map {
                    it.toResponse()
                }

                if(products != null){
                    call.respond(HttpStatusCode.OK, products)

                }else {
                    call.respond(HttpStatusCode.NoContent)

                }

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar produtos com estoque menor que o minimo: ${e.message}")

            }
        }
    }

}

fun Route.getBooksWithStockLowerThanMinimum(bookStockRepository: BookStockRepository){
    authenticate {
        get("/booksWithStockBelowMinimum"){
            try {
                val books = bookStockRepository.getLowStockBooks()?.map {
                    it.toResponse()
                }

                if(books != null){
                    call.respond(HttpStatusCode.OK, books)

                }else {
                    call.respond(HttpStatusCode.NoContent)

                }

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar produtos com estoque menor que o minimo: ${e.message}")

            }
        }
    }

}