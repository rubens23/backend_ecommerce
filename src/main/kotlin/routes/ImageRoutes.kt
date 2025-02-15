package routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.makeProductImageUrl(){
    post("/makeProductImageUrl"){
        try {
            val multipart = call.receiveMultipart()
            var imageUrl: String? = null

            multipart.forEachPart {
                    part->
                if(part is io.ktor.http.content.PartData.FileItem){
                    val fileName = part.originalFileName ?: "image_${System.currentTimeMillis()}.jpg"
                    val file = File("uploads/$fileName")

                    // Garante que a pasta existe
                    file.parentFile.mkdirs()
                    file.writeBytes(part.streamProvider().readBytes()) // Salva o arquivo

                    imageUrl = "http://localhost:8099/uploads/$fileName"
                }
                part.dispose()
            }

            if(imageUrl != null){
                call.respond(HttpStatusCode.OK, mapOf("imageUrl" to imageUrl))
            }else{
                call.respond(HttpStatusCode.BadRequest, "Erro ao fazer upload da imagem")
            }
        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado ${e.message}")
        }

    }

}

fun Application.configureStaticFiles(){
    routing{
        static("/uploads"){
            files("uploads")

        }
    }
}