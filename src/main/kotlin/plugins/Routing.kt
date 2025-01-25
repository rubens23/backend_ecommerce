package plugins

import clients.PaymentGateway
import io.ktor.server.application.*
import io.ktor.server.routing.*
import repositories.PaymentRepository
import repositories.SaleRepository
import routes.getTotalVendas
import routes.processarPagamentoPix

fun Application.configureRouting(
    paymentGateway: PaymentGateway,
    paymentRepository: PaymentRepository,
    saleRepository: SaleRepository
){
    routing {
        processarPagamentoPix(paymentGateway, "/v1/payments/mercadopago", paymentRepository)
        getTotalVendas(saleRepository)
    }

}