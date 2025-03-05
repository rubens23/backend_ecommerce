package models.stock

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.text.SimpleDateFormat
import java.util.*

data class StockMovement(
    @BsonId
    val id: ObjectId = ObjectId(),
    val productId: String,
    val tipo: String, // "entrada" ou "saida"
    val quantidade: Int,
    val data: Long = System.currentTimeMillis(),
    val responsavelId: String //id do responsavel
)


fun StockMovement.toDTO(nomeResponsavel: String = "", nomeProduto: String = ""): StockMovementDTO{
    val date = Date(data)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    return StockMovementDTO(
        id = id.toHexString(),
        productId = productId,
        tipo = tipo,
        quantidade = quantidade,
        data = dateFormat.format(date),
        hora = timeFormat.format(date),
        responsavel = null,
        nomeProduto = nomeProduto,
        nomeResponsavel = nomeResponsavel

    )
}

