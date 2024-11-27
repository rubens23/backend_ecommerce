package repositories

import models.logs.ErrorLog

interface ErrorRepository {
    suspend fun registrarErro(erro: ErrorLog): ErrorLog
    suspend fun listarErros(): List<ErrorLog>
}