package models.cart

import models.product.ItemType

@kotlinx.serialization.Serializable
data class CartItemResponse(
    val userId: String,
    val productId: String,
    val quantity: Int,
    val price: Double,
    val stockQnt: Int,
    val itemType: ItemType
)


fun CartItemResponse.toCartItem(): CartItem{
    return CartItem(
        userId = userId,
        productId = productId,
        quantity = quantity,
        price = price,
        stockQnt = stockQnt,
        itemType = itemType
    )
}
