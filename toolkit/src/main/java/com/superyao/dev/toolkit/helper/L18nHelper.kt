package com.superyao.dev.toolkit.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import java.util.*

object L18nHelper : InitializedHelper() {

    private const val LOCALE_KEY = "SELECTED_LOCALE"

    private lateinit var sharedPreferences: SharedPreferences

    private val gson = Gson()

    /**
     * class App : Application() {
     *     ...
     *     override fun attachBaseContext(base: Context) {
     *         L18nHelper.init(base)
     *         super.attachBaseContext(L18nHelper.localedContext(base));
     *     }
     *     ...
     * }
     */
    fun init(context: Context, sharedPreferencesName: String = "language") {
        sharedPreferences = context.getSharedPreferences(
            sharedPreferencesName,
            Context.MODE_PRIVATE
        )
        initialized = true
    }

    /**
     * - Main function -
     *
     * for attachBaseContext of Application, Activity, Service:
     *
     * override fun attachBaseContext(base: Context) {
     *     ...
     *     super.attachBaseContext(L18nHelper.localedContext(base));
     * }
     *
     * @param context a context from attachBaseContext(Context base)
     * @return Context.createConfigurationContext(overrideConfiguration)
     */
    fun localedContext(context: Context): Context {
        initCheck()
        return context.createConfigurationContext(localedConfiguration(context))
    }

    private fun localedConfiguration(context: Context): Configuration {
        return Configuration(context.resources.configuration).apply { setLocale(currentLocale()) }
    }

    fun currentLocale() = getPersistentLocale() ?: getAdjustedDefault()

    @JvmOverloads
    fun displayName(default: String = currentLocale().displayName): String {
        return if (isLocalePersisted()) {
            currentLocale().displayName
        } else {
            default
        }
    }

    fun isLocalePersisted() = getPersistentLocale() != null

    fun reset() = sharedPreferences.edit().remove(LOCALE_KEY).commit()

    /**
     * 保存之後要顯示的 Locale
     *
     * @param locale  the locale you want to persist
     * @return SharedPreferences.edit().commit()
     */
    fun persistLocale(locale: Locale?): Boolean {
        initCheck()
        return sharedPreferences.edit().putString(LOCALE_KEY, gson.toJson(locale)).commit()
    }

    fun getPersistentLocale(): Locale? {
        initCheck()
        return gson.fromJson(
            sharedPreferences.getString(LOCALE_KEY, ""),
            Locale::class.java
        )
    }

    fun getAdjustedDefault(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getAdjustedDefault()[0]
        } else {
            Locale.getDefault()
        }
    }

    /*
    dialog
     */

    data class Language(
        // If it's null, it will remove the persistent locale.
        val locale: Locale? = null,
        // If it's empty, it will use the default value.
        val display: String = "",
    )

    fun selectLanguageDialogBuilder(
        activity: Activity,
        title: String,
        languages: List<Language>,
        onSelected: Runnable? = null
    ): AlertDialog.Builder {
        initCheck()
        val menu = languages.map {
            when {
                it.display.isNotEmpty() -> it.display
                it.locale != null -> "${it.locale.displayName} (${it.locale})"
                else -> throw IllegalArgumentException("locale is null and display is empty.")
            }
        }
        val select = intArrayOf(0)
        return AlertDialog.Builder(activity)
            .setTitle(title)
            .setSingleChoiceItems(menu.toTypedArray(), select[0]) { _, which ->
                select[0] = which
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                persistLocale(languages[select[0]].locale)
                onSelected?.run()
            }
            .setNegativeButton(android.R.string.cancel, null)
    }
}