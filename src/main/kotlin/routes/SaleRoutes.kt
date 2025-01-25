package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.payment.PaymentStatus
import repositories.SaleRepository

fun Route.getTotalVendas(saleRepository: SaleRepository){
    get("/getSalesTotal"){
        try{
            val vendas = saleRepository.listarVendasPorStatus(PaymentStatus.APROVADO.name)


            if (vendas != null){
                call.respond(HttpStatusCode.OK, vendas.size)
            }else{
                call.respond(HttpStatusCode.OK, "Nenhuma venda foi concluída ainda!")

            }

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }
    }
}