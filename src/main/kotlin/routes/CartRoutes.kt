package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.cart.*
import models.product.book.toResponse
import repositories.BookRepository
import repositories.CartRepository

fun Route.putNewProductInCart(cartRepository: CartRepository){
    authenticate {
        put("/putNewProductInCart/{userId}"){
            try {

                val userId = call.parameters["userId"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Usuário inválido")
                val cartItem = call.receive<CartItemResponse>()


                val adicionouItem = cartRepository.adicionarAoCarrinho(userId, cartItem.productId, cartItem.quantity, cartItem.itemType, cartItem.price)

                if (adicionouItem){
                    call.respond(HttpStatusCode.OK, "item adicionado ao carrinho")


                }else{
                    call.respond(HttpStatusCode.BadRequest, "falha ao adicionar o item no carrinho")

                }


            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

            }
        }
    }
}

fun Route.deleteProductFromCart(cartRepository: CartRepository){
    authenticate {
        delete("/deleteProductFromCart/{userId}/{itemId}"){
            try{
                val userId = call.parameters["userId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Usuário inválido")
                val itemId = call.parameters["itemId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Item inválido")


                val removeuItem = cartRepository.removerItemDoCarrinho(userId, itemId)

                if(removeuItem){
                    call.respond(HttpStatusCode.OK, "item removido do carrinho")
                }else{
                    call.respond(HttpStatusCode.BadRequest, "falha ao remover item do carrinho")
                }

            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
            }

        }
    }
}

fun Route.updateProductInCart(cartRepository: CartRepository){
    authenticate {
        put("/updateProductInCart/{userId}"){
            try{
                val userId = call.parameters["userId"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Usuário inválido")
                val cartItem = call.receive<CartItemResponse>()

                val atualizouItem = cartRepository.atualizarProdutoNoCarrinho(userId, cartItem.productId, cartItem.quantity)

                if(atualizouItem){
                    call.respond(HttpStatusCode.OK, "item atualizado no carrinho")
                }else{
                    call.respond(HttpStatusCode.BadRequest, "falha ao atualizar item no carrinho")
                }
            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")

            }
        }
    }
}

fun Route.getCartWithBooks(bookRepository: BookRepository, cartRepository: CartRepository){
    authenticate {
        get("/getCartWithBooks/{id}"){
            try{
                val userId = call.parameters["id"]?: return@get call.respond(HttpStatusCode.Forbidden)

                val cart = cartRepository.pegarCarrinhoPorUserId(userId)

                if (cart != null){
                    //carrinho tem livros
                    //livros tem que ser passados junto na resposta
                    val cartItemsWithBooks = mutableListOf<CartItemWithBook>()
                    var totalAmount = 0.0

                    for (item in cart.items){
                        val book = bookRepository.buscarLivroPorId(item.productId)

                        if(book != null){
                            cartItemsWithBooks.add(
                                CartItemWithBook(
                                userId = item.userId,
                                productId = item.productId,
                                quantity = item.quantity,
                                price = item.price,
                                bookResponse = book.toResponse()
                            )
                            )

                            totalAmount += item.price * item.quantity
                        }
                    }

                    val cartWithBooks = CartWithBooks(
                        id = cart.id.toHexString(),
                        userId = cart.userId,
                        items = cartItemsWithBooks,
                        totalAmount = totalAmount

                    )

                    call.respond(HttpStatusCode.OK, cartWithBooks)


                }else{
                    call.respond(HttpStatusCode.NotFound, "o carrinho está vazio")
                }



            }catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
            }
        }
    }
}