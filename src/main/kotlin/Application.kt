import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


fun main(){
//

    val mongoPw = System.getenv("MONGO_PW")
    //aqui eu posso iniciar o banco de dados
    val dbName = "db-ecommerce"
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://rubens_23:$mongoPw@cluster0.9yxof3z.mongodb.net/$dbName?retryWrites=true&w=majority&appName=Cluster0"
    ).coroutine
        .getDatabase(dbName)





    //instanciar o admOrderRepository
    //usar injeção de dependencia para não depender da implementacao concreta
    //e aumentar o desacoplamento



}