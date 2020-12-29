package app.beer.restaurant.util

import android.app.Application
import android.content.SharedPreferences

class SharedManager {

    var sharedPreferences: SharedPreferences =
        APP_ACTIVITY.getSharedPreferences("restaurant", Application.MODE_PRIVATE)
    var editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun putString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String) : String = sharedPreferences.getString(key, "") ?: ""


    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String) : Boolean = sharedPreferences.getBoolean(key, false)

    fun putInt(key: String, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String): Int = sharedPreferences.getInt(key, 0)

}