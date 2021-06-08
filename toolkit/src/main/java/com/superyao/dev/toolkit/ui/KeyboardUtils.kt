@file:JvmName("KeyboardUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit.ui

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/*
https://developer.android.com/training/keyboard-input/visibility#ShowOnDemand
 */

fun Context.inputMethodManager() =
    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

fun View.isSoftInputShown() = context.inputMethodManager()?.isActive(this) == true

fun View.showSoftInput(): Boolean {
    return context.inputMethodManager()?.showSoftInput(
        this,
        InputMethodManager.SHOW_IMPLICIT
    ) ?: false
}

fun View.showSoftInputRetry() {
    val delay = 100L
    var delayMax = delay * 10
    val runnable = object : Runnable {
        override fun run() {
            if (!requestFocus() || !showSoftInput()) {
                delayMax -= delay
                if (delayMax > 0) {
                    postDelayed(this, delay)
                }
            }
        }
    }
    postDelayed(runnable, delay)
}

fun View.hideSoftInput() = context.inputMethodManager()?.hideSoftInputFromWindow(windowToken, 0)