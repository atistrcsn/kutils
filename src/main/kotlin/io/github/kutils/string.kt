package io.github.kutils

/**
 * @author atistrcsn - 2017
 */
fun String.truncate(noOfChars: Int): String = substring(0, Math.min(length, noOfChars))

fun String.truncateEllips(noOfChars: Int): String = if (length > noOfChars) truncate(noOfChars).dropLast(3).plus("...") else this
