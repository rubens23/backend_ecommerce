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