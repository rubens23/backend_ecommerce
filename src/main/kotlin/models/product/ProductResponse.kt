package models.product

@kotlinx.serialization.Serializable
data class ProductResponse(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val category: String?,
    val createdAt: Long,
    val minimumStock: Int
)
