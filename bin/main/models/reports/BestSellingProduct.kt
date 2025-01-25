package models.reports

data class BestSellingProduct(
    val productId: String,
    val totalQuantitySold: Int,
    val totalRevenue: Double
)