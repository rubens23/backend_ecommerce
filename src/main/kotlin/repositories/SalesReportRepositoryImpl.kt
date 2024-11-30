package repositories

import com.mongodb.client.model.Filters
import models.order.Order
import models.product.Product
import models.reports.SalesReport
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class SalesReportRepositoryImpl: SalesReportRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()

    private val salesReportDb = db.getCollection<SalesReport>()


    override suspend fun gerarRelatorioVendas(dataInicio: Long, dataFim: Long): SalesReport? {
        try {

            // Validar os timestamps
            if(dataInicio > dataFim){
                // intervalo inválido.
                return null
            }

            // Buscar dados no banco

            return null


        }catch (e: Exception){
            logRepository.registrarLog(e, "gerar relatório de vendas", "SalesReport", null)
            return null

        }

    }

    override suspend fun listarProdutosMaisVendidos(dataInicio: Long, dataFim: Long): List<Product>? {
        return null
    }
}