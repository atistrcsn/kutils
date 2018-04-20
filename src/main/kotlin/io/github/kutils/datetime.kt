package io.github.kutils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.Temporal
import java.util.*
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

/**
 * @author atistrcsn - 2017
 */
val hunZone: ZoneId = ZoneId.of("Europe/Budapest")

val hunLocale: Locale = Locale("hu", "HU")

val hunDatePattern = "yyyy-MM-dd"
val hunTimePattern = "HH:mm"
val hunDateTimePattern = "$hunDatePattern $hunTimePattern"

val hunDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(hunDatePattern, hunLocale)
val hunTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(hunTimePattern, hunLocale)
val hunDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(hunDateTimePattern, hunLocale)

fun hunDateFormatter(pattern: String): DateTimeFormatter = DateTimeFormatter.ofPattern(pattern, hunLocale)
fun hunTimeFormatter(pattern: String): DateTimeFormatter = DateTimeFormatter.ofPattern(pattern, hunLocale)
fun hunDateTimeFormatter(pattern: String): DateTimeFormatter = DateTimeFormatter.ofPattern(pattern, hunLocale)

fun Temporal.local(pattern: String): String = hunDateTimeFormatter(pattern).withZone(hunZone).format(this)

fun Month.narrow(): String = getDisplayName(TextStyle.NARROW, hunLocale)
fun Month.short(): String = getDisplayName(TextStyle.SHORT, hunLocale)
fun Month.full(): String = getDisplayName(TextStyle.FULL, hunLocale)
fun DayOfWeek.narrow(): String = getDisplayName(TextStyle.NARROW, hunLocale)
fun DayOfWeek.short(): String = getDisplayName(TextStyle.SHORT, hunLocale)
fun DayOfWeek.full(): String = getDisplayName(TextStyle.FULL, hunLocale)

fun XMLGregorianCalendar.toLocalDateTime(zoneId: ZoneId = hunZone): LocalDateTime =
        LocalDateTime.ofInstant(toGregorianCalendar().toInstant(), zoneId)

fun LocalDateTime.toXmlGregorianCalendar(): XMLGregorianCalendar =
        GregorianCalendar.from(atZone(hunZone)).let { DatatypeFactory.newInstance().newXMLGregorianCalendar(it) }

data class LocalDateRangeQuery(val startDate: LocalDate, val endDate: LocalDate) {
    constructor(sameDay: LocalDate) : this(sameDay, sameDay)
}

fun LocalDateRangeQuery.toLocalDateTimeQuery(): LocalDateTimeRangeQuery =
        LocalDateTimeRangeQuery(LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX))

class LocalDateTimeRangeQuery(val startDateTime: LocalDateTime, val endDateTime: LocalDateTime)

fun LocalDateTimeRangeQuery.toLocalDateRangeQuery(): LocalDateRangeQuery =
        LocalDateRangeQuery(startDateTime.toLocalDate(), endDateTime.toLocalDate())