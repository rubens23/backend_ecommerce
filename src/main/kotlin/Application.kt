import `dependency-injection`.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.product.Product
import models.product.book.Book
import org.bson.types.ObjectId
import org.koin.core.context.GlobalContext.startKoin
import services.BookService
import services.BookStockService
import services.ProductService


fun main(){

    startKoin{
        modules(appModule)


    }

    // Estou usando esse escopo de corrotina para testar meus repositorios
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val job = coroutineScope.launch {


        // Teste para atualizar um livro
        val atualizado = BookStockService().atualizarEstoque(
            "67487926fcaf7e6f89744b3c",
            92

        )

        println("Livro atualizado: $atualizado")


    }

    runBlocking {
        job.join()
    }












}