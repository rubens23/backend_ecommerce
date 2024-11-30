package models.sale

import models.payment.PaymentStatus
import models.user.Address
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Sale(
    @BsonId
    val id: ObjectId = ObjectId(),         // ID único da venda
    val userId: String,                    // ID do usuário
    val cartId: ObjectId,                    // ID do carrinho relacionado
    val paymentId: ObjectId,                 // ID do pagamento relacionado
    val totalAmount: Double,               // Valor total da venda
    val paymentStatus: PaymentStatus = PaymentStatus.PENDENTE, // Status do pagamento
    val saleStatus: SaleStatus = SaleStatus.PENDENTE,                // Status da venda
    val createdAt: Long = System.currentTimeMillis(), // Data e hora da venda
    val shippingAddress: Address           // Endereço de entrega
)
