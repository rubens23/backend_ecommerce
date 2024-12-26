package plugins

import clients.PaymentGateway
import io.ktor.server.application.*
import io.ktor.server.routing.*
import repositories.PaymentRepository
import routes.processarPagamentoPix

fun Application.configureRouting(
    paymentGateway: PaymentGateway,
    paymentRepository: PaymentRepository
){
    routing {
        processarPagamentoPix(paymentGateway, "/v1/payments/mercadopago", paymentRepository)
    }

}