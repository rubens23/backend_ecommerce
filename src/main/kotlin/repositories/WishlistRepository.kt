package repositories

interface WishlistRepository {

    suspend fun addToWishList(userId: String, productId: String)

    suspend fun removeFromWishlist(userId: String, productId: String)

    suspend fun getWishlistByUser(userId: String): List<String>?
}