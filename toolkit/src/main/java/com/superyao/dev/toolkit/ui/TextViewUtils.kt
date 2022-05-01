@file:JvmName("TextViewUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit.ui

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.annotation.ColorRes

fun TextView.highlightAll(@ColorRes color: Int): TextView {
    SpannableString(text).let {
        it.setSpan(
                ForegroundColorSpan(context.getColor(color)),
                0,
                it.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setText(it, TextView.BufferType.SPANNABLE)
    }
    return this
}

fun TextView.highlightKeyword(
        keyword: String,
        @ColorRes color: Int,
        ignoreCase: Boolean = true
): TextView {
    SpannableString(text).let {
        var start: Int
        var end = 0
        while (true) {
            start = it.indexOf(keyword, end, ignoreCase)
            if (start == -1) break
            end = start + keyword.length
            it.setSpan(
                    ForegroundColorSpan(context.getColor(color)),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        setText(it, TextView.BufferType.SPANNABLE)
    }
    return this
}

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