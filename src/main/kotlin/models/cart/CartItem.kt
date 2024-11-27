package models.cart

data class CartItem(
    val id: String,
    val userId: String,
    val productId: String,
    val quantity: Int,
    val price: Double // Preço salvo no momento da adição ao carrinho
)
