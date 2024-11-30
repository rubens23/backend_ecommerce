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

        // Teste de criarVenda
        val carrinhoId = "6748a95becc5f97359540e6a"
        val pagamentoId = "6749f0590802826f6f961172"
        val totalAmount = 200.0
        val userId = "abcd1234"  // ID do usuário
        val shippingAddress = Address(
            id = "endereco123",
            userId = userId,
            street = "Rua X",
            city = "Cidade Y",
            state = "SP",
            postalCode = "12345-678",
            country = "Brasil"
        )

        // Teste de criarVenda
        val vendaResponse = SaleService().criarVenda(carrinhoId, pagamentoId, totalAmount, userId, shippingAddress)
        if (vendaResponse != null) {
            println("Venda criada com sucesso! Sale ID: ${vendaResponse.id}")
        } else {
            println("Erro ao criar venda")
        }

        // Teste de buscarVendaPorId
        val saleId = "674b1892080ca829754dd0d0"  // Exemplo de ID da venda
        val venda = SaleService().buscarVendaPorId(saleId)
        if (venda != null) {
            println("Venda encontrada: ${venda.id}, Status da venda: ${venda.saleStatus}")
        } else {
            println("Venda não encontrada")
        }

        // Teste de listarVendasPorPeriodo
        val dataInicio = 1672531200000L  // 01 janeiro de 2023
        val dataFim = 1767102988000  // 30 dezembro 2025
        val vendasPorPeriodo = SaleService().listarVendasPorPeriodo(dataInicio, dataFim)
        println("Vendas no período: ${vendasPorPeriodo?.size ?: 0}")

        // Teste de listarVendasPorStatus
        val statusVenda = "CONFIRMADA"  // Status da venda
        val vendasPorStatus = SaleService().listarVendasPorStatus(statusVenda)
        println("Vendas com status '$statusVenda': ${vendasPorStatus?.size ?: 0}")

        // Teste de listarVendasPorStatus
        val statusVenda2 = "2"  // Status da venda
        val vendasPorStatus2 = SaleService().listarVendasPorStatus(statusVenda2)
        println("Vendas com status 2 '$statusVenda2': ${vendasPorStatus2?.size ?: 0}")

    }

    runBlocking {
        job.join()
    }












}