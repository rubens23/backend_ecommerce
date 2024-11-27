package models.product.book

import models.product.Product

class Book(
    id: String,
    name: String,
    description: String?,
    price: Double,
    stock: Int,
    category: String?,
    createdAt: Long = System.currentTimeMillis(),
    val author: String,
    val publisher: String?,
    val pages: Int
): Product(id, name, description, price, stock, category, createdAt)
