package models.reports

@kotlinx.serialization.Serializable
data class BestSellingProductWithName(
    val productId: String,
    val productName: String,
    val totalQuantitySold: Int,
    val totalRevenue: Double
)
