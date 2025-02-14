package models.reports

@kotlinx.serialization.Serializable
data class OrdersChartData(
    val label: String,
    val mes: String,  // Mês, Trimestre, Ano, etc.
    val totalOrders: Int,  // Número total de pedidos
    val ano: String
)
