@file:JvmName("IntentUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import kotlin.system.exitProcess

fun Intent.clear() {
    replaceExtras(null)
    action = null
    data = null
    flags = 0
}

fun shareExtraTextIntent(text: String, title: String): Intent {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    return Intent.createChooser(intent, title)
}

fun shareExtraStreamIntent(uri: Uri, mimeType: String, title: String): Intent {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(FLAG_GRANT_READ_URI_PERMISSION)
    }
    return Intent.createChooser(intent, title)
}

fun getActionViewIntent(url: String): Intent {
    return Intent(Intent.ACTION_VIEW, Uri.parse(url))
}

fun openURL(context: Context, url: String): Result<Unit> {
    return runCatching { context.startActivity(getActionViewIntent(url)) }
}

fun getRestartIntent(context: Context): Intent {
    val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    val componentName = launchIntent?.component
    return Intent.makeRestartActivityTask(componentName)
}

fun restartApp(context: Context) {
    context.startActivity(getRestartIntent(context))
    exitProcess(0)
}