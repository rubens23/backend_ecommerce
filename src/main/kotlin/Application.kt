import `dependency-injection`.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.cart.CartItem
import models.order.Payment
import models.product.Product
import models.product.book.Book
import models.user.Address
import org.bson.types.ObjectId
import org.koin.core.context.GlobalContext.startKoin
import repositories.CartRepository
import services.*


fun main(){

    startKoin{
        modules(appModule)


    }

    // Estou usando esse escopo de corrotina para testar meus repositorios
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val job = coroutineScope.launch {
        // Teste de gerarPedido
        val carrinho = listOf(
            CartItem(userId = "abcd1234", productId = "123", quantity = 2, price = 50.0),
            CartItem(userId = "abcd1234", productId = "456", quantity = 1, price = 100.0)
        )
        val endereco = Address(
            id = "endereco123",
            userId = "abcd1234",
            street = "Rua X",
            city = "Cidade Y",
            state = "SP",
            postalCode = "12345-678",
            country = "Brasil"
        )
        val pagamento = Payment(
            id = "pagamento123",
            orderId = "",  // A ordem ainda não foi gerada, então o campo orderId pode ser vazio inicialmente
            userId = "abcd1234",
            amount = 200.0,  // Soma do valor do carrinho
            paymentMethod = "credit_card",
            status = "completed",
            transactionId = "tx12345"
        )

        // Gerar pedido
        val orderResponse = OrderService().gerarPedido("abcd1234", carrinho, endereco, pagamento)
        println("Pedido gerado com sucesso? ${orderResponse.success}")
        println("Mensagem: ${orderResponse.message}")
        println("Detalhes do pedido: ${orderResponse.order}")

        // Teste de atualizarStatusPedido
        val pedidoId = orderResponse.order?.id.toString()  // Pegando o ID do pedido gerado
        val statusAtualizado = "shipped"  // Status que será atualizado
        val statusAtualizacao = OrderService().atualizarStatusPedido(pedidoId, statusAtualizado)
        println("Status do pedido atualizado com sucesso? $statusAtualizado")
        println("Status de sucesso: $statusAtualizado")

        // Teste de listarPedidos
        val usuarioIdListagem = "abcd1234"
        val pedidos = OrderService().listarPedidos(usuarioIdListagem)
        println("Pedidos do usuário $usuarioIdListagem: ${pedidos?.size}")
        pedidos?.forEach { pedido ->
            println("Pedido ID: ${pedido.id}, Status: ${pedido.orderStatus}")
        }







    }

    runBlocking {
        job.join()
    }












}