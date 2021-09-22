@file:JvmName("UiUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit.ui

import android.text.Editable
import android.widget.EditText
import androidx.core.widget.addTextChangedListener

/*
Prevent clicking too fast
 */

private var lastClickTime = 0L

/**
 * fun yourFunction() {
 *  if (clickTooFast()) return
 *  // Do something what you want
 * }
 */
fun clickTooFast(interval: Int = 500): Boolean {
    var result = false
    val current = System.currentTimeMillis()
    if (current - lastClickTime < interval) result = true
    lastClickTime = current
    return result
}

/*
EditText
 */

fun EditText.addTextChangedListenerAndRun(
    afterTextChanged: (text: Editable?) -> Unit = {}
) {
    afterTextChanged(editableText)
    addTextChangedListener(afterTextChanged = afterTextChanged)
}