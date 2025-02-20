package plugins

import clients.PaymentGateway
import io.ktor.server.application.*
import io.ktor.server.routing.*
import repositories.*
import routes.*

fun Application.configureRouting(
    paymentGateway: PaymentGateway,
    paymentRepository: PaymentRepository,
    saleRepository: SaleRepository,
    orderRepository: OrderRepository,
    stockRepository: StockRepository,
    productRepository: ProductRepository,
    bookRepository: BookRepository
){
    routing {
        processarPagamentoPix(paymentGateway, "/v1/payments/mercadopago", paymentRepository)
        getTotalVendas(saleRepository)
        getPedidosPendentesQuantity(orderRepository)
        getQuantidadeProdutosEmEstoque(stockRepository)
        getSalesByPeriod(saleRepository)
        getOrdersByPeriod(orderRepository)
        getProducts(productRepository)
        getBooks(bookRepository)
        makeProductImageUrl()
        saveNewProduct(productRepository)
        saveNewBook(bookRepository)
        deleteBook(bookRepository)
        deleteProduct(productRepository)
        getBookById(bookRepository)
        getProductById(productRepository)
        updateBook(bookRepository)
        updateProduct(productRepository)
    }

}