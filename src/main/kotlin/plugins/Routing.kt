package plugins

import clients.PaymentGateway
import io.ktor.server.application.*
import io.ktor.server.routing.*
import repositories.OrderRepository
import repositories.PaymentRepository
import repositories.SaleRepository
import repositories.StockRepository
import routes.*

fun Application.configureRouting(
    paymentGateway: PaymentGateway,
    paymentRepository: PaymentRepository,
    saleRepository: SaleRepository,
    orderRepository: OrderRepository,
    stockRepository: StockRepository
){
    routing {
        processarPagamentoPix(paymentGateway, "/v1/payments/mercadopago", paymentRepository)
        getTotalVendas(saleRepository)
        getPedidosPendentesQuantity(orderRepository)
        getQuantidadeProdutosEmEstoque(stockRepository)
        getSalesByPeriod(saleRepository)
    }

}