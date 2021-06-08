@file:JvmName("LogUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit.log

fun Array<StackTraceElement>.string(): String {
    val stringBuilder = StringBuilder("stackTrace.size: ${this.size}\n")
    for (i in this.indices) {
        stringBuilder.append("[$i] ${this[i]}\n")
    }
    return stringBuilder.toString()
}