package repositories

import models.sale.Sale
import models.user.Address

interface SaleRepository {
    suspend fun criarVenda(carrinhoId: String, pagamentoId: String, totalAmount: Double, userId: String, shippingAddress: Address): Sale?
    suspend fun buscarVendaPorId(saleId: String): Sale?
    suspend fun listarVendasPorPeriodo(dataInicio: Long, dataFim: Long): List<Sale>?
    suspend fun listarVendasPorStatus(status: String): List<Sale>?
}