package models.order

import models.cart.CartItem
import models.cart.CartItemResponse
import models.cart.CartResponse
import models.payment.PaymentRequest
import models.payment.pix.PixPaymentDto
import models.payment.pix.PixPaymentResponse
import models.user.AddressDto

@kotlinx.serialization.Serializable
data class OrderRequest(
    val userCart: CartResponse,
    val payment: PaymentRequest,
    val addressDto: AddressDto,
    val pixResponse: PixPaymentResponse?
)