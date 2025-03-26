package repositories

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import models.wishlist.Wishlist
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class BookWishlistRepositoryImpl: WishlistRepository, KoinComponent {

    private val db: CoroutineDatabase by inject()
    private val logRepository: LogRepository by inject()

    private val wishlistDb = db.getCollection<Wishlist>()


    override suspend fun addToWishList(userId: String, productId: String) {
        try {
            wishlistDb.updateOne(
                Filters.eq("_id", userId),
                Updates.addToSet("books", productId),
                UpdateOptions().upsert(true)
            )

        }catch (e: Exception){
            logRepository.registrarLog(e, "addToWishlist", "BookWishlist", userId)
        }
    }

    override suspend fun removeFromWishlist(userId: String, productId: String) {
        try {
            wishlistDb.updateOne(
                Filters.eq("_id", userId),
                Updates.pull("books", productId),
            )

        }catch (e: Exception){
            logRepository.registrarLog(e, "removeFromWishlist", "BookWishlist", userId)
        }
    }

    override suspend fun getWishlistByUser(userId: String): List<String>?{
        return try {
            wishlistDb.find(
                Filters.eq("_id", userId),
            ).first()?.books

        }catch (e: Exception){
            logRepository.registrarLog(e, "removeFromWishlist", "BookWishlist", userId)
            null
        }
    }
}