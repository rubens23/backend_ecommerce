package repositories

import clients.PaymentGateway
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import models.cart.CartItem
import models.payment.*
import models.user.Address
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class PaymentRepositoryImpl: PaymentRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()
    private val paymentGateway: PaymentGateway by inject()

    private val paymentDb = db.getCollection<Payment>()
    override suspend fun adicionarNovoPagamento(payment: Payment): Boolean {
        return try {
            val paymentSaved = paymentDb.insertOne(payment).wasAcknowledged()
            paymentSaved

        }catch (e: Exception){
            logRepository.registrarLog(e, "adicionar pagamento", "Payment", null)
            false

        }


    }

    override suspend fun processarPagamento(
        userId: String,
        carrinho: List<CartItem>,
        endereco: Address?,
        metodoPagamento: PaymentMethod
    ): ProcessarPagamentoResult {

        try {
            // Verificar se o userId é valido
            if(userId.isBlank()){
                return ProcessarPagamentoResult.Error("Usuário inválido")
            }

            // Verificar se o carrinho está vazio
            if(carrinho.isEmpty()){
                return ProcessarPagamentoResult.Error("Carrinho vazio")
            }

            // Verificar se o endereço é válido
            if (endereco == null){
                return ProcessarPagamentoResult.Error("Endereço é obrigatório")
            }

            val valorTotal = carrinho.sumOf { it.price * it.quantity }

            // Criar o pagamento com o status pendente
            val payment = Payment(
                orderId = "", //vazio ainda pois o pedido ainda não foi gerado
                userId = userId,
                amount = valorTotal,
                paymentMethod = metodoPagamento.name,
                status = PaymentStatus.PENDENTE.name,
                transactionId = null, //nulo pois ainda não tem a resposta do gateway
            )



            // Salvar o pagamento no banco de dados
            val paymentSaved = paymentDb.insertOne(payment).wasAcknowledged()
            if(!paymentSaved){
                return ProcessarPagamentoResult.Error("Erro ao salvar o pagamento no banco de dados")
            }

            // iniciar o processamento do pagamento no gateway
            val paymentGatewayResponse = paymentGateway.iniciarGatewayPagamento(metodoPagamento, valorTotal)
                ?: return ProcessarPagamentoResult.Error("Erro no payment gateway response")

            // Atualizar status e transactionId baseado na resposta do gateway
            val updatedPayment = payment.copy(
                status = paymentGatewayResponse.status,
                transactionId = paymentGatewayResponse.transactionId
            )

            // Atualizar no banco de dados o pagamento com o novo status e transactionId
            val paymentUpdated = paymentDb.updateOne(
                Filters.eq("_id", payment.id),
                Updates.combine(
                    Updates.set(Payment::status.name, updatedPayment.status),
                    Updates.set(Payment::transactionId.name, updatedPayment.transactionId)
                )
            )

            if(paymentUpdated.modifiedCount == 0L){
                return ProcessarPagamentoResult.Error("Erro ao atualizar o status e transactionId do pagamento no banco de dados!")

            }

            return ProcessarPagamentoResult.Success(payment = updatedPayment)



        }catch (e: Exception){
            logRepository.registrarLog(e, "processar pagamento", "Payment", userId)
            return ProcessarPagamentoResult.Error("Erro ao processar pagamento: ${e.message}")


        }





    }




    override suspend fun verificarStatusPagamento(paymentId: String): PaymentStatus? {
        return try{
            val filter = Filters.eq("_id", ObjectId(paymentId))
            val payment = paymentDb.findOne(filter)
            if(payment != null){
                val paymentStatus = when (payment.status){
                    "PENDENTE"->{PaymentStatus.PENDENTE}
                    "APROVADO"->{PaymentStatus.APROVADO}
                    "CANCELADO"->{PaymentStatus.CANCELADO}
                    "FALHOU"->{PaymentStatus.FALHOU}
                    "EM PROCESSAMENTO"->{PaymentStatus.EM_PROCESSAMENTO}
                    else-> {null}
                }
                paymentStatus
            }else{
                return null
            }


        }catch (e: Exception){
            logRepository.registrarLog(e, "verificar status pagamento", "Payment", null)
            null
        }
    }

    override suspend fun cancelarPagamento(paymentId: String): Boolean {
        try {
            // Verificar se o pagamento existe no banco antes de tentar cancelar
            val filter = Filters.eq("_id", ObjectId(paymentId))
            val payment = paymentDb.findOne(filter) ?: return false

            // Tentar cancelar o pagamento no gateway
            val cancelouPagamento = paymentGateway.cancelarPagamento(paymentId)

            if (cancelouPagamento) {
                // Atualizar status no banco de dados para "CANCELADO"
                val updateResult = paymentDb.updateOne(
                    Filters.eq("_id", payment.id),
                    Updates.set("status", PaymentStatus.CANCELADO.name)
                )

                // Verificar se a atualização foi bem-sucedida
                return updateResult.modifiedCount > 0
            } else {
                // Caso o cancelamento no gateway tenha falhado
                return false
            }
        } catch (e: Exception) {
            // Logar qualquer exceção que ocorra durante o processo
            logRepository.registrarLog(e, "cancelar pagamento", "Payment", paymentId)
            return false
        }
    }

    override suspend fun atualizarStatusPagamento(paymentId: String, newStatus: PaymentStatus): PaymentStatusUpdateResult {
        return try {
            // Atualizar o status no banco
            val filter = Filters.eq("_id", ObjectId(paymentId))
            val update = Updates.combine(
                Updates.set(Payment::status.name, newStatus)
            )

            val statusUpdated = paymentDb.updateOne(filter, update).modifiedCount > 0

            if(statusUpdated){
                PaymentStatusUpdateResult(
                    success = true,
                    currentStatus = newStatus,
                    message = "payment status updated successfully! new status: $newStatus."
                )
            }else{
                PaymentStatusUpdateResult(
                    success = false,
                    currentStatus = null,
                    message = "Failed to update payment status. Try again"
                )
            }


        } catch (e: Exception) {
            logRepository.registrarLog(e, "atualizar status do pagamento", "Payment", null)
            PaymentStatusUpdateResult(
                success = false,
                currentStatus = null,
                message = "Failed to update payment status because of an error: ${e.message}. Try again"
            )
        }
    }

    override suspend fun obterPagamentoPorId(paymentId: String): Payment? {
        return try{
            val filter = Filters.eq("_id", ObjectId(paymentId))
            paymentDb.findOne(filter)


        }catch (e: Exception){
            logRepository.registrarLog(e, "obter pagamento por id", "Payment", null)
            null
        }

    }

    override suspend fun pegarPagamentos(): List<Payment>? {
        return try{
            paymentDb.find().toList()

        }catch (e: Exception){
            logRepository.registrarLog(e, "pegar pagamentos", "Payment", null)
            null
        }
    }


}