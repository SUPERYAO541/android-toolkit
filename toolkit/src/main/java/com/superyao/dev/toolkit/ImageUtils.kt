@file:JvmName("ImageUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.InputStream

fun getInSampleSizeBitmap(
    inputStream: InputStream,
    targetHeight: Int,
    targetWidth: Int
): Bitmap? {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, this)
        inSampleSize = computeInSampleSize(this, targetHeight, targetWidth)
        inJustDecodeBounds = false
    }
    return BitmapFactory.decodeStream(inputStream, null, options)
}

fun computeInSampleSize(
    options: BitmapFactory.Options,
    targetHeight: Int,
    targetWidth: Int
): Int {
    val h = options.outHeight
    val w = options.outWidth
    var inSampleSize = 1
    if (h > targetHeight || w > targetWidth) {
        val halfH = h / 2
        val halfW = w / 2
        while (halfH / inSampleSize >= targetHeight && halfW / inSampleSize >= targetWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

/*
92% JPEG quality gives a very high-quality image while gaining a significant reduction on the original 100% file size.
85% JPEG quality gives a greater file size reduction with almost no loss in quality.
75% JPEG quality and lower begins to show obvious differences in the image, which can reduce your website user experience.
https://sirv.com/help/resources/jpeg-quality-comparison/
 */
const val JPG_QUALITY_MAX = 100
const val JPG_QUALITY_HIGH = 92
const val JPG_QUALITY_MID = 85
const val JPG_QUALITY_LOW = 75
const val JPG_QUALITY_MIN = 25

fun isOrientationNormal(exifInterface: ExifInterface?): Boolean {
    return exifInterface?.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    ).let {
        it == ExifInterface.ORIENTATION_UNDEFINED || it == ExifInterface.ORIENTATION_NORMAL
    }
}

fun resetOrientation(exifInterface: ExifInterface?) {
    exifInterface?.run {
        resetOrientation()
        saveAttributes()
    }
}

/*
Extension
 */

fun Bitmap.jpgBytes(quality: Int = JPG_QUALITY_MAX) = ByteArrayOutputStream().use {
    this.compress(Bitmap.CompressFormat.JPEG, quality, it)
    it.toByteArray()
}

fun Bitmap.pngBytes() = ByteArrayOutputStream().use {
    this.compress(Bitmap.CompressFormat.PNG, 100, it)
    it.toByteArray()
}