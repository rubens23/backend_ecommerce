package models.cart

data class CartItem(
    val userId: String,
    val productId: String,
    val quantity: Int,
    val price: Double // Preço salvo no momento da adição ao carrinho
)

fun CartItem.toCartItemResponse(): CartItemResponse{
    return CartItemResponse(
        userId = userId,
        productId = productId,
        quantity = quantity,
        price = price
    )
}

