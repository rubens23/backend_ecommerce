package repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import models.product.book.Book
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class BookStockRepositoryImpl: BookStockRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()

    private val booksStockDb = db.getCollection<Book>()

    override suspend fun atualizarEstoque(bookId: String, quantidade: Int): Boolean {
        return try{
            val filter = Filters.eq("_id", ObjectId(bookId))
            val update = Updates.combine(
                Updates.set(Book::stock.name, quantidade),

            )

            val updateResult = booksStockDb.updateOne(filter, update)
            updateResult.modifiedCount > 0

        }catch (e: Exception){
            logRepository.registrarLog(e, "atualizar estoque", "Book Stock", null)
            false
        }
    }

    override suspend fun getStock(productId: String): Int {
        return try{
            val idToBeSearched = ObjectId(productId)
            booksStockDb.findOneById(idToBeSearched)?.stock?:return 0


        }catch(e: Exception){
            logRepository.registrarLog(e, "pegar estoque", "Book Stock", null)
            0

        }
    }


}