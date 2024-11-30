package services

import models.sale.Sale
import models.user.Address
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.SaleRepository

class SaleService: KoinComponent {
    private val saleRepository: SaleRepository by inject()


    suspend fun criarVenda(carrinhoId: String, pagamentoId: String, totalAmount: Double, userId: String, shippingAddress: Address): Sale?{
        return saleRepository.criarVenda(carrinhoId, pagamentoId, totalAmount, userId, shippingAddress)
    }
    suspend fun buscarVendaPorId(saleId: String): Sale?{
        return saleRepository.buscarVendaPorId(saleId)

    }
    suspend fun listarVendasPorPeriodo(dataInicio: Long, dataFim: Long): List<Sale>?{
        return saleRepository.listarVendasPorPeriodo(dataInicio, dataFim)

    }
    suspend fun listarVendasPorStatus(status: String): List<Sale>?{
        return saleRepository.listarVendasPorStatus(status)

    }

}