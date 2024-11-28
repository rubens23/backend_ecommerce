package services

import models.product.Product
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.ProductRepository

class ProductService: KoinComponent {
    private val productRepository: ProductRepository by inject()

    suspend fun addProduct(product: Product): Boolean{
        return productRepository.addProduct(product)

    }
    suspend fun listProducts(): List<Product>{
        return productRepository.listProducts()


    }
    suspend fun getProductById(id: String): Product?{
        return productRepository.getProductById(id)
        //RETORNOU NULO


    }
    suspend fun updateProduct(product: Product): Boolean{
        return productRepository.updateProduct(product)
        //UPDATE NÃO FUNCIONOU


    }
    suspend fun removeProduct(id: String): Boolean{
        return productRepository.removeProduct(id)
        //NÃO REMOVEU


    }

}