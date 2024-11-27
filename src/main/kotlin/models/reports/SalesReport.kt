package models.reports

import models.product.Product

data class SalesReport(
    val startDate: Long,
    val endDate: Long,
    val totalSales: Double,
    val totalOrders: Int,
    val bestSellingProducts: List<Product>
)
