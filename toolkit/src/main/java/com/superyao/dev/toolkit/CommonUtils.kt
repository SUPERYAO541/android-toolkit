@file:JvmName("CommonUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources

class Ref<T>(var v: T)

fun Context.getCompatColorStateList(@ColorRes id: Int): ColorStateList {
    return AppCompatResources.getColorStateList(this, id)
}

fun Context.getCompatDrawable(@DrawableRes id: Int): Drawable? {
    return AppCompatResources.getDrawable(this, id)
}

fun Context.getResourceUri(id: Int): Uri {
    return Uri.parse("android.resource://${packageName}/$id")
}

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