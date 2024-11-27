package models.logs

data class ErrorLog(
    val id: String,
    val errorMessage: String,
    val stackTrace: String?,
    val endpoint: String,
    val createdAt: Long = System.currentTimeMillis()
)
