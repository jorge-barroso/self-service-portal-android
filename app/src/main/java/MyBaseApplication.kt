package com.premfina.esig.selfserviceportal

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

open class MyBaseApplication : AppCompatActivity() {
    //protected val scale = applicationContext.resources.displayMetrics.density
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Main_Background)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/gill_sans.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}