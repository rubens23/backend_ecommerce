package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import repositories.WishlistRepository

fun Route.wishlistRoutes(wishlistRepository: WishlistRepository){
    authenticate {
        post("/wishlist/add"){
            try{
                val request = call.receive<Map<String, String>>()
                val userId = request["userId"]
                val productId = request["productId"]

                if(userId.isNullOrBlank() || productId.isNullOrBlank()){
                    call.respond(HttpStatusCode.BadRequest, "Parâmetros inválidos")
                    return@post
                }

                wishlistRepository.addToWishList(userId, productId)
                call.respond(HttpStatusCode.Created, "Livro adicionado à wishlist com sucesso")

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Erro ao adicionar à wishlist: ${e.message}")

            }
        }
        delete("/wishlist/remove") {
            try {
                val request = call.receive<Map<String, String>>() // Espera um JSON com "userId" e "productId"
                val userId = request["userId"]
                val productId = request["productId"]

                if (userId.isNullOrBlank() || productId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Parâmetros inválidos")
                    return@delete
                }

                wishlistRepository.removeFromWishlist(userId, productId)
                call.respond(HttpStatusCode.OK, "Livro removido da wishlist com sucesso")

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao remover da wishlist: ${e.message}")
            }
        }

        get("/wishlist/{userId}") {
            try {
                val userId = call.parameters["userId"]

                if (userId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "ID do usuário inválido")
                    return@get
                }

                val wishlist = wishlistRepository.getWishlistByUser(userId)

                if (wishlist != null) {
                    call.respond(HttpStatusCode.OK, wishlist)
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao obter a wishlist: ${e.message}")
            }
        }
    }
}