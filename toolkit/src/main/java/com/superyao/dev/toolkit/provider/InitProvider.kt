package com.superyao.dev.toolkit.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri

/**
 * 1.define the class:
 * class AppInitProvider : InitProvider() {
 *     override fun onCreate(): Boolean {
 *         // init here
 *     }
 * }
 *
 * 2.AndroidManifest:
 * <manifest>
 *     <application>
 *         <provider
 *             android:name=".provider.AppInitProvider"
 *             android:authorities="${applicationId}.appinitprovider"
 *             android:exported="false" />
 *     </application>
 * </manifest>
 */
abstract class InitProvider : ContentProvider() {

    final override fun getType(uri: Uri) = throw UnsupportedOperationException()

    final override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ) = throw UnsupportedOperationException()

    final override fun insert(
        uri: Uri,
        values: ContentValues?
    ) = throw UnsupportedOperationException()

    final override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = throw UnsupportedOperationException()

    final override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = throw UnsupportedOperationException()
}