import `dependency-injection`.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.cart.CartItem
import models.payment.Payment
import models.payment.PaymentMethod
import models.payment.PaymentStatus
import models.payment.ProcessarPagamentoResult
import models.user.Address
import org.koin.core.context.GlobalContext.startKoin
import services.*


fun main(){

    startKoin{
        modules(appModule)


    }

    // Estou usando esse escopo de corrotina para testar meus repositorios
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val job = coroutineScope.launch {
        // Teste de processarPagamento
        val carrinho = listOf(
            CartItem(userId = "abcd1234", productId = "6747831759212d5f63860b25", quantity = 2, price = 50.0),
            CartItem(userId = "abcd1234", productId = "6747831759212d5f63860b25", quantity = 1, price = 100.0)
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

        val metodoPagamento = PaymentMethod(
            id = "credit_card", // ID único para o método de pagamento
            nome = "Cartão de Crédito",
            descricao = "Pagamento via cartão de crédito",
            taxa = 3.5 // Taxa adicional para o pagamento
        )
        // Teste de processarPagamento
        val pagamentoResponse = PaymentService().processarPagamento("abcd1234", carrinho, endereco, metodoPagamento)
        when (pagamentoResponse) {
            is ProcessarPagamentoResult.Success -> {
                println("Pagamento processado com sucesso! Transaction ID: ${pagamentoResponse.payment.transactionId}")
            }
            is ProcessarPagamentoResult.Error -> {
                println("Erro ao processar pagamento: ${pagamentoResponse.message}")
            }
        }

//        // Teste de verificarStatusPagamento
        val paymentId = "6749f0590802826f6f961172"
        val statusPagamento = PaymentService().verificarStatusPagamento(paymentId)
        println("Status do pagamento: $statusPagamento")

        // Teste de cancelarPagamento
        val cancelarPagamentoResult = PaymentService().cancelarPagamento(paymentId)
        println("Pagamento cancelado com sucesso? $cancelarPagamentoResult")

        // Teste de atualizarStatusPagamento
        val novoStatus = PaymentStatus.APROVADO
        val statusAtualizado = PaymentService().atualizarStatusPagamento(paymentId, novoStatus)
        println("Status do pagamento atualizado com sucesso? ${statusAtualizado.success}")
        println("Novo status: ${statusAtualizado.currentStatus}, Mensagem: ${statusAtualizado.message}")

        // Teste de obterPagamentoPorId
        val pagamento = PaymentService().obterPagamentoPorId(paymentId)
        if (pagamento != null) {
            println("Pagamento encontrado: ${pagamento.id}, Status: ${pagamento.status}")
        } else {
            println("Pagamento não encontrado")
        }






    }

    runBlocking {
        job.join()
    }












}