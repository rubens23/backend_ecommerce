package repositories

import models.product.Product
import org.koin.core.component.KoinComponent

class StockRepositoryImpl: StockRepository, KoinComponent {
    override suspend fun atualizarEstoque(productId: String, quantidade: Int): Product? {
        //implementação fake só para fins de teste
        return null

    }

    override suspend fun getStock(productId: String): Int {
        //implementação fake só para fins de teste
        return 10
    }

    override suspend fun decrementStock(productId: String, quantity: Int) {

    }
}