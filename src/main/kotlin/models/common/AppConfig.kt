package models.common

data class AppConfig(
    val defaultCurrency: String = "BRL",
    val supportedPaymentMethods: List<String> = listOf("pix", "credit_card"),
    val adminEmail: String = "admin@livraria.com"
)
