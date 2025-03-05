package repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import models.product.Product
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class ProductRepositoryImpl: ProductRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()

    private val productsDb = db.getCollection<Product>()

    override suspend fun addProduct(product: Product): Boolean {
        return try{

            // Verificar se já existe um produto com o mesmo nome, descrição e preço
            val existingProduct = productsDb.findOne(
                and(
                    Product::name eq product.name,
                    Product::description eq product.description,
                    Product::price eq product.price
                )
            )


            if (existingProduct != null){
                logRepository.registrarLog(java.lang.Exception("produto duplicado. Produto com nome: ${product.name} já existe."),
                    "addProduct",
                    "Product",
                    null)

                return false

            }

            productsDb.insertOne(
                product
            ).wasAcknowledged()

        }catch (e: Exception){
            logRepository.registrarLog(e, "adicionar produto", "Product", null)
            false
        }
    }

    override suspend fun listProducts(): List<Product>? {
        return try{
            productsDb.find().toList()

        }catch (e: Exception){
            logRepository.registrarLog(e, "listar produtos", "Product", null)
            null
        }
    }

    override suspend fun getProductById(id: String): Product? {
        return try{
            val idToBeSearched = ObjectId(id)
            productsDb.findOneById(idToBeSearched)

        }catch (e: Exception){
            logRepository.registrarLog(e, "pegar produto por id", "Product", null)
            null
        }
    }

    override suspend fun updateProduct(product: Product): Boolean {
        try{
            val filter = Filters.eq("_id", product.id)
            val update = Updates.combine(
                Updates.set(Product::name.name, product.name),
                Updates.set(Product::description.name, product.description),
                Updates.set(Product::price.name, product.price),
                Updates.set(Product::stock.name, product.stock),
                Updates.set(Product::category.name, product.category),
                Updates.set(Product::createdAt.name, product.createdAt),
            )

            val updateResult = productsDb.updateOne(filter, update)
            return updateResult.modifiedCount > 0



        }catch (e: Exception){
            logRepository.registrarLog(e, "atualizar produto", "Product", null)
            return false
        }
    }

    override suspend fun removeProduct(id: String): Boolean {
        return try{
            val idToBeSearched = ObjectId(id)
            val filter = Filters.eq("_id", idToBeSearched)
            productsDb.deleteOne(filter).wasAcknowledged()

        }catch (e: Exception){
            logRepository.registrarLog(e, "remover produto", "Product", null)
            false
        }
    }

    override suspend fun getProductPrice(productId: String): Double? {
        return try {
            productsDb.findOneById(ObjectId(productId))?.price

        } catch (e: Exception) {
            logRepository.registrarLog(e, "pegar preço do produto", "Cart", null)
            null
        }


    }

    override suspend fun getProductsById(productIds: Set<String>): List<Product>? {
        return try {
            // Convertendo os IDs de String para ObjectId
            val objectIds = productIds.map { ObjectId(it) }

            // Buscando os produtos com os IDs fornecidos
            productsDb.find(Product::id `in` objectIds).toList() // Retorna a lista de produtos encontrados
        } catch (e: Exception) {
            logRepository.registrarLog(e, "buscar produtos por IDs", "Product", null)
            null
        }
    }
}