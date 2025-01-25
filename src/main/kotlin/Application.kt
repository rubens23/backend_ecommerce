import clients.PaymentGateway
import `dependency-injection`.appModule
import io.ktor.http.*
import org.koin.core.context.GlobalContext.startKoin
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.product.Product
import models.user.Address
import models.user.User
import org.koin.core.context.GlobalContext.get
import org.koin.java.KoinJavaComponent
import plugins.configureRouting
import plugins.configureSerialization
import repositories.*
import security.hashing.HashingService


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(){

    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        allowHost("localhost:5173")
        allowHost("127.0.0.1:5173")
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

    configureSerialization()
    configureRouting(paymentGateway = paymentGateway, paymentRepository=paymentRepository, saleRepository = saleRepository, orderRepository = orderRepository)





}