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
        val productRepository: ProductRepository = KoinJavaComponent.get(ProductRepository::class.java)

        // Testes de adicionar produto
        println("Iniciando teste: tentar adicionar um produto com um nome, descrição e preço que ja existe")
        val wasProductAdded = productRepository.addProduct(
            Product(
                name = "Smartphone S2010",
                description = "Smartphone com 128GB de armazenamento, câmera de 48MP e tela de 6.5 polegadas",
                price = 2999.99,
                stock = 2,
                category = "Eletrônicos",
            )
        )

        println("O produto foi adicionado? $wasProductAdded")

        println("Iniciando teste: tentar adicionar um produto com um nome, descrição e preço que ainda não existe")
        val wasProductAdded2 = productRepository.addProduct(
            Product(
                name = "Smartphone S2010",
                description = "Smartphone com 1TB de armazenamento, câmera de 48MP e tela de 6.5 polegadas",
                price = 9999.99,
                stock = 2,
                category = "Eletrônicos",
            )
        )

        println("O produto foi adicionado? $wasProductAdded2")





    }

    runBlocking {
        job.join()
    }












}