package repositories

import models.cart.Cart
import models.product.Product
import models.reports.BestSellingProduct
import models.reports.BestSellingProductWithName
import models.reports.SalesReport

interface SalesReportRepository {
    suspend fun gerarRelatorioVendas(dataInicio: Long, dataFim: Long): SalesReport?
    suspend fun colocarNomeNosProdutosMaisVendidos(bestSellingProducts: List<BestSellingProduct>): List<BestSellingProductWithName>?
    suspend fun colocarNomeNosLivrosMaisVendidos(bestSellingProducts: List<BestSellingProduct>): List<BestSellingProductWithName>?
    suspend fun calcularProdutosMaisVendidos(cartList: List<Cart>): List<BestSellingProduct>
}