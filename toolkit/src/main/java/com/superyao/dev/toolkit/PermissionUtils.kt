@file:JvmName("PermissionUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.app.Activity
import android.content.Context
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
import java.lang.ref.WeakReference

/**
 * The message shown when the user clicks "Don’t ask again".
 */
private const val defaultDontAskAgainDialogMessage =
    "The feature may not work without the requested permissions. " +
            "Do you want to go to the settings page to grant the permissions?"

private const val GRANT_RESULT_GRANTED = 0
private const val GRANT_RESULT_DENIED = -1
private const val GRANT_RESULT_DENIED_DONT_ASK_AGAIN = -2
//@IntDef(
//    GRANT_RESULT_GRANTED,
//    GRANT_RESULT_DENIED,
//    GRANT_RESULT_DENIED_DONT_ASK_AGAIN
//)
//@Retention(AnnotationRetention.SOURCE)
//annotation class GrantResult

// ---------------------------------------------------------------------------------------------
// Allow the system to manage the permission request code:
// https://developer.android.com/training/permissions/requesting#allow-system-manage-request-code
// ---------------------------------------------------------------------------------------------

/**
 * Simple usage:
 *
 * class XxxActivity : AppCompatActivity() {
 *
 *     // step1: declare the PermissionsRequest
 *     private val requestReadExternalStorageRequest = PermissionsRequest(
 *         Manifest.permission.READ_EXTERNAL_STORAGE,
 *         ...
 *     )
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         ...
 *         // step2: init
 *         requestReadExternalStorageRequest.apply {
 *             onGranted = { ... }
 *             onDenied = { doNotAskAgain -> ... }
 *             dontAskAgainDialogMessage = "..."
 *             onDontAskAgainHelpCancel = { ... }
 *         }.init(this)
 *
 *         // step3: now you can request the permissions anywhere
 *         requestReadExternalStorageRequest.request(rationale)
 *         ...
 *     }
 * }
 */
class PermissionsRequest(vararg permissions: String) {
    private val permissions = arrayOf(*permissions)

    private lateinit var weakActivity: WeakReference<ComponentActivity>
    private lateinit var resultLauncher: ActivityResultLauncher<Array<String>>

    var onGranted: (() -> Unit)? = null
    var onDenied: ((doNotAskAgain: Boolean) -> Unit)? = null
    var onDontAskAgainHelpCancel: (() -> Unit)? = null

    var dontAskAgainDialogMessage = ""

    fun init(activity: ComponentActivity) {
        weakActivity = WeakReference(activity)
        weakActivity.get()?.apply {
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                val permissions = it.keys.toTypedArray()
                val grantResults = it.values.toBooleanArray()
                when (handleGrantResults(
                    permissions,
                    grantResults,
                    dontAskAgainDialogMessage,
                    onDontAskAgainHelpCancel
                )) {
                    GRANT_RESULT_GRANTED -> onGranted?.invoke()
                    GRANT_RESULT_DENIED -> onDenied?.invoke(false)
                    GRANT_RESULT_DENIED_DONT_ASK_AGAIN -> onDenied?.invoke(true)
                }
            }.also {
                resultLauncher = it
            }
        }
    }

    private fun Activity.handleGrantResults(
        permissions: Array<String>,
        grantResults: BooleanArray,
        dontAskAgainDialogMessage: String,
        onDontAskAgainHelpCancel: (() -> Unit)? = null,
    ): Int {
        grantResults.forEach {
            if (!it) {
                return doNotAskAgainHandle(
                    permissions,
                    dontAskAgainDialogMessage,
                    onDontAskAgainHelpCancel
                )
            }
        }
        return if (grantResults.isNotEmpty()) GRANT_RESULT_GRANTED else GRANT_RESULT_DENIED
    }

    fun checkPermissions(): Boolean {
        weakActivity.get()?.run {
            permissions.forEach {
                if (ContextCompat.checkSelfPermission(this, it) == PERMISSION_DENIED) {
                    return false
                }
            }
            return true
        }
        return false
    }

    /**
     * Request the permissions, and explain them.
     * Timing of explanation: shouldShowRequestPermissionRationale()
     * https://developer.android.com/training/permissions/requesting#explain
     *
     * @param rationale     the rationale for why would you request the permissions
     * @return return true if all permissions have been granted, otherwise false
     */
    fun request(rationale: String): Boolean {
        weakActivity.get()?.run {
            return requestPermissions(rationale, false, permissions) {
                resultLauncher.launch(permissions)
            }
        }
        return false
    }

    /**
     * Request the permissions, and explain them.
     * Timing of explanation: Before requesting.
     *
     * @param rationale     the rationale for why would you request the permissions
     * @return return true if all permissions have been granted, otherwise false
     */
    fun requestAndExplainFirst(rationale: String): Boolean {
        weakActivity.get()?.run {
            return requestPermissions(rationale, true, permissions) {
                resultLauncher.launch(permissions)
            }
        }
        return false
    }


    /**
     * Request the permissions without explaining.
     *
     * @return return true if all permissions have been granted, otherwise false
     */
    fun requestAndNoExplain(): Boolean {
        weakActivity.get()?.run {
            return requestPermissions("", false, permissions) {
                resultLauncher.launch(permissions)
            }
        }
        return false
    }
}

// ---------------------------------------------------------------------------------------------
// Manage the permission request code yourself:
// https://developer.android.com/training/permissions/requesting#manage-request-code-yourself
// ---------------------------------------------------------------------------------------------

fun Activity.requestPermissions(
    rationale: String,
    requestCode: Int,
    vararg permissions: String,
    onDontAskAgainHelpCancel: (() -> Unit)? = null,
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
    vararg permissions: String,
    onDontAskAgainHelpCancel: (() -> Unit)? = null,
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
    vararg permissions: String,
    onDontAskAgainHelpCancel: (() -> Unit)? = null,
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
 *             if (handleGrantResults(permissions, grantResults)) {
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
fun Activity.handleGrantResults(
    permissions: Array<String>,
    grantResults: IntArray,
    dontAskAgainDialogMessage: String = "",
    onDontAskAgainHelpCancel: (() -> Unit)? = null,
): Boolean {
    grantResults.forEach {
        if (it == PERMISSION_DENIED) {
            doNotAskAgainHandle(permissions, dontAskAgainDialogMessage, onDontAskAgainHelpCancel)
            return false
        }
    }
    return grantResults.isNotEmpty()
}

// ---------------------------------------------------------------------------------------------
// common
// ---------------------------------------------------------------------------------------------

fun Context.checkPermissions(vararg permissions: String): Boolean {
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
 * @param onRequest   the function of permission request
 * @return return true if all permissions have been granted, otherwise false
 */
private fun Activity.requestPermissions(
    rationale: String,
    explainFirst: Boolean,
    permissions: Array<String>,
    onRequest: () -> Unit,
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
            dialog(rationale, onPositive = onRequest)
        } else {
            onRequest()
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
    dontAskAgainDialogMessage: String,
    onDontAskAgainHelpCancel: (() -> Unit)? = null,
): Int {
    // 檢查是否有權限同時滿足以下 2 點:
    // 1.checkSelfPermission == PERMISSION_DENIED
    // 2.shouldShowRequestPermissionRationale() == false
    permissions.forEach {
        if (ActivityCompat.checkSelfPermission(this, it) == PERMISSION_DENIED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, it)) {
                dialog(
                    if (dontAskAgainDialogMessage.isNotEmpty()) {
                        dontAskAgainDialogMessage
                    } else {
                        defaultDontAskAgainDialogMessage
                    },
                    onNegative = onDontAskAgainHelpCancel,
                    onPositive = {
                        // 跳轉到該 app 的「應用程式資訊」
                        Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", packageName, null)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }.run {
                            startActivity(this)
                        }
                    }
                )
                return GRANT_RESULT_DENIED_DONT_ASK_AGAIN
            }
        }
    }
    return GRANT_RESULT_DENIED
}

// ---------------------------------------------------------------------------------------------
// others
// ---------------------------------------------------------------------------------------------

private fun Activity.dialog(
    message: String,
    onNegative: (() -> Unit)? = null,
    onPositive: () -> Unit,
) {
    AlertDialog.Builder(this).apply {
        setTitle(android.R.string.dialog_alert_title)
        setMessage(message)
        setPositiveButton(android.R.string.ok) { _, _ -> onPositive() }
        if (onNegative != null) {
            setNegativeButton(android.R.string.cancel) { _, _ -> onNegative() }
        }
        setCancelable(false)
    }.show()
}