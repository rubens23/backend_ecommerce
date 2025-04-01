package models.cart

import models.product.ItemType

data class CartItem(
    val userId: String,
    val productId: String,
    val quantity: Int,
    val price: Double, // Preço salvo no momento da adição ao carrinho
    val itemType: ItemType
)

fun CartItem.toCartItemResponse(): CartItemResponse{
    return CartItemResponse(
        userId = userId,
        productId = productId,
        quantity = quantity,
        price = price,
        itemType = itemType
    )
}

