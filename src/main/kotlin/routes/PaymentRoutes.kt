package routes

import clients.PaymentGateway
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.payment.Payment
import models.payment.PaymentMethod
import models.payment.pix.PixPaymentRequest
import models.payment.pix.PixPaymentResponse
import models.payment.toResponse
import repositories.PaymentRepository
import repositories.UserRepository

fun Route.processarPagamentoPix(pixPaymentClient: PaymentGateway,
                                pixPaymentPath: String,
paymentRepository: PaymentRepository){
    authenticate {
        post(pixPaymentPath) {
            try {
                val pixPaymentRequest = call.receive<PixPaymentRequest>()
                val paymentResponse = pixPaymentClient.processarPagamentoPix(pixPaymentRequest)

                if (paymentResponse != null) {
                    // Cria um objeto Payment para salvar no banco de dados
                    val pagamento = Payment(
                        orderId = "",
                        userId = pixPaymentRequest.userId,
                        amount = pixPaymentRequest.valor,
                        paymentMethod = PaymentMethod.PIX.name,
                        status = paymentResponse.status,//sera que aqui é certeza que o pagamento ainda estará pendente?
                        transactionId = paymentResponse.id.toString(),
                        details = mapOf() // aqui vai os detalhes especificos do pagamento

                    )

                    // Salva o pagamento no banco de dados
                    val salvouPagamento = paymentRepository.adicionarNovoPagamento(pagamento)

                    if (salvouPagamento) {
                        call.respond(HttpStatusCode.OK, paymentResponse)

                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Erro ao salvar pagamento PIX")

                    }


                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Erro ao processar pagamento PIX")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
            }

        }
    }

}

fun Route.getAllPayments(paymentRepository: PaymentRepository, userRepository: UserRepository){
    authenticate {
        get("/getAllPayments"){
            try{
                val payments = paymentRepository.pegarPagamentos()

                if(payments != null){
                    //para cada pagamento terei que pegar o nome do usuario
                    //cada pagamento tem um userId que pode ser usado
                    //para pegar o nome do user
                    val userIdsFromPayments = payments.map { it.userId }.toSet()

                    val payers = userRepository.getUsersById(userIdsFromPayments)

                    if(payers != null){

                        val paymentsDto = payments.map {
                            payment->
                            val payer = payers.firstOrNull{it.id.toHexString() == payment.userId}

                            payment.toResponse(payer?.name?:"")
                        }

                        call.respond(HttpStatusCode.OK, paymentsDto)

                    }else{
                        call.respond(HttpStatusCode.NotFound)

                    }
                }else{
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar pagamentos: ${e.message}")

            }
        }
    }
}

fun Route.getPixPaymentDetails(paymentRepository: PaymentRepository){
    authenticate {
        get("/getPixPayment/{paymentId}"){
            try {
                // Recupera o paymentId da URL
                val paymentId = call.parameters["paymentId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Payment ID é obrigatório")

                // Busca o pagamento no banco de dados
                val pagamento = paymentRepository.getPixPayment(paymentId)

                if (pagamento != null) {
                    // Caso o pagamento seja encontrado, cria a resposta PixPaymentResponse
                    val pixPaymentResponse = PixPaymentResponse(
                        id = 0, // id não implementado para o PixPaymentResponse
                        status = pagamento.status,
                        statusDetail = pagamento.statusDetail,
                        qrCode = pagamento.qrCode,
                        qrCodeBase64 = pagamento.qrCodeBase64,
                        ticketUrl = pagamento.ticketUrl,
                        vencimento = pagamento.vencimento
                    )

                    // Retorna os dados do pagamento
                    call.respond(HttpStatusCode.OK, pixPaymentResponse)

                } else {
                    // Caso o pagamento não seja encontrado, retorna um erro 404
                    call.respond(HttpStatusCode.NotFound, "Pagamento não encontrado")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar detalhes do pagamento: ${e.message}")
            }
        }
    }
}

fun Route.updateOrderWithPix(paymentRepository: PaymentRepository){
    authenticate {
        put("/orders/{orderId}/payment"){
            try{
                val orderId = call.parameters["orderId"]?:return@put call.respond(HttpStatusCode.BadRequest, "Order ID é obrigatório")

                //Recebe o pixPaymentResponse do corpo da requisição
                val pixPaymentResponse = call.receive<PixPaymentResponse>()

                val updatedOrder = paymentRepository.updateOrderWithPix(orderId, pixPaymentResponse)

                if(updatedOrder != null){
                    call.respond(HttpStatusCode.OK, "Pedido atualizado com sucesso")
                }else{
                    call.respond(HttpStatusCode.NotFound, "Pedido não atualizado")
                }

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Erro ao atualizar o pedido com pix: ${e.message}")
            }
        }
    }
}