package repositories

import models.product.Product
import models.reports.SalesReport

interface SalesReportRepository {
    suspend fun gerarRelatorioVendas(dataInicio: String, dataFim: String): SalesReport
    suspend fun listarProdutosMaisVendidos(dataInicio: String, dataFim: String): List<Product>
}