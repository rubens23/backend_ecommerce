package services

import models.product.Product
import models.reports.SalesReport
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.SalesReportRepository

class SalesReportService: KoinComponent {
    private val salesReportRepository: SalesReportRepository by inject()

    suspend fun gerarRelatorioVendas(dataInicio: Long, dataFim: Long): SalesReport?{
        return salesReportRepository.gerarRelatorioVendas(dataInicio, dataFim)

    }

    suspend fun listarProdutosMaisVendidos(dataInicio: Long, dataFim: Long): List<Product>?{
        return salesReportRepository.listarProdutosMaisVendidos(dataInicio, dataFim)

    }
}