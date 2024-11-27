package repositories

import models.logs.ActivityLog
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne

class LogRepositoryImpl(
    db: CoroutineDatabase
): LogRepository {

    private val logsDb = db.getCollection<ActivityLog>()



    override suspend fun registrarLog(e: Exception, metodo: String, action: String, userId: String?): ActivityLog {
        try {
            val log =    ActivityLog(
                details = "Erro ao $metodo: ${e.message}\ndetalhes do erro: ${e.stackTraceToString()}",
                action = action,
                userId = userId


            )
            logsDb.insertOne(log)
            return log

        }catch (e: Exception){
            throw e

        }



    }

    override suspend fun listarLogs(): List<ActivityLog> {
        try {
            return logsDb.find().toList()
        }catch (e: Exception){
            throw e
        }
    }
}