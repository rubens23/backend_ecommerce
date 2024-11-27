package repositories

import jdk.internal.net.http.common.Log
import models.logs.ActivityLog

interface LogRepository {
    suspend fun registrarLog(log: ActivityLog): ActivityLog
    suspend fun listarLogs(): List<ActivityLog>
}