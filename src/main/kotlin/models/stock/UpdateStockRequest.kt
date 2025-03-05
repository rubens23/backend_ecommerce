package models.stock

@kotlinx.serialization.Serializable
data class UpdateStockRequest(
    val tipo: String, //"entrada" ou "saida"
    val quantidade: Int
)


