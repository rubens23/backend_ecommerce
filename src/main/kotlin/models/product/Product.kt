package models.product

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

open class Product(
    @BsonId
    val id: ObjectId = ObjectId(),
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val category: String?,
    val createdAt: Long = System.currentTimeMillis()
)

fun Product.toResponse() = ProductResponse(
    id = this.id.toHexString(),  // Convertendo ObjectId para String
    name = this.name,
    description = this.description,
    price = this.price,
    stock = this.stock,
    category = this.category,
    createdAt = this.createdAt
)


fun Product.copyManual(
    name: String = this.name,
    description: String? = this.description,
    price: Double = this.price,
    stock: Int = this.stock,
    category: String? = this.category
): Product {
    return Product(
        id = this.id,
        name = name,
        description = description,
        price = price,
        stock = stock,
        category = category,
        createdAt = this.createdAt
    )
}
