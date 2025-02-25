import clients.PaymentGateway
import `dependency-injection`.appModule
import io.ktor.http.*
import org.koin.core.context.GlobalContext.startKoin
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.product.Product
import models.user.Address
import models.user.Role
import models.user.User
import org.koin.core.context.GlobalContext.get
import org.koin.java.KoinJavaComponent
import org.slf4j.event.Level
import plugins.configureRouting
import plugins.configureSecurity
import plugins.configureSerialization
import repositories.*
import routes.configureStaticFiles
import security.hashing.HashingService
import security.token.JwtTokenService
import security.token.TokenConfig


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(){

    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Put)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization) // Permitir envio de tokens no cabeçalho
        allowHeader(HttpHeaders.Cookie)  // Permitir o envio de cookies
        allowCredentials = true // Permitir envio de cookies

        allowHost("localhost:5173")
        allowHost("127.0.0.1:5173")
    }

    install(CallLogging){
        level = Level.DEBUG
    }


    startKoin{
        modules(appModule)


    }

    // Estou usando esse escopo de corrotina para testar meus repositorios
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val job = coroutineScope.launch {
        val stockRepository: BookStockRepository = KoinJavaComponent.get(BookStockRepository::class.java)







    }

    runBlocking {
        job.join()
    }

    val paymentGateway: PaymentGateway = KoinJavaComponent.get(PaymentGateway::class.java)
    val paymentRepository: PaymentRepository = KoinJavaComponent.get(PaymentRepository::class.java)
    val saleRepository: SaleRepository = KoinJavaComponent.get(SaleRepository::class.java)
    val orderRepository: OrderRepository = KoinJavaComponent.get(OrderRepository::class.java)
    val stockRepository: StockRepository = KoinJavaComponent.get(StockRepository::class.java)
    val productRepository: ProductRepository = KoinJavaComponent.get(ProductRepository::class.java)
    val bookRepository: BookRepository = KoinJavaComponent.get(BookRepository::class.java)
    val salesReportRepository: SalesReportRepository = KoinJavaComponent.get(SalesReportRepository::class.java)
    val userRepository: UserRepository = KoinJavaComponent.get(UserRepository::class.java)
    val hashingService: HashingService = KoinJavaComponent.get(HashingService::class.java)
    val jwtTokenService: JwtTokenService = KoinJavaComponent.get(JwtTokenService::class.java)

//
//    val tokenConfig = TokenConfig(
//        issuer = "http://localhost:8099", //aqui vai o dominio do backend
//        audience = "minha loja",
//        expiresIn = 3600000,
//        secret = System.getenv("JWT_SECRET")
//    )
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 3600000,
        secret = System.getenv("JWT_SECRET")
    )

    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(paymentGateway = paymentGateway, paymentRepository=paymentRepository,
        saleRepository = saleRepository, orderRepository = orderRepository, stockRepository = stockRepository,
    productRepository = productRepository, bookRepository = bookRepository, salesReportRepository = salesReportRepository,
    userRepository = userRepository, hashingService = hashingService, jwtTokenService = jwtTokenService, tokenConfig = tokenConfig)
    configureStaticFiles()







}