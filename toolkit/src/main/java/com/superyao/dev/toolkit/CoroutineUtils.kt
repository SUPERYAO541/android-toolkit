@file:JvmName("CoroutineUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import kotlinx.coroutines.delay

suspend fun delayPredicate(
        delayMax: Long = Long.MAX_VALUE,
        interval: Long = 50,
        predicate: () -> Boolean
): Boolean {
    var past = 0L
    while (!predicate()) {
        past += interval
        if (past < delayMax) {
            delay(interval)
        } else {
            return false
        }
    }
    return true
}