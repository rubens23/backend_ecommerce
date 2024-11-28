package repositories

import models.logs.ActivityLog

interface LogRepository {
    suspend fun registrarLog(e: Exception, metodo: String, action: String, userId: String?): ActivityLog
    suspend fun listarLogs(): List<ActivityLog>


}