package `dependency-injection`

import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import repositories.*

val appModule = module {
    //place where to define all the components to be injected


    single{
        val mongoPw = System.getenv("MONGO_PW")
        //aqui eu posso iniciar o banco de dados
        val dbName = "db-ecommerce"
        val db = KMongo.createClient(
            connectionString = "mongodb+srv://rubens_23:$mongoPw@cluster0.9yxof3z.mongodb.net/$dbName?retryWrites=true&w=majority&appName=Cluster0"
        ).coroutine
            .getDatabase(dbName)
        db

    }

    single<LogRepository>{LogRepositoryImpl()}
    single<AdmOrderRepository>{AdmOrderRepositoryImpl()}
    single<ProductRepository> { ProductRepositoryImpl() }
}