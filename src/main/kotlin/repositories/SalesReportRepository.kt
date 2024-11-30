package repositories

import models.product.Product
import models.reports.SalesReport

interface SalesReportRepository {
    suspend fun gerarRelatorioVendas(dataInicio: Long, dataFim: Long): SalesReport?
    suspend fun listarProdutosMaisVendidos(dataInicio: Long, dataFim: Long): List<Product>?
}