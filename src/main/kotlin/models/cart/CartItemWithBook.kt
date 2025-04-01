package models.cart

import models.product.book.BookResponse

@kotlinx.serialization.Serializable
data class CartItemWithBook(
    val userId: String,
    val productId: String,
    val quantity: Int,
    val price: Double,
    val bookResponse: BookResponse,
    val stockQnt: Int
)
