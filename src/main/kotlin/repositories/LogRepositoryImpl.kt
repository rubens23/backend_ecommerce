package repositories

import models.logs.ActivityLog
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class LogRepositoryImpl: LogRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()

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