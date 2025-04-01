package models.order

import models.cart.CartResponse
import models.payment.PaymentRequest
import models.payment.pix.PixPaymentResponse
import models.user.AddressDto

@kotlinx.serialization.Serializable
data class OrderRequest(
    val userCart: CartResponse,
    val addressDto: AddressDto,
)