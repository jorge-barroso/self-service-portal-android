package com.premfina.esig.selfserviceportal

import android.os.Bundle


class SettingsActivity : Drawer() {
    companion object {
        const val ownbrowserPreferenceKey: String = "ownbrowser_preference"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, SettingsFragment()).commit()
    }
}
