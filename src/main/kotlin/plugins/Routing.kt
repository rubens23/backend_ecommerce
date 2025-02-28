package plugins

import clients.PaymentGateway
import io.ktor.server.application.*
import io.ktor.server.routing.*
import repositories.*
import routes.*
import security.hashing.HashingService
import security.token.JwtTokenService
import security.token.TokenConfig

fun Application.configureRouting(
    paymentGateway: PaymentGateway,
    paymentRepository: PaymentRepository,
    saleRepository: SaleRepository,
    orderRepository: OrderRepository,
    stockRepository: StockRepository,
    productRepository: ProductRepository,
    bookRepository: BookRepository,
    salesReportRepository: SalesReportRepository,
    userRepository: UserRepository,
    hashingService: HashingService,
    jwtTokenService: JwtTokenService,
    tokenConfig: TokenConfig,
    refreshTokenRepository: RefreshTokenRepository
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
        getOrderById(orderRepository)
        getOrders(orderRepository)
        deleteOrder(orderRepository)
        getTotalVendasAmount(saleRepository)
        getOrdersQuantity(orderRepository)
        pegarLivrosMaisVendidosPorPeriodo(salesReportRepository)
        pegarProdutosMaisVendidosPorPeriodo(salesReportRepository)
        registerNewUser(userRepository, hashingService)
        loginUser(userRepository, hashingService, jwtTokenService, tokenConfig, refreshTokenRepository)
        refreshToken(refreshTokenRepository, userRepository, jwtTokenService, tokenConfig)
    }

}