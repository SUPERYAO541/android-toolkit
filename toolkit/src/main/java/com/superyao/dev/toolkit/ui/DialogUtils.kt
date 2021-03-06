@file:JvmName("DialogUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit.ui

import android.app.Activity
import android.text.util.Linkify
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

fun AlertDialog.Builder.okButton(
    onClick: (() -> Unit)? = null
): AlertDialog.Builder = setPositiveButton(android.R.string.ok) { _, _ ->
    onClick?.invoke()
}

fun AlertDialog.Builder.cancelButton(
    onClick: (() -> Unit)? = null
): AlertDialog.Builder = setNegativeButton(android.R.string.cancel) { _, _ ->
    onClick?.invoke()
}

fun AlertDialog.lockPositive(lock: Boolean) {
    getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
        isEnabled = !lock
        alpha = if (lock) .5f else 1f
    }
}

fun AlertDialog.lockNegative(lock: Boolean) {
    getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
        isEnabled = !lock
        alpha = if (lock) .5f else 1f
    }
}

fun AlertDialog.lockNeutral(lock: Boolean) {
    getButton(AlertDialog.BUTTON_NEUTRAL)?.apply {
        isEnabled = !lock
        alpha = if (lock) .5f else 1f
    }
}

fun AlertDialog.messageTextView() = findViewById<TextView>(android.R.id.message)

fun AlertDialog.textSelectable(selectable: Boolean = true): AlertDialog {
    findViewById<TextView>(android.R.id.message)?.setTextIsSelectable(selectable)
    return this
}

fun AlertDialog.linkify(): AlertDialog {
    findViewById<TextView>(android.R.id.message)?.also {
        Linkify.addLinks(it, Linkify.WEB_URLS)
    }
    return this
}

fun <T : CharSequence> Activity.messageDialog(
    title: String,
    message: T,
    cancelable: Boolean = true
) = AlertDialog.Builder(this)
    .setTitle(title)
    .setMessage(message)
    .setCancelable(cancelable)
    .okButton()
    .show()
    .textSelectable()
    .linkify()

fun Activity.singleChoiceDialogBuilder(
    title: String,
    items: Array<String>,
    defaultIdx: Int = 0,
    positiveName: String = getString(android.R.string.cancel),
    onPositive: (which: Int) -> Unit
): AlertDialog.Builder {
    val selected = arrayOf(defaultIdx)
    return AlertDialog.Builder(this)
        .setTitle(title)
        .setSingleChoiceItems(items, defaultIdx) { _, which -> selected[0] = which }
        .setPositiveButton(positiveName) { _, _ ->
            onPositive(selected[0])
        }
}