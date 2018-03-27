package io.github.kutils

import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Encodes the ByteArray into Base64 encoded one.
 * @author atistrcsn - 2017
 */
inline val ByteArray.base64 get() = Base64.getEncoder().encodeToString(this)

/**
 * Encodes the string into Base64 encoded one.
 * @author Suresh G (@sur3shg)
 */
inline val String.base64 get() = Base64.getEncoder().encodeToString(toByteArray(Charsets.US_ASCII))

/**
 * Decodes the base64 string.
 * @author Suresh G (@sur3shg)
 */
inline val String.base64Decode get() = base64DecodeBytes.toString(Charsets.US_ASCII)

inline val String.md5x16
    get() = MessageDigest.getInstance("MD5").apply { update(toByteArray()) }.digest()

/**
 * Encrypt this string with HMAC-SHA1 using the specified [key].
 *
 * @author Suresh G (@sur3shg)
 * @param key Encryption key
 * @return Encrypted output
 */
fun String.hmacSHA1(key: String): ByteArray {
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA1"))
    return mac.doFinal(toByteArray(Charsets.UTF_8))
}

/**
 * Encrypt this string with AES-128 using the specified [key].
 * Ported from - https://goo.gl/J1H3e5
 *
 * @author Suresh G (@sur3shg)
 * @param key Encryption key.
 * @return Encrypted output.
 */
fun String.aes128Encrypt(key: String): ByteArray {
    val nkey = key.normalizeString(16)
    val msg = rightPadString('{', 16)
    val cipher = Cipher.getInstance("AES/ECB/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(nkey.toByteArray(Charsets.UTF_8), "AES"))
    return cipher.doFinal(msg.toByteArray(Charsets.UTF_8))
}
