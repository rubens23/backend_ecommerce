package routes

import clients.MercadoPagoClient
import clients.PaymentGateway
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.payment.Payment
import models.payment.PaymentMethod
import models.payment.PixPaymentRequest
import repositories.PaymentRepository

fun Route.processarPagamentoPix(pixPaymentClient: PaymentGateway,
                                pixPaymentPath: String,
paymentRepository: PaymentRepository){
    post(pixPaymentPath){
        try {
            val pixPaymentRequest = call.receive<PixPaymentRequest>()
            val paymentResponse = pixPaymentClient.processarPagamentoPix(pixPaymentRequest)

            if (paymentResponse != null) {
                // Cria um objeto Payment para salvar no banco de dados
                val pagamento = Payment(
                    orderId = "",
                    userId = pixPaymentRequest.userId,
                    amount = pixPaymentRequest.valor,
                    paymentMethod = PaymentMethod.PIX,
                    status = paymentResponse.status,//sera que aqui é certeza que o pagamento ainda estará pendente?
                transactionId = paymentResponse.id.toString()

                )

                // Salva o pagamento no banco de dados
                val salvouPagamento = paymentRepository.adicionarNovoPagamento(pagamento)

                if(salvouPagamento){
                    call.respond(HttpStatusCode.OK, paymentResponse)

                }else{
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