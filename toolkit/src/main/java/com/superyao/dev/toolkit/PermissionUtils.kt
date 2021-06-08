@file:JvmName("PermissionUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_DENIED

/**
 * The message shown when the user clicks "Don’t ask again".
 */
private const val defaultDoNotAskAgainMessage =
    "The feature may not work without the requested permissions. " +
            "Do you want to go to the settings page to grant the permissions?"

// ---------------------------------------------------------------------------------------------
// Allow the system to manage the permission request code:
// https://developer.android.com/training/permissions/requesting#allow-system-manage-request-code
// ---------------------------------------------------------------------------------------------

/**
 * Simple usage:
 *
 * class XxxActivity : AppCompatActivity() {
 *
 *     private val permissionLauncher = requestPermissionLauncher {
 *         // onGranted
 *     }
 *
 *     private fun requestReadExternalStorage() {
 *         requestPermissions(
 *             permissionLauncher
 *             rationale,
 *             Manifest.permission.READ_EXTERNAL_STORAGE
 *         )
 *     }
 * }
 */
fun ComponentActivity.requestPermissionLauncher(
    doNotAskAgainMessage: String = "",
    onDenied: (() -> Unit)? = null,
    onGranted: (() -> Unit)? = null,
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        val permissions = it.keys.toTypedArray()
        val grantResults = it.values.toBooleanArray()
        if (checkGrantResults(permissions, grantResults, doNotAskAgainMessage)) {
            onGranted?.invoke()
        } else {
            onDenied?.invoke()
        }
    }
}

/**
 * Request the permissions, and explain them.
 * Timing of explanation: shouldShowRequestPermissionRationale()
 * https://developer.android.com/training/permissions/requesting#explain
 *
 * @param rationale     the rationale for why would you request the permissions
 * @return return true if all permissions have been granted, otherwise false
 */
fun Activity.requestPermissions(
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
    rationale: String,
    vararg permissions: String
): Boolean {
    val permissionArray = arrayOf(*permissions)
    return requestPermissions(rationale, false, permissionArray) {
        requestPermissionLauncher.launch(permissionArray)
    }
}

/**
 * Request the permissions, and explain them.
 * Timing of explanation: Before requesting.
 *
 * @param rationale     the rationale for why would you request the permissions
 * @return return true if all permissions have been granted, otherwise false
 */
fun Activity.requestPermissionsExplainFirst(
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
    rationale: String,
    vararg permissions: String
): Boolean {
    val permissionArray = arrayOf(*permissions)
    return requestPermissions(rationale, true, permissionArray) {
        requestPermissionLauncher.launch(permissionArray)
    }
}


/**
 * Request the permissions without explaining.
 *
 * @return return true if all permissions have been granted, otherwise false
 */
fun Activity.requestPermissionsNoExplain(
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
    vararg permissions: String
): Boolean {
    val permissionArray = arrayOf(*permissions)
    return requestPermissions("", false, permissionArray) {
        requestPermissionLauncher.launch(permissionArray)
    }
}

private fun Activity.checkGrantResults(
    permissions: Array<String>,
    grantResults: BooleanArray,
    doNotAskAgainMessage: String,
): Boolean {
    grantResults.forEach {
        if (!it) {
            doNotAskAgainHandle(permissions, doNotAskAgainMessage)
            return false
        }
    }
    return grantResults.isNotEmpty()
}

// ---------------------------------------------------------------------------------------------
// Manage the permission request code yourself:
// https://developer.android.com/training/permissions/requesting#manage-request-code-yourself
// ---------------------------------------------------------------------------------------------

fun Activity.requestPermissions(
    rationale: String,
    requestCode: Int,
    vararg permissions: String
): Boolean {
    return requestPermissions(rationale, false, arrayOf(*permissions)) {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            requestCode
        )
    }
}

fun Activity.requestPermissionsExplainFirst(
    rationale: String,
    requestCode: Int,
    vararg permissions: String
): Boolean {
    return requestPermissions(rationale, true, arrayOf(*permissions)) {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            requestCode
        )
    }
}

fun Activity.requestPermissionsNoExplain(
    requestCode: Int,
    vararg permissions: String
): Boolean {
    return requestPermissions("", false, arrayOf(*permissions)) {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            requestCode
        )
    }
}

/**
 * 此方法應被使用在 onRequestPermissionsResult():
 *
 * override fun onRequestPermissionsResult(
 *     requestCode: Int,
 *     permissions: Array<out String>,
 *     grantResults: IntArray
 * ) {
 *     super.onRequestPermissionsResult(requestCode, permissions, grantResults)
 *     when (requestCode) {
 *         REQUEST_EXTERNAL_STORAGE_PERMISSION -> {
 *             if (checkGrantResults(permissions, grantResults)) {
 *                 // do something after all permissions have been granted
 *             }
 *         }
 *     }
 * }
 *
 * @param permissions  申請的權限
 * @param grantResults 授予結果
 * @return 權限是否全部都有獲得
 */
fun Activity.checkGrantResults(
    permissions: Array<String>,
    grantResults: IntArray,
    doNotAskAgainMessage: String = "",
): Boolean {
    grantResults.forEach {
        if (it == PERMISSION_DENIED) {
            doNotAskAgainHandle(permissions, doNotAskAgainMessage)
            return false
        }
    }
    return grantResults.isNotEmpty()
}

// ---------------------------------------------------------------------------------------------
// common
// ---------------------------------------------------------------------------------------------

fun Activity.checkPermissions(vararg permissions: String): Boolean {
    permissions.forEach {
        if (ContextCompat.checkSelfPermission(this, it) == PERMISSION_DENIED) {
            return false
        }
    }
    return true
}

/**
 * the flow of permissions request
 *
 * @param rationale         the rationale for why would you request the permissions
 * @param explainFirst      if true, explain the rationale before request the permissions
 * @param requestFunction   the function of permission request
 * @return return true if all permissions have been granted, otherwise false
 */
private fun Activity.requestPermissions(
    rationale: String,
    explainFirst: Boolean,
    permissions: Array<String>,
    requestFunction: () -> Unit
): Boolean {
    var anyPermissionHasDenied = false
    var shouldShowRequestPermissionRationale = false
    for (permission in permissions) {
        if (!checkPermissions(permission)) {
            anyPermissionHasDenied = true
            // 檢查是否應向 user 解釋
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                shouldShowRequestPermissionRationale = true
                break
            }
        }
    }
    if (anyPermissionHasDenied) {
        // 是否告知理由後才請求
        if ((explainFirst || shouldShowRequestPermissionRationale) && rationale.isNotEmpty()) {
            dialog(rationale, requestFunction)
        } else {
            requestFunction()
        }
        return false // 缺少權限
    }
    return true // 已獲得所有權限
}

/**
 * 檢查是否有權限被選擇了 Don't ask again
 * 有的話，詢問 user 並引導至「應用程式資訊」去授予權限
 */
private fun Activity.doNotAskAgainHandle(
    permissions: Array<String>,
    doNotAskAgainMessage: String,
) {
    // 檢查是否有權限同時滿足以下 2 點:
    // 1.checkSelfPermission == PERMISSION_DENIED
    // 2.shouldShowRequestPermissionRationale() == false
    permissions.forEach {
        if (ActivityCompat.checkSelfPermission(this, it) == PERMISSION_DENIED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, it)) {
                dialog(
                    if (doNotAskAgainMessage.isNotEmpty()) {
                        doNotAskAgainMessage
                    } else {
                        defaultDoNotAskAgainMessage
                    }
                ) {
                    // 跳轉到該 app 的「應用程式資訊」
                    Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.Builder().scheme("package").authority(packageName).build()
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }.run {
                        startActivity(this)
                    }
                }
                return
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// others
// ---------------------------------------------------------------------------------------------

/**
 * 顯示 Dialog
 *
 * @param message       顯示的內容
 * @param onPositive    按下 positive button 後執行的方法
 */
private fun Activity.dialog(message: String, onPositive: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(android.R.string.dialog_alert_title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { _, _ -> onPositive() }
        .setNegativeButton(android.R.string.cancel, null)
        .setCancelable(false)
        .show()
}