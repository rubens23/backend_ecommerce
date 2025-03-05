package models.stock

import org.bson.types.ObjectId
import java.text.SimpleDateFormat
import java.util.*

@kotlinx.serialization.Serializable
data class StockMovementDTO(
    val id: String,
    val productId: String,
    val tipo: String,
    val quantidade: Int,
    val data: String,
    val hora: String,
    val responsavel: String?,
    val nomeProduto: String,
    val nomeResponsavel: String,
)

fun StockMovementDTO.toStockMovement(responsavelId: String): StockMovement {
    // Converte a data e hora recebidas no DTO para um Long (timestamp)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    // Faz a conversão da data
    val date = dateFormat.parse(this.data)
    val time = timeFormat.parse(this.hora)

    // Combina a data e hora para obter o timestamp
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, time.hours)
    calendar.set(Calendar.MINUTE, time.minutes)
    calendar.set(Calendar.SECOND, time.seconds)




    // Cria e retorna o StockMovement a partir do DTO
    return StockMovement(
        id = ObjectId(),
        productId = this.productId,
        tipo = this.tipo,
        quantidade = this.quantidade,
        data = calendar.timeInMillis,
        responsavelId = responsavelId
    )
}
