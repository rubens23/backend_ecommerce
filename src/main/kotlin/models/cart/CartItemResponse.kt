package models.cart

import models.product.ItemType

@kotlinx.serialization.Serializable
data class CartItemResponse(
    val userId: String,
    val productId: String,
    val quantity: Int,
    val price: Double,
    val itemType: ItemType
)


fun CartItemResponse.toCartItem(itemType: String): CartItem{
    val parsedItemType = try {
        ItemType.valueOf(itemType) // Converte a String para o Enum
    } catch (e: IllegalArgumentException) {
        null
    }
    return CartItem(
        userId = userId,
        productId = productId,
        quantity = quantity,
        price = price,
        itemType = parsedItemType?:ItemType.INVALIDO
    )
}


