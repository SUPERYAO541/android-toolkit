@file:JvmName("PermissionUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_DENIED

/**
 * The message shown when the user clicks "Don’t ask again".
 */
private const val DEFAULT_DONT_ASK_AGAIN_HELP_MESSAGE =
    "The feature may not work without the requested permissions. " +
            "Do you want to go to the settings page to grant the permissions?"

private enum class GrantResult {
    GRANT_RESULT_GRANTED,
    GRANT_RESULT_DENIED,
    GRANT_RESULT_DENIED_DONT_ASK_AGAIN,
}

// ---------------------------------------------------------------------------------------------
// Allow the system to manage the permission request code:
// https://developer.android.com/training/permissions/requesting#allow-system-manage-request-code
// ---------------------------------------------------------------------------------------------

/**
 * Simple usage:
 *
 * class MainActivity : AppCompatActivity() {
 *
 *     // step1: declare the PermissionsRequest
 *     private val readExternalStorageRequest = PermissionsRequest(
 *         Manifest.permission.READ_EXTERNAL_STORAGE,
 *         ...
 *     )
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         ...
 *
 *         // step2: customize the request
 *         readExternalStorageRequest.apply {
 *             rationale = "The rationale of permission you request."
 *             onGranted = { ... }
 *             onDenied = { ... }
 *         }
 *
 *         // step3: now you can request the permissions anywhere
 *         readExternalStorageRequest.request(this)
 *     }
 * }
 */
class PermissionsRequest(componentActivity: ComponentActivity, vararg permissions: String) {

    private val permissions = arrayOf(*permissions)

    var rationale = ""

    var dontAskAgainHelpMessage = DEFAULT_DONT_ASK_AGAIN_HELP_MESSAGE

    var onGranted: (() -> Unit)? = null

    var onDenied: (() -> Unit)? = null

    var onDeniedDontAskAgain = {
        componentActivity.run { // default
            if (isFinishing || isDestroyed) return@run
            dontAskAgainHelpDialog(dontAskAgainHelpMessage, onDenied) {
                goToApplicationDetailsSettings()
                onGoToAppDetailsSettings?.invoke()
            }
        }
    }

    var onGoToAppDetailsSettings: (() -> Unit)? = null

    private val resultLauncher = componentActivity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionResultMap ->
        componentActivity.run {
            if (isFinishing || isDestroyed) return@registerForActivityResult
            when (checkGrantResult(permissionResultMap)) {
                GrantResult.GRANT_RESULT_GRANTED -> onGranted?.invoke()
                GrantResult.GRANT_RESULT_DENIED -> onDenied?.invoke()
                GrantResult.GRANT_RESULT_DENIED_DONT_ASK_AGAIN -> onDeniedDontAskAgain()
            }
        }
    }

    fun unregister() {
        resultLauncher.unregister()
    }

    /**
     * Request the permissions, and explain them.
     * Timing of explanation: shouldShowRequestPermissionRationale()
     * https://developer.android.com/training/permissions/requesting#explain
     *
     * @param activity
     * @param explainNow
     * @return return true if all permissions have been granted, otherwise false
     */
    fun request(activity: Activity, explainNow: Boolean = false): Boolean {
        return activity.requestPermissions(rationale, explainNow, permissions) {
            resultLauncher.launch(permissions)
        }.also { granted ->
            if (granted) {
                onGranted?.invoke()
            }
        }
    }

    fun checkPermissions(context: Context): Boolean {
        return context.checkPermissions(*permissions)
    }
}

// ---------------------------------------------------------------------------------------------
// Manage the permission request code yourself:
// https://developer.android.com/training/permissions/requesting#manage-request-code-yourself
// ---------------------------------------------------------------------------------------------

fun Activity.requestPermissions(
    requestCode: Int,
    permissions: Array<String>,
    rationale: String = "",
    explainNow: Boolean = false,
): Boolean {
    return requestPermissions(rationale, explainNow, permissions) {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
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
 *             when (checkGrantResult(permissions, grantResults)) {
 *                 GrantResult.GRANT_RESULT_GRANTED -> {}
 *                 GrantResult.GRANT_RESULT_DENIED -> {}
 *                 GrantResult.GRANT_RESULT_DENIED_DONT_ASK_AGAIN -> {
 *                     dontAskAgainHelpDialog(...)
 *                 }
 *             }
 *         }
 *     }
 * }
 *
 * @param permissions  申請的權限
 * @param grantResults 授予結果
 * @return 權限是否全部都有獲得
 */
private fun Activity.checkGrantResult(
    permissions: Array<String>,
    grantResults: IntArray
): GrantResult {
    val grantBooleanResults = grantResults.map { it != PERMISSION_DENIED }
    val permissionResultMap = permissions.zip(grantBooleanResults).toMap()
    return checkGrantResult(permissionResultMap)
}

// ---------------------------------------------------------------------------------------------
// General
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
 * @param permissionResultMap   key: Permission, value: GrantResult
 */
private fun Activity.checkGrantResult(permissionResultMap: Map<String, Boolean>): GrantResult {
    var anyPermissionHasDenied = false
    permissionResultMap.forEach {
        // GRANT_RESULT_DENIED_DONT_ASK_AGAIN
        // 檢查 permission 是否同時滿足以下 2 點:
        // 1.權限被拒絕: checkSelfPermission(...) == PERMISSION_DENIED
        if (!it.value) {
            // 2.權限不需顯示理由: shouldShowRequestPermissionRationale(...) == false
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, it.key)) {
                return GrantResult.GRANT_RESULT_DENIED_DONT_ASK_AGAIN
            }
            anyPermissionHasDenied = true
        }
    }
    return if (anyPermissionHasDenied) {
        GrantResult.GRANT_RESULT_DENIED
    } else {
        GrantResult.GRANT_RESULT_GRANTED
    }
}

/**
 * the flow of permissions request
 */
private fun Activity.requestPermissions(
    rationale: String,
    explainNow: Boolean,
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
        if ((explainNow || shouldShowRequestPermissionRationale) && rationale.isNotEmpty()) {
            dialog(rationale, onPositive = onRequest)
        } else {
            onRequest()
        }
        return false // 缺少權限
    }
    return true // 已獲得所有權限
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

fun Activity.dontAskAgainHelpDialog(
    message: String = DEFAULT_DONT_ASK_AGAIN_HELP_MESSAGE,
    onCancel: (() -> Unit)? = null,
    onGoToAppDetailsSettings: (() -> Unit)? = null,
) {
    dialog(
        message,
        onNegative = onCancel,
        onPositive = {
            goToApplicationDetailsSettings()
            onGoToAppDetailsSettings?.invoke()
        }
    )
}

fun Activity.goToApplicationDetailsSettings() {
    Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }.let {
        startActivity(it)
    }
}