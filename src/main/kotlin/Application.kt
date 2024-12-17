import `dependency-injection`.appModule
import org.koin.core.context.GlobalContext.startKoin
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.user.Address
import models.user.User
import org.koin.core.context.GlobalContext.get
import repositories.UserRepository
import org.koin.java.KoinJavaComponent
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
        val stockRepository: StockRepository = KoinJavaComponent.get(StockRepository::class.java)

        // Teste de atualizar o estoque
        println("Iniciando teste: Atualizar o teste")
        val atualizouEstoque = stockRepository.atualizarEstoque(
            productId = "674864076847222bb2707389",
            quantidade = 99
        )
        println("Atualizou o estoque? $atualizouEstoque")

        // Teste de pegar a quantidade em estoque
        println("Iniciando Teste: Pegando quantidade do item em estoque")

        val quantidanteEstoque = stockRepository.getStock(
            productId = "674864076847222bb2707389",
        )
        println("Quantidade em estoque: $quantidanteEstoque")




    }

    runBlocking {
        job.join()
    }












}