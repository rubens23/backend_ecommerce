import `dependency-injection`.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.product.Product
import org.bson.types.ObjectId
import org.koin.core.context.GlobalContext.startKoin
import services.ProductService


fun main(){

    startKoin{
        modules(appModule)


    }

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val job = coroutineScope.launch {
       ProductService().addProduct(
            Product(
                name = "Smartphone S2010",
                description = "Smartphone com 128GB de armazenamento, câmera de 48MP e tela de 6.5 polegadas",
                price = 2999.99,
                stock = 10,
                category = "Eletrônicos"
            )
        )

        val listaProdutos = ProductService().listProducts()
        listaProdutos
        ProductService().updateProduct(product = Product(
            id = ObjectId("67477f9d95910a72e9d4c46c"),
            price = 1000.0,
            stock = 47,
            category = "Eletronicos",
            name = "celular",
            description = null

        ))
        val productById = ProductService().getProductById("67477f9d95910a72e9d4c46c")
        productById
        val removeu = ProductService().removeProduct("67477f9d95910a72e9d4c46c")
        removeu


    }

    runBlocking {
        job.join()
    }











}