@file:JvmName("VibratorUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

fun Context.vibrator(): Vibrator {
    return if (Build.VERSION.SDK_INT >= 31) {
        (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}

fun Context.effectHeavyClickVibrate() {
    vibrator().run {
        if (hasVibrator()) {
            when {
                Build.VERSION.SDK_INT >= 29 -> {
                    vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                }
                Build.VERSION.SDK_INT >= 26 -> {
                    vibrate(VibrationEffect.createOneShot(20, 128))
                }
                else -> vibrate(20)
            }
        }
    }
}

fun Context.effectTickVibrate() {
    vibrator().run {
        if (hasVibrator()) {
            when {
                Build.VERSION.SDK_INT >= 29 -> {
                    vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                }
                Build.VERSION.SDK_INT >= 26 -> {
                    vibrate(VibrationEffect.createOneShot(5, 64))
                }
                else -> vibrate(5)
            }
        }
    }
}