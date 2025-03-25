package clients

import models.payment.*
import models.payment.pix.PixPaymentRequest
import models.payment.pix.PixPaymentResponse

interface PaymentGateway {

    //Esses dois primeiros metodos eu vou pensar na implementação depois
    //suspend fun iniciarGatewayPagamento(metodoPagamento: PaymentMethod, valorTotal: Double): PaymentGatewayResponse
    //suspend fun cancelarPagamento(paymentId: String): Boolean

    suspend fun processarPagamentoPix(
        pixPaymentRequest: PixPaymentRequest
    ): PixPaymentResponse?

    suspend fun pegarChavePix(idPix: String): String

    //Eu tenho que pensar melhor na implementação desse método e
    //ver se ele realmente vai ser necessário
    suspend fun iniciarGatewayPagamento(metodoPagamento: PaymentMethod, valorTotal: Double): Payment?
    suspend fun cancelarPagamento(paymentId: String): Boolean
}