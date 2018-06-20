package com.premfina.esig.selfserviceportal

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log

class App : Application() {
    var isNightModeEnabled: Int = 1
        set(value) {
            if (value < 3 && value > -1)
                field = value
        }

    override fun onCreate() {
        super.onCreate()
        // We load the Night Mode state here
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        this.isNightModeEnabled = mPrefs.getString("night_mode_preference", "1").toInt()
        Log.i("Night Mode: ", isNightModeEnabled.toString())
        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }
}