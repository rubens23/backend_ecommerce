package repositories

import models.product.Product

interface ProductRepository {
    suspend fun addProduct(product: Product): Boolean
    suspend fun listProducts(): List<Product>?
    suspend fun getProductById(id: String): Product?
    suspend fun updateProduct(product: Product): Boolean
    suspend fun removeProduct(id: String): Boolean
    suspend fun getProductPrice(productId: String): Double?
    suspend fun getProductsById(productIds: Set<String>): List<Product>?
}