import `dependency-injection`.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.product.Product
import models.product.book.Book
import org.bson.types.ObjectId
import org.koin.core.context.GlobalContext.startKoin
import services.BookService
import services.ProductService


fun main(){

    startKoin{
        modules(appModule)


    }

    // Estou usando esse escopo de corrotina para testar meus repositorios
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val job = coroutineScope.launch {
        // Teste para adicionar um livro
        val adicionou = BookService().adicionarLivro(
            Book(
                name = "O Hobbit",
                description = "Aventura fantástica de J.R.R. Tolkien",
                price = 49.99,
                stock = 150,
                category = "Fantasia",
                author = "J.R.R. Tolkien",
                publisher = "HarperCollins",
                pages = 310,
                bookCover = "https://example.com/hobbit.jpg"
            )
        )
        println("Livro adicionado: $adicionou")

        // Teste para listar todos os livros
        val listaLivros = BookService().listarLivros()
        println("Livros listados: ${listaLivros?.size} livros encontrados")

        // Teste para atualizar um livro
        val atualizado = BookService().atualizarLivro(
            Book(
                id = ObjectId("674878789b963841abc1b72b"),
                name = "O Hobbit - Edição Especial",
                description = "Edição especial da famosa obra de J.R.R. Tolkien",
                price = 79.99,
                stock = 100,
                category = "Fantasia",
                author = "J.R.R. Tolkien",
                publisher = "HarperCollins",
                pages = 310,
                bookCover = "https://example.com/hobbit_edition_especial.jpg"
            )
        )
        println("Livro atualizado: $atualizado")

        // Teste para buscar um livro por ID
        val livroPorId = BookService().buscarLivroPorId("674878789b963841abc1b72b")
        println("Livro encontrado por ID: ${livroPorId?.name}")

        // Teste para buscar livros por critérios (como nome e autor)
        val criterios = mapOf(
            "name" to "Hobbit", // Nome do livro
            "author" to "Tolkien" // Autor do livro
        )
        val livrosPorCriterios = BookService().buscarLivrosPorCriterios(criterios)
        println("Livros encontrados por critérios: ${livrosPorCriterios?.size} livros encontrados")

        // Teste para remover um livro
        val removeu = BookService().removerLivro("674878789b963841abc1b72b")
        println("Livro removido: $removeu")
    }

    runBlocking {
        job.join()
    }












}