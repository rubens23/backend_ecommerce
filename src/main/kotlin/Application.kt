import `dependency-injection`.appModule
import org.koin.core.context.GlobalContext.startKoin
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.product.Product
import models.user.Address
import models.user.User
import org.koin.core.context.GlobalContext.get
import repositories.UserRepository
import org.koin.java.KoinJavaComponent
import repositories.BookStockRepository
import repositories.ProductRepository
import repositories.StockRepository
import security.hashing.HashingService


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(){


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












}