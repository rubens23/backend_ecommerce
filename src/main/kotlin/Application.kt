import `dependency-injection`.appModule
import org.koin.core.context.GlobalContext.startKoin
import services.*
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


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
        // Teste de gerarRelatorioVendas
        val dataInicio = 1672531200000L  // 01 janeiro de 2023
        val dataFim = 1767102988000L    // 30 dezembro de 2025
        val salesReportResponse = SalesReportService().gerarRelatorioVendas(dataInicio, dataFim)
        if (salesReportResponse != null) {
            println("Relatório de vendas gerado com sucesso!")
            println("Relatório: Total de vendas: ${salesReportResponse.totalSales}, Total de pedidos: ${salesReportResponse.totalOrders}")
        } else {
            println("Erro ao gerar relatório de vendas")
        }






    }

    runBlocking {
        job.join()
    }












}