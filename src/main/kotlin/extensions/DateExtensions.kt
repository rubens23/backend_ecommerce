package extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toMonthYear(): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    return localDateTime.format(formatter)
}