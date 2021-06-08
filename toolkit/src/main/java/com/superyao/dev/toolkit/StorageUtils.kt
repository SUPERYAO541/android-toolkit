@file:JvmName("StorageUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.text.format.Formatter
import java.io.File
import java.io.InputStream

fun externalAvailable() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        && !Environment.isExternalStorageRemovable()

fun formatFileSize(context: Context, size: Long): String = Formatter.formatFileSize(context, size)

/*
Extension
 */

fun Context.cacheDir(sub: String? = null): File =
    sub?.let { File(cacheDir, it).apply { mkdirs() } } ?: cacheDir

fun Context.filesDir(sub: String? = null): File =
    sub?.let { File(filesDir, it).apply { mkdirs() } } ?: filesDir

fun Context.externalCacheDir(sub: String? = null): File =
    if (externalAvailable()) {
        sub?.let { File(externalCacheDir, it).apply { mkdirs() } } ?: externalCacheDir ?: cacheDir
    } else {
        cacheDir(sub)
    }

fun Context.externalFilesDir(sub: String? = null): File =
    if (externalAvailable()) getExternalFilesDir(sub) ?: filesDir(sub) else filesDir(sub)

fun File.deleteRecursively(keyword: String? = null, keepRoot: Boolean = true) {
    try {
        if (isDirectory) {
            listFiles()?.forEach {
                it.deleteRecursively(keyword, false)
            }
        }
        if (!keepRoot) {
            if (keyword == null) {
                delete()
            } else if (name.contains(keyword)) {
                delete()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun File.copyTo(file: File): File =
    inputStream().use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
            file
        }
    }

fun InputStream.copyTo(file: File): File =
    use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
            file
        }
    }

fun Uri.copyTo(context: Context, file: File): File =
    context.contentResolver.openInputStream(this).use { input ->
        file.outputStream().use { output ->
            input?.copyTo(output)
            file
        }
    }

fun Uri.isExist(context: Context): Boolean {
    var result = false
    try {
        context.contentResolver.query(
            this,
            null,
            null,
            null, null,
            null
        ).use {
            if (it != null && it.moveToFirst()) {
                result = true
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}

fun Uri.getFileName(context: Context): String {
    var fileName = "Unknown"
    try {
        context.contentResolver.query(
            this,
            null,
            null,
            null,
            null,
            null
        ).use {
            fileName = if (it != null && it.moveToFirst()) {
                it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            } else {
                path ?: fileName
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return fileName
}

fun Uri.getFileSize(context: Context): Long {
    var size: Long = 0
    try {
        context.contentResolver.query(
            this,
            null,
            null,
            null,
            null,
            null
        ).use {
            if (it != null && it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (!it.isNull(sizeIndex)) {
                    size = it.getString(sizeIndex).toLong()
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return size
}

fun Uri.getBytes(context: Context) = context.contentResolver.openInputStream(this).use {
    it?.readBytes()
}