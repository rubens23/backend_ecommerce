package repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import models.product.Product
import models.stock.StockMovement
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class StockRepositoryImpl: StockRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()

    private val productsDb = db.getCollection<Product>()
    private val stockMovementsDb = db.getCollection<StockMovement>()


    override suspend fun atualizarEstoque(productId: String, quantidade: Int): Boolean{
        return try{
            val filter = Filters.eq("_id", ObjectId(productId))
            val update = Updates.combine(
                Updates.set(Product::stock.name, quantidade)
            )

            val updateResult = productsDb.updateOne(filter, update)
            updateResult.modifiedCount > 0

        }catch(e: Exception){
            logRepository.registrarLog(e, "atualizar estoque", "Product Stock", null)
            false

        }

    }

    override suspend fun getStock(productId: String): Int {
        return try{
            val idToBeSearched = ObjectId(productId)
            productsDb.findOneById(idToBeSearched)?.stock?:return 0


        }catch(e: Exception){
            logRepository.registrarLog(e, "pegar estoque", "Product Stock", null)
            0

        }
    }

    override suspend fun getStockCurrentQuantity(): Int {
        return try{
            productsDb.find().toList().sumOf { it.stock }?:0


        }catch(e: Exception){
            logRepository.registrarLog(e, "get stock current quantity", "Product Stock", null)
            0

        }
    }


    override suspend fun saveStockMovement(movimentacao: StockMovement): Boolean {
        return try {
            stockMovementsDb.insertOne(movimentacao).wasAcknowledged()
        }catch (e: Exception){
            logRepository.registrarLog(e, "save stock movement", "ProductStock", null)
            false

        }



    }

    override suspend fun getAllStockMovements(): List<StockMovement>?{
        return try{
            stockMovementsDb.find().toList()

        }catch (e: Exception){
            logRepository.registrarLog(e, "get all stock movements", "ProductStock", null)
            null

        }
    }

    override suspend fun getLowStockProducts(): List<Product>?{
        return try{
            productsDb.find().toList()
                .filter { it.stock <= (it.minimumStock ?: 0) }

        }catch (e: Exception){
            logRepository.registrarLog(e, "get low stock products", "Product Stock", null)
            null
        }
    }


}