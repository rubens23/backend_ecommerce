package repositories

import com.mongodb.client.model.Filters
import models.payment.PaymentStatus
import models.sale.Sale
import models.sale.SaleStatus
import models.user.Address
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class SaleRepositoryImpl: SaleRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()

    private val saleDb = db.getCollection<Sale>()
    override suspend fun criarVenda(
        carrinhoId: String,
        pagamentoId: String,
        totalAmount: Double,
        userId: String,
        shippingAddress: Address
    ): Sale? {
        try {
            val sale = Sale(
                cartId = ObjectId(carrinhoId),
                paymentId = ObjectId(pagamentoId),
                totalAmount = totalAmount,
                userId = userId,
                saleStatus = SaleStatus.CONFIRMADA,
                paymentStatus = PaymentStatus.APROVADO,
                shippingAddress = shippingAddress

            )

            // Inserir a venda
            val saleWasSaved = saleDb.insertOne(sale).wasAcknowledged()

            return if (saleWasSaved){
                sale
            }else{
                null
            }





        }catch (e: Exception){
            logRepository.registrarLog(e, "criar venda", "Sale", userId)
            return null


        }

    }



    override suspend fun buscarVendaPorId(saleId: String): Sale? {
        return try{
            val idToBeSearched = ObjectId(saleId)
            saleDb.findOneById(idToBeSearched)



        }catch (e: Exception){
            logRepository.registrarLog(e, "buscar venda por id", "Sale", null)
            null
        }
    }

    override suspend fun listarVendasPorPeriodo(dataInicio: Long, dataFim: Long): List<Sale>? {
        return try{
            val filtroPeriodo = Filters.and(
                Filters.gte(Sale::createdAt.name, dataInicio), // data maior ou igual a dataInicio
                Filters.lte(Sale::createdAt.name, dataFim) // Data menor ou igual a dataFim
            )

            saleDb.find(filtroPeriodo).toList()





        }catch (e: Exception){
            logRepository.registrarLog(e, "listar vendas por período", "Sale", null)
            null
        }
    }

    override suspend fun listarVendasPorStatus(status: String): List<Sale>? {
        return try{
            val filtro = Filters.and(
                Filters.eq(Sale::saleStatus.name, status),
            )

            saleDb.find(filtro).toList()





        }catch (e: Exception){
            logRepository.registrarLog(e, "listar vendas por status", "Sale", null)
            null
        }
    }
}