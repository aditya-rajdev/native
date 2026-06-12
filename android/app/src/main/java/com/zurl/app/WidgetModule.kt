package com.zurl.app

import android.content.Context
import android.content.SharedPreferences
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class WidgetModule(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "WidgetModule"
    }

    @ReactMethod
    fun saveLinks(data: String) {

        val prefs: SharedPreferences =
            reactApplicationContext.getSharedPreferences(
                "widget_data",
                Context.MODE_PRIVATE
            )

        prefs.edit()
            .putString("links", data)
            .apply()
    }

    @ReactMethod
    fun saveSingleWidgetUrl(url: String) {

        val prefs =
            reactApplicationContext.getSharedPreferences(
                "widget_data",
                Context.MODE_PRIVATE
            )

        prefs.edit()
            .putString("single_widget_url", url)
            .apply()
    }
}