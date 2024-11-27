package models.product

open class Product(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val category: String?,
    val createdAt: Long = System.currentTimeMillis()
)