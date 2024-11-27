package models.notification

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    val read: Boolean = false
)
