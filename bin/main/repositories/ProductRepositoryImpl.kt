package repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import models.product.Product
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class ProductRepositoryImpl: ProductRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()

    private val productsDb = db.getCollection<Product>()

    override suspend fun addProduct(product: Product): Boolean {
        try{
            return productsDb.insertOne(
                product
            ).wasAcknowledged()

        }catch (e: Exception){
            logRepository.registrarLog(e, "adicionar produto", "Product", null)
            throw e
        }
    }

    override suspend fun listProducts(): List<Product> {
        try{
            return productsDb.find().toList()

        }catch (e: Exception){
            logRepository.registrarLog(e, "listar produtos", "Product", null)
            throw e
        }
    }

    override suspend fun getProductById(id: String): Product? {
        try{
            return productsDb.findOneById(id)

        }catch (e: Exception){
            logRepository.registrarLog(e, "pegar produto por id", "Product", null)
            throw e
        }
    }

    override suspend fun updateProduct(product: Product): Boolean {
        try{
            val filter = Filters.eq(Product::id.toString(), product.id)
            val update = Updates.combine(
                Updates.set(Product::name.toString(), product.name),
                Updates.set(Product::description.toString(), product.description),
                Updates.set(Product::price.toString(), product.price),
                Updates.set(Product::stock.toString(), product.stock),
                Updates.set(Product::category.toString(), product.category),
                Updates.set(Product::createdAt.toString(), product.createdAt),
            )

            return productsDb.updateOne(filter, update).wasAcknowledged()



        }catch (e: Exception){
            logRepository.registrarLog(e, "atualizar produto", "Product", null)
            throw e
        }
    }

    override suspend fun removeProduct(id: String): Boolean {
        try{
            val filter = Filters.eq(Product::id.toString(), id)
            return productsDb.deleteOne(filter).wasAcknowledged()

        }catch (e: Exception){
            logRepository.registrarLog(e, "remover produto", "Product", null)
            throw e
        }
    }
}