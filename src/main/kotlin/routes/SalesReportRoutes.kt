package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import repositories.SalesReportRepository

fun Route.pegarLivrosMaisVendidosPorPeriodo(salesReportRepository: SalesReportRepository){
    get("/getBestSellingBooks"){
        try {
            val filter = call.request.queryParameters["filter"]
            val dataInicio = call.request.queryParameters["dataInicio"]?.toLongOrNull()
            val dataFim = call.request.queryParameters["dataFim"]?.toLongOrNull()

            val now = System.currentTimeMillis()
            val MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000L
            val DAYS_IN_A_MONTH = 30L
            val DAYS_IN_A_YEAR = 365L
            val MONTHS_IN_A_QUARTER = 3L

            val (start, end) = when{
                filter == "last_year"->{
                    Pair(now - (DAYS_IN_A_YEAR * MILLISECONDS_IN_A_DAY), now)

                }
                filter == "last_quarter" ->{
                    Pair(now - (MONTHS_IN_A_QUARTER * DAYS_IN_A_MONTH * MILLISECONDS_IN_A_DAY), now)

                }
                dataInicio != null && dataFim != null ->{
                    Pair(dataInicio, dataFim)

                }
                else ->{
                    call.respond(HttpStatusCode.BadRequest, "Faltam parâmetros ou filtro inválido")
                    return@get
                }
            }


            val salesReport = salesReportRepository.gerarRelatorioVendas(start, end)

            if(salesReport != null){
                val livrosMaisVendidos = salesReportRepository.colocarNomeNosLivrosMaisVendidos(salesReport.bestSellingProducts)
                if(livrosMaisVendidos != null){
                    call.respond(HttpStatusCode.OK, livrosMaisVendidos)
                }else{
                    call.respond(HttpStatusCode.NoContent, "Erro ao encontrar livros mais vendidos")

                }

            }else{
                call.respond(HttpStatusCode.NotFound, "Nenhum relatorio de vendas foi encontrado")


            }


        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }
    }
}

fun Route.pegarProdutosMaisVendidosPorPeriodo(salesReportRepository: SalesReportRepository){
    get("/getBestSellingProducts"){
        try {
            val filter = call.request.queryParameters["filter"]
            val dataInicio = call.request.queryParameters["dataInicio"]?.toLongOrNull()
            val dataFim = call.request.queryParameters["dataFim"]?.toLongOrNull()

            val now = System.currentTimeMillis()
            val MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000L
            val DAYS_IN_A_MONTH = 30L
            val DAYS_IN_A_YEAR = 365L
            val MONTHS_IN_A_QUARTER = 3L

            val (start, end) = when{
                filter == "last_year"->{
                    Pair(now - (DAYS_IN_A_YEAR * MILLISECONDS_IN_A_DAY), now)

                }
                filter == "last_quarter" ->{
                    Pair(now - (MONTHS_IN_A_QUARTER * DAYS_IN_A_MONTH * MILLISECONDS_IN_A_DAY), now)

                }
                dataInicio != null && dataFim != null ->{
                    Pair(dataInicio, dataFim)

                }
                else ->{
                    call.respond(HttpStatusCode.BadRequest, "Faltam parâmetros ou filtro inválido")
                    return@get
                }
            }


            val salesReport = salesReportRepository.gerarRelatorioVendas(start, end)

            if(salesReport != null){
                val produtosMaisVendidos = salesReportRepository.colocarNomeNosLivrosMaisVendidos(salesReport.bestSellingProducts)
                if(produtosMaisVendidos != null){
                    call.respond(HttpStatusCode.OK, produtosMaisVendidos)
                }else{
                    call.respond(HttpStatusCode.NoContent, "Erro ao encontrar produtos mais vendidos")

                }

            }else{
                call.respond(HttpStatusCode.NotFound, "Nenhum relatorio de vendas foi encontrado")


            }


        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }
    }

}