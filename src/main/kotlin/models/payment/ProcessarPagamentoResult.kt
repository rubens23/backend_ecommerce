package models.payment

sealed class ProcessarPagamentoResult {
    data class Success(val payment: Payment): ProcessarPagamentoResult()
    data class Error(val message: String): ProcessarPagamentoResult()

}