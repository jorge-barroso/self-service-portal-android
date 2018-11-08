package com.premfina.esig.selfserviceportal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import org.jetbrains.annotations.NotNull
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

open class MyBaseApplication : AppCompatActivity() {
    //protected val scale = applicationContext.resources.displayMetrics.density
    protected lateinit var sharedPreferences: SharedPreferences
    private lateinit var notificationManager: NotificationManager
    private lateinit var channel: NotificationChannel
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        setTheme(R.style.Main_Background)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/gill_sans.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_title)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            channel = NotificationChannel("PremFina", name, importance)
        }
    }

    protected fun resetChannel(@NotNull descriptionText: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}