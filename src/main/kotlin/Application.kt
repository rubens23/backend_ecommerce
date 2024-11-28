import `dependency-injection`.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.product.Product
import models.product.book.Book
import org.bson.types.ObjectId
import org.koin.core.context.GlobalContext.startKoin
import repositories.CartRepository
import services.BookService
import services.BookStockService
import services.CartService
import services.ProductService


fun main(){

    startKoin{
        modules(appModule)


    }

    // Estou usando esse escopo de corrotina para testar meus repositorios
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val job = coroutineScope.launch {
        val adicionou = CartService().adicionarAoCarrinho("abcd1234", "6747831759212d5f63860b25", 5)
        println("adicionou ao carrinho? $adicionou")

        //CartService().esvaziarCarrinho("abcd1234")

        val itensCarrinho = CartService().listarItensCarrinho("abcd1234")
        println("qnt carrinho  1? ${itensCarrinho?.size}")

        val itensCarrinho2 = CartService().listarItensCarrinho("abcde12345")
        println("qnt carrinho  2? ${itensCarrinho2?.size}")


        //val removeu = CartService().removerItemDoCarrinho("abcd1234", "6747831759212d5f63860b25")
        //println("removeu do carrinho? $removeu")











    }

    runBlocking {
        job.join()
    }












}