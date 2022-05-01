@file:JvmName("StorageUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.text.format.Formatter
import java.io.File
import java.io.InputStream

fun isExternalAvailable(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
            && !Environment.isExternalStorageRemovable()
}

fun formatFileSize(context: Context, size: Long): String {
    return Formatter.formatFileSize(context, size)
}

/*
Extension
 */

fun Context.getCacheSubDir(sub: String? = null): File {
    return sub?.let { File(cacheDir, it).apply { mkdirs() } } ?: cacheDir
}

fun Context.getFilesSubDir(sub: String? = null): File {
    return sub?.let { File(filesDir, it).apply { mkdirs() } } ?: filesDir
}

fun Context.getExternalCacheSubDir(sub: String? = null): File {
    return if (isExternalAvailable()) {
        sub?.let { File(externalCacheDir, it).apply { mkdirs() } } ?: externalCacheDir ?: cacheDir
    } else {
        getCacheSubDir(sub)
    }
}

fun Context.getExternalFilesSubDir(sub: String? = null): File {
    return if (isExternalAvailable()) getExternalFilesDir(sub)
            ?: getFilesSubDir(sub) else getFilesSubDir(sub)
}

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

fun File.copyTo(file: File): File {
    return inputStream().use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
            file
        }
    }
}

fun InputStream.copyTo(file: File): File {
    return use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
            file
        }
    }
}

fun Uri.copyTo(context: Context, file: File): File {
    return context.contentResolver.openInputStream(this).use { input ->
        file.outputStream().use { output ->
            input?.copyTo(output)
            file
        }
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

@SuppressLint("Range")
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

fun Uri.getBytes(context: Context): ByteArray? {
    return context.contentResolver.openInputStream(this).use {
        it?.readBytes()
    }
}