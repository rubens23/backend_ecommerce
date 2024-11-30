package models.reports

import models.product.Product
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class SalesReport(
    @BsonId
    val id: ObjectId = ObjectId(),
    val startDate: Long,
    val endDate: Long,
    val totalSales: Double,
    val totalOrders: Int,
    val bestSellingProducts: List<Product>
)
