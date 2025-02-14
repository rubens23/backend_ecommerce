package routes

import extensions.toMonthYear
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.payment.PaymentStatus
import models.reports.SalesChartData
import repositories.SaleRepository
import repositories.SalesReportRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Route.getTotalVendas(saleRepository: SaleRepository){
    get("/getSalesTotal"){
        try{
            val vendas = saleRepository.listarVendasPorStatus(PaymentStatus.APROVADO.name)


            if (vendas != null){
                call.respond(HttpStatusCode.OK, vendas.size)
            }else{
                call.respond(HttpStatusCode.OK, "Nenhuma venda foi concluída ainda!")

            }

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }
    }
}



fun Route.getSalesByPeriod(saleRepository: SaleRepository){
    get("/salesForChart"){
        val filter = call.request.queryParameters["filter"]
        val dataInicio = call.request.queryParameters["dataInicio"]?.toLongOrNull()
        val dataFim = call.request.queryParameters["dataFim"]?.toLongOrNull()

        val now = System.currentTimeMillis()
        val MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000L
        val DAYS_IN_A_MONTH = 30L
        val DAYS_IN_A_YEAR = 365L
        val MONTHS_IN_A_QUARTER = 3L

        //label que identifica o tipo do filtro
        var label = ""

        val (start, end) = when{
            filter == "last_year"->{
                label = "vendas no último ano"
                Pair(now - (DAYS_IN_A_YEAR * MILLISECONDS_IN_A_DAY), now)

            }
            filter == "last_quarter" ->{
                label = "vendas no último trimestre"
                Pair(now - (MONTHS_IN_A_QUARTER * DAYS_IN_A_MONTH * MILLISECONDS_IN_A_DAY), now)

            }
            dataInicio != null && dataFim != null ->{
                label = "vendas por período"
                Pair(dataInicio, dataFim)

            }
            else ->{
                call.respond(HttpStatusCode.BadRequest, "Faltam parâmetros ou filtro inválido")
                return@get
            }
        }

        try{
            val sales = saleRepository.listarVendasPorPeriodo(start, end)

            if(sales != null){
                val salesChartData = mutableListOf<SalesChartData>()

                val startMonth = Instant.ofEpochMilli(start).atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1)
                val endMonth = Instant.ofEpochMilli(end).atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1)

                var currentMonth = startMonth
                while (!currentMonth.isAfter(endMonth)) {
                    val monthLabel = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                    val (mes, ano) = monthLabel.split(" ")
                    val vendasNoMes = sales.filter { it.createdAt.toMonthYear() == monthLabel }
                    salesChartData.add(
                        SalesChartData(
                            label= label,
                            mes = mes,
                            ano = ano,
                            totalSales = vendasNoMes.size,
                            totalAmount = vendasNoMes.sumOf { it.totalAmount }
                        )
                    )
                    currentMonth = currentMonth.plusMonths(1)
                }
                call.respond(HttpStatusCode.OK, salesChartData)


            }else{
                call.respond(HttpStatusCode.NotFound, "Nenhuma venda encontrada no período específicado")
            }

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Erro na rota de pegar vendas por periodo: ${e.message}")
        }
    }


}