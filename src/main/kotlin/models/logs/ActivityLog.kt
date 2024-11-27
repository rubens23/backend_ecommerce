package models.logs

data class ActivityLog(
    val id: String,
    val userId: String?,
    val action: String, // Ex.: "login", "checkout", "update_profile"
    val details: String?,
    val createdAt: Long = System.currentTimeMillis()
)
