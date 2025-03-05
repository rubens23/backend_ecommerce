package models.product.book

@kotlinx.serialization.Serializable
data class BookResponse(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val category: String?,
    val author: String,
    val publisher: String?,
    val pages: Int,
    val bookCover: String,
    val createdAt: Long,
    val minimumStock: Int
)
