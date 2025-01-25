package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import repositories.OrderRepository

fun Route.getPedidosPendentesQuantity(orderRepository: OrderRepository){
    get("/getPedidosPendentesQuantity"){
        try {
            val pedidos = orderRepository.listarPedidosPorStatus("processing")

            if(pedidos != null){
                call.respond(HttpStatusCode.OK, pedidos.size)
            }else{
                call.respond(HttpStatusCode.OK, "Nenhum pedido está pendente!")
            }

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }
    }
}