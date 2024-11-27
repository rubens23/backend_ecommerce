import dependency_injection.appModule
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.inject
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import services.AdmOrderService


fun main(){

    startKoin{
        modules(appModule)


    }

    //testar meus repositorios e ver se os metodos estao corretos







}