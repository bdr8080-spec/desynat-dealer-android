package com.desynatdealer.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.desynatdealer.R
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.model.login.req.LoginResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreference(context: Context) {
    val preference: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = preference.edit()

    companion object {
        var instance: SharedPreference? = null
        fun get(ctx: Context): SharedPreference {
            if (instance == null) {
                instance = SharedPreference(ctx)
            }
            return instance!!
        }
    }

    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is Int -> edit { it.putInt(key, value) }
            is String? -> edit { it.putString(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            else -> Log.e("TAG", "Setting shared pref failed for key: $key and value: $value ")
        }
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    inline operator fun <reified T : Any> SharedPreferences.get(
        key: String,
        defaultValue: T? = null
    ): T? {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String) as T?
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    var isTutorial: Boolean
        get() = preference["tutorial", false]!!
        set(value) = preference.set("tutorial", value)

    var isSubscription: Boolean
        get() = preference["isSubscription", false]!!
        set(value) = preference.set("isSubscription", value)

    var language: String
        get() = preference["language", "en"]!!
        set(value) = preference.set("language", value)

    var accessToken: String
        get() = preference["accessToken", ""]!!
        set(value) = preference.set("accessToken", value)

    var deviceToken: String
        get() = preference["deviceToken", ""]!!
        set(value) = preference.set("deviceToken", value)

    var badgeCount: Int
        get() = preference["badgeCount", 0]!!
        set(value) = preference.set("badgeCount", value)

    fun saveCountryList(countryList: List<GetCountryResponse.Data>) {
        val json = Gson().toJson(countryList)
        preference.set("countryList", json)
    }

    fun getCountryList(): List<GetCountryResponse.Data>? {
        val json = preference["countryList", ""] ?: return null
        return try {
            val type = object : TypeToken<List<GetCountryResponse.Data>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun saveUser(user: GetSellerInfo.Data) {
        val json = Gson().toJson(user)
        preference.set("userData", json)
    }

    fun getUser(): GetSellerInfo.Data? {
        val json = preference["userData", ""] ?: return null
        return try {
            Gson().fromJson(json, GetSellerInfo.Data::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearUser() {
        preference.set("userData", "")
    }

}