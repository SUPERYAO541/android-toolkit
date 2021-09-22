@file:JvmName("CommonUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat

class Ref<T>(var v: T)

fun Context.color(@ColorRes color: Int, theme: Resources.Theme? = null) =
    ResourcesCompat.getColor(resources, color, theme)

fun Context.colorStateList(@ColorRes id: Int): ColorStateList =
    AppCompatResources.getColorStateList(this, id)

fun Context.drawable(@DrawableRes id: Int) =
    AppCompatResources.getDrawable(this, id)

fun String.handleNewLine() = replace("\\n", "\n")

fun Context.resourceUri(id: Int): Uri = Uri.parse("android.resource://${packageName}/$id")

fun PackageManager.isInstalledByGooglePlay(applicationId: String): Boolean {
    return try {
        @Suppress("DEPRECATION")
        val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getInstallSourceInfo(applicationId).installingPackageName
        } else {
            getInstallerPackageName(applicationId)
        }
        if (installer == "com.android.vending") {
            return true
        }
        false
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}