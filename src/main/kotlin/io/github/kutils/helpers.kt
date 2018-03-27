package io.github.kutils

import sun.misc.HexDumpEncoder
import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URL
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.jar.Attributes
import java.util.jar.Manifest
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

/**
 * @author atistrcsn - 2017
 */

fun Random.nextInt(range: IntRange): Int = range.start + nextInt(range.last - range.start)

fun rand(range: IntRange): Int = ThreadLocalRandom.current().nextInt(range)

fun <T> Array<T>.rand(): T = get(Random().nextInt(0..size))

/**
 *
 *
 *
 *
 * Common extension functions.
 *
 * @author Suresh G (@sur3shg)
 */
const val SPACE = " "

val LINE_SEP = System.lineSeparator()

val FILE_SEP = File.separator

/**
 * Prints the [Any.toString] to console.
 */
inline val Any?.p get() = println(this)

/**
 * Pseudo Random number generator.
 */
val RAND = Random(System.nanoTime())

/**
 * Prepend an empty string of size [col] to the string.
 *
 * Doesn't preserve original line endings.
 */
fun String.indent(col: Int) = prependIndent(SPACE.repeat(col))

/**
 * Prepend an empty string of size [col] to each string in the list by skipping first [skip] strings.
 *
 * @param skip number of head elements to skip from indentation.
 *             Default to 0 if it's out of bound of list size [0..size]
 */
fun List<String>.indent(col: Int, skip: Int = 0): List<String> {
    val skipCount = if (skip in 0..size) skip else 0
    return mapIndexed { idx, str -> if (idx < skipCount) str else str.indent(col) }
}

/**
 * Convert [Byte] to hex. '0x100' OR is used to preserve the leading zero in case of single hex digit.
 */
val Byte.hex get() = Integer.toHexString(toInt() and 0xFF or 0x100).substring(1, 3).toUpperCase()

/**
 * Convert [Byte] to octal. '0x200' OR is used to preserve the leading zero in case of two digit octal.
 */
val Byte.oct get() = Integer.toOctalString(toInt() and 0xFF or 0x200).substring(1, 4)

/**
 * Convert [ByteArray] to hex.
 */
val ByteArray.hex get() = map(Byte::hex).joinToString(" ")

/**
 * Convert [ByteArray] into the classic: "Hexadecimal Dump".
 */
val ByteArray.hexDump get() = HexDumpEncoder().encode(this)

/**
 * Convert [ByteArray] to octal
 */
val ByteArray.oct get() = map(Byte::oct).joinToString(" ")

/**
 * Hex and Octal util methods for Int and Byte
 */
val Int.hex get() = Integer.toHexString(this).toUpperCase()

val Int.oct get() = Integer.toOctalString(this)

val Byte.hi get() = toInt() and 0xF0 shr 4

val Byte.lo get() = toInt() and 0x0F

/**
 * Convert string to hex.
 */
val String.hex: String get() = toByteArray(Charsets.UTF_8).hex

/**
 * Convert String to octal
 */
val String.oct: String get() = toByteArray(Charsets.UTF_8).oct

/**
 * IPV4 regex pattern
 */
val ip_regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$".toRegex()

val String.isIPv4 get() = matches(ip_regex)

/**
 *  Create an MD5 hash of a string.
 */
val String.md5 get() = hash(toByteArray(Charsets.UTF_8), "MD5")

/**
 *  Create an SHA1 hash of a string.
 */
val String.sha1 get() = hash(toByteArray(Charsets.UTF_8), "SHA-1")

/**
 *  Create an SHA256 hash of a string.
 */
val String.sha256 get() = hash(toByteArray(Charsets.UTF_8), "SHA-256")

/**
 * Decodes the base64 string to byte array. It removes all extra spaces in the
 * input string before doing the base64 decode operation.
 */
inline val String.base64DecodeBytes: ByteArray
    get() {
        val str = replace("\\s+".toRegex(), "")
        return Base64.getDecoder().decode(str)
    }

/**
 * Trim the ASCII whitespace.
 */
fun String.trimAsciiWhitespace() = trim('\t', '\n', '\u000C', '\r', ' ')

/**
 *  Create an MD5 hash of [ByteArray].
 */
val ByteArray.md5 get() = hash(this, "MD5")

/**
 *  Create an SHA1 hash of [ByteArray].
 */
val ByteArray.sha1 get() = hash(this, "SHA-1")

/**
 *  Create an SHA256 hash of [ByteArray].
 */
val ByteArray.sha256 get() = hash(this, "SHA-256")

/**
 * Returns the byte size of the common binary suffixes.
 */
inline val Int.KB get() = this * 1024L
inline val Int.MB get() = this.KB * 1024
inline val Int.GB get() = this.MB * 1024
inline val Int.TB get() = this.GB * 1024

/**
 * Returns human readable binary prefix for multiples of bytes.
 *
 * @param si [true] if it's SI unit, else it will be treated as Binary Unit.
 */
fun Long.toBinaryPrefixString(si: Boolean = false): String {
    // SI and Binary Units
    val unit = if (si) 1_000 else 1_024
    return when {
        this < unit -> "$this B"
        else -> {
            val (prefix, suffix) = when (si) {
                true -> "kMGTPEZY" to "B"
                false -> "KMGTPEZY" to "iB"
            }
            // Get only the integral part of the decimal
            val exp = (Math.log(this.toDouble()) / Math.log(unit.toDouble())).toInt()
            // Binary Prefix mnemonic that is prepended to the units.
            val binPrefix = "${prefix[exp - 1]}$suffix"
            // Count => (unit^0.x * unit^exp)/unit^exp
            String.format("%.2f %s", this / Math.pow(unit.toDouble(), exp.toDouble()), binPrefix)
        }
    }
}

/**
 * Returns human readable binary prefix for multiples of bytes.
 *
 * @param si [true] if it's SI unit, else it will be treated as Binary Unit.
 */
fun Int.toBinaryPrefixString(si: Boolean = false) = toLong().toBinaryPrefixString(si)

/**
 * Get the root cause by walks through the exception chain to the last element,
 * "root" of the tree, using [Throwable.getCause], and returns that exception.
 */
val Throwable?.rootCause: Throwable?
    get() {
        var cause = this
        while (cause?.cause != null) {
            cause = cause.cause
        }
        return cause
    }

/**
 * Find the [msg] hash using the given hashing [algo]
 */
private fun hash(msg: ByteArray, algo: String): String {
    val md = MessageDigest.getInstance(algo)
    md.reset()
    md.update(msg)
    val msgDigest = md.digest()
    return msgDigest.hex
}

/**
 * Pad this String to a desired multiple on the right using a specified character.
 *
 * @param padding Padding character.
 * @param multipleOf Number which the length must be a multiple of.
 */
fun String.rightPadString(padding: Char, multipleOf: Int): String {
    if (isEmpty()) throw IllegalArgumentException("Must supply non-empty string")
    if (multipleOf < 2) throw  IllegalArgumentException("Multiple ($multipleOf) must be greater than one.")
    val needed = multipleOf - (length % multipleOf)
    return padEnd(length + needed, padding)
}

/**
 * Normalize a string to a desired length by repeatedly appending itself and/or truncating.
 *
 * @param desiredLength Desired length of string.
 */
fun String.normalizeString(desiredLength: Int): String {
    if (isEmpty()) throw IllegalArgumentException("Must supply non-empty string")
    if (desiredLength < 0) throw IllegalArgumentException("Desired length ($desiredLength) must be greater than zero.")
    var buf = this
    if (length < desiredLength) {
        buf = repeat(desiredLength / length + 1)
    }
    return buf.substring(0, desiredLength)
}

/**
 * Centers the String in a larger String of size [size].
 * Uses [padChar] as the value to pad the String.
 */
fun String.center(size: Int, padChar: Char = ' ') = when {
    size > length -> {
        val pads = (size - length) / 2
        padStart(length + pads, padChar).padEnd(size, padChar)
    }
    else -> this
}

/**
 * Deletes the files or directory (recursively) represented by this path.
 */
fun Path.delete() {
    if (Files.notExists(this)) {
        return
    }
    if (Files.isDirectory(this)) {
        Files.walkFileTree(this, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes) = let {
                Files.delete(file)
                FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path, exc: IOException) = let {
                Files.delete(dir)
                FileVisitResult.CONTINUE
            }
        })
    } else {
        Files.delete(this)
    }
}

/**
 * Exits the system with [msg]
 */
fun exit(status: Int, msg: (() -> String)? = null) {
    if (msg != null) {
        println(msg())
    }
    System.exit(status)
}

/**
 * Returns the jar [Manifest] of the class. Returns [null] if the class
 * is not bundled in a jar (Classes in an unpacked class hierarchy).
 */
inline val <T : Any> KClass<T>.jarManifest: Manifest?
    get() {
        val res = java.getResource("${java.simpleName}.class")
        val conn = res.openConnection()
        return if (conn is JarURLConnection) conn.manifest else null
    }

/**
 * Returns the jar url of the class. Returns the class file url
 * if the class is not bundled in a jar.
 */
inline val <T : Any> KClass<T>.jarFileURL: URL
    get() {
        val res = java.getResource("${java.simpleName}.class")
        val conn = res.openConnection()
        return if (conn is JarURLConnection) conn.jarFileURL else conn.url
    }

/**
 * Common build info attributes
 */
enum class BuildInfo(val attr: String) {
    Author("Built-By"),
    Date("Built-Date"),
    JDK("Build-Jdk"),
    Target("Build-Target"),
    OS("Build-OS"),
    KotlinVersion("Kotlin-Version"),
    CreatedBy("Created-By"),
    Title(Attributes.Name.IMPLEMENTATION_TITLE.toString()),
    Vendor(Attributes.Name.IMPLEMENTATION_VENDOR.toString()),
    AppVersion(Attributes.Name.IMPLEMENTATION_VERSION.toString())
}

/**
 * Returns the [BuildInfo] attribute value from jar manifest [Attributes]
 */
fun Attributes?.getVal(name: BuildInfo): String = this?.getValue(name.attr) ?: "N/A"

/**
 * Add property delegates which call get/set on the given KProperty reference
 *
 * var foo: String by ::fooImpl
 *
 * var fooImpl: String
 *         get() = ...
 *        set(value) { ... }
 *
 * @see - https://youtrack.jetbrains.com/issue/KT-8658
 */
operator fun <R> KProperty0<R>.getValue(instance: Nothing?, metadata: KProperty<*>): R = get()

operator fun <R> KMutableProperty0<R>.setValue(instance: Nothing?, metadata: KProperty<*>, value: R) = set(value)

operator fun <T, R> KProperty1<T, R>.getValue(instance: T, metadata: KProperty<*>): R = get(instance)

operator fun <T, R> KMutableProperty1<T, R>.setValue(instance: T, metadata: KProperty<*>, value: R) = set(instance, value)