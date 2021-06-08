@file:JvmName("SecureUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.util.Base64
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/*
base64
 */

private const val base64Flag = Base64.NO_WRAP or Base64.URL_SAFE

fun base64Encode(content: String) = Base64.encodeToString(content.toByteArray(), base64Flag)

fun base64Decode(content: String) = String(Base64.decode(content, base64Flag))

/*
hash
 */

fun sha256(content: String) = sha256(content.toByteArray(StandardCharsets.UTF_8))

fun sha256(bytes: ByteArray): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashed = messageDigest.digest(bytes)
    return BigInteger(hashed).toString(16)
}