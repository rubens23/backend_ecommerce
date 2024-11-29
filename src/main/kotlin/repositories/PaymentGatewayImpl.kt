package repositories

import kotlinx.coroutines.delay
import models.payment.PaymentGatewayResponse
import models.payment.PaymentMethod
import models.payment.PaymentStatus

class PaymentGatewayImpl: PaymentGateway {
    override suspend fun iniciarGatewayPagamento(
        metodoPagamento: PaymentMethod,
        valorTotal: Double
    ): PaymentGatewayResponse {
        //resposta fake do gateway enquanto eu ainda não implementei ele de verdade

        delay(1000)

        // Para fins de simulação, vamos criar uma resposta com dados fixos.
        return PaymentGatewayResponse(
            transactionId = "trans_${System.currentTimeMillis()}",
            status = PaymentStatus.PENDENTE,
            amount = valorTotal,
            paymentMethod = metodoPagamento.nome,
            message = "Pagamento iniciado com sucesso"
        )
    }

    override suspend fun cancelarPagamento(paymentId: String): Boolean {
        return true
    }
}