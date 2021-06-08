@file:JvmName("UiUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit.ui

import android.text.Editable
import android.widget.EditText
import androidx.core.widget.addTextChangedListener

/*
TextView
 */

//fun highlightKeyword(text: String, keyword: String, color: Int): Spanned {
//    return highlightKeyword(text, keyword, String.format("#%06X", 0xFFFFFF and color))
//}
//
//fun highlightKeyword(text: String, keyword: String, colorHex: String): Spanned {
//    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//        Html.fromHtml(
//            text.replace(
//                keyword,
//                String.format("<font color=\"%s\">%s</font>", colorHex, keyword)
//            ),
//            Html.FROM_HTML_MODE_COMPACT
//        )
//    } else {
//        @Suppress("DEPRECATION")
//        Html.fromHtml(
//            text.replace(
//                keyword,
//                String.format("<font color=\"%s\">%s</font>", colorHex, keyword)
//            )
//        )
//    }
//}

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