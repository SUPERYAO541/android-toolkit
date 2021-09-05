@file:JvmName("TimeUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import java.text.SimpleDateFormat
import java.util.*

const val MIN_S = 60
const val HOUR_S = MIN_S * 60
const val DAY_S = HOUR_S * 24

const val MIN_MS = MIN_S * 1000
const val HOUR_MS = HOUR_S * 1000
const val DAY_MS = DAY_S * 1000

/**
 * parse the unix time to long array: longArrayOf(d, h, m, s, ms)
 */
private fun Long.unixTimeToLongArray(): LongArray {
    val d = this / (DAY_S * 1000) // 0
    val h = this / (HOUR_S * 1000) - d * 24 // 1
    val m = this / (MIN_S * 1000) - d * 24 * 60 - h * 60 // 2
    val s = this / 1000 - d * DAY_S - h * HOUR_S - m * MIN_S // 3
    val ms = this - d * DAY_S * 1000 - h * HOUR_S * 1000 - m * MIN_S * 1000 - s * 1000 // 4
    return longArrayOf(d, h, m, s, ms)
}

fun Long.unixTimeHHmmssFormat(): String {
    return unixTimeToLongArray().let { "%02d:%02d:%02d".format(it[1], it[2], it[3]) }
}

val commonSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

fun currentUtcMs() = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis