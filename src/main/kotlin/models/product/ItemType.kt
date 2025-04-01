package models.product

/**
 * Tipo de item que pode ser adicionado ao carrinho.
 *
 * Os valores válidos são:
 * - [PRODUCT] para um produto genérico
 * - [BOOK] para um livro
 */
enum class ItemType {
    /**
     * Produto genérico
     */
    PRODUCT,

    /**
     *  Livro
     */
    BOOK,

    /**
     * TIPO INVÁLIDO
     */
    INVALIDO
}