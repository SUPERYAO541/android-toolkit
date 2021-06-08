package com.superyao.dev.toolkit.log

import timber.log.Timber

/**
 * Usage:
 *
 * class App : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         if (BuildConfig.DEBUG) {
 *             Timber.plant(DebugTree())
 *         }
 *         ...
 *     }
 * }
 *
 */
class DebugTree(
    private val prefixTag: String = "",
    private val richElementTag: Boolean = true,
) : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (prefixTag.isBlank()) {
            super.log(priority, "$prefixTag $tag", message, t)
        } else {
            super.log(priority, tag, message, t)
        }
    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        return if (richElementTag) {
            String.format(
                "[%s][%s][%s]",
                super.createStackElementTag(element),
                element.methodName,
                element.lineNumber
            )
        } else {
            super.createStackElementTag(element)
        }
    }
}