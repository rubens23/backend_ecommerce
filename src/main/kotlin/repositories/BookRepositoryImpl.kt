package repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import models.product.book.Book
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class BookRepositoryImpl: BookRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()

    private val booksDb = db.getCollection<Book>()


    override suspend fun adicionarLivro(livro: Book): Boolean {
        return try{
            booksDb.insertOne(
                livro
            ).wasAcknowledged()

        }catch (e: Exception){
            logRepository.registrarLog(e, "adicionar livro", "Book", null)
            false
        }
    }

    override suspend fun listarLivros(): List<Book>? {
        return try{
            booksDb.find().toList()

        }catch (e: Exception){
            logRepository.registrarLog(e, "listar livros", "Book", null)
            null
        }
    }

    override suspend fun buscarLivroPorId(id: String): Book? {
        return try{
            val idToBeSearched = ObjectId(id)
            booksDb.findOneById(idToBeSearched)



        }catch (e: Exception){
            logRepository.registrarLog(e, "buscar livro por id", "Book", null)
            null
        }
    }

    override suspend fun buscarLivrosPorCriterios(criterios: Map<String, String>): List<Book>? {
        return try{
            val filters = mutableListOf<Bson>()

            // Itera pelos critérios do mapa e adiciona filtros
            criterios.forEach { (key, value) ->
                when (key) {
                    "name" -> filters.add(Filters.regex(key, ".*$value.*", "i")) // Busca pelo nome, ignorando case
                    "author" -> filters.add(Filters.regex(key, ".*$value.*", "i")) // Busca pelo autor
                    "category" -> filters.add(Filters.eq(key, value)) // Categoria exata
                    "priceMin" -> filters.add(Filters.gte("price", value.toDouble())) // Preço mínimo
                    "priceMax" -> filters.add(Filters.lte("price", value.toDouble())) // Preço máximo
                    "stockMin" -> filters.add(Filters.gte("stock", value.toInt())) // Estoque mínimo
                    "stockMax" -> filters.add(Filters.lte("stock", value.toInt())) // Estoque máximo
                    else -> Unit // Ignora chaves desconhecidas
                }
            }

            val filter = if(filters.isNotEmpty()) Filters.and(filters) else Filters.empty()

            booksDb.find(filter).toList()





        }catch (e: Exception){
            logRepository.registrarLog(e, "buscar livros por criterios", "Book", null)
            null
        }
    }

    override suspend fun atualizarLivro(book: Book): Boolean {
        return try{
            val filter = Filters.eq("_id", book.id)
            val update = Updates.combine(
                Updates.set(Book::name.name, book.name),
                Updates.set(Book::description.name, book.description),
                Updates.set(Book::price.name, book.price),
                Updates.set(Book::stock.name, book.stock),
                Updates.set(Book::category.name, book.category),
                Updates.set(Book::createdAt.name, book.createdAt),
                Updates.set(Book::author.name, book.author),
                Updates.set(Book::publisher.name, book.publisher),
                Updates.set(Book::pages.name, book.pages),
                Updates.set(Book::bookCover.name, book.bookCover)
            )

            val updateResult = booksDb.updateOne(filter, update)
            updateResult.modifiedCount > 0

        }catch (e: Exception){
            logRepository.registrarLog(e, "atualizar livro", "Book", null)
            false
        }
    }

    override suspend fun removerLivro(id: String): Boolean {
        return try{
            val idToBeSearched = ObjectId(id)
            val filter = Filters.eq("_id", idToBeSearched)
            booksDb.deleteOne(filter).wasAcknowledged()

        }catch (e: Exception){
            logRepository.registrarLog(e, "adicionar livro", "Book", null)
            false
        }
    }
}