package models.cart

@kotlinx.serialization.Serializable
data class CartItemResponse(
    val userId: String,
    val productId: String,
    val quantity: Int,
    val price: Double
)


fun CartItemResponse.toCartItem(): CartItem{
    return CartItem(
        userId = userId,
        productId = productId,
        quantity = quantity,
        price = price
    )
}
