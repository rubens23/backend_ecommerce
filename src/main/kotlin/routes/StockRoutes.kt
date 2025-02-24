package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import repositories.StockRepository

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