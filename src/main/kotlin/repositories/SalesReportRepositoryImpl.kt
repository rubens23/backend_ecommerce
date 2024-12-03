package repositories

import models.cart.Cart
import models.product.Product
import models.reports.BestSellingProduct
import models.reports.SalesReport
import models.sale.Sale
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class SalesReportRepositoryImpl: SalesReportRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()
    private val saleRepository: SaleRepository by inject()
    private val orderRepository: OrderRepository by inject()
    private val cartRepository: CartRepository by inject()

    private val salesReportDb = db.getCollection<SalesReport>()


    override suspend fun gerarRelatorioVendas(dataInicio: Long, dataFim: Long): SalesReport? {

            return try {
                if (!validarIntervaloDeDatas(dataInicio, dataFim)) return null

                val listaDeVendasPorPeriodo = saleRepository.listarVendasPorPeriodo(dataInicio, dataFim) ?: return null
                val qntDePedidosPorPeriodo = orderRepository.pegarQuantidadeTotalDePedidosPorPeriodo(dataInicio, dataFim)

                val cartList = carregarCarrinhos(listaDeVendasPorPeriodo)
                val bestSellingProducts = calcularProdutosMaisVendidos(cartList)

                val relatorio = construirRelatorio(
                    dataInicio = dataInicio,
                    dataFim = dataFim,
                    vendas = listaDeVendasPorPeriodo,
                    totalPedidos = qntDePedidosPorPeriodo,
                    produtosMaisVendidos = bestSellingProducts
                )

                val adicionouRelatorio = salesReportDb.insertOne(relatorio).wasAcknowledged()
                if (adicionouRelatorio){
                    return relatorio
                }else{
                    return null
                }
            } catch (e: Exception) {
                logRepository.registrarLog(e, "gerar relatório de vendas", "SalesReport", null)
                null
            }

    }



    private fun construirRelatorio(
        dataInicio: Long,
        dataFim: Long,
        vendas: List<Sale>,
        totalPedidos: Int?,
        produtosMaisVendidos: List<BestSellingProduct>
    ): SalesReport {
        return SalesReport(
            startDate = dataInicio,
            endDate = dataFim,
            totalSales = vendas.size,
            totalAmount = vendas.sumOf { it.totalAmount },
            totalOrders = totalPedidos ?: 0,
            bestSellingProducts = produtosMaisVendidos
        )
    }


    private fun calcularProdutosMaisVendidos(cartList: List<Cart>): List<BestSellingProduct> {
        val quantidadeDeProdutosVendidos = mutableMapOf<String, Int>()

        cartList.forEach { cart ->
            cart.items.forEach { item ->
                quantidadeDeProdutosVendidos[item.productId] =
                    quantidadeDeProdutosVendidos.getOrDefault(item.productId, 0) + item.quantity
            }
        }

        val vendasDeProdutosOrdenada = quantidadeDeProdutosVendidos.entries.sortedByDescending { it.value }

        return vendasDeProdutosOrdenada.take(3).map { entry ->
            val productId = entry.key
            val quantidadeTotalVendida = entry.value
            val rendaTotal = cartList.flatMap { it.items }
                .filter { it.productId == productId }
                .sumOf { it.price * it.quantity }

            BestSellingProduct(
                productId = productId,
                totalQuantitySold = quantidadeTotalVendida,
                totalRevenue = rendaTotal
            )
        }
    }

    private fun validarIntervaloDeDatas(dataInicio: Long, dataFim: Long): Boolean {
        return dataInicio <= dataFim
    }

    private suspend fun carregarCarrinhos(listaDeVendas: List<Sale>?): List<Cart> {
        return listaDeVendas?.mapNotNull { venda ->
            venda
            cartRepository.pegarCarrinhoPorId(venda.cartId.toHexString())
        } ?: emptyList()
    }

}