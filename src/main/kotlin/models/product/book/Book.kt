package models.product.book

import models.product.Product
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

class Book(
    @BsonId
    id: ObjectId = ObjectId(),
    name: String,
    description: String?,
    price: Double,
    stock: Int,
    category: String?,
    createdAt: Long = System.currentTimeMillis(),
    val author: String,
    val publisher: String?,
    val pages: Int,
    val bookCover: String = ""
): Product(id, name, description, price, stock, category, createdAt)
