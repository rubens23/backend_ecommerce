package clients

import com.mercadopago.MercadoPagoConfig
import com.mercadopago.client.common.IdentificationRequest
import com.mercadopago.client.payment.PaymentClient
import com.mercadopago.client.payment.PaymentCreateRequest
import com.mercadopago.client.payment.PaymentPayerRequest
import com.mercadopago.core.MPRequestOptions
import com.mercadopago.exceptions.MPException
import models.payment.Payment
import models.payment.PaymentMethod
import models.payment.pix.PixPaymentRequest
import models.payment.pix.PixPaymentResponse
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

//https://www.mercadopago.com.br/developers/pt/docs/checkout-api/integration-configuration/integrate-with-pix


class MercadoPagoClient : PaymentGateway {

    //esboço da implementação dos dois primeiros metodos do gateway
    /*
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

     */

    /*
    override suspend fun cancelarPagamento(paymentId: String): Boolean {
        return true
    }

     */


    override suspend fun processarPagamentoPix(
        pixPaymentRequest: PixPaymentRequest
    ): PixPaymentResponse? {
        return try {

            val mercadopagoAccessTokenTest = System.getenv("ACCESS_TOKEN_MERCADOPAGO")

            //pegar o access token la no mercado pago depois de criar uma conta
            //armazenar o token como variavel de ambiente
            MercadoPagoConfig.setAccessToken(mercadopagoAccessTokenTest)


            val customHeaders = HashMap<String, String>()
            val idempotencyKey = UUID.randomUUID().toString()
            customHeaders["x-idempotency-key"] = idempotencyKey

            val requestOptions = MPRequestOptions.builder()
                .customHeaders(customHeaders)
                .build()

            val paymentClient = PaymentClient()

            val paymentCreateRequest = PaymentCreateRequest.builder()
                .transactionAmount(BigDecimal(pixPaymentRequest.valor))
                .description("Compra na loja de livros")
                .paymentMethodId("pix")
                .payer(
                    PaymentPayerRequest.builder()
                        .email(pixPaymentRequest.email)
                        .firstName(pixPaymentRequest.nome)
                        .identification(
                            IdentificationRequest.builder().type("CPF").number(pixPaymentRequest.cpf).build()
                        ).build()
                ).build()

            val paymentResponse = paymentClient.create(paymentCreateRequest, requestOptions)

            PixPaymentResponse(
                id = paymentResponse.id,
                status = paymentResponse.status,
                statusDetail = paymentResponse.statusDetail,
                qrCode = paymentResponse.pointOfInteraction.transactionData.qrCode,
                qrCodeBase64 = paymentResponse.pointOfInteraction.transactionData.qrCodeBase64,
                ticketUrl = paymentResponse.pointOfInteraction.transactionData.ticketUrl
            )


        } catch (e: MPException) {
            throw MPException("Erro ao gerar PIX", e)
        } catch (e: Exception) {
            throw Exception("Erro desconhecido ao gerar PIX", e)

        }
    }

    override suspend fun pegarChavePix(idPix: String): String {
        return ""
    }

    override suspend fun iniciarGatewayPagamento(metodoPagamento: PaymentMethod, valorTotal: Double): Payment? {
        return null
    }

    override suspend fun cancelarPagamento(paymentId: String): Boolean {
        return false
    }


}