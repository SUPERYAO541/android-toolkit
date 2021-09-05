@file:JvmName("UiUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit.ui

import android.text.Editable
import android.widget.EditText
import androidx.core.widget.addTextChangedListener

/*
EditText
 */

fun EditText.afterTextChangedListener(
    runOnceOnFirst: Boolean = false,
    afterTextChanged: (text: Editable?) -> Unit = {}
) {
    if (runOnceOnFirst) afterTextChanged(editableText)
    addTextChangedListener(afterTextChanged = afterTextChanged)
}