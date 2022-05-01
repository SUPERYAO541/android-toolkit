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

class L18nHelper {

    private lateinit var sharedPreferences: SharedPreferences

    private val gson = Gson()

    /**
     * Please init the L18nHelper:
     * class App : Application() {
     *     override fun attachBaseContext(base: Context) {
     *         L18nHelper.instance.init(base)
     *         super.attachBaseContext(L18nHelper.instance.getLocaledContext(base));
     *     }
     * }
     */
    fun init(context: Context, sharedPreferencesName: String = DEFAULT_SHARED_PREFERENCES_NAME) {
        sharedPreferences = context.getSharedPreferences(sharedPreferencesName,
                Context.MODE_PRIVATE)
    }

    /**
     * for attachBaseContext of Application, Activity, Service:
     * override fun attachBaseContext(base: Context) {
     *     super.attachBaseContext(L18nHelper.instance.getLocaledContext(base))
     * }
     *
     * @param context a context from attachBaseContext(Context base)
     * @return Context.createConfigurationContext(overrideConfiguration)
     */
    fun getLocaledContext(context: Context): Context {
        return context.createConfigurationContext(getLocaledConfiguration(context))
    }

    private fun getLocaledConfiguration(context: Context): Configuration {
        return Configuration(context.resources.configuration).apply { setLocale(getCurrentLocale()) }
    }

    fun getCurrentLocale(): Locale {
        return getPersistentLocale() ?: getAdjustedDefault()
    }

    @JvmOverloads
    fun getCurrentDisplayName(default: String = getCurrentLocale().displayName): String {
        return if (isLocalePersisted()) getCurrentLocale().displayName else default
    }

    fun isLocalePersisted(): Boolean {
        return getPersistentLocale() != null
    }

    fun reset(): Boolean {
        return sharedPreferences.edit().remove(LOCALE_KEY).commit()
    }

    /**
     * 保存之後要顯示的 Locale
     *
     * @param locale  the locale you want to persist
     * @return SharedPreferences.edit().commit()
     */
    fun persistLocale(locale: Locale?): Boolean {
        return sharedPreferences.edit().putString(LOCALE_KEY, gson.toJson(locale)).commit()
    }

    fun getPersistentLocale(): Locale? {
        return gson.fromJson(sharedPreferences.getString(LOCALE_KEY, ""),
                Locale::class.java)
    }

    data class Language(
            // If it's null, it will remove the persistent locale.
            val locale: Locale? = null,
            // If it's empty, it will use the default value.
            val display: String = "",
    )

    companion object {
        private const val DEFAULT_SHARED_PREFERENCES_NAME = "language"
        private const val LOCALE_KEY = "SELECTED_LOCALE"

        val instance by lazy { L18nHelper() }

        fun getAdjustedDefault(): Locale {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleList.getAdjustedDefault()[0]
            } else {
                Locale.getDefault()
            }
        }

        fun selectLanguageDialogBuilder(
                activity: Activity,
                title: String,
                languages: List<Language>,
                onSelected: Runnable? = null
        ): AlertDialog.Builder {
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
                        instance.persistLocale(languages[select[0]].locale)
                        onSelected?.run()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
        }
    }
}