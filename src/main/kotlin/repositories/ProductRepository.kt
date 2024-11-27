package repositories

import models.product.Product

interface ProductRepository {
    suspend fun addProduct(product: Product): Product
    suspend fun listProducts(): List<Product>
    suspend fun getProductById(id: String): Product?
    suspend fun updateProduct(product: Product): Product
    suspend fun removeProduct(id: String): Boolean
}