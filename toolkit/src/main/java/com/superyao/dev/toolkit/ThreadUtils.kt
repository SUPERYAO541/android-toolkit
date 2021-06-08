@file:JvmName("ThreadUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

fun sleepPredicate(delayMax: Long, interval: Long = 50, predicate: () -> Boolean): Boolean {
    var past = 0L
    while (!predicate()) {
        past += interval
        if (past < delayMax) Thread.sleep(interval)
        else return false
    }
    return true
}