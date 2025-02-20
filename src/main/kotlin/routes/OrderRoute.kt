package routes

import extensions.toMonthYear
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.order.toDto
import models.product.book.toResponse
import models.reports.OrdersChartData
import models.reports.SalesChartData
import repositories.BookRepository
import repositories.OrderRepository
import repositories.SaleRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun Route.getOrderById(orderRepository: OrderRepository){
    get("/getOrderById/{id}"){
        try {
            val id = call.parameters["id"]

            if(id.isNullOrBlank()){
                call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                return@get
            }

            val order = orderRepository.getOrderById(id)

            if(order != null){
                call.respond(HttpStatusCode.OK, order.toDto())
            }else{
                call.respond(HttpStatusCode.NoContent)
            }
        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }


    }
}

fun Route.getOrders(orderRepository: OrderRepository){
    get("/getOrders"){
        try {


            val orders = orderRepository.getAllOrders()?.map{
                it.toDto()
            }

            if(orders != null){
                call.respond(HttpStatusCode.OK, orders)
            }else{
                call.respond(HttpStatusCode.NoContent)
            }
        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }


    }
}

fun Route.deleteOrder(orderRepository: OrderRepository){
    delete("/deleteOrder/{id}"){
        try{
            val id = call.parameters["id"]

            if(id.isNullOrBlank()){
                call.respond(HttpStatusCode.BadRequest, "ID inválido ou não fornecido")
                return@delete
            }

            val wasDeleted = orderRepository.removerPedido(id)

            if(wasDeleted){
                call.respond(HttpStatusCode.OK, "Produto deletado com sucesso")
            }else{
                call.respond(HttpStatusCode.NotFound, "Produto não deletado")
            }

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }

    }
}

fun Route.getPedidosPendentesQuantity(orderRepository: OrderRepository){
    get("/getPedidosPendentesQuantity"){
        try {
            val pedidos = orderRepository.listarPedidosPorStatus("processing")

            if(pedidos != null){
                call.respond(HttpStatusCode.OK, pedidos.size)
            }else{
                call.respond(HttpStatusCode.OK, "Nenhum pedido está pendente!")
            }

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado: ${e.message}")
        }
    }
}

fun Route.getOrdersByPeriod(orderRepository: OrderRepository){
    get("/ordersForChart"){
        val filter = call.request.queryParameters["filter"]
        val dataInicio = call.request.queryParameters["dataInicio"]?.toLongOrNull()
        val dataFim = call.request.queryParameters["dataFim"]?.toLongOrNull()

        val now = System.currentTimeMillis()
        val MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000L
        val DAYS_IN_A_MONTH = 30L
        val DAYS_IN_A_YEAR = 365L
        val MONTHS_IN_A_QUARTER = 3L

        //label que identifica o tipo do filtro
        val label = "Pedidos Realizados"

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

        try{
            val orders = orderRepository.listarPedidosPorPeriodo(start, end)

            if(orders != null){
                val ordersChartData = mutableListOf<OrdersChartData>()

                val startMonth = Instant.ofEpochMilli(start).atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1)
                val endMonth = Instant.ofEpochMilli(end).atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1)

                var currentMonth = startMonth
                while (!currentMonth.isAfter(endMonth)) {
                    val monthLabel = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                    val (mes, ano) = monthLabel.split(" ")
                    val ordensNoMes = orders.filter { it.createdAt.toMonthYear() == monthLabel }
                    ordersChartData.add(
                        OrdersChartData(
                            label= label,
                            mes = mes,
                            ano = ano,
                            totalOrders = ordensNoMes.size,
                        )
                    )
                    currentMonth = currentMonth.plusMonths(1)
                }
                call.respond(HttpStatusCode.OK, ordersChartData)


            }else{
                call.respond(HttpStatusCode.NotFound, "Nenhum pedido encontrada no período específicado")
            }

        }catch (e: Exception){
            call.respond(HttpStatusCode.InternalServerError, "Erro na rota de pegar pedidos por periodo: ${e.message}")
        }
    }


}