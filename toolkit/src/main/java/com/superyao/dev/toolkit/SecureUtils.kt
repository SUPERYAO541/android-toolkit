@file:JvmName("SecureUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

fun getSHA256(content: String): String {
    return getSHA256(content.toByteArray(StandardCharsets.UTF_8))
}

fun getSHA256(bytes: ByteArray): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashed = messageDigest.digest(bytes)
    return BigInteger(hashed).toString(16)
}