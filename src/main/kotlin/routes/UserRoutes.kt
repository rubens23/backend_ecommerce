package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.product.toResponse
import org.apache.hc.core5.http.HttpStatus
import repositories.UserRepository

fun Route.getAllAdmins(userRepository: UserRepository){
    authenticate {
        get("/getAllAdmins"){
            try {
                val admins = userRepository.getAllAdmins()

                if (admins != null) {
                    call.respond(HttpStatusCode.OK, admins)
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

            }
        }
    }
}

fun Route.getAdmin(userRepository: UserRepository){
    authenticate {
        get("/getAdminById/{id}"){
            try{
                val id = call.parameters["id"]

                if(id.isNullOrBlank()){
                    call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                }

                val admin = userRepository.getAdminById(id)

                if(admin != null){
                    call.respond(HttpStatusCode.OK, admin)
                }else{
                    call.respond(HttpStatusCode.NotFound)
                }

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
            }

        }
    }
}