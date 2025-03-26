package models.wishlist

import org.bson.codecs.pojo.annotations.BsonId

data class Wishlist(
    @BsonId
    val userId: String,
    val books: List<String> = emptyList() // Lista de IDs dos Livros

)
