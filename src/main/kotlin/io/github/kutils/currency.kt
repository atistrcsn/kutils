package io.github.kutils

import java.text.NumberFormat
import java.util.*

/**
 * @author atistrcsn - 2017
 */
fun Number.formatWithCurrency(locale: Locale? = null): String = numberFormat(locale).format(this)

private fun numberFormat(locale: Locale? = null): NumberFormat = NumberFormat.getCurrencyInstance(locale ?: hunLocale).apply {
    isGroupingUsed = true
    if (locale == null) maximumFractionDigits = 0
}

fun Number.formatWithUSD(): String = numberFormat(Locale.US).format(this)
fun Number.formatWithEUR(): String = numberFormat(Locale.FRANCE).format(this)
