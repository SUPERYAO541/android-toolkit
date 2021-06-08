@file:JvmName("SharedPreferencesUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import timber.log.Timber

private val gson: Gson by lazy { Gson() }

@Suppress("UNCHECKED_CAST")
fun <T> SharedPreferences.getValue(
    key: String,
    default: T,
    exceptionHandle: (() -> T)? = null
): T {
    return try {
        when (default) {
            is Boolean -> getBoolean(key, default) as T
            is Float -> getFloat(key, default) as T
            is Int -> getInt(key, default) as T
            is Long -> getLong(key, default) as T
            is String -> getString(key, default) as T
            is Set<*> -> getStringSet(key, default as Set<String>) as T
            else -> {
                val json = getString(key, "")
                if (json.isNullOrEmpty()) {
                    throw IllegalArgumentException("The json is null or empty!")
                }
                gson.fromJson(json, (default as Any).javaClass) as T
            }
        }
    } catch (e: Exception) {
        Timber.e(e)
        exceptionHandle?.invoke() ?: default
    }
}

@Suppress("CommitPrefEdits", "UNCHECKED_CAST")
private fun <T> SharedPreferences.putValue(key: String, value: T): SharedPreferences.Editor? {
    return try {
        when (value) {
            is Boolean -> edit().putBoolean(key, value)
            is Float -> edit().putFloat(key, value)
            is Int -> edit().putInt(key, value)
            is Long -> edit().putLong(key, value)
            is String -> edit().putString(key, value)
            is Set<*> -> edit().putStringSet(key, value as Set<String>)
            else -> edit().putString(key, gson.toJson(value))
        }
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
}

fun <T> SharedPreferences.getList(
    key: String,
    classOfT: Class<T>,
    exceptionHandle: (() -> List<T>)? = null
): List<T> {
    return try {
        val json = this.getString(key)
        val jsonArray = JsonParser.parseString(json).asJsonArray
        jsonArray.map { gson.fromJson(it, classOfT) }
    } catch (e: Exception) {
        Timber.e(e)
        exceptionHandle?.invoke() ?: listOf()
    }
}

@Suppress("CommitPrefEdits")
fun <T> SharedPreferences.putList(
    key: String,
    list: List<T>
): SharedPreferences.Editor? {
    return try {
        val jsonArray = JsonArray()
        list.forEach { jsonArray.add(gson.toJsonTree(it)) }
        edit().putString(key, jsonArray.toString())
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
}

/*
Convenience
 */

fun SharedPreferences.getString(key: String) = getValue(key, "")
fun SharedPreferences.getStringSet(key: String): Set<String> = getValue(key, setOf())
fun SharedPreferences.getInt(key: String) = getValue(key, 0)
fun SharedPreferences.getLong(key: String) = getValue(key, 0L)
fun SharedPreferences.getFloat(key: String) = getValue(key, 0f)
fun SharedPreferences.getBoolean(key: String) = getValue(key, false)

fun <T> SharedPreferences.commitValue(key: String, value: T) =
    putValue(key, value)?.commit() == true

fun <T> SharedPreferences.applyValue(key: String, value: T) =
    putValue(key, value)?.apply()

fun <T> SharedPreferences.commitList(key: String, value: List<T>) =
    putList(key, value)?.commit() == true

fun <T> SharedPreferences.applyList(key: String, value: List<T>) =
    putList(key, value)?.apply()