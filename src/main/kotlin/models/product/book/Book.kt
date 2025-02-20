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

fun Book.toResponse() = BookResponse(
    id = this.id.toHexString(),
    name = this.name,
    description = this.description,
    price = this.price,
    stock = this.stock,
    category = this.category,
    author = this.author,
    publisher = this.publisher,
    pages = this.pages,
    bookCover = this.bookCover,
    createdAt = this.createdAt
)

fun Book.copyManual(
    name: String = this.name,
    description: String? = this.description,
    price: Double = this.price,
    stock: Int = this.stock,
    category: String? = this.category,
    author: String = this.author,
    publisher: String? = this.publisher,
    pages: Int = this.pages,
    bookCover: String = this.bookCover
): Book {
    return Book(
        id = this.id,
        name = name,
        description = description,
        price = price,
        stock = stock,
        category = category,
        createdAt = this.createdAt,
        author = author,
        publisher = publisher,
        pages = pages,
        bookCover = bookCover
    )
}
