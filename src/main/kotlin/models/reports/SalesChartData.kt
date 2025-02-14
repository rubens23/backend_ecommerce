package models.reports

@kotlinx.serialization.Serializable
data class SalesChartData(
    val label: String,
    val mes: String,  // Mês, Trimestre, Ano, etc.
    val totalSales: Int,  // Número total de vendas no período
    val totalAmount: Double,  // Valor total das vendas no período
    val ano: String
)
